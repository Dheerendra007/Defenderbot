@file:Suppress("unused")

package com.oceansapparel.util

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

object PreferenceConnector {

    private const val PREF_NAME = "ocreanapparel"
    private val sharedPreferences: SharedPreferences? = null
    private const val MODE = Context.MODE_PRIVATE
    const val IS_LOGIN = "isLogin"
    const val APP_TOUR = "appTour"
    const val USER_ID = "userId"
    const val PARENT_ID = "parentId"
    const val USER_FNAME = "userFName"
    const val USER_NAME = "userName"
    const val USER_MOBILE = "userMobile"
    const val USER_EMAIL = "userEmail"
    const val USER_TYPE = "usertype"
    const val USER_PROFILE_PIC = "userprofilepic"
    const val USER_AUTH = "userAuthToken"
    const val IS_Guest_LOGIN = "isGuestLogin"
    const val CHILD_ID = "childId"
    const val SOCIAL_ID = "socialid"
    const val SOCIAL_NAME = "socialname"
    const val SOCIAL_EMAIL = "socailemail"
    const val DEVICE_TOKEN = "deviceToken"
    const val NOTIFICATION_NUMBER = "notificationNumber"
    const val NOTIFICATION_COUNT = "notificationCount"
    const val NO_OF_CHILD= "noofchild"
    const val PERMISSION = "Permission"

    private fun getPreferences(context: Context): SharedPreferences {
        return sharedPreferences ?: context.getSharedPreferences(PREF_NAME, MODE)
    }

    private fun getEditor(context: Context): Editor {
        return getPreferences(context).edit()
    }

    fun writeBoolean(context: Context, key: String, value: Boolean) {
        getEditor(context).putBoolean(key, value).commit()
    }

    fun readBoolean(context: Context, key: String, defValue: Boolean): Boolean {
        return getPreferences(context).getBoolean(key, defValue)
    }

    fun writeInteger(context: Context, key: String, value: Int) {
        getEditor(context).putInt(key, value).commit()
    }

    fun readInteger(context: Context, key: String, defValue: Int): Int {
        return getPreferences(context).getInt(key, defValue)
    }

    fun writeString(context: Context, key: String, value: String) {
        getEditor(context).putString(key, value).commit()
    }

    fun readString(context: Context, key: String, defValue: String): String? {
        return getPreferences(context).getString(key, defValue)
    }

    fun remove(context: Context, key: String) {
        getEditor(context).remove(key).commit()
    }

    fun clear(context: Context) {
        getEditor(context).clear().commit()
    }

}
