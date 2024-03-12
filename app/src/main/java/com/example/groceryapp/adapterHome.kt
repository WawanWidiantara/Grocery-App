package com.example.groceryapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class adapterHome(private val context: Context, private val homeList : ArrayList<dataHome>) : RecyclerView.Adapter<adapterHome.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_home, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = homeList[position]

        //getId
        val getId = currentItem.id
        val getImage = currentItem.image

        //getImage
        Picasso.get()
            .load(currentItem.image)
            .into(holder.imageHome)


        holder.imageHome.setOnClickListener {
            val intent = Intent(context, DetailCatalog::class.java)
            intent.putExtra("id", getId.toString())
            intent.putExtra("image", getImage)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return homeList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val imageHome : ImageView = itemView.findViewById(R.id.imageHome)
    }

}