package com.example.foodly.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodly.R
import com.example.foodly.adapter.RestaurantMenuAdapter
import com.example.foodly.fragment.FavoriteFragment
import com.example.foodly.model.RestaurantMenu
import com.example.foodly.util.ConnectionManager.Companion.checkConnectivity
import com.example.foodly.util.Constants
import com.squareup.picasso.Picasso


class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var restaurantImage:ImageView
    lateinit var btnProceedToCart: Button
    lateinit var menuProgress: RelativeLayout
    var restaurantMenuList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resturant_menu)

        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        restaurantImage = findViewById(R.id.rest_image)
        toolBar = findViewById(R.id.toolMenuBar)

        restaurantId = intent.getStringExtra("restaurantId")!!
        restaurantName = intent.getStringExtra("restaurantName")!!
        val bundle: Bundle? = intent.extras
        bundle?.getString("restaurantImage")?.let {
            Picasso.get().load(it).error(R.drawable.restaurant_image)
                .into(restaurantImage)
        }
        layoutManager = LinearLayoutManager(this)
        menuProgress = findViewById(R.id.progressMenu)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

        fetchData()
        setToolBar()

    }

    fun fetchData() {

        val queue = Volley.newRequestQueue(this)
        val url = Constants.url.rest_URL+restaurantId

        if (checkConnectivity(this)) {

            menuProgress.visibility = View.INVISIBLE
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val restaurant = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    restaurant.getString("id"),
                                    restaurant.getString("name"),
                                    restaurant.getString("cost_for_one")
                                )

                                restaurantMenuList.add(menuObject)
                                val intent = Intent(this@RestaurantMenuActivity, FavoriteFragment::class.java)
                                intent.putExtra("QuestionListExtra",
                                    restaurantMenuList )
                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCartLayout,
                                    btnProceedToCart,
                                    restaurantMenuList
                                )


                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(this, "Failed to connect to server!!!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }, Response.ErrorListener
                {
                    Toast.makeText(this, "Volley Error", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = Constants.url.json
                        headers["token"] = Constants.url.key
                        return headers
                    }

                }

            queue.add(jsonObjectRequest)

        } else {
            Toast.makeText(this, "Try again later!!!", Toast.LENGTH_SHORT).show()
        }


    }

    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {

                super.onBackPressed()

            }
        }
        return super.onOptionsItemSelected(item)
    }


}