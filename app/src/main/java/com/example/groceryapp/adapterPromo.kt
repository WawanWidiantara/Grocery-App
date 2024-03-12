package com.example.groceryapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject


class adapterPromo (private val context: Context, private val promoList : ArrayList<dataPromo>) : RecyclerView.Adapter<adapterPromo.MyViewHolder>() {

    //animation
    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_promo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapterPromo.MyViewHolder, position: Int) {
        val currentItem = promoList[position]

        //get data
        val getId = currentItem.id
        val getTitle = currentItem.title
        val getKet = currentItem.jenis

        //getImage
        Picasso.get()
            .load(currentItem.image)
            .into(holder.imagePromo)
        var getImage = Picasso.get()
            .load(currentItem.image)
            .into(holder.imagePromo)
        holder.titlePromo.text = currentItem.title
        holder.jenisPromo.text = currentItem.jenis
        holder.hargaPromo.text = "Rp" + " " + currentItem.harga.toString()



        holder.diskonPromo.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.diskonPromo.text = "Rp"+ " " + currentItem.member.toString()
        }
        holder.container.setOnClickListener {
            val intent = Intent(context, DetailProduct::class.java)
            intent.putExtra("id", getId.toString())
            Log.d("ini dari adapter", getId.toString())
            context.startActivity(intent)

        }

        //getToken
        val sharedPreference =  context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token","")

        Log.d("ini token di adaper promo", token.toString())
        //postAcivateProduct
        holder.btnAktifkan.setOnClickListener {

            val dialogPesanan = LayoutInflater.from(context).inflate(R.layout.dialog_activate, null)
//            val alertPesanan = AlertDialog.Builder(context).setView(dialogPesanan)
//            val dialog = alertPesanan.create()
//            dialog.show()


            //inisialisai
            val imagePesanan = dialogPesanan.findViewById<ImageView>(R.id.imagePesanan)
            val titlePesanan = dialogPesanan.findViewById<TextView>(R.id.titlePesanan)
            val ketPesanan = dialogPesanan.findViewById<TextView>(R.id.keteranganPesanan)


            //fungsi
            Picasso.get()
                .load(currentItem.image)
                .into(imagePesanan)
            titlePesanan.text = getTitle
            ketPesanan.text = getKet

            Toast(context).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 250)
                view = dialogPesanan
            }.show()




            val jsonObject = JSONObject()
            try {
                jsonObject.put("product_id", getId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            AndroidNetworking.post("https://grocery-api.tonsu.site/products/activate")

                .addJSONObjectBody(jsonObject)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("token", "Bearer" + " " + token)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.d("ini respon berhasil activate", response.toString())
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


        }
            holder.imagePromo.setOnClickListener {
                // If there's an animation in progress, cancel it
                // immediately and proceed with this one.
                currentAnimator?.cancel()

                // Load the high-resolution "zoomed-in" image.
                Picasso.get()
                    .load(currentItem.image)
                    .into(holder.expandImage)

                // Calculate the starting and ending bounds for the zoomed-in image.
                // This step involves lots of math. Yay, math.
                val startBoundsInt = Rect()
                val finalBoundsInt = Rect()
                val globalOffset = Point()

                // The start bounds are the global visible rectangle of the thumbnail,
                // and the final bounds are the global visible rectangle of the container
                // view. Also set the container view's offset as the origin for the
                // bounds, since that's the origin for the positioning animation
                // properties (X, Y).
                holder.imagePromo.getGlobalVisibleRect(startBoundsInt)
                holder.container.getGlobalVisibleRect(finalBoundsInt, globalOffset)
                startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
                finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

                val startBounds = RectF(startBoundsInt)
                val finalBounds = RectF(finalBoundsInt)

                // Adjust the start bounds to be the same aspect ratio as the final
                // bounds using the "center crop" technique. This prevents undesirable
                // stretching during the animation. Also calculate the start scaling
                // factor (the end scaling factor is always 1.0).
                val startScale: Float
                if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
                    // Extend start bounds horizontally
                    startScale = startBounds.height() / finalBounds.height()
                    val startWidth: Float = startScale * finalBounds.width()
                    val deltaWidth: Float = (startWidth - startBounds.width()) / 2
                    startBounds.left -= deltaWidth.toInt()
                    startBounds.right += deltaWidth.toInt()
                } else {
                    // Extend start bounds vertically
                    startScale = startBounds.width() / finalBounds.width()
                    val startHeight: Float = startScale * finalBounds.height()
                    val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
                    startBounds.top -= deltaHeight.toInt()
                    startBounds.bottom += deltaHeight.toInt()
                }

                // Hide the thumbnail and show the zoomed-in view. When the animation
                // begins, it will position the zoomed-in view in the place of the
                // thumbnail.
//        thumbView.alpha = 0f

                holder.expandImage.visibility = View.VISIBLE

                // Set the pivot point for SCALE_X and SCALE_Y transformations
                // to the top-left corner of the zoomed-in view (the default
                // is the center of the view).
                holder.expandImage.pivotX = 0f
                holder.expandImage.pivotY = 0f

                // Construct and run the parallel animation of the four translation and
                // scale properties (X, Y, SCALE_X, and SCALE_Y).
                currentAnimator = AnimatorSet().apply {
                    play(
                        ObjectAnimator.ofFloat(
                            holder.expandImage,
                            View.X,
                            startBounds.left,
                            finalBounds.left)
                    ).apply {
                        with(ObjectAnimator.ofFloat(holder.expandImage, View.Y, startBounds.top, finalBounds.top))
                        with(ObjectAnimator.ofFloat(holder.expandImage, View.SCALE_X, startScale, 1f))
                        with(ObjectAnimator.ofFloat(holder.expandImage, View.SCALE_Y, startScale, 1f))
                    }
                    duration = shortAnimationDuration.toLong()
                    interpolator = DecelerateInterpolator()
                    addListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {
                            currentAnimator = null
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            currentAnimator = null
                        }
                    })
                    start()
                }

                // Upon clicking the zoomed-in image, it should zoom back down
                // to the original bounds and show the thumbnail instead of
                // the expanded image.
                holder.expandImage.setOnClickListener {
                    currentAnimator?.cancel()

                    // Animate the four positioning/sizing properties in parallel,
                    // back to their original values.
                    currentAnimator = AnimatorSet().apply {
                        play(ObjectAnimator.ofFloat(holder.expandImage, View.X, startBounds.left)).apply {
                            with(ObjectAnimator.ofFloat(holder.expandImage, View.Y, startBounds.top))
                            with(ObjectAnimator.ofFloat(holder.expandImage, View.SCALE_X, startScale))
                            with(ObjectAnimator.ofFloat(holder.expandImage, View.SCALE_Y, startScale))
                        }
                        duration = shortAnimationDuration.toLong()
                        interpolator = DecelerateInterpolator()
                        addListener(object : AnimatorListenerAdapter() {

                            override fun onAnimationEnd(animation: Animator) {
                                holder.imagePromo.alpha = 1f
                                holder.expandImage.visibility = View.GONE
                                currentAnimator = null
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                holder.imagePromo.alpha = 1f
                                holder.expandImage.visibility = View.GONE
                                currentAnimator = null
                            }
                        })
                        start()
                    }
                }
            }
        //animation
        shortAnimationDuration = holder.itemView.resources.getInteger(android.R.integer.config_shortAnimTime)

    }



    override fun getItemCount(): Int {
        return  promoList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) //you can pass whatever information you need from the item that will be clicked, here, i want to further use his position in the adapter's list


    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val imagePromo : ImageView = itemView.findViewById(R.id.imagePromo)
        val titlePromo : TextView = itemView.findViewById(R.id.titlePromo)
        val jenisPromo : TextView = itemView.findViewById(R.id.jenisPromo)
        val hargaPromo : TextView = itemView.findViewById(R.id.hargaPromo)
        val diskonPromo : TextView = itemView.findViewById(R.id.diskonPromo)
        val expandImage : ImageView = itemView.findViewById(R.id.expanded_image)
        val container : CardView = itemView.findViewById(R.id.container)
        val btnAktifkan :  CardView = itemView.findViewById(R.id.btnAktifkanPromo)
    }
}