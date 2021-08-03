package com.cabral.emaishapay.adapters.Shop

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.R
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts
import kotlinx.android.synthetic.main.order_details_item.view.*

class SalesDetailsAdapter(val context: Context, val orderDetailsList: List<ShopOrderProducts?>) : RecyclerView.Adapter<SalesDetailsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_details_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(orderDetailsList.get(position), position)
     
    }

    override fun getItemCount(): Int = orderDetailsList.size


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private  var pos : Int =0
        private var current:ShopOrderProducts? = null

        fun setData(shopOrderProductsDetails: ShopOrderProducts?, position: Int) {
            shopOrderProductsDetails?.let {
                itemView.txtProductName.text = it.product_name
                itemView.txtQty.text = context.getString(R.string.quantity) + "  " + it.product_qty
                itemView.txtWeight.text = context.getString(R.string.weight) + "  " + it.product_weight

                val base64Image = it.product_image
                val price = it.product_price.toDouble()
                val quantity = it.product_qty.toInt()
                val cost = quantity * price
                val currency = context.getString(R.string.currency)
                val totalCostContent="$currency $price x $quantity = $currency $cost"
                itemView.txtTotalCost.text = totalCostContent
                if (base64Image != null) {
                    if (base64Image.isEmpty() || base64Image.length < 6) {
                        itemView.imgProduct.setImageResource(R.drawable.image_placeholder)
                    } else {
                        val bytes = Base64.decode(base64Image, Base64.DEFAULT)
                        itemView.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                    }
                }
                this.current=it
                this.pos=position
            }
        }

    }

}