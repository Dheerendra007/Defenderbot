package com.defenderbot.util.duonavigationdrawer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.ParseException
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@JvmField
var originLatlong = ""

@JvmField
var destinationLatlong = ""

@JvmField
var desti_address = ""

@SuppressLint("StaticFieldLeak")
object Constant {

    var appContext: Context? = null
    var _docpicurl: String? = null
    var URL_ABOUTUS = "http://quickbots.co.in/writeups/#/about-us"
    var URL_CONTACTUS = "http://quickbots.co.in/writeups/#/contact-us"
    //
    var uploadActivity: Activity? = null
    var SIGNIN_TYPE = "NATIVE"
    var DEVICE_TOKEN = "XYZ"
    var PLATFORM_TYPE = "ANDROID"
    var ROLE_CLIENT = "CLIENT"
    var SCREEN = ""
    var USER_EMAIL_FORGOT = ""
    var CANCEL_BOOL = false
    var POSITION_MENU = 0
    var INTERNET_ERROR = "Please check your internet connection"
    var SERVER_ERROR = "Server not working"
    var PARANT = "parent"

    var DOB = ""
    var DOBPARENT = ""

    var USER_ID_PASS_CHANGE = 0
    var CHILD_POSITION = 0
    var CHILD_ID = 0

    var ISGEO = true//Inset shift element
    var LATITUDE = ""//Inset shift element
    var LONGITUDE = ""//Inset shift element
    var LATLONG = false//Inset shift element
    //    public static boolean BREAK_STATUS = false;//Inset shift element
    var SHIFT_STATUS_STR = "start"//Inset shift element
    var SHIFT_BREAK_STR = "break"
    var GEOSTARTED = false//Inset shift element
    var MATCHGEO = true//Inset shift element\
    var CHILD_DELETE = false//Inset shift element
    var CHILD_SAVE = false//Inset shift element



    var _pingsUsersList = ArrayList<Any>()

    var _notificationCount: TextView? = null

    var _currentActivityContext: Context? = null

    val context: Context?
        get() = appContext


    /*	public static String getDateFormat(Date date){
		String newDate="";
		String  newFormat= "dd-MM-yyyy";
		SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
		newDate=String.valueOf(sdf2.format(date));
		return newDate;
	}*/

    // using Calendar class
    /*Calendar ci = Calendar.getInstance();

		String CiDateTime = "" + ci.get(Calendar.YEAR) + "-" +
		    (ci.get(Calendar.MONTH) + 1) + "-" +
		    ci.get(Calendar.DAY_OF_MONTH) + " " +
		    ci.get(Calendar.HOUR) + ":" +
		    ci.get(Calendar.MINUTE) +  ":" +
		    ci.get(Calendar.SECOND);*///SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //String currentDateandTime = sdf.format(new Date());
    // using SimpleDateFormat class
    val currentDeviceTime: String
        get() {
            val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val newtime = sdfDateTime.format(Date(System.currentTimeMillis()))
            println(newtime)
            return newtime
        }

    /**
     * @return
     */
    val osVersion: String
        get() {
            val myVersion = Build.VERSION.RELEASE
            println("VERSION$myVersion")
            return myVersion
        }

    /**
     * @return
     */
    val model: String
        get() {
            var model = Build.MODEL
            model = model.replace(" ", "_")
            println("MODEL$model")
            return model
        }


    var DEVICE_TYPE = "android"
    var REFER_CODE = "admin123"
    val NETWORK_NOT_PRESENT = "Your network connection is too slow or may not be working"


    fun convertPassMd5(pass: String): String? {
        var pass = pass
        var password: String? = null
        val mdEnc: MessageDigest
        try {
            mdEnc = MessageDigest.getInstance("MD5")
            mdEnc.update(pass.toByteArray(), 0, pass.length)
            pass = BigInteger(1, mdEnc.digest()).toString(16)
            while (pass.length < 32) {
                pass = "0$pass"
            }
            password = pass
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }

        return password
    }

    fun showAlertMessage(message: String, activity: Activity, flag: Boolean) {
        val alertbox = AlertDialog.Builder(activity)
        alertbox.setMessage(message)
        alertbox.setNeutralButton("Dismiss") { arg0, arg1 ->
            if (flag) {
                activity.finish()
            } else {
                arg0.cancel()
            }
        }
        alertbox.show()
    }

