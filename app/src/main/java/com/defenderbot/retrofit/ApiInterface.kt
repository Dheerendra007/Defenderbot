package com.defenderbot.retrofit



import com.defenderbot.model.CommonResponse
import com.defenderbot.model.childlatlong.ChildLatLongResponse
import com.defenderbot.model.childlist.ChildListResponse
import com.defenderbot.model.getchildlatlong.GetChildLatLongResponse
import com.defenderbot.model.getpolyline.PolylineResponse
import com.defenderbot.model.inserpolygon.InsertPolygonResponse
import com.defenderbot.model.login.LoginResponse
import com.defenderbot.model.profile.ProfileResponse
import com.defenderbot.model.signup.SignUpResponse
import com.defenderbot.retrofit.ApiClient.ACTION_CHANGE_PASSWORD
import com.defenderbot.retrofit.ApiClient.ACTION_CHILD_GET_LATLONG
import com.defenderbot.retrofit.ApiClient.ACTION_CHILD_LIST
import com.defenderbot.retrofit.ApiClient.ACTION_CHILD_STOP_TRACKING
import com.defenderbot.retrofit.ApiClient.ACTION_DELETE_CHILDPROFILE
//import com.defenderbot.retrofit.ApiClient.ACTION_CHILD_SEND_LATLONG
import com.defenderbot.retrofit.ApiClient.ACTION_EDIT_PROFILE
import com.defenderbot.retrofit.ApiClient.ACTION_FORGOT_PASSWORD
import com.defenderbot.retrofit.ApiClient.ACTION_GET_GEO_LOCATION
import com.defenderbot.retrofit.ApiClient.ACTION_GET_PROFILE
import com.defenderbot.retrofit.ApiClient.ACTION_INSERT_GEO_LOCATION
import com.defenderbot.retrofit.ApiClient.ACTION_LOGIN
import com.defenderbot.retrofit.ApiClient.ACTION_MATCH_GEO_LOCATION
import com.defenderbot.retrofit.ApiClient.ACTION_REGISTER
import com.defenderbot.retrofit.ApiClient.ACTION_REGISTER_VARIFY
import com.defenderbot.retrofit.ApiClient.ACTION_RESET_PASSWORD
import com.defenderbot.retrofit.ApiClient.ACTION_RESET_PASSWORD_VARIFY
import com.defenderbot.retrofit.ApiClient.USER_URL
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @FormUrlEncoded
    @POST(USER_URL + ACTION_REGISTER)
    fun registerUser(@Field("email_id") email_id: String,
                     @Field("username") username: String,
                     @Field("password") password: String,
                     @Field("confirm_password") confirm_password: String,
                     @Field("user_type") user_type: String,
                     @Field("parent_id") parent_id: String,
                     @Field("profile_picture") profile_picture: String,
                     @Field("contact_number") contact_number: String):Call<SignUpResponse>

    @Multipart
    @POST(USER_URL + ACTION_REGISTER)
    fun registerUserChild(@Part("parent_id") parent_id: RequestBody,
                          @Part("first_name") first_name: RequestBody,
                          @Part("last_name") last_name: RequestBody,
                          @Part("username") username: RequestBody,
                          @Part("email_id") email_id: RequestBody,
                          @Part("DOB") DOB: RequestBody,
                          @Part("contact_number") contact_number: RequestBody,
                          @Part("user_type") user_type: RequestBody,
                          @Part("contact_name") contact_name: RequestBody,
                          @Part myfile: MultipartBody.Part,
                          @Part("password") password: RequestBody,
                          @Part("confirm_password") confirm_password: RequestBody,
                          @Part("emergency_contact_name_1") emergency_contact_name_1: RequestBody,
                          @Part("emergency_contact_number_1") emergency_contact_number_1: RequestBody,
                          @Part("emergency_contact_email_1") emergency_contact_email_1: RequestBody,
                          @Part("emergency_contact_name_2") emergency_contact_name2: RequestBody,
                          @Part("emergency_contact_number_2") emergency_contact_number2: RequestBody,
                          @Part("emergency_contact_email_2") emergency_contact_email2: RequestBody,
                          @Part("relation_with_child_1") relation_with_child_1: RequestBody,
                          @Part("relation_with_child_2") relation_with_child_2: RequestBody,
                          @Part("medical_condition") medical_condition: RequestBody,
                          @Part("blood_type") blood_type: RequestBody,
                          @Part("address") address: RequestBody,
                          @Part("state") state: RequestBody,
                          @Part("city") city: RequestBody,
                          @Part("zip_code") zip_code: RequestBody,
                          @Part("pin") pin: RequestBody,
                          @Part("lat_long") lat_long: RequestBody ):Call<SignUpResponse>

    @Multipart
    @POST(USER_URL + ACTION_REGISTER)
    fun registerUserChildNoImg(@Part("parent_id") parent_id: RequestBody,
                               @Part("first_name") first_name: RequestBody,
                               @Part("last_name") last_name: RequestBody,
                               @Part("username") username: RequestBody,
                               @Part("email_id") email_id: RequestBody,
                               @Part("DOB") DOB: RequestBody,
                               @Part("contact_number") contact_number: RequestBody,
                               @Part("user_type") user_type: RequestBody,
                               @Part("contact_name") contact_name: RequestBody,
                               @Part("password") password: RequestBody,
                               @Part("confirm_password") confirm_password: RequestBody,
                               @Part("emergency_contact_name_1") emergency_contact_name_1: RequestBody,
                               @Part("emergency_contact_number_1") emergency_contact_number_1: RequestBody,
                               @Part("emergency_contact_email_1") emergency_contact_email_1: RequestBody,
                               @Part("emergency_contact_name_2") emergency_contact_name2: RequestBody,
                               @Part("emergency_contact_number_2") emergency_contact_number2: RequestBody,
                               @Part("emergency_contact_email_2") emergency_contact_email2: RequestBody,
                               @Part("relation_with_child_1") relation_with_child_1: RequestBody,
                               @Part("relation_with_child_2") relation_with_child_2: RequestBody,
                               @Part("medical_condition") medical_condition: RequestBody,
                               @Part("blood_type") blood_type: RequestBody,
                               @Part("address") address: RequestBody,
                               @Part("state") state: RequestBody,
                               @Part("city") city: RequestBody,
                               @Part("zip_code") zip_code: RequestBody,
                               @Part("pin") pin: RequestBody,
                               @Part("lat_long") lat_long: RequestBody ):Call<SignUpResponse>



    @FormUrlEncoded
    @POST(USER_URL + ACTION_LOGIN)
    fun loginUser(@Field("username") username: String,
                  @Field("password") password: String):Call<LoginResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_REGISTER_VARIFY)
    fun registerVarify(@Field("otp") otp: String,@Field("user_id") user_id: String):Call<CommonResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_FORGOT_PASSWORD)
    fun forgotPassword(@Field("email_id") email_id: String):Call<CommonResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_RESET_PASSWORD_VARIFY)
    fun resetPasswordVarify(@Field("reset_otp") reset_otp: String):Call<CommonResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_CHANGE_PASSWORD)
    fun changePassword( @Field("user_id") user_id: String,@Field("old_password") old_password: String,
                        @Field("new_password") new_password: String, @Field("confirm_password")
                        confirm_password: String):Call<CommonResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_RESET_PASSWORD)
    fun resetPassword( @Field("email_id") user_id: String, @Field("password") new_password: String,
                       @Field("confirm_password") confirm_password: String):Call<CommonResponse>



    @FormUrlEncoded
    @POST(USER_URL + ACTION_INSERT_GEO_LOCATION)
    fun insertGeoLocation(@Field("parent_id") parent_id: String,
                          @Field("child_id") child_id: String,
                          @Field("geo_location") geo_location: String,
                          @Field("and_geo_location") and_geo_location: String,
                          @Field("start_lat") start_lat: String,
                          @Field("start_long") start_long: String,
                          @Field("end_lat") end_lat: String,
                          @Field("end_long") end_long: String,
                          @Field("child_lat") child_lat: String,
                          @Field("child_long") child_long: String,
                          @Field("location_id") location_id: String,
                          @Field("name") name: String,
                          @Field("origin") origin: String,
                          @Field("destination") destination: String,
                          @Field("destination_address") destination_address: String):Call<InsertPolygonResponse>



    @FormUrlEncoded
    @POST(USER_URL + ACTION_GET_GEO_LOCATION)
    fun getGeoLocation(@Field("parent_id") parent_id: String,
                       @Field("child_id") child_id: String):Call<PolylineResponse>
    @FormUrlEncoded
    @POST(USER_URL + ACTION_MATCH_GEO_LOCATION)
    fun matchGeoLocation(@Field("parent_id") parent_id: String,
                         @Field("child_id") child_id: String,
                         @Field("geo_location") geo_location: String,
                         @Field("battery_percent") battery_percent: String,
                         @Field("tracking_status") tracking_status: String,
                         @Field("last_online_update") last_online_update: String):Call<ChildLatLongResponse>

    @Multipart
    @POST(USER_URL + ACTION_EDIT_PROFILE)
    fun editParentProfile(
            @Part("parent_id") parent_id: RequestBody,
            @Part("relation_with_child_1") relation_with_child_1: RequestBody,
            @Part("relation_with_child_2") relation_with_child_2: RequestBody,
            @Part("user_id") user_id: RequestBody,
                          @Part("username") username: RequestBody,
                          @Part("email_id") email_id: RequestBody,
                          @Part("address") address: RequestBody,
                          @Part("dob") dob: RequestBody,
                          @Part("contact_number") contact_number: RequestBody,
                          @Part("state") state: RequestBody,
                          @Part("city") city: RequestBody,
                          @Part("zip_code") zip_code: RequestBody,
                          @Part myfile: MultipartBody.Part ,
                          @Part("emergency_id_1") emergency_id_1: RequestBody,
                          @Part("emergency_contact_name_1") emergency_contact_name_1: RequestBody,
                          @Part("emergency_contact_number_1") emergency_contact_number_1: RequestBody,
                          @Part("emergency_contact_email_1") emergency_contact_email_1: RequestBody,
                          @Part("emergency_id_2") emergency_id_2: RequestBody,
                          @Part("emergency_contact_name_2") emergency_contact_name_2: RequestBody,
                          @Part("emergency_contact_number_2") emergency_contact_number_2: RequestBody,
                          @Part("emergency_contact_email_2") emergency_contact_email_2: RequestBody,
                          @Part("medical_condition") medical_condition: RequestBody,
                          @Part("blood_type") blood_type: RequestBody,
                          @Part("lat_long") lat_long: RequestBody):Call<CommonResponse>


    @Multipart
    @POST(USER_URL + ACTION_EDIT_PROFILE)
    fun editParentProfileNoImg(
            @Part("parent_id") parent_id: RequestBody,
            @Part("relation_with_child_1") relation_with_child_1: RequestBody,
            @Part("relation_with_child_2") relation_with_child_2: RequestBody,
            @Part("user_id") user_id: RequestBody,
                               @Part("username") username: RequestBody,
                               @Part("email_id") email_id: RequestBody,
                               @Part("address") address: RequestBody,
                               @Part("dob") dob: RequestBody,
                               @Part("contact_number") contact_number: RequestBody,
                               @Part("state") state: RequestBody,
                               @Part("city") city: RequestBody,
                               @Part("zip_code") zip_code: RequestBody,
                               @Part("emergency_id_1") emergency_id_1: RequestBody,
                               @Part("emergency_contact_name_1") emergency_contact_name_1: RequestBody,
                               @Part("emergency_contact_number_1") emergency_contact_number_1: RequestBody,
                               @Part("emergency_contact_email_1") emergency_contact_email_1: RequestBody,
                               @Part("emergency_id_2") emergency_id_2: RequestBody,
                               @Part("emergency_contact_name_2") emergency_contact_name_2: RequestBody,
                               @Part("emergency_contact_number_2") emergency_contact_number_2: RequestBody,
                               @Part("emergency_contact_email_2") emergency_contact_email_2: RequestBody,
                               @Part("medical_condition") medical_condition: RequestBody,
                               @Part("blood_type") blood_type: RequestBody,
                               @Part("lat_long") lat_long: RequestBody):Call<CommonResponse>


    @FormUrlEncoded
    @POST(USER_URL + ACTION_GET_PROFILE)
    fun getProfile(@Field("user_id") user_id: String):Call<ProfileResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_CHILD_LIST)
    fun childList(@Field("parent_id") userfile: String):Call<ChildListResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_CHILD_GET_LATLONG)
    fun getchildCurrentLatLong(@Field("child_id") child_id: String):Call<GetChildLatLongResponse>

    @FormUrlEncoded
    @POST(USER_URL + ACTION_CHILD_STOP_TRACKING)
    fun stopChildTracking(@Field("parent_id") parent_id: String,
                          @Field("child_id") child_id: String,
                          @Field("pin") long: String):Call<CommonResponse>


    @FormUrlEncoded
    @POST(USER_URL + ACTION_DELETE_CHILDPROFILE)
    fun deleteChild(@Field("child_id") child_id: String):Call<CommonResponse>


}
