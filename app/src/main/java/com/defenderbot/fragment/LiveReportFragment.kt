package com.defenderbot.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R
import com.defenderbot.activity.ChildProfileActivity
import com.defenderbot.adapter.ChildListAdapter
import com.defenderbot.adapter.VideoAdapter
import com.defenderbot.model.ChildListBean
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_accedentreport.view.*
import kotlinx.android.synthetic.main.fragment_childlist.view.*
import kotlinx.android.synthetic.main.fragment_livereport.view.*
import java.util.ArrayList

class LiveReportFragment : androidx.fragment.app.Fragment(), View.OnClickListener , OnMapReadyCallback,VideoAdapter.OnItemClick {



    //    var view: View? = null
    lateinit var viewLay: View
    private lateinit var mMap: GoogleMap
    lateinit var mMapView: MapView
    internal var loginType = ""
    internal lateinit var mAdapter: VideoAdapter
    internal var videoList: MutableList<ChildListBean> = ArrayList<ChildListBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewLay = inflater.inflate(R.layout.fragment_livereport, container, false)
        mMapView = viewLay.findViewById(R.id.mapview) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this) //this is important
        setOnclick()
        setTabUI("video")
        setRecyclerView()
        return viewLay
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setOnclick() {
        viewLay.lay_livereporting.setOnClickListener(this)
        viewLay.lay_video.setOnClickListener(this)
        viewLay.lay_photo.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.lay_livereporting -> setTabUI("livereporting")
            R.id.lay_video -> setTabUI("video")
            R.id.lay_photo -> setTabUI("photo")
        }
    }

    override fun showOrderDetail(position: Int, dataItem: ChildListBean) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal fun setRecyclerView() {
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        videoList.add(ChildListBean("",""))
        mAdapter = VideoAdapter(videoList, this.activity!!, this)
        viewLay.recyler_video.setHasFixedSize(true)
//        val mLayoutManager = LinearLayoutManager(activity)
//        viewLay.recyler_video.setLayoutManager(mLayoutManager)
//        viewLay.recyler_video.addItemDecoration(DividerItemDecoration(activity!!, LinearLayoutManager.HORIZONTAL))
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        viewLay.recyler_video.setLayoutManager(linearLayoutManager)
//        viewLay.recyler_video.setItemAnimator(DefaultItemAnimator())
        viewLay.recyler_video.setAdapter(mAdapter)

    }

//    private fun signupIntent() {
//        startActivity(Intent(activity, ChildProfileActivity::class.java))
//    }

    internal fun setTabUI(actionType: String) {
        loginType = actionType
        when (actionType) {
            "livereporting" -> {
                viewLay.tv_livereporting.setTextColor(resources.getColor(R.color.color_blue_dark))
                viewLay.tv_video.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.tv_photo.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.img_livereport_line.setVisibility(View.VISIBLE)
                viewLay.img_video_line.setVisibility(View.INVISIBLE)
                viewLay.img_photo_line.setVisibility(View.INVISIBLE)
            }
            "video" -> {
                viewLay.tv_livereporting.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.tv_video.setTextColor(resources.getColor(R.color.color_blue_dark))
                viewLay.tv_photo.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.img_livereport_line.setVisibility(View.INVISIBLE)
                viewLay.img_video_line.setVisibility(View.VISIBLE)
                viewLay.img_photo_line.setVisibility(View.INVISIBLE)
            }
            "photo" -> {
                viewLay.tv_livereporting.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.tv_video.setTextColor(resources.getColor(R.color.colortexthint))
                viewLay.tv_photo.setTextColor(resources.getColor(R.color.color_blue_dark))
                viewLay.img_livereport_line.setVisibility(View.INVISIBLE)
                viewLay.img_video_line.setVisibility(View.INVISIBLE)
                viewLay.img_photo_line.setVisibility(View.VISIBLE)
            }
        }
    }

}