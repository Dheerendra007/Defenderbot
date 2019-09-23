package com.defenderbot.activity.childmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.defenderbot.R
import com.defenderbot.util.Utils
import com.defenderbot.util.otpview.OnOtpCompletionListener
import kotlinx.android.synthetic.main.activity_set_passcode.*
import kotlinx.android.synthetic.main.activity_set_passcode.btn_varify
import kotlinx.android.synthetic.main.activity_set_passcode.otp_view
import kotlinx.android.synthetic.main.activity_varify_code.*

var passCode =""

class PasscodeChildActivity : AppCompatActivity() ,View.OnClickListener, OnOtpCompletionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode_child)
        setOnclick()
    }

    private fun setOnclick() {
        iv_back!!.setOnClickListener(this)
       btn_varify!!.setOnClickListener(this)
        otp_view!!.setOtpCompletionListener(this)
    }

    override fun onOtpCompleted(otp: String?) {
        passCode = otp.toString()
    }


    override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.iv_back -> {
                startbool =true;
                finish()

            }

            R.id.btn_varify ->  if(validate()) {
                finish()
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (passCode.length == 0) {
            Utils.ShowSnackbarOther(this, btn_varify, getString(R.string.passcode_blank))
            valid = false
        } else if (passCode.length < 4) {
            Utils.ShowSnackbarOther(this, btn_varify, getString(R.string.passcode_valid))
            valid = false
        }
        return valid
    }

}
