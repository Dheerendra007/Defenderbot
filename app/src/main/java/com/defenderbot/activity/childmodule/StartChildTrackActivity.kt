package com.defenderbot.activity.childmodule

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.activity.SetPasscodeActivity

import com.defenderbot.model.CommonResponse
import com.defenderbot.model.childlatlong.ChildLatLongResponse
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.defenderbot.util.duonavigationdrawer.Constant.LATITUDE
import com.defenderbot.util.duonavigationdrawer.Constant.LATLONG
import com.defenderbot.util.duonavigationdrawer.Constant.LONGITUDE
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.activity_start_tracking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


var startbool = false

class StartChildTrackActivity : AppCompatActivity() , View.OnClickListener{

    internal lateinit var dialog: Dialog
    internal lateinit var dialog_GPS: Dialog
    internal lateinit var latitude: String
    internal lateinit var longitude:String
    private var mLocationRequest: LocationRequest? = null

    private val UPDATE_INTERVAL = (30 * 1000).toLong()  /* 30 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
//    var startbool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_child_track)
        btn_starttracking!!.setOnClickListener(this@StartChildTrackActivity)
//        dialog = Dialog(this)
//        dialog_GPS = Dialog(this)

    }

    // Trigger new location updates at interval
    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setInterval(UPDATE_INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // do work here
                onLocationChanged(locationResult!!.lastLocation)
            }
        },
                Looper.myLooper())

    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined
        //        String msg = "Updated Location: " +
        //                Double.toString(location.getLatitude()) + "," +
        //                Double.toString(location.getLongitude());
        //        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
        latitude = java.lang.Double.toString(location.latitude)
        longitude = java.lang.Double.toString(location.longitude)
        LATITUDE = latitude
        LONGITUDE = longitude
//        dialog_GPS.dismiss()
        if (!Constant.LATLONG) {
            LATLONG = true
            Constant.SCREEN = "Homescreen"
//            fragment = HomeFragment()
//            setFragment(fragment)
        }
//        if (GEOSTARTED) {
            matchGeoAPI()
//        }
        //        mListener.callGetstatusAPI(latitude,latitude);

        //        matchGeoAPI();
    }

    override fun onResume() {
        super.onResume()
        if(passCode.length>3){
            stopChildTrackingAPI()
        }else{
//            startbool = false
            setView()
        }
    }

    override fun onClick(p0: View?) {
        startbool = !startbool

        if(!startbool){
            setPassIntent()
        }else{
            startLocationUpdates()
        }
        setView()
    }

    private fun setPassIntent() {
        passCode = "";
        startActivity(Intent(this, PasscodeChildActivity::class.java))
    }


    fun setView() {
        if (startbool) {
            btn_starttracking.setText("STOP TRACKING")
        } else {
            btn_starttracking.setText("START TRACKING")
        }
    }



    private fun matchGeoAPI() {
//        Utils.showProgressDialog(this)
        System.out.println("PARENT_ID ="+ PreferenceConnector.readInteger(
                this, PreferenceConnector.PARENT_ID,0).toString())
        System.out.println("CHILD_ID ="+ PreferenceConnector.readInteger(this,
                PreferenceConnector.USER_ID,0).toString())
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
//        System.out.println("User ID = "+ PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val call = apiInterface.matchGeoLocation(PreferenceConnector.readInteger(
                this, PreferenceConnector.PARENT_ID,0).toString(),
                PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString(),
                (""+latitude+"AND"+longitude),"20","ON","update")
        call.enqueue(object : Callback<ChildLatLongResponse> {
            override fun onResponse(call: Call<ChildLatLongResponse>, response: Response<ChildLatLongResponse>) {
                try {
                    if (response.body()!!.status == 200) {
//                    Toast.makeText(this@StartTrackingActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    } else {
//                    Toast.makeText(this@StartTrackingActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    }
                }catch (exe: Exception){

                }

                //                                        myLoading.dismiss();\
//                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<ChildLatLongResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@StartChildTrackActivity)) {
//                    Toast.makeText(this@StartTrackingActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
//                    Toast.makeText(this@StartTrackingActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
//                Utils.hideProgressDialog()
            }
        })

    }

    private fun stopChildTrackingAPI() {
//        Utils.showProgressDialog(this)
        System.out.println("PARENT_ID ="+PreferenceConnector.readInteger(
                this, PreferenceConnector.PARENT_ID,0).toString())
        System.out.println("CHILD_ID ="+PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
//        System.out.println("User ID = "+ PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val call = apiInterface.stopChildTracking(PreferenceConnector.readInteger(
                this, PreferenceConnector.PARENT_ID,0).toString(),
                PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString(),
                passCode)
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                try {
                    if (response.body()!!.status == 200) {
                        startbool = false;
                        Toast.makeText(this@StartChildTrackActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    } else {
                        startbool = true;
                        Toast.makeText(this@StartChildTrackActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    }
                    setView()
                }catch (exe:Exception ){

                }

                //                                        myLoading.dismiss();\
//                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                startbool = !startbool
                setView()
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@StartChildTrackActivity)) {
                    Toast.makeText(this@StartChildTrackActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@StartChildTrackActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
//                Utils.hideProgressDialog()
            }
        })

    }

}
