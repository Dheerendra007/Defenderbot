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
import com.defenderbot.util.duonavigationdrawer.Constant.USER_EMAIL_FORGOT
import com.oceansapparel.util.PreferenceConnector
import kotlinx.android.synthetic.main.activity_varify_email.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() ,View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setOnclick()
    }

    private fun setOnclick() {
        fab_back!!.setOnClickListener(this@ForgotPasswordActivity)
        btn_submit!!.setOnClickListener(this@ForgotPasswordActivity)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_submit ->  if(validate()) {
                forgotPassAPI()
            }
            R.id.fab_back -> finish()
        }
    }

    private fun validate(): Boolean {
        var valid = true

        val email = et_email.text.toString()

        if (email.length == 0) {
            Utils.ShowSnackbarOther(this, btn_submit, getString(R.string.email_blank))
            valid = false
        } else if (!Constant.checkEmail(email)) {
            Utils.ShowSnackbarOther(this, btn_submit, getString(R.string.email_validate))
            valid = false
        }

        return valid
    }


    // SIGNUP Varify API
    private fun forgotPassAPI() {
//        System.out.println("USER_ID = " +PreferenceConnector.readInteger(this, PreferenceConnector.USER_ID,0).toString())
        Utils.showProgressDialog(this)
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.forgotPassword(et_email.text.toString())
        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.body()!!.status == 200) {
                    Constant.SCREEN = "ForgotPassword"
                    USER_EMAIL_FORGOT = et_email.text.toString()
                    startActivity(Intent(this@ForgotPasswordActivity, VarifyCodeActivity::class.java))
                    finish()
//                    Toast.makeText(this@ForgotPasswordActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@ForgotPasswordActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();\
                Utils.hideProgressDialog()
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                if (Utils.isNetworkAvailable(this@ForgotPasswordActivity)) {
                    Toast.makeText(this@ForgotPasswordActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                }
                //                                        myLoading.dismiss();
                Utils.hideProgressDialog()
            }
        })
    }


}
