package com.defenderbot.util

import android.app.*
import android.os.Bundle
import android.widget.TextView
import android.widget.DatePicker
import android.widget.Toast
import com.defenderbot.R
import com.defenderbot.util.duonavigationdrawer.Constant.DOB
import com.defenderbot.util.duonavigationdrawer.Constant.DOBPARENT
import java.text.DateFormat
import java.time.Month
import java.util.Calendar


class DatePickerFragmentParent : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var calendar:Calendar
    private lateinit var DAY:String
    private lateinit var MONTH:String


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Initialize a calendar instance
        calendar = Calendar.getInstance()

        // Get the system current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        /*
            **** reference source developer.android.com ***

            DatePickerDialog(Context context)
                Creates a new date picker dialog for the current date using the
                parent context's default date picker dialog theme.

            DatePickerDialog(Context context, int themeResId)
                Creates a new date picker dialog for the current date.

            DatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener,
            int year, int month, int dayOfMonth)
                Creates a new date picker dialog for the specified date using the parent
                context's default date picker dialog theme.

            DatePickerDialog(Context context, int themeResId, DatePickerDialog.OnDateSetListener
            listener, int year, int monthOfYear, int dayOfMonth)
                Creates a new date picker dialog for the specified date.
        */

        // Initialize a new date picker dialog and return it
        return DatePickerDialog(
                activity, // Context
                // Put 0 to system default theme or remove this parameter
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, // Theme
                this, // DatePickerDialog.OnDateSetListener
                year, // Year
                month, // Month of year
                day // Day of month
        )
    }


    // When date set and press ok button in date picker dialog
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Toast.makeText(
                activity,
                "Date Set : ${formatDate(year,month,day)}"
                ,Toast.LENGTH_SHORT
        ).show()
        DAY = day.toString()
        MONTH = (month+1).toString()

//        DOB = "yyyy-MM-dd"
        if(day.toString().length==1){
            DAY = "0"+DAY
        }
        if(month.toString().length==1){
            MONTH = "0"+MONTH
        }

        DOBPARENT = year.toString()+"-"+MONTH+"-"+DAY
        // Display the selected date in text view
        activity.findViewById<TextView>(R.id.et_pbirthdate).text = formatDate(year,month,day)
    }


    // Custom method to format date
    private fun formatDate(year:Int, month:Int, day:Int):String{
        // Create a Date variable/object with user chosen date
        calendar.set(year, month, day, 0, 0, 0)
        val chosenDate = calendar.time

        // Format the date picker selected date
        val df = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return df.format(chosenDate)
    }
}