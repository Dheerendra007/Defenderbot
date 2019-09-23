package com.defenderbot.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.model.signup.Data
import com.defenderbot.model.signup.SignUpRequest
import com.defenderbot.model.signup.SignUpResponse
import com.defenderbot.retrofit.ApiClient
import com.defenderbot.retrofit.ApiInterface
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import com.oceansapparel.util.PreferenceConnector
import com.oceansapparel.util.PreferenceConnector.USER_EMAIL
import com.oceansapparel.util.PreferenceConnector.USER_ID
import com.oceansapparel.util.PreferenceConnector.USER_NAME
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(),View.OnClickListener{

    var signUpRequest: SignUpRequest = SignUpRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setOnclick()
    }

    private fun setOnclick() {
        btn_signup!!.setOnClickListener(this@SignUpActivity)
        fab_back!!.setOnClickListener(this@SignUpActivity)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_signup -> if(validate()) {
                attemptSignUpAPI()
            }
            R.id.fab_back -> finish()
        }
    }

    private fun varifyCodeIntent() {
        startActivity(Intent(this@SignUpActivity, VarifyCodeActivity::class.java))
    }

    private fun validate(): Boolean {
        var valid = true
        val username = et_username.text.toString()
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        val confirm_password = et_confirm_password.text.toString()

        if (username.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.username_blank))
            valid = false
        }else if (email.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.email_blank))
            valid = false
        } else if (!Constant.checkEmail(email)) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.email_validate))
            valid = false
        } else if (password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.password_blank))
            valid = false
        } else if (password.length < 4) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.password_valid))
            valid = false
        } else if (confirm_password.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.confirm_pass_blank))
            valid = false
        } else if (!confirm_password.equals(password)) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.confirm_pass_notmatch))
            valid = false
        }

        return valid
    }

    fun setValueDB(data: Data) {
        PreferenceConnector.writeInteger(this, USER_ID, data.userId!!)
        PreferenceConnector.writeString(this,USER_NAME,data.username!!)
        PreferenceConnector.writeString(this,USER_EMAIL,data.emailId!!)
        varifyCodeIntent()
    }

    // SIGNUP API
    private fun attemptSignUpAPI() {

        signUpRequest.username        = et_username.text.toString()
        signUpRequest.emailId         = et_email.text.toString()
        signUpRequest.password        = et_password.text.toString()
        signUpRequest.confirmPassword = et_confirm_password.text.toString()

        if (signUpRequest != null) {
            Utils.showProgressDialog(this)
            val apiInterface = ApiClient.client.create(ApiInterface::class.java)
            val call = apiInterface.registerUser(signUpRequest.emailId!!, signUpRequest.username!!,
                    signUpRequest.password!!, signUpRequest.confirmPassword!!,
                    "parent","0","","")
            call.enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.body()!!.status == 200) {
                        setValueDB(response.body()!!.data!!)
                    } else {
                        Toast.makeText(this@SignUpActivity, response.body()!!.message,Toast.LENGTH_SHORT).show()
                    }
                    //                                        myLoading.dismiss();\
                    Utils.hideProgressDialog()
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    //                    Utils.ShowSnackbarOther(context, btnLogin, "Error:" + t.getMessage());
                    if (Utils.isNetworkAvailable(this@SignUpActivity)) {
                        Toast.makeText(this@SignUpActivity, Constant.SERVER_ERROR, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignUpActivity, Constant.INTERNET_ERROR, Toast.LENGTH_SHORT).show()
                    }
                    //                                        myLoading.dismiss();
                    Utils.hideProgressDialog()
                }
            })
        }
    }
}
