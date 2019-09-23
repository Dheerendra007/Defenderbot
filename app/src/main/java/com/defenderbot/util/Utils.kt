package com.defenderbot.util

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import com.defenderbot.R
import com.defenderbot.activity.LoginActivity

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

//import com.google.i18n.phonenumbers.PhoneNumberUtil;
//import com.google.i18n.phonenumbers.Phonenumber;

object Utils {

    var appContext:Context? = null
    var PREFERENCE = "competishun"
    lateinit var snackbar: Snackbar
    var c = charArrayOf('K', 'M', 'B', 'T')
    var dialogConform:AlertDialog? = null
    var pDialog:ProgressDialog? = null

    fun isNetworkAvailable(context:Context):Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo!!.isConnected
    }


    fun ShowSnackbarOther(context:Context, v:View, msg:String) {

        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, View.OnClickListener { snackbar.dismiss() })
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

        val snackbarView = snackbar.view
        val snackbarTextId = R.id.snackbar_text
        val textView = snackbarView.findViewById<TextView>(snackbarTextId)
        textView.maxLines = 5

        snackbar.show()

    }

    //    public static void ShowSnackbarRelogin(final Context context, View v, String msg) {
    //
    //        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_INDEFINITE)
    //                .setAction(R.string.ok, new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View view) {
    //                        Utils.LogOut(context);
    //                    }
    //                });
    //        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorLightPrimary));
    //
    //        View snackbarView = snackbar.getView();
    //        int snackbarTextId = android.support.design.R.id.snackbar_text;
    //        TextView textView = snackbarView.findViewById(snackbarTextId);
    //        textView.setMaxLines(5);
    //
    //        snackbar.show();
    //
    //    }

    fun LogOut(context:Context) {

        try
        {

            val nMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nMgr!!.cancelAll()

            val prefs = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.remove(PREFERENCE)
            editor.clear()
            editor.apply()
            context.getSharedPreferences(PREFERENCE, 0).edit().clear().apply()

            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }
        catch (e:Exception) {
            e.printStackTrace()
        }

    }

    /*    public static boolean checkNumber(String number, String alpha) {

           PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

           try {
               Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(number, alpha);

               return phoneUtil.isValidNumber(swissNumberProto);
           } catch (Exception e) {
               e.printStackTrace();
               return false;
           }
       }*/

    //    public static void checkIfPermissionsGranted(final Context appContext) {
    //        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
    //        alertDialogBuilder.setMessage(appContext.getString(R.string.permission));
    //        alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface arg0, int arg1) {
    //                goToSettings(appContext);
    //            }
    //        });
    //
    //        alertDialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //
    //            }
    //        });
    //        dialogConform = alertDialogBuilder.create();
    //        dialogConform.show();
    //        dialogConform.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(appContext.getResources().getColor(R.color.colorAccent));
    //        dialogConform.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(appContext.getResources().getColor(R.color.colorAccent));
    //    }

    private fun goToSettings(appContext:Context) {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + appContext.packageName))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        appContext.startActivity(myAppSettings)
    }

    fun coolFormat(n:Double, iteration:Int):String {
        val d = n.toLong() / 100 / 10.0
        val isRound = d * 10 % 10 == 0.0//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (if (d < 1000)
        //this determines the class, i.e. 'k', 'm' etc
            ((if (d > 99.9 || isRound || (!isRound && d > 9.99))
            //this decides whether to trim the decimals
                d.toInt() * 10 / 10
            else
                (d).toString() + "" // (int) d * 10 / 10 drops the decimal
                    )).toString() + "" + c[iteration]
        else
            coolFormat(d, iteration + 1))

    }

    fun shareApp(context:Context, text:String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        i.putExtra(Intent.EXTRA_TEXT, text + "\nhttps://play.google.com/store/apps/details?id=" + context.packageName)
        context.startActivity(Intent.createChooser(i, "Share via:"))

    }

    fun rateusApp(context:Context) {
        val url = "https://play.google.com/store/apps/details?id=" + context.packageName
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    fun compressBitmap(context:Context, URI:Uri):File? {

        var imageFile:File? = null
        try
        {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, URI)

            val datasecond = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, datasecond)

            val bitmapdata = datasecond.toByteArray()

            // write the bytes in file
            imageFile = File((Environment.getExternalStorageDirectory()).toString() + File.separator + "foodtriangle_profile.jpeg")

            /* if (imageFile.exists())
                           imageFile.delete();

                       imageFile.createNewFile();*/

            val fo = FileOutputStream(imageFile!!)
            fo.write(bitmapdata)
            fo.close()

            return imageFile

        }
        catch (e:Exception) {
            e.printStackTrace()
        }

        return imageFile
    }

    fun emailValidator(email:String):Boolean {
        val pattern:Pattern
        val matcher:Matcher
        val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    //    public static void getDeviceID(Context context) {
    //
    //        Utils.setSharedPreference(context, Constant.USER_DEVICEID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    //    }

    fun showProgressDialog(mContext:Context) {
        pDialog = null
        pDialog = ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog)
        pDialog!!.setMessage("Loading...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    fun hideProgressDialog() {
        if (pDialog!!.isShowing)
            pDialog!!.hide()
        pDialog!!.dismiss()
    }

    fun checkEmail(email:String):Boolean {
        val expression = ("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*" + "+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
        val emailPattern = Pattern.compile(expression)
        return emailPattern.matcher(email).matches()
    }

    //    public static boolean isNetworkAvailable(Context context) {
    //        // TODO Auto-generated method stub
    //        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //        assert connMgr != null;
    //        return connMgr.getActiveNetworkInfo() != null
    //                && connMgr.getActiveNetworkInfo().isAvailable()
    //                && connMgr.getActiveNetworkInfo().isConnected();
    //    }
}// final class ends here

