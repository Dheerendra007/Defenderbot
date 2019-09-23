package com.defenderbot.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.defenderbot.R
import com.defenderbot.util.Utils
import com.defenderbot.util.duonavigationdrawer.Constant
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_varify_email.*
import kotlinx.android.synthetic.main.activity_varify_email.et_email

class VarifyEmailActivity : AppCompatActivity(),View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_varify_email)
        setOnclick()
    }

    private fun setOnclick() {
        fab_back!!.setOnClickListener(this@VarifyEmailActivity)
        btn_submit!!.setOnClickListener(this@VarifyEmailActivity)
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.btn_submit ->  if(validate()) {
                varifyCodeIntent()
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

    private fun varifyCodeIntent() {
        startActivity(Intent(this@VarifyEmailActivity, VarifyCodeActivity::class.java))
    }

  /*  private fun validate(): Boolean {
        var valid = true
        val username = et_username.text.toString()
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        val confirm_password = et_confirm_password.text.toString()

        if (username.length == 0) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.username_blank))
            valid = false
        } else if (password.length < 4) {
            Utils.ShowSnackbarOther(this, btn_signup, getString(R.string.password_valid))
            valid = false
        }
        return valid
    }

    // SIGNUP Varify API
    private fun signUpVarifyAPI() {

            Utils.showProgressDialog(this)
            val apiInterface = ApiClient.client.create(ApiInterface::class.java)
            val call = apiInterface.registerVarify(user, signUpRequest.username!!, signUpRequest.password!!, signUpRequest.confirmPassword!!)
            call.enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.body()!!.status == 200) {
                        setValueDB(response.body()!!.data)
                    } else {
                        Toast.makeText(this@SignUpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
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
*/


}
