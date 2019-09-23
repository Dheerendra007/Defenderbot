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
import com.defenderbot.util.otpview.OnOtpCompletionListener
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.activity_varify_code.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VarifyCodeActivity : AppCompatActivity() ,View.OnClickListener, OnOtpCompletionListener {


    private var varifyCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_varify_code)
        setOnclick()
    }

    private fun setOnclick() {
        fab_back!!.setOnClickListener(this)
        btn_varify!!.setOnClickListener(this)
        otp_view!!.setOtpCompletionListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_varify -> if(validate()) {
                if (Constant.SCREEN.equals("ForgotPassword")) {
                    resetPassVarifyAPI()
                }else{
                    signUpVarifyAPI()
                }
            }
//            R.id.btn_varify -> newPasswordIntent()
            R.id.fab_back -> finish()
        }
    }

    override fun onOtpCompleted(otp: String?) {
        varifyCode = otp.toString()
    }

    private fun newPasswordIntent() {
        startActivity(Intent(this, NewPasswordActivity::class.java))
        finish()
    }

    private fun mainIntent() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    private fun validate(): Boolean {
        var valid = true
        if (varifyCode.length == 0) {
            Utils.ShowSnackbarOther(this, btn_varify, getString(R.string.otp_blank))
            valid = false
        } else if (varifyCode.length < 4) {
            Utils.ShowSnackbarOther(this, btn_varify, getString(R.string.otp_valid))
            valid = false
        }
        return valid
    }

    // SIGNUP Varify API
    private fun signUpVarifyAPI() {
//        System.out.println("USER_ID = " +PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.registerVarify(varifyCode,
                PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.body()!!.status == 200) {
                        mainIntent()
                } else {
                    Toast.makeText(this@VarifyCodeActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@VarifyCodeActivity)) {
                    Toast.makeText(this@VarifyCodeActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VarifyCodeActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }


    // SIGNUP Varify API
    private fun resetPassVarifyAPI() {
//        System.out.println("USER_ID = " +PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.resetPasswordVarify(varifyCode)
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.body()!!.status == 200) {
                    newPasswordIntent()
                } else {
                    Toast.makeText(this@VarifyCodeActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@VarifyCodeActivity)) {
                    Toast.makeText(this@VarifyCodeActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VarifyCodeActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }

}
