package com.defenderbot.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R

import com.squareup.picasso.Picasso
import java.util.ArrayList
import kotlinx.android.synthetic.main.item_child_loc.view.*

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.defenderbot.activity.MainActivity
import com.defenderbot.model.childlist.DataItem
import com.defenderbot.retrofit.ApiClient


class ChildListLocAdapter(val items : ArrayList<DataItem>, val context: MainActivity, listener: ChildListLocAdapter.OnClickItem) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    internal lateinit var fragment: androidx.fragment.app.Fragment
    var mListener: ChildListLocAdapter.OnClickItem = listener;


    interface OnClickItem {
        fun showChild(position: Int)
    }

    init {
        mListener =listener;
    }

//    ChildListLocAdapter(val items : ArrayList<DataItem>, val context: MainActivity,listener: ChildListLocAdapter.OnClickItem){
//        mListener = listener;
//    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_child_loc, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(ApiClient.IMAGE_URL+items.get(position).image).into(holder?.ivprofile_image);
        if(items.get(position).is_selected!!){
            holder?.ivprofile_image.borderColor = Color.RED
        }else{
            holder?.ivprofile_image.borderColor = Color.WHITE
        }
        holder?.ivprofile_image.setOnClickListener(View.OnClickListener {
//            CHILD_POSITION = position;
//            CHILD_ID = items.get(position).userId
//            context.callFragment()
            mListener.showChild(position)
        })
    }

    private fun setFragment(frg: androidx.fragment.app.Fragment?) {
        if (frg != null) {
//            var frg: Fragment? = null
//            frg = getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG")
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.detach(frg)
            ft.attach(frg)
            ft.commit()

        }
    }
}

class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ivprofile_image= view.profile_image
}
