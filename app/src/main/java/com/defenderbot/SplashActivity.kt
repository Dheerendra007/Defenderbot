package com.defenderbot

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.defenderbot.activity.LoginActivity
import com.defenderbot.activity.MainActivity
import com.defenderbot.activity.childmodule.StartChildTrackActivity
import com.defenderbot.util.MarshMallowPermission
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity :  AppCompatActivity() {

    private val DELAY = 3000
    internal var timer: Timer? = null
    internal lateinit var myTimerTask: MyTimerTask
    internal lateinit var intent: Intent
    //    TextView appversion;
    internal var versionCode = 1
    internal lateinit var dialog: Dialog

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tv_version.setText("Version : "+appVersion())
        //        appversion = (TextView)findViewById(R.id.appversion);
        //        appversion.setText("Version : "+appVersion());
        //        checkAppUpdate();
        dialog = Dialog(this)
        //        new GooglePlayStoreAppVersionNameLoader().execute();
        timer = Timer()
        myTimerTask = MyTimerTask()
        timer!!.schedule(myTimerTask, DELAY.toLong())
    }


    internal inner class MyTimerTask : TimerTask() {

        override fun run() {
            runOnUiThread {
//                polygonIntent()
                if(PreferenceConnector.readInteger(this@SplashActivity, PreferenceConnector.USER_ID,
                                0)!=0 &&
                        PreferenceConnector.readString(this@SplashActivity,
                                PreferenceConnector.USER_TYPE,"").equals("parent") ){
                    callhomescreen()
                }else if(PreferenceConnector.readInteger(this@SplashActivity, PreferenceConnector.USER_ID,0)!=0 &&
                        PreferenceConnector.readString(this@SplashActivity,
                                PreferenceConnector.USER_TYPE,"").equals("child") ) {
                    callChildhomescreen()
                }
                else{
                    callloginscreen()
                }
                if (timer != null) {
                    timer!!.cancel()
                    timer = null
                }
            }
        }
    }

    private fun callloginscreen() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }
    private fun callhomescreen() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun callChildhomescreen() {
        intent = Intent(this, StartChildTrackActivity::class.java)
        startActivity(intent)
        this.finish()
    }

//    private fun polygonIntent() {
//        startActivity(Intent(this, PolygonActivity::class.java))
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun appVersion(): String {
        val manager = packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

//        val marshMallowPermission = MarshMallowPermission(this)
//        if (!marshMallowPermission.checkPermissionForStorage())
//            marshMallowPermission.requestPermissionForStorage()
        return info!!.versionName
    }

//    private void callHomecreen()
//    {
//        Intent homeintent = new Intent(this,MainActivity.class);
////        homeintent.putExtra("logintype","user");
//        startActivity(homeintent);
//        finish();
//    }

}