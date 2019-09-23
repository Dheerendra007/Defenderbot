package com.defenderbot.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.model.CommonResponse
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.activity_new_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordActivity : AppCompatActivity(),View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)
        setOnclick()
    }

    private fun setOnclick() {
//        fab_back!!.setOnClickListener(this)
        btn_proceed!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_proceed -> if(validate()) {
                changePasswordAPI()
            }
        }
    }

    private fun mainIntent() {
//        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun validate(): Boolean {
        var valid = true
        val password = et_password.text.toString()
        val confirm_password = et_confirm_password.text.toString()

       if (password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_proceed, getString(R.string.password_blank))
            valid = false
        } else if (password.length < 4) {
            Utils.ShowSnackbarOther(this, btn_proceed, getString(R.string.password_valid))
            valid = false
        } else if (confirm_password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_proceed, getString(R.string.confirm_pass_blank))
            valid = false
        } else if (!confirm_password.equals(password)) {
            Utils.ShowSnackbarOther(this, btn_proceed, getString(R.string.confirm_pass_notmatch))
            valid = false
        }
        return valid
    }

    // SIGNUP Varify API
    private fun changePasswordAPI() {

        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.resetPassword(Constant.USER_EMAIL_FORGOT, et_password.text.toString(),
                et_confirm_password.text.toString())
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.body()!!.status == 200) {
                    mainIntent()
                } else {
                    Toast.makeText(this@NewPasswordActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@NewPasswordActivity)) {
                    Toast.makeText(this@NewPasswordActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@NewPasswordActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }

}