    fun showToast(activity: Context?, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }


    fun convertBitmapToBase64(image: Bitmap): String? {
        var base64Image: String? = null
        val resizedBitmap = getResizedBitmap(image, 200)
        try {
            //	Bitmap immagex=resizedBitmap;
            val baos = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            val i = b.size
            if (i > 2097152) {
                Constant.showToast(uploadActivity, "Image size is too large.")
            } else {
                base64Image = Base64.encodeToString(b, Base64.DEFAULT)
            }

        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return base64Image
    }


    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    fun convertUrlToBase64(src: String) {
        Thread(Runnable {
            try {
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bm = BitmapFactory.decodeStream(input)
                Constant._docpicurl = convertBitmapToBase64(bm)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    fun toBase64fromString(text: String): String {
        return Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)
    }

    /**
     * createFileInSDCard
     *
     * @param path
     * @param fileName
     * @return
     */
    fun createFileInSDCard(path: String, fileName: String): File? {
        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val dir = File(extStorageDirectory + path)
        try {
            if (!dir.exists() && dir.mkdirs()) {
                println("Directory created")
            } else {
                println("Directory is not created")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var file: File? = null
        try {
            if (dir.exists()) {
                file = File(dir, fileName)
                file.createNewFile()
            } else {
                runOnUiThread(Runnable { })
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
            runOnUiThread(Runnable { })
        }

        return file
    }

    private fun runOnUiThread(runnable: Runnable) {

    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun dateFormat(Date: String, read: String, write: String): String {
        val readFormat = SimpleDateFormat(read)

        val writeFormat = SimpleDateFormat(write)
        var date: java.util.Date? = null
        try {
            try {
                date = readFormat.parse(Date)
            } catch (e: java.text.ParseException) {

                e.printStackTrace()
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var formattedDate = ""
        if (date != null) {
            formattedDate = writeFormat.format(date)
        }

        println(formattedDate)

        return formattedDate
    }

    /**
     * getDeviceID
     *
     * @param appContext
     * @return
     */
//    fun getDeviceID(appContext: Context): String {
//        val phoneManager = /* getApplicationContext() */
//                appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
//        return phoneManager.deviceId
//    }


    fun showGroupChatInvitationAlert(title: String, message: String,
                                     roomname: String, conn: Connection, inviter: String) {


        //	connect(user,password);

        Thread(Runnable {
            //				SplashActivity.joinMultiUserChat(Constant.userName, Constant.password, roomname.split("@")[0]);
        }).start()
    }


    fun currentDate(): String {
        /*Calendar calendar = Calendar.getInstance();
		long time = calendar.getTimeInMillis();*/
        //return calendar.getTime();
        /*Calendar cal = Calendar.getInstance();
		cal.getTime().toString();

		return cal.getTime().toString();*/

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault())
        val currentLocalTime = calendar.time
        val date = SimpleDateFormat("Z")
        val localTime = date.format(currentLocalTime)
        return localTime.substring(0, 3) + ":" + localTime.substring(3, 5)
    }

    fun dateFormat(Date: String): String {
        val readFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        val writeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: java.util.Date? = null
        try {
            try {
                date = readFormat.parse(Date.split("\\+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            } catch (e: java.text.ParseException) {

                e.printStackTrace()
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var formattedDate = ""
        if (date != null) {
            formattedDate = writeFormat.format(date)
        }

        println(formattedDate)

        return formattedDate
    }

    fun setDate(): String {

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val calendar = Calendar.getInstance()
        val time = calendar.timeInMillis
        //return calendar.getTime();
        val cal = Calendar.getInstance()
        cal.time.toString()

        val now = Date()

//return cal.getTime().toString();
        return formatter.format(now)


    }

    /*public static String chatDate() {
		SimpleDateFormat readformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

		SimpleDateFormat writeformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



		String actual = writeformatter.format(now);
		//return cal.getTime().toString();
		return actual;


	}*/

    fun dateFormatCompare(date1: String, date2: String): Int {
        //int difference = -1;
        //SimpleDateFormat readDateFormat = new SimpleDateFormat("EEE MMM d ");
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var difference = -1
        var Date1: Date? = null
        var Date2: Date? = null
        try {
            try {
                Date1 = formatter.parse(date1)
                Date2 = formatter.parse(date2)
                val time1 = Date1!!.time
                val time2 = Date2!!.time

                if (time1 > time2) {
                    difference = -1
                } else if (time1 == time2) {
                    difference = 0
                } else if (time1 < time2) {
                    difference = 1
                }

            } catch (e: java.text.ParseException) {

                e.printStackTrace()
            }

        } catch (e: ParseException) {

            e.printStackTrace()
            //   difference = -1 ;
        }

        //difference = Date1.compareTo(Date2);
        return difference
    }

    fun fixOrientationBitmap(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        if (bitmap.width > bitmap.height) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            return bitmap
        }
        return bitmap
    }


    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    fun setNotificationCount() {
        Thread(Runnable {
            try {
                (Constant._currentActivityContext as Activity).runOnUiThread {
                    Constant._notificationCount!!.text = Constant._pingsUsersList.size.toString()
                    Constant._notificationCount!!.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }

    fun unicodeEscaped(ch: Char): String {
        if (ch.toInt() < 0x10) {
            return "\\u000" + Integer.toHexString(ch.toInt())
        } else if (ch.toInt() < 0x100) {
            return "\\u00" + Integer.toHexString(ch.toInt())
        } else if (ch.toInt() < 0x1000) {
            return "\\u0" + Integer.toHexString(ch.toInt())
        }
        return "\\u" + Integer.toHexString(ch.toInt())
    }

    /**
     * Converts the string to the unicode format '\u0020'.
     *
     *
     * This format is the Java source code format.
     *
     *
     * If `null` is passed in, `null` will be returned.
     *
     *
     * <pre>
     * CharUtils.unicodeEscaped(null) = null
     * CharUtils.unicodeEscaped(' ')  = "\u0020"
     * CharUtils.unicodeEscaped('A')  = "\u0041"
    </pre> *
     *
     * @param ch the character to convert, may be null
     * @return the escaped unicode string, null if null input
     */
//    fun unicodeEscaped(ch: Char?): String? {
//        return if (ch == null) {
//            null
//        } else unicodeEscaped(ch.charValue())
//    }


    fun scaleImage(bitmap: Bitmap, requirdwidth: Int, requirdHeight: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        val xScale = requirdwidth.toFloat() / width
        val yScale = requirdHeight.toFloat() / height
        var scale = 0f
        if (xScale <= yScale) {
            scale = if (xScale <= yScale) xScale else yScale
        } else if (xScale >= yScale) {
            scale = if (xScale >= yScale) xScale else yScale
        }
        // Create a matrix for the scaling and add the scaling data
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // Create a new bitmap and convert it to a format understood by the ImageView
        val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        val result = BitmapDrawable(scaledBitmap)
        width = scaledBitmap.width
        height = scaledBitmap.height
        return scaledBitmap
    }

    //    public static boolean isNetworkAvailable(Context _context) {
    //        ConnectivityManager connectivityManager
    //                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    //        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    //    }



    fun getlatLong(_context: Context) {

        //		GPSTracker gps=null;
        //		gps = new GPSTracker(_context);
        //		if (gps.canGetLocation()) {
        //			latitute_user = gps.getLatitude();
        //			longitute_user = gps.getLongitude();
        //		} else {
        //			gps.showSettingsAlert();
        //		}
        //		latLng=new LatLng(latitute_user, longitute_user);
        //		System.out.println("latitude_user is "+latitute_user);
        //		System.out.println("longitude_user is "+longitute_user);
        //latitute_user=Double.parseDouble("");
        //longitute_user=Double.parseDouble("");
    }


    //Hide  Keyboard
    fun hideKeyBoard(context: Activity) {
        val focusedView = context.currentFocus
        //			Toast.makeText(context,"not hide", 1).show();
        if (focusedView != null) {
            //       Toast.makeText(context,"hide", 1).show();
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(context.window.currentFocus!!.windowToken, 0)
        }
    }


    //end changes
    fun hideKeyBord(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun emailValidator(email: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    // returns object for myriad pro regular font
    fun setTypeface1(activity: Context): Typeface {
        return Typeface.createFromAsset(activity.assets, "Roboto-Regular.ttf")
    }

    // setting font for view title with helvetica neue
    fun setViewTitleFont(activity: Context, textTitleBar: TextView) {
        val face = Typeface.createFromAsset(activity.assets, "Roboto-Regular.ttf")
        textTitleBar.typeface = face
    }

    // traversing heirarchy of views and setting fonts to all internal views
    fun overrideFonts(context: Context, v: View) {
        try {
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    val child = v.getChildAt(i)
                    overrideFonts(context, child)
                }
            } else if (v is TextView) {
                v.typeface = Constant.setTypeface1(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    @SuppressLint("SimpleDateFormat")
    fun setgetDate(_date: String?): String {
        if (_date != null && _date != "" && _date != null && _date != "null" && _date != "0000-00-00 00:00:00") {
            val form = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = form.parse(_date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postFormater = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            return postFormater.format(date)
        } else {
            return ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setgetDateAMPM(_date: String?): String {
        if (_date != null && _date != "" && _date != null && _date != "null" && _date != "0000-00-00 00:00:00") {
            val form = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = form.parse(_date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postFormater = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            return postFormater.format(date)
        } else {
            return ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setgetDateAMPMAndMonthInAlphaBets(_date: String?): String {
        if (_date != null && _date != "" && _date != null && _date != "null" && _date != "0000-00-00 00:00:00") {
            val form = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var date: Date? = null
            try {
                date = form.parse(_date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postFormater = SimpleDateFormat("dd MMM yyyy hh:mm a")
            return postFormater.format(date)
        } else {
            return ""
        }
    }

    // checks whether string has null or blank value and returns false for null or blank
    fun isStringExists(str: String?): Boolean {
        var str: String? = (str ?: return false) as? String ?: return false
        if (str.equals("null", ignoreCase = true)) {
            return false
        }
        if (str.equals("<null>", ignoreCase = true)) {
            return false
        }
        if (str.equals("(null)", ignoreCase = true)) {
            return false
        }
        str = str?.trim { it <= ' ' }
        return if (str == "") {
            false
        } else true
    }

    @SuppressLint("SimpleDateFormat")
    fun setgetDateOnly(_date: String?): String {
        if (_date != null && _date != "" && _date != null && _date != "null" && _date != "0000-00-00 00:00:00") {
            val form: SimpleDateFormat
            if (_date.trim { it <= ' ' }.contains(" ")) {
                form = SimpleDateFormat("yyyy-MM-dd")
            } else {
                form = SimpleDateFormat("yyyy-MM-dd")
            }
            //30/04/2015
            var date: Date? = null
            try {
                date = form.parse(_date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postFormater = SimpleDateFormat("dd/MM/yyyy")
            var newDateStr: String? = postFormater.format(date)
            if (newDateStr == null) {
                newDateStr = ""
            }
            return newDateStr
        } else {
            return ""
        }
    }

    fun checkEmail(email: String): Boolean {
        val expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*" + "+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
        val emailPattern = Pattern.compile(expression)
        return emailPattern.matcher(email).matches()
    }

    fun isValidPhoneNumber(mobile: String): Boolean {
        val regEx = "^[0-9]{10}$"
        return mobile.matches(regEx.toRegex())
    }

    fun isNetworkAvailable(context: Context): Boolean {
        // TODO Auto-generated method stub
        val connMgr = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (connMgr.activeNetworkInfo != null
                && connMgr.activeNetworkInfo.isAvailable
                && connMgr.activeNetworkInfo.isConnected)
    }

    fun showAlert(msg: String, context: Context) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle("Alert").setMessage(msg).setPositiveButton("Ok") { dialog, id -> dialog.dismiss() }
        val dialog = builder.create()
        try {
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "d MMM yyyy h:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: java.text.ParseException) {
            e.printStackTrace()
        }

        return str
    }

//    03-jul-2019

    fun parseDateToyyyyMMdd(time: String): String? {
//        val inputPattern = "yyyy-MM-dd HH:mm:ss"
//        val outputPattern = "d MMM yyyy h:mm a"
        var inputPattern ="";
        if(time.indexOf("-") !=-1){
            inputPattern = "d-MMM-yyyy"
        }else{
            inputPattern = "MMM d,yyyy"
        }

        val outputPattern = "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: java.text.ParseException) {
//            e.printStackTrace()
            inputPattern = "dd MMM yyyy"
            val inputFormat2 = SimpleDateFormat(inputPattern)
            try {
                var date2: Date? = null
                date2 = inputFormat2.parse(time)
                str = outputFormat.format(date2)
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }
        }

        return str
    }

}