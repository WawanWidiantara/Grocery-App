package com.example.groceryapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class adapterActivate(private val context: Context, private val activaList : ArrayList<dataActivate>) :RecyclerView.Adapter<adapterActivate.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterActivate.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_riwayat_activate, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapterActivate.MyViewHolder, position: Int) {
        val currentItem = activaList[position]

        //getInvoice
        val getInvoice = currentItem.invoice
        val getStatus = currentItem.status

        if (getStatus.equals(1)){
            holder.textChek.text = "Selesai"
            holder.parentCheckColor.setBackgroundColor(Color.parseColor("#39C7A5"))
        }else{
            holder.textChek.text = "Dibatalkan"
            holder.parentCheckColor.setBackgroundColor(Color.parseColor("#F51A1A"))
        }

        holder.tanggal.text = currentItem.date
        holder.total.text = currentItem.total.toString()
        holder.invoice.text = "INVOICE :" + " " + currentItem.invoice


        holder.parentContainter.setOnClickListener {
            val intent = Intent(context, DetailRiwayat::class.java)
            intent.putExtra("invoice", getInvoice)
            context.startActivity(intent)


        }







    }

    override fun getItemCount(): Int {
        return activaList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val parentCheckColor : LinearLayout = itemView.findViewById(R.id.parentCheck)
        val textChek : TextView = itemView.findViewById(R.id.textCheck)
        val invoice : TextView = itemView.findViewById(R.id.textInvoice)
        val tanggal : TextView = itemView.findViewById(R.id.textTanggal)
        val total : TextView = itemView.findViewById(R.id.textTotalBayar)
        val parentContainter : CardView = itemView.findViewById(R.id.parent)
    }

}