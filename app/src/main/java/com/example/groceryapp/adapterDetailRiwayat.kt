package com.example.groceryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class adapterDetailRiwayat(private val context: Context, private val detailRiwayatList : ArrayList<dataDetailRiwayat>) : RecyclerView.Adapter<adapterDetailRiwayat.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterDetailRiwayat.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_detail_riwayat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapterDetailRiwayat.MyViewHolder, position: Int) {
        val currentItem = detailRiwayatList[position]

        Picasso.get()
            .load(currentItem.image)
            .into(holder.imageDetail)
        holder.titleDetail.text = currentItem.nama
        holder.jumlahDetail.text = "Jumlah :" + " " + currentItem.jumlah.toString()
        holder.hargaDetail.text = "Rp" + " " + currentItem.harga.toString()
        holder.totalHargaDetail.text = "Total : Rp" + " " + currentItem.totalHarga.toString()
    }

    override fun getItemCount(): Int {
        return detailRiwayatList.size
    }

    class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

        val imageDetail : ImageView = itemView.findViewById(R.id.imageRiwayat)
        val titleDetail : TextView = itemView.findViewById(R.id.titleRiwayat)
        val jumlahDetail : TextView = itemView.findViewById(R.id.jumlahRiwayat)
        val hargaDetail : TextView = itemView.findViewById(R.id.hargaSatuanRiwayat)
        val totalHargaDetail : TextView = itemView.findViewById(R.id.totalHargaRiwayat)

    }
}