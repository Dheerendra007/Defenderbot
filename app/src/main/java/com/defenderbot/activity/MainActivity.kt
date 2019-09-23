package com.defenderbot.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.defenderbot.R
import com.defenderbot.adapter.ChildListLocAdapter
import com.defenderbot.fragment.*
import com.defenderbot.geofencing.DrawingOption
import com.defenderbot.model.childlist.ChildListResponse
import com.defenderbot.model.childlist.DataItem
import com.defenderbot.model.getpolyline.ChildlocationItem
import com.defenderbot.model.getpolyline.PolylineResponse
import com.defenderbot.model.inserpolygon.InsertPolygonResponse
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiClient.IMAGE_URL
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.defenderbot.util.duonavigationdrawer.desti_address
import com.defenderbot.util.duonavigationdrawer.destinationLatlong
import com.defenderbot.util.duonavigationdrawer.originLatlong
import com.google.android.gms.maps.model.LatLng
import com.oceansapparel.util.PreferenceConnector
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


//@JvmField
//var childlist = ArrayList<DataItem>()

class MainActivity : AppCompatActivity(), com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener,
        com.defenderbot.interfaces.Callback,View.OnClickListener,ChildListLocAdapter.OnClickItem {

    internal lateinit var fragment: androidx.fragment.app.Fragment
    internal lateinit var mAdapter: ChildListLocAdapter
    internal var childlist: ArrayList<DataItem> = ArrayList<DataItem>()
    var screenFrg ="ChildListFrag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Remove default title text
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

//        val navigationView = findViewById(R.id.nav_view) as NavigationView
        nav_view.setNavigationItemSelectedListener(this)
//        TextView tv_myprofile = (TextView)nav_view.findViewById(R.id.tv_myprofile);
        val hView = nav_view.getHeaderView(0)
        val tv_profile = hView.findViewById(R.id.tv_profile) as TextView
        val tv_username = hView.findViewById(R.id.tv_username) as TextView
        val iv_profile = hView.findViewById(R.id.iv_profile) as CircleImageView

        tv_username.setText(PreferenceConnector.readString(this, PreferenceConnector.USER_NAME,""))
        Picasso.get().load(IMAGE_URL+PreferenceConnector.readString(this, PreferenceConnector.USER_PROFILE_PIC,"")).into(iv_profile)
//        Picasso.get().load("http://52.12.22.233/defenderbot/uploads/avatar.png").into(iv_profile)
        tv_profile.setOnClickListener {
            editProfile()
        }
        fragment = ChildListFragment()
        toolbar_title.text = resources.getString(R.string.titel_childrenlist)
        setFragment(fragment)
        setRecyclerView()
        setOnclick()
        getChildListAPI()

    }

    override fun onResume() {
        super.onResume()
        if (Constant.CHILD_DELETE){
            Constant.CHILD_DELETE =false
            getChildListAPI()
        }
      /*  if (Constant.CHILD_SAVE){
            Constant.CHILD_SAVE =false
            setGeoFencingAPI()
        }*/
    }


    private fun editProfile() {
        intent = Intent(this, EditParentActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    private fun setOnclick() {
        lay_add_child!!.setOnClickListener(this@MainActivity)
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.lay_add_child -> createIntent()
        }
    }

    private fun createIntent() {
        startActivity(Intent(this, CreateChildActivity::class.java))
    }

    internal fun setRecyclerView() {
        mAdapter = ChildListLocAdapter(childlist, this,this)
        recyclerview.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        recyclerview.setAdapter(mAdapter)
    }

    internal fun setChildList(mchildlist: ArrayList<DataItem>){
        childlist.clear()
        if(mchildlist.size>0) {
            childlist.addAll(mchildlist)
            mAdapter.notifyDataSetChanged()
            btn_addchild.setBackgroundResource(R.drawable.add_child)
            showChild(0)
        }else{
            btn_addchild.setBackgroundResource(R.drawable.add_safe_place_glow)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_childrenlistening -> {
                // Handle the camera action
                screenFrg  ="ChildListFrag"
                fragment = ChildListFragment()
                toolbar_title.setText(resources.getString(R.string.titel_childrenlist))
                toolbar_title.visibility = View.GONE
                lay_recylerview.visibility = View.VISIBLE
                lay_add_child.visibility = View.VISIBLE
            }
            R.id.nav_livereporting -> {
                screenFrg  ="LiveReportFrag"
                fragment = LiveReportFragment()
                toolbar_title.setText(resources.getString(R.string.titel_livereport))
                toolbar_title.visibility = View.GONE
                lay_recylerview.visibility = View.VISIBLE
                lay_add_child.visibility = View.GONE
            }
            R.id.nav_accedent_reporting -> {
                screenFrg  ="AccedentReportFrag"
                fragment = AccedentReportFragment()
                toolbar_title.setText(resources.getString(R.string.titel_accidentreport))
                toolbar_title.visibility = View.GONE
                lay_recylerview.visibility = View.VISIBLE
                lay_add_child.visibility = View.GONE
            }
            R.id.nav_setting -> {
                fragment = SettingFragment()
                toolbar_title.setText(resources.getString(R.string.titel_setting))
                toolbar_title.visibility = View.VISIBLE
                lay_recylerview.visibility = View.GONE
            }
            R.id.nav_contactus -> {
                fragment = ContactusFragment()
                toolbar_title.setText(resources.getString(R.string.titel_contactus))
                toolbar_title.visibility = View.VISIBLE
                lay_recylerview.visibility = View.GONE
            }
            R.id.nav_logout -> {
                AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Do you want to logout?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            // Continue with delete operation
                            loginIntent()
                            finish()
                        }

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        setFragment(fragment)
        return true
    }

    private fun loginIntent() {
        PreferenceConnector.clear(this)
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun setFragment(fragment: androidx.fragment.app.Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack("TAG")
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun showChild(position: Int) {
        Constant.CHILD_POSITION = position;
        Constant.CHILD_ID = childlist.get(position).userId!!
//        desti_address = childlist.get(position).destinationAddress!!
        for (it in childlist) {
//            println(name)
            it.is_selected =false
        }
        childlist.get(position).is_selected =true
        mAdapter.notifyDataSetChanged()
        setGeoFencingAPI()

    }


    override fun callFragment() {
        fragment = ChildListFragment()
        toolbar_title.text = resources.getString(R.string.titel_childrenlist)
        setFragment(fragment)
    }


    fun setLatLong(data: List<com.defenderbot.model.getpolyline.DataItem>?){
        coordList.clear()
        if(data!!.size>0) {
            data.forEachIndexed { index, dataItem ->
                coordList.add(LatLng(data.get(index).lat!!, data.get(index).lng!!))
            }


        }
    }

    fun setLocationList(data: List<ChildlocationItem>?){
        childLocationList.clear()
        if(data!!.size>0) {
            childLocationList.addAll(data)
        }

    }

    internal var GeoFancingSize = 0
    internal var latlongslistStrFirst: String = ""
    internal var latlongslistStr: String = ""

    fun getlatlong(){
        GeoFancingSize =0
        latlongslistStrFirst ="";
        latlongslistStr = "";

        for (item in coordList)
        {
            GeoFancingSize++;
            if(GeoFancingSize==1){
                latlongslistStrFirst = latlongslistStr+item.latitude.toString()+"AND"+item.longitude.toString()
            }
            if(GeoFancingSize== coordList.size){
                latlongslistStr = latlongslistStr+item.latitude.toString()+"AND"+item.longitude.toString()+"NEXT"
                latlongslistStr = latlongslistStr+latlongslistStrFirst
            }else{
                latlongslistStr = latlongslistStr+item.latitude.toString()+"AND"+item.longitude.toString()+"NEXT"
            }

        }

//          createGeoFencingAPI()
    }

    private fun getChildListAPI() {
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        System.out.println("User ID = "+PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val call = apiInterface.childList(PreferenceConnector.readInteger(this,
                PreferenceConnector.USER_ID,0).toString())
        call.enqueue(object : Callback<ChildListResponse> {
            override fun onResponse(call: Call<ChildListResponse>, response: Response<ChildListResponse>) {
                Utils.hideProgressDialog()
                if (response.body()!!.status == 200) {
                    PreferenceConnector.writeInteger(this@MainActivity, PreferenceConnector.NO_OF_CHILD, response.body()!!.data!!.size)
                    setChildList((response.body()!!.data as ArrayList<DataItem>?)!!)
                    setGeoFencingAPI()
                }
//                else {
//                    Toast.makeText(this@MainActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
//                }
                //                                        myLoading.dismiss();\
//                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<ChildListResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@MainActivity)) {
                    Toast.makeText(this@MainActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })

    }


    private fun setGeoFencingAPI() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getGeoLocation(PreferenceConnector.readInteger(this,
                PreferenceConnector.USER_ID,0).toString(),Constant.CHILD_ID.toString())
        call.enqueue(object : Callback<PolylineResponse> {
            override fun onResponse(call: Call<PolylineResponse>, response: Response<PolylineResponse>) {

                if (response.body()!!.status == 200) {
                    setLatLong(response.body()!!.childlocation!!.get(0)!!.data as List<com.defenderbot.model.getpolyline.DataItem>?)
                    setLocationList(response.body()!!.childlocation!! as List<ChildlocationItem>?)


                    location_id = response.body()!!.childlocation!!.get(0)!!.id!!
                    try {
                        location_name = response.body()!!.childlocation!!.get(0)!!.name!!
                    }catch (exe: Exception){
                        location_name = "";
                    }
                    if(response.body()!!.childlocation!!.get(0)!!.originlat!! !=0.1) {
                        originLat= response.body()!!.childlocation!!.get(0)!!.originlat!!
                    }
                    if(response.body()!!.childlocation!!.get(0)!!.originlong!! !=0.1) {
                        originlong= response.body()!!.childlocation!!.get(0)!!.originlong!!
                    }
                    if (response.body()!!.childlocation!!.get(0)!!.originlat!! !=0.1 && response.body()!!.childlocation!!.get(0)!!.originlong!! !=0.1){
                        originLatlong = (response.body()!!.childlocation!!.get(0)!!.originlat).toString()+","+
                                response.body()!!.childlocation!!.get(0)!!.originlong
                    }

                    if(response.body()!!.childlocation!!.get(0)!!.destinationlat!! !=0.1) {
                        destinationLat = response.body()!!.childlocation!!.get(0)!!.destinationlat!!
                    }
                    if(response.body()!!.childlocation!!.get(0)!!.destinationlong!! !=0.1) {
                        destinationlong = response.body()!!.childlocation!!.get(0)!!.destinationlong!!
                    }

                    if (response.body()!!.childlocation!!.get(0)!!.destinationlat!! !=0.1 && response.body()!!.childlocation!!.get(0)!!.destinationlong!! !=0.1){
                        destinationLatlong =(response.body()!!.childlocation!!.get(0)!!.destinationlat).toString()+","+
                                response.body()!!.childlocation!!.get(0)!!.destinationlong
                    }

                } else {
                    if(response.body()!!.addLong!! !=0.1 && response.body()!!.addLong!! !=0.1) {
                        childLocationList.clear()
                        coordList.clear()
                        destinationLat = 0.1
                        destinationlong = 0.1
                        originLat= response.body()!!.addLat!!
                        originlong= response.body()!!.addLong!!

                    }


                }

                if(screenFrg.equals("ChildListFrag")) {
                    callFragment()
                }

            }

            override fun onFailure(call: Call<PolylineResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@MainActivity)) {
                    Toast.makeText(this@MainActivity, Constant.SERVER_ERROR,
                            Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, Constant.INTERNET_ERROR,
                            Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
//                Utils.hideProgressDialog()
            }
        })

    }


}
