package com.defenderbot.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.widget.DatePicker
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.model.CommonResponse
import com.defenderbot.model.profile.Data
import com.defenderbot.model.profile.ProfileResponse


import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.DatePickerFragment
import com.defenderbot.util.DatePickerFragmentParent
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.defenderbot.util.duonavigationdrawer.Constant.DOBPARENT
import com.oceansapparel.util.PreferenceConnector
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_child_profile.*
import kotlinx.android.synthetic.main.activity_edit_child_profile.iv_back

import kotlinx.android.synthetic.main.activity_edit_parent.*
import kotlinx.android.synthetic.main.activity_edit_parent.btn_updateimg
import kotlinx.android.synthetic.main.activity_edit_parent.profile_image
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class EditParentActivity : AppCompatActivity(), View.OnClickListener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_parent)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setOnclick()
        getProfileAPI()
    }

    private fun setOnclick() {
        btn_changepassword!!.setOnClickListener(this)
        btn_update!!.setOnClickListener(this)
        iv_back!!.setOnClickListener(this)
        et_pbirthdate!!.setOnClickListener(this)
        btn_updateimg!!.setOnClickListener(this)
//        et_bloodtype!!.setOnClickListener(this)
    }




    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.iv_back -> finish()
            R.id.btn_update -> if(validate()) {
                updateProfileAPI()
            }
            R.id.btn_changepassword->setPassIntent()
            R.id.et_pbirthdate -> {
                // Initialize a new DatePickerFragment
                val newFragment = DatePickerFragmentParent()
                // Show the date picker dialog
                newFragment.show(fragmentManager, "Date Picker")
            }
//            R.id.et_pbirthdate -> setDate()
            R.id.btn_updateimg -> updateImage()

        }
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
        if (resultCode == Activity.RESULT_OK) {
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




    private fun setPassIntent() {
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }

 /*   private fun setDate() {
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, year, monthOfYear, dayOfMonth ->
                    //                    et_birthdate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year) }, mYear, mMonth, mDay)
                    et_pbirthdate.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()) }, mYear, mMonth, mDay)

        datePickerDialog.show()
    }*/




    private fun validate(): Boolean {
        var valid = true
//        val pname     = et_pname.text.toString()
        val pemail = et_pemail.text.toString()
        val paddress = et_paddress.text.toString()
        val pphoneno = et_pphoneno.text.toString()
        val pbirthdate = et_pbirthdate.text.toString()

//        if (pname.length == 0) {
//            Utils.ShowSnackbarOther(this, btn_update, getString(R.string.prent_blank))
//            valid = false
//        }  else
            if (paddress.length == 0) {
            Utils.ShowSnackbarOther(this, btn_update, getString(R.string.address_blank))
            valid = false
        }else  if (pphoneno.length == 0) {
            Utils.ShowSnackbarOther(this, btn_update, getString(R.string.phoneno_blank))
            valid = false
        }else  if (pbirthdate.length == 0) {
            Utils.ShowSnackbarOther(this, btn_update, getString(R.string.dob_blank))
            valid = false
        }


        return valid
    }

    // SIGNUP API
    private fun updateProfileAPI() {

        var call: Call<CommonResponse>? = null


            val imgFile = File(selectedImgPath)
            val imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile)
            val imageFile = MultipartBody.Part.createFormData("profile_picture", imgFile.getName(), imgBody)


            Utils.showProgressDialog(this@EditParentActivity)
            val apiInterface = ApiClient.client.create(ApiInterface::class.java)

            val userID: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                    PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID, 0).toString());
            val userName: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                    tv_pusername.text.toString());
        val useremail: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                et_pemail.text.toString());
            val address: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                    et_paddress.text.toString());
         /*   val DOB: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                    et_pbirthdate.text.toString());*/
        val DOB: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                DOBPARENT);
            val phoneNo: RequestBody = RequestBody.create(MediaType.parse("text/plain"),
                    et_pphoneno.text.toString());
            val state: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                    "");
            val city: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                    "");
            val zipcode: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                    "");
        val emer_con_id: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
            val emer_con_name: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                    "");
            val emer_con_number: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                   "");
            val emer_con_email: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                   "");
        val emer_con_id2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
               "");
        val emer_con_name2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
        val emer_con_number2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
        val emer_con_email2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");

            val medical_condition: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
            val blood_type: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");
        val lat_long: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");

