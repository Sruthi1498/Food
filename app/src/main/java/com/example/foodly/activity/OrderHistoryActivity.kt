package com.example.foodly.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodly.R
import com.example.foodly.adapter.OrderHistoryAdapter
import com.example.foodly.model.OrderHistoryRestaurant
import com.example.foodly.util.ConnectionManager
import com.example.foodly.util.ConnectionManager.Companion.checkConnectivity
import com.example.foodly.util.Constants
import org.json.JSONException
import java.lang.Exception

class OrderHistoryActivity : AppCompatActivity() {

    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var menuAdapter1: OrderHistoryAdapter
    lateinit var recyclerViewAllOrders: RecyclerView
    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var orderHistoryLayout: RelativeLayout
    lateinit var noOrders: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        recyclerViewAllOrders = findViewById(R.id.recyclerViewAllOrders)
        toolBar = findViewById(R.id.toolBarOrderHistory)
        orderHistoryLayout = findViewById(R.id.orderHistoryLayout)
        noOrders = findViewById(R.id.noOrders)

        setToolBar()
        setItemsForEachRestaurant()
    }

    private fun setItemsForEachRestaurant() {

        layoutManager1 = LinearLayoutManager(this)
        val orderedRestaurantList = ArrayList<OrderHistoryRestaurant>()
        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        val userId = sharedPreferences.getString("user_id", "000")
        if (checkConnectivity(this)) {

            orderHistoryLayout.visibility = View.VISIBLE

            try {
                val queue = Volley.newRequestQueue(this)
                val url =
                    Constants.url.orderUrL+userId
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            val data = response.getJSONArray("data")
                            if (data.length() == 0) {

                                Toast.makeText(
                                    this,
                                    "No Orders Placed yet!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                noOrders.visibility = View.VISIBLE

                            } else {
                                noOrders.visibility = View.INVISIBLE

                                for (i in 0 until data.length()) {
                                    val restaurantItem = data.getJSONObject(i)
                                    val restaurantObject = OrderHistoryRestaurant(
                                        restaurantItem.getString("order_id"),
                                        restaurantItem.getString("restaurant_name"),
                                        restaurantItem.getString("total_cost"),
                                        restaurantItem.getString("order_placed_at").substring(0, 10)
                                    )

                                    orderedRestaurantList.add(restaurantObject)
                                    menuAdapter1 = OrderHistoryAdapter(this, orderedRestaurantList)
                                    recyclerViewAllOrders.adapter = menuAdapter1
                                    recyclerViewAllOrders.layoutManager = layoutManager1
                                }
                            }
                        }
                        orderHistoryLayout.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        orderHistoryLayout.visibility = View.INVISIBLE

                        Toast.makeText(
                            this,
                            "Error in loading Order History. Try again!!!",
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
                Toast.makeText(
                    this,
                    "Failed to connect to server!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            catch (exception: Exception){
                exception.stackTrace
            }

        } else {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
    }

    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "My Previous Orders"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
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