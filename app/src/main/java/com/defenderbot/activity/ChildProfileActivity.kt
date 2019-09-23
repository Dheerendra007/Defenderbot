package com.defenderbot.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import kotlinx.android.synthetic.main.activity_child_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Build
import android.widget.TextView
import com.defenderbot.model.profile.Data
import com.defenderbot.model.profile.ProfileResponse

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_child_profile.iv_back
import kotlinx.android.synthetic.main.activity_edit_child_profile.*

class ChildProfileActivity : AppCompatActivity() ,View.OnClickListener{

    val MY_PERMISSION_LOCATION =1
    var toolbar_title: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_profile)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar_title = toolbar.findViewById(R.id.toolbar_title) as TextView
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setOnclick()
        marshmallowGPSPremissionCheck()
        getProfileAPI()
    }

    private fun setOnclick() {
        iv_edit!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        lay_creategeofencing!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.iv_edit -> editChildIntent()
            R.id.iv_back -> finish()
//            R.id.lay_creategeofencing -> polygonIntent()

        }
    }

    override fun onResume() {
        super.onResume()
        if (Constant.CHILD_DELETE){
            finish()
        }
    }

    private fun editChildIntent() {
        startActivity(Intent(this, EditChildProfileActivity::class.java))
    }

//    private fun polygonIntent() {
//        startActivity(Intent(this, PolygonActivity::class.java))
//    }

    private fun marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_LOCATION)
        } else {
            //   gps functions.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }
    }

    // GET PROFILE API
    private fun getProfileAPI() {

        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getProfile(Constant.CHILD_ID.toString())
        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>,
                                    response: Response<ProfileResponse>) {
                if (response.body()!!.status == 200) {
                    setValueDB(response.body()!!.data!!)
                } else {
                    Toast.makeText(this@ChildProfileActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@ChildProfileActivity)) {
                    Toast.makeText(this@ChildProfileActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChildProfileActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })

    }

    fun setValueDB(data: Data) {
        toolbar_title!!.setText(data.username)
        Picasso.get().load(ApiClient.IMAGE_URL + data.image).into(iv_profile)
        tv_cphone.setText(data.contactNumber)
        tv_cdob.setText(data.dOB)
        Constant.DOB = data.dOB.toString()
        tv_cbloodtype.setText(data.bloodType)
        if(data.emergencyContact!!.size>0) {
            tv_cemergencyphonno.setText(data.emergencyContact!![0]!!.number)

        }
//        tv_cemergencyphonno.setText(data.emergencyContact!![0]!!.number) // missing in API
        tv_caddress.setText(data.address)
        tv_cmedicalcond.setText(data.medicalCondition) // missing in API
        tv_battery.setText(data.currentStatus!!.batteryPercent.toString())
        tv_trackingtatus.setText(data.currentStatus!!.trackingStatus)
        tv_mile.setText(data.currentStatus!!.distanceFrom)
        tv_cstatustimeago.setText(data.currentStatus!!.lastOnlineStatus)

    }

}
