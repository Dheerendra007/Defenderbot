package com.defenderbot.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R

class SettingFragment : androidx.fragment.app.Fragment(), View.OnClickListener {


    //    var view: View? = null
    lateinit var viewLay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewLay = inflater.inflate(R.layout.fragment_setting, container, false)
        setOnclick()
        //        getHomeData();
        return viewLay
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setOnclick() {


    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


//    override fun onClick(view: View) {
//        when (view.id) {
//            R.id.ll_digital_book -> startActivity(Intent(activity, DigitalBookActivity::class.java))
//        }
//    }


}// Required empty public constructor

