package com.example.groceryapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Handler

class adapterMember(private val context: Context, private val memberList : ArrayList<dataMember>) : RecyclerView.Adapter<adapterMember.MyViewHolder>() {

    private lateinit var textView: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterMember.MyViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_member, parent, false)
        return MyViewHolder(itemView)
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: adapterMember.MyViewHolder, position: Int) {
        val currentItem = memberList[position]

        //getid
        val getId = currentItem.product_id
        //get total harga
        val getHargaAsli = currentItem.hargaAsli
        val getHargaDiskon = currentItem.hargaDiskon
        val getHargaMember = currentItem.hargaMember
        holder.titleMember.text = currentItem.namaProduk
        holder.priceMember.text = "Rp" + " " + currentItem.hargaAsli.toString()



        Picasso.get()
            .load(currentItem.image)
            .into(holder.imageMember)

        holder.diskonMember.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.diskonMember.text = "Rp" + " " + currentItem.hargaMember.toString()
        }
        holder.jumlahMember.text = currentItem.jumlah.toString()
        val totalHarga = getHargaAsli - getHargaMember
        holder.totalMember.text = "Total Rp" + " " + totalHarga.toString()
        Log.d("Total harga awal", totalHarga.toString())
        val jumlahPesenan = currentItem.jumlah.toString()
        var num = 0 + jumlahPesenan.toInt()
        holder.addItem.setOnClickListener {
            num++
            val jsonObject = JSONObject()
            try {
                jsonObject.put("product_id", getId)
                jsonObject.put("quantity", num)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val sharedPreference =  context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val token = sharedPreference?.getString("token","")

            AndroidNetworking.post("https://grocery-api.tonsu.site/products/activate")

                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("token", "Bearer" + " " + token)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("ini respon register", response.toString())
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(context, "kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onError(error: ANError) {
                        // handle error
                    }
                })

//            val hitungHargaDiskon = currentItem.hargaDiskon * num
//            Log.d("Harga diskon tambah", currentItem.hargaDiskon.toString())
//            Log.d("Harga diskon tambah", hitungHargaDiskon.toString())
//            val hitungHargaMember = currentItem.hargaMember * num
//            Log.d("Harga member tambah", currentItem.hargaMember.toString())
//            Log.d("Harga member tambah", hitungHargaMember.toString())
            val total = totalHarga * num
            Log.d("Total tambah", currentItem.hargaAsli.toString())
            holder.jumlahMember.text = num.toString()
            holder.totalMember.text = "Total Rp" + " " + total.toString()
        }

        holder.removeItem.setOnClickListener {
            num--
            if (num < 1){
                num = 0
                memberList.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }
            val jsonObject = JSONObject()
            try {
                jsonObject.put("product_id", getId)
                jsonObject.put("quantity", num)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val sharedPreference =  context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val token = sharedPreference?.getString("token","")

            AndroidNetworking.post("https://grocery-api.tonsu.site/products/activate")

                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("token", "Bearer" + " " + token)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("ini respon register", response.toString())
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(context, "kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onError(error: ANError) {
                        // handle error
                    }
                })


//            val hitungHargaDiskon = currentItem.hargaDiskon * num
//            val hitungHargaMember = currentItem.hargaMember * num
            val total = totalHarga * num
            holder.jumlahMember.text = num.toString()
            holder.totalMember.text = "Total Rp" + " " + total.toString()




        }

        holder.btnDelete.setOnClickListener {



            holder.jumlahMember.text = "0"
            val jsonObject = JSONObject()
            try {
                jsonObject.put("product_id", getId)
                jsonObject.put("quantity", 0)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val sharedPreference =  context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val token = sharedPreference?.getString("token","")

            AndroidNetworking.post("https://grocery-api.tonsu.site/products/activate")

                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("token", "Bearer" + " " + token)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("ini respon register", response.toString())
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(context, "kesalahan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onError(error: ANError) {
                        // handle error
                    }
                })
            memberList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }





    }

    override fun getItemCount(): Int {
        return memberList.size
    }






    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val imageMember : ImageView = itemView.findViewById(R.id.imageMember)
        val titleMember : TextView = itemView.findViewById(R.id.titleMember)
        val priceMember : TextView = itemView.findViewById(R.id.priceMember)
        val diskonMember : TextView = itemView.findViewById(R.id.diskonMember)
        val removeItem : ImageView = itemView.findViewById(R.id.removeItem)
        val addItem : ImageView = itemView.findViewById(R.id.addItem)
        val totalMember : TextView = itemView.findViewById(R.id.totalMember)
        val jumlahMember : TextView = itemView.findViewById(R.id.jumlahMember)
        val btnDelete : ImageView = itemView.findViewById(R.id.btnDelete)
//        val parentContainer : LinearLayout = itemView.findViewById(R.id.container)
//        val btnQrCode : Button = itemView.findViewById(R.id.btnGenerate)

    }

}