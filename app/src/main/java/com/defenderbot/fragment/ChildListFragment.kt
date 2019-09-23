package com.defenderbot.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.activity.ChildProfileActivity
import com.defenderbot.activity.CreateChildActivity

import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.duonavigationdrawer.Constant

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.child_list_empty_layout.view.*

import kotlinx.android.synthetic.main.fragment_childlist.view.*
import android.location.Location
import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.defenderbot.geofencing.DataModel
import com.defenderbot.geofencing.DrawingOption
import com.defenderbot.geofencing.DrawingOptionBuilder
import com.defenderbot.geofencing.MapsActivity.*
import com.defenderbot.model.getchildlatlong.GetChildLatLongResponse
import com.defenderbot.model.getpolyline.ChildlocationItem
import com.defenderbot.model.getpolyline.DataItem
import com.defenderbot.model.getpolyline.PolylineResponse
import com.defenderbot.model.inserpolygon.InsertPolygonResponse
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.desti_address
import com.defenderbot.util.duonavigationdrawer.destinationLatlong
import com.defenderbot.util.duonavigationdrawer.originLatlong


import com.github.bkhezry.extramaputils.builder.ExtraPolygonBuilder
import com.github.bkhezry.extramaputils.builder.ViewOptionBuilder
import com.github.bkhezry.extramaputils.model.ViewOption
import com.github.bkhezry.extramaputils.utils.MapUtils;
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*

//import com.google.android.gms.common.util.MapUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.fragment_childlist.view.no_record_fragment
import org.joda.time.DateTime
import org.xml.sax.ErrorHandler
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.functions.Action1
import rx.subscriptions.CompositeSubscription
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.util.concurrent.TimeUnit

@JvmField
var coordList = ArrayList<LatLng>()

@JvmField
var childLocationList = ArrayList<ChildlocationItem>()


@JvmField
var originLat = 22.7533
@JvmField
var originlong = 75.8937

//@JvmField
//var destinationLat = 22.7244
//@JvmField
//var destinationlong = 75.8839

@JvmField
var destinationLat = 0.1
@JvmField
var destinationlong = 0.1



@JvmField
var location_id = 0

@JvmField
var location_name = ""




  class ChildListFragment : Fragment(), View.OnClickListener , OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks,
          GoogleApiClient.OnConnectionFailedListener{

    lateinit var viewLay: View
    var formattedDateT = ""
      var IMAGE_CHILD = ""

    private lateinit var mMap: GoogleMap

      var initial_latitude  = 36.778259
      var initial_longitude = -119.417931
      var initial_marker    = "Parent"
      lateinit var fusedLocationClient: FusedLocationProviderClient
      var currentDrawingType: DrawingOption.DrawingType? = null
      val REQUEST_CODE = 1
      internal var latlongslistStr: String = ""
      internal var latlongslistStrFirst: String = ""
      internal var GeoFancingSize = 0
      private var polygon: Polygon? = null
      private val markerList = ArrayList<Marker>()
      private var mGoogleApiClient: GoogleApiClient? = null
      private var locationUpdatesObservable: Observable<Location>? = null
      private var compositeSubscription: CompositeSubscription? = null
      private var locationProvider: ReactiveLocationProvider? = null
      private var lastKnownLocationObservable: Observable<Location>? = null
      var childLocation = LatLng(initial_latitude, initial_longitude);
      var geobool = false
//      var location_id = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewLay = inflater.inflate(R.layout.fragment_childlist, container, false)
        showUI(viewLay)

//        if(Constant.CHILD_ID != 0) {
//            setGeoFencingAPI(false)//1
//        }
        initRequestingLocation()
        requestActivatingGPS()
        setOnclick()
        setCurrentDate()
        return viewLay
    }




    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map_fragment) as SupportMapFragment?
        if (mapFragment != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location!=null) {
                            initial_latitude = location!!.latitude
                            initial_longitude = location!!.longitude
                        }
                        mapFragment.getMapAsync(this)
                        // Got last known location. In some rare situations this can be null.
                    }

        }
    }


    fun showUI( viewLay: View){
        System.out.println("Child No : "+PreferenceConnector.readInteger(this.activity!!, PreferenceConnector.NO_OF_CHILD,0))
        if(PreferenceConnector.readInteger(this.activity!!, PreferenceConnector.NO_OF_CHILD,0)>0){
            viewLay.lay_value.visibility = View.VISIBLE
            viewLay.lay_empty.visibility = View.GONE
        }else{
            viewLay.lay_value.visibility = View.GONE
            viewLay.lay_empty.visibility = View.VISIBLE
        }

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        setChildLatLongAPI()

    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setOnclick() {
        viewLay.lay_child_profile.setOnClickListener(this)
        viewLay.iv_bubble.setOnClickListener(this)
        viewLay.lay_editgeofancing.setOnClickListener(this)
    }


    override fun onMapReady(map: GoogleMap?) {
   //     System.err.println("OnMapReady start")
        mMap = map as GoogleMap;

//        val sydney = LatLng(initial_latitude, initial_longitude);
//        val sydney = LatLng(initial_latitude, initial_longitude);
//        mMap.addMarker(MarkerOptions().position(sydney).title(initial_marker));

       if(geobool) {
           geobool =false
           try {

//                            val url = URL(ApiClient.IMAGE_URL +IMAGE_CHILD)
//
//                            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
               mMap.addMarker(MarkerOptions().position(childLocation).title("Your Child is here"))
//                            mMap.addMarker(MarkerOptions().position(sydney).title("Your Child is here").
//                                    icon(BitmapDescriptorFactory.fromBitmap(bmp)))
               mMap.moveCamera(CameraUpdateFactory.newLatLng(childLocation))
           } catch (exe: Exception) {
               System.out.println("Exception = " + exe)
           }
       }
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(childLocation,15f)))

        if (coordList.size > 0 )  {
                drawPolygon(coordList)
//                }
                if (destinationLat != 0.1 && destinationlong != 0.1) {
                    showRoute()
                }
        }
