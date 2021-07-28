package com.example.foodly.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodly.R
import com.example.foodly.adapter.RestaurantRecycleAdapter
import com.example.foodly.database.RestaurantDatabase
import com.example.foodly.database.RestaurantEntity
import com.example.foodly.model.Restaurant
import com.example.foodly.util.ConnectionManager.Companion.checkConnectivity
import com.example.foodly.util.Constants
import com.google.gson.Gson
import com.muddzdev.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.coroutines.launch
import org.json.JSONException


class FavoriteFragment : Fragment(), RestaurantRecycleAdapter.CallBackListener {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favoriteAdapter: RestaurantRecycleAdapter
    lateinit var favoriteProgressLayout: RelativeLayout
    var restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        layoutManager = LinearLayoutManager(activity)
        recyclerView = view.findViewById(R.id.favRecycler)
        favoriteProgressLayout = view.findViewById(R.id.progressFavLayout)
         fetchData()

        return view

    }


override fun onItemClick(holder: RestaurantRecycleAdapter.RestaurantViewHolder, position: Int) {
    Log.d("tag", "working fragment")
    val restaurant = restaurantInfoList[position]
    val restaurantEntity = RestaurantEntity(restaurant.restaurantId, restaurant.restaurantName)
    if (!updateDb(restaurantEntity, 1)) {
        val result = updateDb(restaurantEntity, 2)
        if (result) {
                StyleableToast.Builder(requireContext())
                    .textColor(Color.WHITE)
                    .iconStart(R.drawable.ic_favorite)
                    .length(100)
                    .backgroundColor(Color.RED)
                    .show()

            holder.txtFav.tag = "liked"
            holder.txtFav.background =
                context?.resources?.getDrawable(R.drawable.ic_fav_fill)
        } else {
            Toast.makeText(
                context,
                "Database error. Please Try Again",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
         val result = updateDb(restaurantEntity, 3)
        if (result) {
                StyleableToast.Builder(requireContext())
                    .textColor(Color.WHITE)
                    .iconStart(R.drawable.ic_favorite)
                    .length(100)
                    .backgroundColor(Color.RED)
                    .show()
            holder.txtFav.tag = "unliked"
            holder.txtFav.background =
                context?.resources?.getDrawable(R.drawable.ic_fav_outline)
        } else {
            Toast.makeText(
                context,
                "Database error. Please Try Again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    updateDb(restaurantEntity, 1)
    holder.txtFav.tag = "liked"
    holder.txtFav.background =
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_fav_fill)
}

fun updateDb(restaurantEntity: RestaurantEntity, mode: Int): Boolean {
    val db =
        Room.databaseBuilder(requireContext(), RestaurantDatabase::class.java, "restaurant-db")
            .build()
    var flag = false

    lifecycleScope.launch {
        when (mode) {
            1 -> {
                db.restaurantDao().getAllRestaurant(restaurantEntity.restaurant_Id)
                db.close()
                flag = true
                   }

            2 -> {
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                flag = true
            }

            3 -> {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                flag = true
            }
        }
    }
    return flag
}

private fun fetchData() {
    if (checkConnectivity(activity as Context)) {
        favoriteProgressLayout.visibility = View.INVISIBLE
        try {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if (success) {
                        restaurantInfoList.clear()
                        val data = response.getJSONArray("data")

                        for (i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantEntity = RestaurantEntity(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name")
                            )

                            val status = updateDb(restaurantEntity, 1)
                            Log.d("tag","status is $status")

                            if (status) {
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)
                            }
                        }
                        favoriteAdapter = RestaurantRecycleAdapter(activity as Context, restaurantInfoList, this)
                        recyclerView.adapter = favoriteAdapter
                        recyclerView.layoutManager = layoutManager
                        if (restaurantInfoList.size == 0) {
                            nothingFavIV.visibility = View.VISIBLE
                            nothingFavTV.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Failed to connect to server!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
                        "Failed to connect to server!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = Constants.url.json
                        headers["token"] = Constants.url.key
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (exception: Exception) {
            exception.stackTrace
        }
    } else {

        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
        alterDialog.setTitle("No Internet")
        alterDialog.setMessage("Check Internet Connection!")
        alterDialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_SETTINGS)
            startActivity(settingsIntent)
        }
        alterDialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(activity as Activity)
        }
        alterDialog.setCancelable(false)
        alterDialog.create()
        alterDialog.show()
    }
}

}