//        val parentID: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
//                PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,
//                        0).toString());

        val relationwithchild1: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");

        val relationwithchild2: RequestBody = RequestBody.create (MediaType.parse("text/plain"),
                "");

            if(selectedImgPath.equals("")) {
                call = apiInterface.editParentProfileNoImg(userID,relationwithchild1,relationwithchild2,userID, userName,useremail, address,
                        DOB, phoneNo,state,city,zipcode,emer_con_id,emer_con_name,emer_con_number,emer_con_email,
                        emer_con_id2,emer_con_name2,emer_con_number2,emer_con_email2,
                        medical_condition,blood_type,lat_long)
            }else{
                call = apiInterface.editParentProfile(userID,relationwithchild1,relationwithchild2,userID, userName, useremail,address, DOB,
                        phoneNo, state, city, zipcode, imageFile,emer_con_id,emer_con_name,emer_con_number,emer_con_email,
                        emer_con_id2,emer_con_name2,emer_con_number2,emer_con_email2,
                        medical_condition,blood_type,lat_long)
            }

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.body()!!.status == 200) {

                    Toast.makeText(this@EditParentActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditParentActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@EditParentActivity)) {
                    Toast.makeText(this@EditParentActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditParentActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }

    // GET PROFILE API
    private fun getProfileAPI() {
        Constant.USER_ID_PASS_CHANGE = PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0)
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getProfile( PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>,
                                    response: Response<ProfileResponse>) {
                if (response.body()!!.status == 200) {
                    setValueDB(response.body()!!.data!!)
                } else {
                    Toast.makeText(this@EditParentActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@EditParentActivity)) {
                    Toast.makeText(this@EditParentActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditParentActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })

    }

    fun setValueDB(data: Data) {
        PreferenceConnector.writeInteger(this, PreferenceConnector.USER_ID,data.userId!!)
        PreferenceConnector.writeInteger(this, PreferenceConnector.PARENT_ID,data.parentId!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_NAME,data.username!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_EMAIL,data.emailId!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_TYPE,data.userType!!)
        PreferenceConnector.writeString(this, PreferenceConnector.USER_PROFILE_PIC,data.image!!)
        setValue(data)

    }

    private fun setValue(data: Data) {
        Picasso.get().load(ApiClient.IMAGE_URL +PreferenceConnector.readString(this, PreferenceConnector.USER_PROFILE_PIC,"")).into(profile_image)
//        tv_pusername.setText(PreferenceConnector.readString(this, PreferenceConnector.USER_NAME,""))
        tv_pusername.setText(PreferenceConnector.readString(this, PreferenceConnector.USER_NAME,""))
        et_pemail.setText(PreferenceConnector.readString(this, PreferenceConnector.USER_EMAIL,""))
        et_paddress.setText(data.address)
        et_pphoneno.setText(data.contactNumber)
        et_pbirthdate.setText(data.dOB)
        DOBPARENT = data.dOB.toString()
    }

    //-------------

    private fun checkPermission(): Boolean {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setCancelable(true)
                alertBuilder.setTitle(getString(R.string.permission_necessary))
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event)
                alertBuilder.setPositiveButton(android.R.string.yes)
                { dialog, which -> ActivityCompat.requestPermissions(this@EditParentActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE) }
                val alert = alertBuilder.create()
                alert.show()
                Log.e("", "permission denied, show dialog")
            } else {
                ActivityCompat.requestPermissions(this@EditParentActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_IMG)
        }
    }
}


