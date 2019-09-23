package com.defenderbot.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.activity.childmodule.StartChildTrackActivity
import com.defenderbot.model.childlist.ChildListResponse
import com.defenderbot.model.login.Data
import com.defenderbot.model.login.LoginResponse
import kotlinx.android.synthetic.main.activity_login.*
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.oceansapparel.util.PreferenceConnector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(),View.OnClickListener{

    internal var permissionsRequired = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    private val PERMISSION_CALLBACK_CONSTANT = 100
    private val REQUEST_PERMISSION_SETTING = 101
    private var sentToSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setOnclick()
        accessPermission()
    }

    private fun setOnclick() {
        btn_signin!!.setOnClickListener(this@LoginActivity)
        btn_signup!!.setOnClickListener(this@LoginActivity)
        btn_forgot!!.setOnClickListener(this@LoginActivity)
    }

    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_signin -> if(validate()) {
                attemptLoginAPI()
            }
//             R.id.btn_signin -> mainIntent()
            R.id.btn_signup -> signupIntent()
            R.id.btn_forgot->  forgotPassIntent()
        }
    }

    private fun signupIntent() {
        startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
    }

    private fun forgotPassIntent() {
        startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
    }

    private fun mainIntent() {
        Utils.hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
    }


    private fun startTrackingIntent() {
        startActivity(Intent(this, StartChildTrackActivity::class.java))
    }


    private fun validate(): Boolean {
        var valid = true

        val username = et_username.text.toString()
        val password = et_password.text.toString()

        if (username.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.usernameid_blank))
            valid = false
        }
        /*else if (!Constant.checkEmail(email)) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.email_validate))
            valid = false
        } */
        else if (password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.password_blank))
            valid = false
        } else if (password.length < 4) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.password_valid))

            valid = false
        }

        return valid
    }

    private fun setUiForgot(show: Boolean){
        if(show) {
            lay_roundrect.setBackgroundResource(R.drawable.roundrectred_input)
            btn_forgot.visibility = View.VISIBLE
        }else{
            lay_roundrect.setBackgroundResource(R.drawable.roundrect_input)
            btn_forgot.visibility = View.GONE
        }
    }

    fun setValueDB(data: Data) {
        PreferenceConnector.writeInteger(this, PreferenceConnector.USER_ID,data.userId!!)
        PreferenceConnector.writeInteger(this, PreferenceConnector.PARENT_ID,data.parentId!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_FNAME,data.username!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_NAME,data.username!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_EMAIL,data.emailId!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_TYPE,data.userType!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_PROFILE_PIC,data.image!!)
//        mainIntent()
        if(data.userType.equals(Constant.PARANT,true)){
            getChildAPI()
//            mainIntent()
        }else{
            startTrackingIntent()
        }

    }

    // LOGIN API
    private fun attemptLoginAPI() {

        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.loginUser(et_username.text.toString(), et_password.text.toString())
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.body()!!.status == 200) {
                    setUiForgot(false)
                    setValueDB(response.body()!!.data!!)
                    finish()
                }else  if (response.body()!!.status == 203) {
                    setUiForgot(true)
//                    Toast.makeText(this@LoginActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                } else {
                    setUiForgot(false)
                    Toast.makeText(this@LoginActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@LoginActivity)) {
                    Toast.makeText(this@LoginActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })

    }

    private fun createIntent() {
        Utils.hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun getChildAPI() {
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        System.out.println("User ID = "+PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        val call = apiInterface.childList(PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        call.enqueue(object : Callback<ChildListResponse> {
            override fun onResponse(call: Call<ChildListResponse>, response: Response<ChildListResponse>) {
                if (response.body()!!.status == 200) {
                    PreferenceConnector.writeInteger(this@LoginActivity, PreferenceConnector.NO_OF_CHILD, response.body()!!.data!!.size)
//                    setChildList((response.body()!!.data as ArrayList<DataItem>?)!!)
                    if( PreferenceConnector.readInteger(this@LoginActivity, PreferenceConnector.NO_OF_CHILD,0)==0){
                        createIntent()
                    }else{
                        mainIntent()
                    }
                } else {
                    PreferenceConnector.writeInteger(this@LoginActivity, PreferenceConnector.NO_OF_CHILD, 0)
//                    Toast.makeText(this@LoginActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    createIntent()
                }

                //                                        myLoading.dismiss();\

            }

            override fun onFailure(call: Call<ChildListResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@LoginActivity)) {
                    Toast.makeText(this@LoginActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })

    }

    /*-------------------------------------------Access Marshmello Permission---------------------------------------*/

    private fun accessPermission() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])) {
                //Show Information about why you need the permission
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs all permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(this@LoginActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else if (
                    PreferenceConnector.readBoolean(this@LoginActivity, PreferenceConnector.PERMISSION,
                            false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs all permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    //                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant", Toast.LENGTH_LONG).show();
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
            }
            PreferenceConnector.writeBoolean(this@LoginActivity,PreferenceConnector.PERMISSION, true)
//            SharedPreferencesDB.writeBoolean(SharedPreferencesDB.PERMISSION, true)
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission()
            }
        }
    }

    private fun proceedAfterPermission() {
        //        callHomecreen();
        //        startService(new Intent(getApplicationContext(), PowerButtonService.class));
        //        txtPermissions.setText("We've got all permissions");
        //                setUpPane();
        //        Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }

    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission()
            }
        }
    }


}
