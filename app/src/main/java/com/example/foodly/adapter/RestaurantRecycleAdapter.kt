package com.example.foodly.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodly.R
import com.example.foodly.activity.RestaurantMenuActivity
import com.example.foodly.database.RestaurantEntity
import com.example.foodly.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantRecycleAdapter(val context: Context, private var itemList: ArrayList<Restaurant>,val listener: CallBackListener) :
    RecyclerView.Adapter<RestaurantRecycleAdapter.RestaurantViewHolder>() {
    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtFav: TextView = view.findViewById(R.id.favTextView)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtFoodName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtFoodRating)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val restaurantImage: ImageView = view.findViewById(R.id.foodImageView)
        val rlContent: RelativeLayout = view.findViewById(R.id.rlContent)

    }
    interface CallBackListener {
        fun onItemClick(holder:RestaurantViewHolder, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard, parent, false)

        return RestaurantViewHolder(view)

    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        val restaurant = itemList[position]
        RestaurantEntity(restaurant.restaurantId, restaurant.restaurantName)

        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        holder.txtRestaurantPrice.text = "${restaurant.restaurantPrice}/Person"

        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurant_image)
            .into(holder.restaurantImage)

        holder.rlContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId", restaurant.restaurantId)
            intent.putExtra("restaurantName", restaurant.restaurantName)
            intent.putExtra("restaurantImage", restaurant.restaurantImage.toString())
            intent.putExtra("restaurantPrice", restaurant.restaurantPrice.toString())
            intent.putExtra("restaurantRating", restaurant.restaurantRating.toString())
            context.startActivity(intent)
        }

        /*val checkFav = FavoriteFragment.updateDb(restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.txtFav.tag = "liked"
            holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_fill)
        } else {
            holder.txtFav.tag = "unliked"
            holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_outline)
        }*/
        holder.txtFav.setOnClickListener {
            Log.d("tag","working from adapter")


            listener?.onItemClick(holder,position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}



