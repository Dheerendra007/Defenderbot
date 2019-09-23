package com.defenderbot.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.defenderbot.R

import com.defenderbot.adapter.ChildLocationAdapter
import com.defenderbot.fragment.*
import com.defenderbot.geofencing.DrawingOption
import com.defenderbot.geofencing.DrawingOptionBuilder
import com.defenderbot.model.ChildListBean
import com.defenderbot.model.getpolyline.ChildlocationItem
import com.defenderbot.model.getpolyline.DataItem
import com.defenderbot.util.duonavigationdrawer.desti_address

import com.defenderbot.util.duonavigationdrawer.destinationLatlong
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_child_profile.*
import kotlinx.android.synthetic.main.activity_child_profile.iv_back
import kotlinx.android.synthetic.main.activity_childlocation_list.*
import kotlinx.android.synthetic.main.fragment_accedentreport.view.*
@JvmField
var ChildLocationBool = false

class ChildLocationListActivity : AppCompatActivity(), View.OnClickListener, ChildLocationAdapter.OnItemClick {

    internal lateinit var mAdapter: ChildLocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_childlocation_list)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
//        toolbar_title = toolbar.findViewById(R.id.toolbar_title) as TextView
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setOnclick()
        setRecyclerView()
    }

    private fun setOnclick() {
        iv_back!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.iv_back -> finish()
//            R.id.lay_creategeofencing -> polygonIntent()

        }
    }


    override fun itemDetail(position: Int, childlocationItem: ChildlocationItem) {
        coordList.clear()
        if(childlocationItem.data!!.size>0) {

            childlocationItem.data.forEachIndexed { index, dataItem ->
                coordList.add(LatLng(childlocationItem.data.get(index)!!.lat!!, childlocationItem.data.get(index)!!.lng!!))
            }
        }
        location_id = childlocationItem.id!!
        location_name = childlocationItem.name!!
        ChildLocationBool =true
        destinationLat = childlocationItem.destinationlat!!
        destinationlong = childlocationItem.destinationlong!!
        destinationLatlong = destinationLat.toString() + "," + destinationlong.toString()
        desti_address = childlocationItem.destination_address.toString()
        finish()
    }

    internal fun setRecyclerView() {
        mAdapter = ChildLocationAdapter( this!!, this)
        recycler_childlocation.setHasFixedSize(true)
        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_childlocation.setLayoutManager(mLayoutManager)
        recycler_childlocation.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
        recycler_childlocation.setItemAnimator(androidx.recyclerview.widget.DefaultItemAnimator())
        recycler_childlocation.setAdapter(mAdapter)
    }



}



