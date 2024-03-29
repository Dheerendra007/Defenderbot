package com.defenderbot.adapter

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R
import com.defenderbot.model.ChildListBean
import java.util.ArrayList

class VideoAdapter (videolist: List<ChildListBean>, internal var mcontext: Context, mListener: VideoAdapter.OnItemClick) :
        androidx.recyclerview.widget.RecyclerView.Adapter<VideoAdapter.MyViewHolder>() {
    internal var videolist: List<ChildListBean> = ArrayList<ChildListBean>()
    internal var dialog: Dialog


    interface OnItemClick {
        fun showOrderDetail(position: Int, dataItem: ChildListBean)
    }

    inner class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    }

    init {
        this.videolist = videolist
//        this.mListener = mListener
        dialog = Dialog(mcontext)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_videolist, parent, false)
        return MyViewHolder(itemView)
//        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val orderItem = videolist[position]
//        if (!orderItem.getImage().equalsIgnoreCase("")) {
//            Picasso.get().load(orderItem.getImage())
//                    .into(holder.profile_image)
//        }
//        holder.tv_name!!.setText(orderItem.getUsername())
//        holder.tv_price!!.setText(String.valueOf(orderItem.getRatePerWord()) + " $/Word")
//        holder.tv_noofreview!!.text = "(" + orderItem.getOrderCompleted() + " order completed)"
//        holder.rating!!.rating = java.lang.Float.valueOf(orderItem.getRating())
//        holder.layout!!.setOnClickListener { mListener.showOrderDetail(position, orderItem) }
    }


    override fun getItemCount(): Int {
        return videolist.size
    }

    private fun updateOrderAPI() {
        //   progressDialog.show();
        //        VolleyAPIRequest.CallAPIMap(mcontext, "POST", POST_UPDATE_STATUS_CODE,
        //                SoapServicePath.GetSoapServicePath(SoapServicePath.POST_UPDATE_STATUS), Constant.AUTH_KEY,
        //                this, getJsonparam());
    }

    fun getResponse(response: String, ResponceCode: Int) {

    }

//    companion object {
//        lateinit var mListener: VideoAdapter.OnItemClick
//    }

}