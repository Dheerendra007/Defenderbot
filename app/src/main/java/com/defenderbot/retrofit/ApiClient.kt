package com.defenderbot.retrofit


import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
//    http://52.12.22.233/defenderbot/user/match_geo_location
//    http://drobot.stackpigeon.com/user/userRegistration

    const val BASE_URL = "http://52.12.22.233/defenderbot/"
//    http://34.217.65.92/user/login_user
    //    public static final String BASE_URL_ADMIN = Constant.ADMIN_URL;
    const  val USER_URL = "user/"
//    const val COMMON_URL = "common/"
//    const val API_URL = "api/"

    const val  IMAGE_URL= "http://52.12.22.233/defenderbot"


    const val ACTION_REGISTER               = "userRegistration"//done
    const val ACTION_LOGIN                  = "login_user"//done
    const val ACTION_REGISTER_VARIFY        = "register_verify"
    const val ACTION_FORGOT_PASSWORD        = "ForgotPassword" //done
    const val ACTION_RESET_PASSWORD_VARIFY  = "resetpwd_verify"//done
    const val ACTION_CHANGE_PASSWORD        = "change_password"//done
    const val ACTION_RESET_PASSWORD         = "reset_password"//done
    const val ACTION_CHILD_ADD              = "childAdd"
    const val ACTION_EMERGENCY_CONTACT      = "emergency_contact"
    const val ACTION_SETPASSCODE_VERIFY     = "setpasscodeVerify"
    const val ACTION_INSERT_GEO_LOCATION    = "insert_geo_location"
    const val ACTION_GET_GEO_LOCATION       = "get_match_geo_location"
    const val ACTION_MATCH_GEO_LOCATION     = "match_geo_location"
    const val ACTION_SHOW_CHILD_PROFILE     = "showChildprofile"
    const val ACTION_FACEBOOK_SIGNUP        = "Facebooksignup"
    const val ACTION_GOOGLE_SIGNUP          = "googlesignup"
    const val ACTION_FACEBOOK_LOGIN         = "FacebookLogin"
    const val ACTION_GOOGLE_LOGIN           = "googleLogin"
    const val ACTION_DELETE_CHILDPROFILE    = "delete_child_profile"
    const val ACTION_EDIT_CHILDPROFILE      = "EditChildProfile"
    const val ACTION_EDIT_PROFILE           = "edit_user_profile" //done
    const val ACTION_GET_PROFILE            = "view_user_profile"//done
    const val ACTION_UPLOAD_VIDEO           = "uploadVideo"
    const val ACTION_CHILD_LIST             = "total_child"   //done
//    const val ACTION_CHILD_SEND_LATLONG   = "insert_live_location"
    const val ACTION_CHILD_GET_LATLONG      = "get_live_location"
    const val ACTION_CHILD_STOP_TRACKING    = "verify_passcode"
//    http://drobot.stackpigeon.com/user/verify_passcode
    //    http://localhost/Defenderboot/user/total_child/

    /*  http://drobot.stackpigeon.com/user/showChildprofile/58
      http://drobot.stackpigeon.com/user/deleteChildprofile/73*/

    private var retrofit: Retrofit? = null
    private val retrofitAdmin: Retrofit? = null


    val client: Retrofit
        get() {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)

            val gson = GsonBuilder()
                    .setLenient()
                    .create()


            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(httpClient.build())
                        .build()
            }
            return this.retrofit!!
        }


}