//        Toast.makeText(this.context, "OnMapReady end", Toast.LENGTH_LONG).show()
    }

      private fun drawPolygon(latLngList: List<LatLng>) {
          if (polygon != null) {
              polygon!!.remove()
          }
          val polygonOptions = PolygonOptions()
          polygonOptions.fillColor(resources.getColor(R.color.color_input))
          polygonOptions.strokeColor(10)
          polygonOptions.strokeWidth(10f)
          polygonOptions.addAll(latLngList)
          polygon = mMap.addPolygon(polygonOptions)
      }

      private fun getBitmapFromDrawable(context: Activity, icon: Int): Bitmap {
          val drawable = ContextCompat.getDrawable(context, icon)
          val obm = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
          val canvas = Canvas(obm)
          drawable.setBounds(0, 0, canvas.width, canvas.height)
          drawable.draw(canvas)
          return obm
      }


      internal fun showRoute() {

          val now = DateTime()
          var result: DirectionsResult? = null
          try {
              result = DirectionsApi.newRequest(getGeoContext()).mode(TravelMode.DRIVING).origin("$originLat,$originlong").destination("$destinationLat,$destinationlong").departureTime(now).await()
          } catch (e: ApiException) {
              e.printStackTrace()
          } catch (e: InterruptedException) {
              e.printStackTrace()
          } catch (e: IOException) {
              e.printStackTrace()
          }

          try {
              if (result != null) {
                  addMarkersToMap(result, mMap)
                  addPolyline(result, mMap)
              }
          } catch (exe: Exception) {
              Toast.makeText(activity, "Please update address in child profile", Toast.LENGTH_SHORT).show()
          }


          //        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
      }

      private fun getGeoContext(): GeoApiContext {
          val geoApiContext = GeoApiContext()
          return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key)).setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS)
      }

      private fun getEndLocationTitle(results: DirectionsResult): String {
          return "Time :" + results.routes[0].legs[0].duration.humanReadable +
                  " Distance :" + results.routes[0].legs[0].distance.humanReadable
      }

      private fun addPolyline(results: DirectionsResult, mMap: GoogleMap) {
          val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
          mMap.addPolyline(PolylineOptions().addAll(decodedPath))
      }

      private fun addMarkersToMap(results: DirectionsResult, mMap: GoogleMap) {
          @SuppressLint("ResourceType") @IdRes val iconHome = R.drawable.ic_home_black_24dp
          val bitmapHome = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(activity!!, iconHome))
          mMap.addMarker(MarkerOptions().position(LatLng(results.routes[0].legs[0].startLocation.lat,
                  results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress)).setIcon(bitmapHome)
          mMap.addMarker(MarkerOptions().position(LatLng(results.routes[0].legs[0].endLocation.lat,
                  results.routes[0].legs[0].endLocation.lng))
                  .title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)))
      }

      private fun initRequestingLocation() {
          mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                  .addApi(LocationServices.API)
                  .addConnectionCallbacks(this)
                  .addOnConnectionFailedListener(this).build()
          mGoogleApiClient!!.connect()
          compositeSubscription = CompositeSubscription()
          locationProvider = ReactiveLocationProvider(activity)
          lastKnownLocationObservable = locationProvider!!.getLastKnownLocation()
      }

      override fun onConnectionFailed(p0: ConnectionResult) {

      }

      override fun onConnected(p0: Bundle?) {

      }

      override fun onConnectionSuspended(p0: Int) {

      }

      private fun requestActivatingGPS() {
          val locationRequest = LocationRequest.create()
                  .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                  .setNumUpdates(5)
                  .setInterval(100)
          locationUpdatesObservable = locationProvider!!.getUpdatedLocation(locationRequest)
          val builder = LocationSettingsRequest.Builder()
                  .addLocationRequest(locationRequest).setAlwaysShow(true)
          val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
          result.setResultCallback { locationSettingsResult ->
              val status = locationSettingsResult.status
              when (status.statusCode) {
//                  LocationSettingsStatusCodes.SUCCESS -> getLastKnowLocation()
                  LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                      // Show the dialog by calling startResolutionForResult(),
                      // and check the result in onActivityResult().
                      status.startResolutionForResult(activity,
                              REQUEST_CHECK_SETTINGS)
                  } catch (e: IntentSender.SendIntentException) {
                      e.printStackTrace()
                  }

                  LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(TAG, "Error happen during show Dialog for Turn of GPS")
              }
          }
      }


      override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.lay_child_profile ->
                if(Constant.CHILD_ID != 0) {
                    viewChildProfileIntent()
                }else{
                    Toast.makeText(activity,"Please select child",Toast.LENGTH_SHORT).show()
                }

            R.id.iv_bubble -> createIntent()
            R.id.lay_editgeofancing-> {
                if(Constant.CHILD_ID != 0) {
//                    setGeoFencingAPI(true)//1
                    polygonIntent()//2
                }else{
                    Toast.makeText(activity,"Please select child",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun polygonIntent() {
        currentDrawingType = DrawingOption.DrawingType.POLYGON
        val intent = DrawingOptionBuilder()
                .withLocation(originLat, originlong)
                .withFillColor(Color.argb(60, 0, 0, 255))
                .withStrokeColor(Color.argb(100, 255, 0, 0))
                .withStrokeWidth(3)
                .withRequestGPSEnabling(false)
                .withDrawingType(currentDrawingType)
                .build(activity)
        startActivityForResult(intent, REQUEST_CODE)
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
//            val dataModel = data.extras!!.getParcelable(POINTS)
            val dataModel: DataModel = data.extras!!.getParcelable(POINTS)
            var viewOption: ViewOption? = null
            if (currentDrawingType === DrawingOption.DrawingType.POLYGON) {
                coordList.clear()
                coordList.addAll(dataModel!!.points)
                getlatlong()
                viewOption = ViewOptionBuilder()
                        .withIsListView(false)
                        .withPolygons(
                                ExtraPolygonBuilder()
                                        .setFillColor(Color.argb(100, 0, 0, 255))
                                        .setPoints(dataModel!!.points)
                                        .setStrokeColor(Color.argb(100, 255, 0, 0))
                                        .setStrokeWidth(5)
                                        .build())
                        .build()
            }
            mMap.clear()
            MapUtils.showElements(viewOption, mMap, activity)

        }
    }

    private fun viewChildProfileIntent() {
        startActivity(Intent(activity, ChildProfileActivity::class.java))
    }

    private fun createIntent() {
        startActivity(Intent(activity, CreateChildActivity::class.java))
    }

    private fun setCurrentDate(){
        var c = Calendar.getInstance().getTime()
        println("Current time => $c")

//        var df = SimpleDateFormat("dd-MM-yyyy")
        var df = SimpleDateFormat("yyyy-MM-dd")
        var formattedDate = df.format(c).toString()
                formattedDateT =formattedDate
    }

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

          createGeoFencingAPI()
      }

    private fun setChildLatLongAPI() {
//        Utils.showProgressDialog(activity!!)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
//        System.out.println("User ID = "+ PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val call = apiInterface.getchildCurrentLatLong(
               ""+ Constant.CHILD_ID)
        call.enqueue(object : Callback<GetChildLatLongResponse> {
            override fun onResponse(call: Call<GetChildLatLongResponse>, response: Response<GetChildLatLongResponse>) {
                if (response.body()!!.status == 200) {
//                    viewLay.no_record_fragment.visibility = View.GONE
//                    viewLay.lay_value.visibility = View.VISIBLE
                    if(response.body()!!.data!!!=null) {
                        childLocation= LatLng(response.body()!!.data!!.lat!!.toDouble(),
                                response.body()!!.data!!.jsonMemberLong!!.toDouble())
                        IMAGE_CHILD = response.body()!!.data!!.image.toString();
                       try {
//                            val url = URL(ApiClient.IMAGE_URL +response.body()!!.data!!.image)

//                            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            mMap.addMarker(MarkerOptions().position(childLocation).title("Your Child is here"))
//                            mMap.addMarker(MarkerOptions().position(childLocation).title("Your Child is here").
//                                    icon(BitmapDescriptorFactory.fromBitmap(bmp)))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(childLocation))
                        }catch (exe:Exception){
                           System.out.println("Exception = "+exe)
                           geobool =true
                        }
                    }
//                    Utils.hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<GetChildLatLongResponse>, t: Throwable) {
                 Toast.makeText(activity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
            }
        })

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

      //API Call

      private fun createGeoFencingAPI() {

          Utils.showProgressDialog(activity!!)
          val apiInterface = ApiClient.client.create(ApiInterface::class.java)
          val call = apiInterface.insertGeoLocation(PreferenceConnector.readInteger(activity!!,
                  PreferenceConnector.USER_ID,0).toString(),Constant.CHILD_ID.toString(),latlongslistStr,
                  coordList.toString(),"","","","",
                  initial_latitude.toString(),initial_longitude.toString(),
                  location_id.toString(),location_name,originLatlong, destinationLatlong,desti_address)
                  call.enqueue(object : Callback<InsertPolygonResponse> {
              override fun onResponse(call: Call<InsertPolygonResponse>, response: Response<InsertPolygonResponse>) {
                  if (response.body()!!.status == 200) {
                      setGeoFencingAPI()
                    Toast.makeText(activity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                  } else {
                      Toast.makeText(activity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                  }
                  Utils.hideProgressDialog()
              }

              override fun onFailure(call: Call<InsertPolygonResponse>, t: Throwable) {
                  //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                  if (Utils.isNetworkAvailable(activity!!)) {
                      Toast.makeText(activity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                  } else {
                      Toast.makeText(activity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                  }
                  //                                        myLoading.dismiss();
                  Utils.hideProgressDialog()
              }
          })

      }

      private fun setGeoFencingAPI() {

          val apiInterface = ApiClient.client.create(ApiInterface::class.java)
          val call = apiInterface.getGeoLocation(PreferenceConnector.readInteger(activity!!,
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

              }

              override fun onFailure(call: Call<PolylineResponse>, t: Throwable) {
                  //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                  if (Utils.isNetworkAvailable(activity!!)) {
                      Toast.makeText(activity, Constant.SERVER_ERROR,
                              Toast.LENGTH_SHORT).show()
                  } else {
                      Toast.makeText(activity, Constant.INTERNET_ERROR,
                              Toast.LENGTH_SHORT).show()
                  }
                  //                                        myLoading.dismiss();
//                Utils.hideProgressDialog()
              }
          })

      }


  }

