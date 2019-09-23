package com.defenderbot.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R
import com.defenderbot.fragment.childLocationList
import com.defenderbot.model.getpolyline.ChildlocationItem
import kotlinx.android.synthetic.main.item_child_location.view.*




class ChildLocationAdapter( internal var mcontext: Context, mListener: OnItemClick) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ChildLocationAdapter.MyViewHolder>() {
//    internal var childList: List<ChildlocationItem> = ArrayList<ChildlocationItem>()
    internal var dialog: Dialog
    internal var mListener:OnItemClick


    interface OnItemClick {
        fun itemDetail(position: Int, childlocationItem: ChildlocationItem)
    }

    inner class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tv_location = view.tv_location
        val   tv_locationaddress= view.tv_locationaddress
        val layout = view.layout
    }

    init {
//        this.childList = childList
        this.mListener = mListener
        dialog = Dialog(mcontext)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildLocationAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_child_location, parent, false)
        return MyViewHolder(itemView)
//        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val items = childList[position]
        val items = childLocationList[position]
//        holder.tv_addorigin.
                holder?.tv_location?.text = items.name
        holder?.tv_locationaddress?.text = items.destination_address

        holder?.layout?.setOnClickListener { mListener.itemDetail(position, items) }

//        holder.layout!!.setOnClickListener { mListener.showOrderDetail(position, orderItem) }
    }


    override fun getItemCount(): Int {
        return childLocationList.size
    }

    fun getResponse(response: String, ResponceCode: Int) {

    }

//    companion object {
//        lateinit var mListener: ChildLocationAdapter.OnItemClick
//    }

}