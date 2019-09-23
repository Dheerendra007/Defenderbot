package com.defenderbot.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.model.signup.SignUpResponse
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.DatePickerFragment
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
//import com.google.maps.model.LatLng
import com.oceansapparel.util.PreferenceConnector
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_child.*
import kotlinx.android.synthetic.main.activity_create_child.btn_save
import kotlinx.android.synthetic.main.activity_create_child.btn_setpassword
import kotlinx.android.synthetic.main.activity_create_child.tv_address
import kotlinx.android.synthetic.main.activity_create_child.et_birthdate
import kotlinx.android.synthetic.main.activity_create_child.et_bloodtype
import kotlinx.android.synthetic.main.activity_create_child.btn_updateimg
import kotlinx.android.synthetic.main.activity_create_child.et_childname
import kotlinx.android.synthetic.main.activity_create_child.tv_city
import kotlinx.android.synthetic.main.activity_create_child.et_conpassword
import kotlinx.android.synthetic.main.activity_create_child.et_username
import kotlinx.android.synthetic.main.activity_create_child.et_emailchild
import kotlinx.android.synthetic.main.activity_create_child.et_emrcontactname
import kotlinx.android.synthetic.main.activity_create_child.et_emremail
import kotlinx.android.synthetic.main.activity_create_child.et_emrphonenopr
import kotlinx.android.synthetic.main.activity_create_child.et_emrcontactname2
import kotlinx.android.synthetic.main.activity_create_child.et_emremail2
import kotlinx.android.synthetic.main.activity_create_child.et_emrphonenopr2
import kotlinx.android.synthetic.main.activity_create_child.et_existingmedical
import kotlinx.android.synthetic.main.activity_create_child.et_password_child
import kotlinx.android.synthetic.main.activity_create_child.et_phoneno
import kotlinx.android.synthetic.main.activity_create_child.tv_state
import kotlinx.android.synthetic.main.activity_create_child.tv_zipcode
import kotlinx.android.synthetic.main.activity_create_child.iv_back
import kotlinx.android.synthetic.main.activity_edit_child_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import java.io.File
import java.io.IOException
import java.util.*

class CreateChildActivity : AppCompatActivity(), View.OnClickListener {

    var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    var indexBlood: Int = 0
    var itemSelected = 0
    var alertDialog: AlertDialog? = null
    private var selectedImgPath = ""
    private var selectedImgSendPath: Uri? = null
    private val SELECT_IMG = 1
    private val PERMISSION_REQUEST_CODE = 200
    val AUTOCOMPLETE_REQUEST_CODE = 101
    var latlong = ""

    // Set the fields to specify which types of place data to
// return after the user has made a selection.
    val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
//    internal var fields: List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_child)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setOnclick()
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))


// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.name + ", " + place.id)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: $status")
            }
        })

    }

    private fun setOnclick() {
        btn_setpassword!!.setOnClickListener(this)
        btn_save!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        et_birthdate!!.setOnClickListener(this)
        et_bloodtype!!.setOnClickListener(this)
        btn_updateimg!!.setOnClickListener(this)
        tv_address!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.iv_back -> finish()
            R.id.btn_save -> if(validate()) {
                addchildAPI()
            }
            R.id.btn_setpassword->setPassIntent()
            R.id.et_birthdate -> {
                // Initialize a new DatePickerFragment
                val newFragment = DatePickerFragment()
                // Show the date picker dialog
                newFragment.show(fragmentManager, "Date Picker")
            }
            R.id.et_bloodtype -> selectBlood()
            R.id.btn_updateimg -> updateImage()
            R.id.tv_address ->{
// Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }
    }

    private fun setPassIntent() {
        passCode = "";
        startActivity(Intent(this, SetPasscodeActivity::class.java))
    }

/*    private fun setDate() {
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, year, monthOfYear, dayOfMonth ->
                    //                    et_birthdate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year) }, mYear, mMonth, mDay)
                    et_birthdate.setText(year.toString()+ "-" +  getmonth((monthOfYear + 1).toString()) + "-" + getdate(dayOfMonth.toString())) },
                mYear, mMonth, mDay)
        datePickerDialog.show()
    }*/

    internal fun getmonth(monthL: String): String {
        var month = ""
        if (monthL.length == 1) {
            month = "0$monthL"
        } else {
            month = monthL
        }
        return month
    }

    internal fun getdate(dateL: String): String {
        var date = ""
        if (dateL.length == 1) {
            date = "0$dateL"
        } else {
            date = dateL
        }
        return date
    }


    private fun selectBlood() {
        val singleChoiceItemsBlood: ArrayList<String> = resources.getStringArray(R.array.dialog_single_choice_blood_array).toCollection(ArrayList<String>())
        alertDialog = AlertDialog.Builder(this)
                .setTitle("Select your Blood group")
                .setSingleChoiceItems(ArrayAdapter(this@CreateChildActivity,R.layout.layout_bloodview, singleChoiceItemsBlood),
                        itemSelected, DialogInterface.OnClickListener { dialogInterface, selectedIndex ->
                    et_bloodtype.setText(singleChoiceItemsBlood.get(selectedIndex))
                    alertDialog!!.dismiss()
                })
//                .setPositiveButton("Ok", null)
//                .setNegativeButton("Cancel", null)
                .show()

    }

    private fun updateImage() {
        if (!checkPermission()) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_IMG)
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue()
            } else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, SELECT_IMG)
            }
        }
    }

    internal var bitmap: Bitmap? = null

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                tv_address.setText(place.name)
//                latlong = place.latLng.toString()
                latlong = place.latLng!!.latitude.toString()+","+place.latLng!!.longitude.toString()
                val geocoder = Geocoder(this)
                try {
                    val addresses = geocoder.getFromLocation(place.latLng!!.latitude,
                            place.latLng!!.longitude, 1)
                    val address = addresses[0].getAddressLine(0)
                    val city = addresses[0].locality
                    val state = addresses[0].adminArea
                    val zipcode = addresses[0].postalCode
                    //String country = addresses.get(0).getAddressLine(2);
                    tv_state.setText(state)
                    tv_city.setText(city)
                    tv_zipcode.setText(zipcode)
                } catch (e: IOException) {

                    e.printStackTrace()
                }
                Log.i("", "Place: " + place.name + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("", status.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else   if (resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            selectedImgPath = ""
            selectedImgPath = getPathImg(selectedImage)
            val file_extn = selectedImgPath.substring(selectedImgPath.lastIndexOf(".") + 1)
            selectedImgSendPath = data.data
            Picasso.get().load(selectedImgSendPath).into(profile_image)
            if (file_extn == "img" || file_extn == "jpg" || file_extn == "jpeg" || file_extn == "gif" || file_extn == "png") {
                //FINE
            } else {
                //NOT IN REQUIRED FORMAT
            }


        }

    }

    fun getPathImg(uri: Uri?): String {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        val imagePath = cursor.getString(column_index)
        //        iv_profile.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        //        selectedImgSendPath = getImageURLBase64(imagePath);
        return cursor.getString(column_index)
    }


    //-------------

    private fun checkPermission(): Boolean {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setCancelable(true)
                alertBuilder.setTitle(getString(R.string.permission_necessary))
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event)
                alertBuilder.setPositiveButton(android.R.string.yes)
                { dialog, which -> ActivityCompat.requestPermissions(this@CreateChildActivity, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE) }
                val alert = alertBuilder.create()
                alert.show()
                Log.e("", "permission denied, show dialog")
            } else {
                ActivityCompat.requestPermissions(this@CreateChildActivity,
                        arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_IMG)
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val childname     = et_childname.text.toString()
        val childusername = et_username.text.toString()
        val childemailchild= et_emailchild.text.toString()
        val password_child= et_password_child.text.toString()
        val confirm_password = et_conpassword.text.toString()
        val address = tv_address.text.toString()
        val state = tv_state.text.toString()
        val city = tv_city.text.toString()
        val zipcode = tv_zipcode.text.toString()
        val phoneno = et_phoneno.text.toString()
        val birthdate = et_birthdate.text.toString()
        val existingmedical = et_existingmedical.text.toString()
        val bloodtype = et_bloodtype.text.toString()
        val emrcontactname = et_emrcontactname.text.toString()
        val emrphonenopr = et_emrphonenopr.text.toString()
        val emremail     = et_emremail.text.toString()

        val emrcontactname2 = et_emrcontactname2.text.toString()
        val emrphonenopr2 = et_emrphonenopr2.text.toString()
        val emremail2     = et_emremail2.text.toString()



        if (childname.length == 0 && childname.equals("")) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.child_blank))
            valid = false
        }else if (childusername.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.username_blank))
            valid = false
        } else  if (childemailchild.length != 0 && !Constant.checkEmail(childemailchild)) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.email_validate))
            valid = false
        }
        /*else if (!Constant.checkEmail(emailchild)) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.email_validate))
            valid = false
        }*/
        else if (password_child.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.password_blank))
            valid = false
        } else if (password_child.length < 4) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.password_valid))
            valid = false
        } else if (confirm_password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.confirm_pass_blank))
            valid = false
        } else if (!confirm_password.equals(password_child)) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.confirm_pass_notmatch))
            valid = false
        }else  if (address.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.address_blank))
            valid = false
        }else  if (state.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.state_blank))
            valid = false
        }else  if (city.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.city_blank))
            valid = false
        }else  if (zipcode.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.zipcode_blank))
            valid = false
        }else  if (phoneno.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.phoneno_blank))
            valid = false
        }else  if (birthdate.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.dob_blank))
            valid = false
        }else  if (existingmedical.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.existingmadical_blank))
            valid = false
        }else  if (bloodtype.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.bloodtype_blank))
            valid = false
        } else  if (emrcontactname.length != 0 && emrphonenopr.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.emgcontactno_blank))
            valid = false
        } else  if (emremail.length != 0 && !Constant.checkEmail(emremail)) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.email_validate))
            valid = false
        } else  if (emrcontactname2.length != 0 && emrphonenopr2.length == 0) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.emgcontactno_blank))
            valid = false
        }else  if (emremail2.length != 0 && !Constant.checkEmail(emremail2)) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.email_validate))
            valid = false
        }
        else  if (passCode.equals("")) {
            Utils.ShowSnackbarOther(this, btn_save, getString(R.string.passcode_blank))
            valid = false
        }

        return valid
    }

    // SIGNUP API
    private fun addchildAPI() {

        var call: Call<SignUpResponse>? =null
        val imgFile = File(selectedImgPath)
        val imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val imageFile = MultipartBody.Part.createFormData("profile_picture", imgFile.getName(), imgBody)


        Utils.showProgressDialog(this@CreateChildActivity)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val userID: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString());
        val childFName: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_childname.text.toString());
        val childLName: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
        val userName: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_username.text.toString());
        val emailid: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emailchild.text.toString());
     /*   val DOB: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                Constant.parseDateToyyyyMMdd(et_birthdate.text.toString()));*/
        val DOB: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                Constant.DOB)
        val phoneNo: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_phoneno.text.toString());
        val userType: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "child");
        val contactName: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "test");
        val password: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_password_child.text.toString());
        val confirmPassword: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_conpassword.text.toString());
        val emrContactName: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emrcontactname.text.toString());
        val emrphone: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emrphonenopr.text.toString());
        val emrContactemail: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emremail.text.toString());

        val emrContactName2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emrcontactname2.text.toString());
        val emrphone2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emrphonenopr2.text.toString());
        val emrContactemail2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_emremail2.text.toString());

        val relation: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "son");
        val relation2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "son");

        val medical_condition: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_existingmedical.text.toString());
        val passCodeReq: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                passCode);
        val state: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                tv_state.text.toString());
        val city: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                tv_city.text.toString());
        val zipcode: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                tv_zipcode.text.toString());
        val blood_type: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                et_bloodtype.text.toString());
        val address: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                tv_address.text.toString());
        val lat_long: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                latlong);


        if(!selectedImgPath.equals("")) {
             call = apiInterface.registerUserChild(userID, childFName, childLName, userName, emailid, DOB,
                    phoneNo, userType, contactName, imageFile, password, confirmPassword,
                     emrContactName, emrphone,  emrContactemail,emrContactName2, emrphone2,  emrContactemail2,relation,relation2,
                     medical_condition,blood_type,address,  state, city, zipcode,passCodeReq,lat_long)
        }else{
            call = apiInterface.registerUserChildNoImg(userID, childFName, childLName, userName, emailid, DOB,
                    phoneNo, userType, contactName, password, confirmPassword,
                    emrContactName, emrphone,  emrContactemail,emrContactName2, emrphone2,  emrContactemail2,relation,relation2,
                    medical_condition,blood_type,address,  state, city, zipcode,passCodeReq,lat_long)
        }


        call.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {

                if (response.body()!!.status == 200) {
                    PreferenceConnector.writeInteger(this@CreateChildActivity, PreferenceConnector.NO_OF_CHILD, 1)
                    finish()
                    mainIntent()
                    Toast.makeText(this@CreateChildActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreateChildActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@CreateChildActivity)) {
                    Toast.makeText(this@CreateChildActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {

                    Toast.makeText(this@CreateChildActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }
    private fun mainIntent() {
        startActivity(Intent(this, MainActivity::class.java))
    }



}


