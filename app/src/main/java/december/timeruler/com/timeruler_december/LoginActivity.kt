/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package december.timeruler.com.timeruler_december

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.*
import android.location.Address
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import december.timeruler.com.timeruler_december.DBHELPERS.LOGINDATADBHELPER
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.lang.Exception
import java.util.*
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.Adapters.GeofenceAdapter
import december.timeruler.com.timeruler_december.DBHELPERS.APIATAY
import december.timeruler.com.timeruler_december.DBHELPERS.OFFLINELOGDBHELPER
import december.timeruler.com.timeruler_december.Model.*
import december.timeruler.com.timeruler_december.Settings.SettingsAdmin
import december.timeruler.com.timeruler_december.Settings.SettingsUsers
import december.timeruler.com.timeruler_december.Model.JavaToKotlin
import kotlinx.android.synthetic.main.activity_add_geofence.*

import okhttp3.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat


class LoginActivity : AppCompatActivity(), SurfaceCamera.PassData {
    override fun passstring(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    val TAG = "LoginActivity"

    companion object {
        lateinit var usernamesettings: String
        lateinit var username: String
        lateinit var password: String
        lateinit var lat: String
        lateinit var long: String
        lateinit var login_mode: String
        lateinit var userLevel: String
        lateinit var elapsedTime: String

    }

    var globalPushOnce = 0
    lateinit var ipDialog: AlertDialog
    lateinit var globalServertimemilis: String
    lateinit var filepath: File
    lateinit var filename: String
    lateinit var globalGeofenceList: GeofenceModelList
    var notif_id = 1023
    var haveWifi = false
    lateinit var mGPSDialog: AlertDialog
    var globalSavePasswordBoolean: Boolean = false
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private lateinit var myLOGINDATADBHELPER: LOGINDATADBHELPER

    private var client: FusedLocationProviderClient? = null
    lateinit var globalServerTime: CurrentTimeList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /* Push Offline Data*/
        getServerTime()

        getGeofences()

        // startActivity(Intent(this,SettingsAdmin::class.java))
        var myApiAtay = APIATAY(this)
        if (myApiAtay.apilist.size == 0) {
            showIPentry()
        }
        //  getUserLevel("000002")
//            Log.e(TAG, userLevel)
        setupDigitalClock()


        myLOGINDATADBHELPER = LOGINDATADBHELPER(this)

        var intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter)
        getLoginData()
        // textListener()
//        savedInstanceState ?: supportFragmentManager.beginTransaction()
//            .replace(R.id.container, Camera2BasicFragment.newInstance())
//            .commit()
        checkGPSIsEnabled()
        askPermissions()
        getLocation()


//
//        var javaLocation = JavaToKotlin()
        var javaLocation = JavaToKotlin()
        javaLocation.getLocationWhenOffline(this)

        //myJavaFunctions.getLocationWhenOffline(this)
        btn_login.setOnClickListener {
            username = et_username.text.toString()
            password = et_password.text.toString()

            if (login_mode.equals("online")) {
                Log.e(TAG, "online")

                verifyLoginb(username, password)

            } else {
                var success = false
                Log.e(TAG, "offline = " + splashScreen.globalUserList)
                for (i in 0 until splashScreen.globalUserList.size) {


                    Log.e(TAG, splashScreen.globalUserList[i].userName)
                    if (splashScreen.globalUserList[i].userName.equals(username) && splashScreen.globalUserList[i].userPass.equals(
                            password
                        )
                    ) {
                        saveLogin()
                        success = true
                        var myIntent = Intent(this, SurfaceCamera::class.java)
                        startActivityForResult(myIntent, 1)


                    }


                }
                if (!success)
                    Toast.makeText(this@LoginActivity, "Enter valid credentials", Toast.LENGTH_SHORT).show()


            }


        }

        iv_settings.setOnClickListener { showSettingsLoginDialog() }

        iv_close_username.setOnClickListener {

            et_username.setText("")
            iv_close_username.visibility = View.GONE

        }
        iv_close_password.setOnClickListener {

            et_password.setText("")
            iv_close_password.visibility = View.GONE

        }
    }

    fun showSettingsLoginDialog() {


        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_settings_login, null)

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)


        val username = dialogView.findViewById(R.id.dialog_et_username) as TextView
        val password = dialogView.findViewById(R.id.dialog_et_password) as TextView
        val submit = dialogView.findViewById(R.id.dialog_submit) as Button
        val cancel = dialogView.findViewById(R.id.dialog_cancel) as Button


        val b = dialogBuilder.create()
        b.show()

        submit.setOnClickListener {

            Log.e(TAG, "submit " + username.text.toString())
            usernamesettings = username.text.toString()
            doAsync {
                verifyLoginsettings(username.text.toString(), password.text.toString())
            }


        }

        cancel.setOnClickListener {
            b.dismiss()

        }


    }

    fun saveLogin() {
        var myLoginDataList = myLOGINDATADBHELPER.getLoginData("wala lng")

        if (globalSavePasswordBoolean) {
            var credentialExist = false
            for (i in 0 until myLoginDataList.size) {

                Log.e(TAG, "credentials" + myLoginDataList[i].userName + " " + myLoginDataList[i].userPass)
                if (username.equals(myLoginDataList[i].userName)) {
                    credentialExist = true
                }
            }
            if (!credentialExist)
                Log.e(TAG, myLOGINDATADBHELPER.addLoginDATA(username, password).toString())

        } else {
            myLOGINDATADBHELPER.dropTable2()

        }
    }

    fun getUserLevel(username: String) {
        var myUserlevel = "default"
        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/personal_information/$username"
            val mClient = OkHttpClient()
            val mRequest = Request.Builder()
                .url(url)
                .build()

            mClient.newCall(mRequest).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.e("error", "${e.toString()}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    try {
                        val mBody = response?.body()?.string()
                        val mGSON = GsonBuilder().create()
                        val personalInfo = mGSON.fromJson(mBody, PersonalInformation::class.java)
                        runOnUiThread {
                            userLevel = personalInfo.data.userlevel
                            Log.e(TAG, username + " userLevel - " + userLevel)

                            if (userLevel.equals("ADMIN")) {
                                val myIntent = Intent(this@LoginActivity, SettingsAdmin::class.java)
                                startActivity(myIntent)
                            } else {
                                val myIntent = Intent(
                                    this@LoginActivity,
                                    SettingsUsers::class.java
                                )
                                startActivity(myIntent)
                            }

                        }
                    } catch (e: Exception) {
                        Log.e("error", "$e")
                        Toast.makeText(this@LoginActivity, "Enter valid credentials", Toast.LENGTH_SHORT).show()

                    }

                }

            })

        }
    }

    fun verifyApi(foldername: String, ipaddress: String) {
        var myUserlevel = "default"
        doAsync {

            val url = "http://$ipaddress/${foldername}/user_controller/userlist"
            val mClient = OkHttpClient()
            val mRequest = Request.Builder()
                .url(url)
                .build()

            mClient.newCall(mRequest).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.e("error", "${e.toString()}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    try {
                        val mBody = response?.body()?.string()
                        val mGSON = GsonBuilder().create()
                        // val personalInfo         = mGSON.fromJson(mBody, PersonalInformation::class.java)
                        Log.e(TAG, "APICHECK - ${response?.isSuccessful}")
                        if (response!!.isSuccessful) {
                            var myAPIATAY = APIATAY(this@LoginActivity)

                            myAPIATAY.addOFFlineDATA(foldername, ipaddress)
                            Log.e(
                                TAG,
                                myAPIATAY.updateTable(ipaddress, foldername).toString()
                            )
                            Log.e(TAG, myAPIATAY.apilist.toString())
                            ipDialog.dismiss()

                        } else toast("Invalid API !")
                    } catch (e: Exception) {
                        Log.e("error", "$e")
                        Toast.makeText(this@LoginActivity, "Enter valid credentials", Toast.LENGTH_SHORT).show()

                    }

                }

            })

        }
    }

    fun push_userlog2(myAttendance: Attendance) {
        var myLogTime: String = " "
        var myLogDate: String = " "
        Log.e(TAG, myAttendance.toString())
        Log.e(TAG, "giataykudasai")
        doAsync {
            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/save_userlog"
            val mClient = OkHttpClient()
            val formBodyBuilder = MultipartBody.Builder()
            Log.e(TAG, "sulod" + myAttendance.toString())
            var myOfflineDBHELPER = OFFLINELOGDBHELPER(this@LoginActivity)


//            if(myAttendance.userElapsedTime.toLong()>SystemClock.elapsedRealtime())
//            {
//                var difference = myAttendance.userElapsedTime.toInt()  - SystemClock.elapsedRealtime().toInt()
//
//                if(difference>1800000){
//                    var  format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//                    var  format1 = SimpleDateFormat("dd/MM/yyyy")
//                    var  format2 = SimpleDateFormat("HH:mm:ss")
//
//
//                    var mydate =  Date(difference.toLong())
//
//                    Log.e(TAG,"boot time - "+format.format(mydate)+"boot time 1 -"+format1.format(mydate)+"boot time 2 - "+format2.format(mydate))
//
//                    myLogTime = format2.format(mydate)
//                    myLogDate =format1.format(mydate)
//
//
//
//                }
//                else{
//
//                    myLogTime = myAttendance.userLoginTime
//                    myLogDate = myAttendance.userLoginDate
//
//
//
//                }
//
//            }
//            else{


            formBodyBuilder.setType(MultipartBody.FORM)
            formBodyBuilder.addFormDataPart("idno", myAttendance.userName)
            formBodyBuilder.addFormDataPart("action", myAttendance.userAction)
            formBodyBuilder.addFormDataPart("time", myAttendance.userLoginTime)
            formBodyBuilder.addFormDataPart("date", myAttendance.userLoginDate)
            formBodyBuilder.addFormDataPart("long", myAttendance.userLong)
            formBodyBuilder.addFormDataPart("lat", myAttendance.userLat)

            Log.e(TAG, formBodyBuilder.toString())
            filepath = File(getPath(getImageUri(this@LoginActivity, myAttendance.userBitmap)))
            filename = getFileName(this@LoginActivity, getImageUri(this@LoginActivity, myAttendance.userBitmap))

            val requestBody = RequestBody.create(MediaType.parse("image/*"), filepath)
            val fileToUpload = MultipartBody.Part.createFormData("file", filename, requestBody)
            formBodyBuilder.addPart(fileToUpload)

            val formBody = formBodyBuilder.build()

            var builder = Request.Builder()
            builder = builder.url(url)
            builder = builder.post(formBody)
            val request = builder.build()

            mClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val mBody = response?.body()?.string()
                    val mGSON = GsonBuilder().create()

                    uiThread {
                        filepath.delete()
                        Log.i("file", filename)
                        Log.i("mBody", mBody)
                        Log.i("mGSON", mGSON.toString())
                    }
                    runOnUiThread {
                        Log.e(
                            TAG, "Responses - " +
                                    "${response.isSuccessful} " +
                                    "${response.body()} "
                        )
                        if (response.isSuccessful) {
                            displayNotification(myAttendance)


                        }
                        if (response.isSuccessful) {
                            Log.e(
                                TAG,
                                "deleted or not " + myOfflineDBHELPER.deleteFrom(
                                    myAttendance.userName,
                                    myAttendance.userLoginTime
                                )
                            )


                        }

                    }


                    Log.e(TAG, "push is successful - " + response.isSuccessful.toString())

                }
            })

            Log.e(TAG, "Ahakdesu")
        }

    }


    fun verifyLoginsettings(idno: String, password: String) {


        var booleanSuccess: Boolean = false
        var atay = "*ad"

        val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/login/$idno/$password"
        val mClient = OkHttpClient()
        val mRequest = Request.Builder()
            .url(url)
            .build()

        mClient.newCall(mRequest).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("error", "${e.toString()}")
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    val mBody = response?.body()?.string()
                    val mGSON = GsonBuilder().create()
                    val loginFeed = mGSON.fromJson(mBody, Login::class.java)
                    Log.e(TAG, " verify Login - " + loginFeed.message)
                    if (loginFeed.message.equals("Login Successful.")) {

                        getUserLevel(idno)

                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }


                } catch (e: Exception) {
                    Log.e("error", "$e")

                }
            }

        })


    }

    fun verifyLoginb(idno: String, password: String) {
//        var myIntent = Intent(this, Samsung_camera::class.java)
//        startActivityForResult(myIntent, 1)
        var booleanSuccess: Boolean = false
        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/login/$idno/$password"
            val mClient = OkHttpClient()
            val mRequest = Request.Builder()
                .url(url)
                .build()

            mClient.newCall(mRequest).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.e("error", "${e.toString()}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    try {
                        val mBody = response?.body()?.string()
                        val mGSON = GsonBuilder().create()
                        val loginFeed = mGSON.fromJson(mBody, Login::class.java)
                        Log.e(TAG, " verify Login - " + loginFeed.message)
                        runOnUiThread {

                            if (loginFeed.message.equals("Login Successful.")) {
                                Log.e(TAG, "Success Login")
                                checkGeofence()
                                saveLogin()
                                var myIntent = Intent(this@LoginActivity, SurfaceCamera::class.java)
                                startActivityForResult(myIntent, 1)

                                booleanSuccess = true
                            } else {
                                booleanSuccess = false
                                Log.e(TAG, "Fail Login")

                                uiThread {
                                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()

                                }
                            }

                        }
                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }


    fun showLoginDialogSuccess(myAttendanceParce: AttendanceParce, userLogMode: String) {


        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_dialog_log_success, null)

        val mLogPhoto = dialogView.findViewById(R.id.logPhoto) as ImageView
        val mLogName = dialogView.findViewById(R.id.log_name) as TextView
        val mLogAction = dialogView.findViewById(R.id.log_action) as TextView
        val mLogTime = dialogView.findViewById(R.id.log_time) as TextView

        val mLogDate = dialogView.findViewById(R.id.log_date) as TextView
        val mLogLat = dialogView.findViewById(R.id.log_lat) as TextView
        val mLogLong = dialogView.findViewById(R.id.log_long) as TextView
        val mDialogTitle = dialogView.findViewById(R.id.dialogTitle) as TextView
        if (userLogMode.equals("offline")) {
//
            mDialogTitle.setBackgroundColor(resources.getColor(R.color.red))
            mDialogTitle.text = "Offline log to be pushed"
        }




        mLogPhoto.setImageBitmap(myAttendanceParce.userBitmap)
        mLogName.text = "John Doe"
        mLogAction.text = myAttendanceParce.userAction
        mLogTime.text = myAttendanceParce.userLoginTime
        mLogDate.text = myAttendanceParce.userLoginDate
        mLogLat.text = myAttendanceParce.userLat
        mLogLong.text = myAttendanceParce.userLong
        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton("OK") { dialog, whichButton ->
        }
        val b = dialogBuilder.create()

        b.show()


    }


    fun showIPentry() {
        var myBoolean = false
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_api, null)

        val myUserIP = dialogView.findViewById(R.id.dialog_et_ip) as EditText
        val myUserFolder = dialogView.findViewById(R.id.dialog_et_folder) as EditText
        val mySubmitIP = dialogView.findViewById(R.id.submit_ip) as Button

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)

        ipDialog = dialogBuilder.create()

        ipDialog.show()


        mySubmitIP.setOnClickListener {

            // verifyLoginsettings()
            verifyApi(myUserFolder.text.toString(), myUserIP.text.toString())


        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivity result")
        Log.e(TAG, "$requestCode $resultCode")
        if (resultCode == SurfaceCamera.REQUEST_CODE) {
            var userLogMode = data!!.getStringExtra("surface_mode")
            var userAttendance = data!!.getParcelableExtra<AttendanceParce>("userAttendance")
            Log.e(TAG, "request " + userAttendance.userAction + userAttendance.userLoginDate)
            showLoginDialogSuccess(userAttendance, userLogMode)


        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        System.exit(0)

        return super.onKeyDown(keyCode, event)

    }

    fun askPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
///method to get Images

            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(
                        applicationContext,
                        "Your Permission is needed to get access the camera",
                        Toast.LENGTH_LONG
                    ).show()
                }
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.CAMERA

                    ), 0
                )
            }
        }
    }

    fun checkGeofence() {
            Log.e(TAG,"check if user is within geofence")

        var userLocation = Location("User Location")
        userLocation.latitude = lat.toDouble()
        userLocation.longitude = long.toDouble()
        for( i in 0 until globalGeofenceList.data.size){
        var geofenceLocation = Location("Geofence")
            geofenceLocation.latitude = globalGeofenceList.data[i].latitude.toDouble()
            geofenceLocation.longitude = globalGeofenceList.data[i].longitude.toDouble()
            Log.e(TAG,geofenceLocation.latitude.toString()+"   JY  "+geofenceLocation.longitude.toString())
            var distance = userLocation.distanceTo(geofenceLocation)/1000
            var distanceFOrmatted = String.format("%.2f", distance)
                Log.e(TAG,globalGeofenceList.data[i].workplace_name + " $distanceFOrmatted ")
        }


    }


    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
doAsync {
    val Radius = 6371// radius of earth in Km
    val lat1 = StartP.latitude
    val lat2 = EndP.latitude
    val lon1 = StartP.longitude
    val lon2 = EndP.longitude
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
            * Math.sin(dLon / 2))
    val c = 2 * Math.asin(Math.sqrt(a))
    val valueResult = Radius * c
    val km = valueResult / 1
    val newFormat = DecimalFormat("####")
    val kmInDec = Integer.valueOf(newFormat.format(km))
    val meter = valueResult % 1000
    val meterInDec = Integer.valueOf(newFormat.format(meter))
    Log.i(
        "Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec
    )
    var distance = Radius * c
    Log.e(TAG, distance.toString() + " CalculateByDISTANCE")

}
        return 123123321.123123

    }
    fun getAddress(location: Location) {
        //Log.e(TAG, "getting address")
   doAsync {
            val geocoder: Geocoder = Geocoder(this@LoginActivity, Locale.getDefault())
            val addresses: List<Address>

            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            var postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName
            val locale = addresses[0].locale
            val subAdminArea = addresses[0].subAdminArea
            val premises = addresses[0].premises
            val countrycode = addresses[0].countryCode
            val subLocality = addresses[0].subLocality
            val adminArea = addresses[0].adminArea
            val addressline = addresses[0].getAddressLine(0)

//            Log.e(
//                TAG, "City: $city \n   " +
//                        "state: $state \n" +
//                        "country: $country \n" +
//                        "postalCode: $postalCode \n" +
//                        "knownName: $knownName \n" +
//                        "locale: $locale \n" +
//                        "subAdminArea: $subAdminArea \n" +
//                        "premises: $premises \n" +
//                        "countrycode: $countrycode\n" +
//                        "subLocality: $subLocality" +
//                        "adminArea: $adminArea"
//            )
            var fulladress = addresses[0].toString()
       //        Log.e(TAG, "$addressline - Addressline  $fulladress ")
            if (postalCode == null)
                postalCode = " "

            var returnAddress = "$knownName, $city, $subAdminArea, $postalCode, $country"
            uiThread {
                tv_adress.text = returnAddress
            }
        }

    }

    fun getLocation() {

Log.e(TAG,"Getting Location")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            @SuppressLint("SimpleDateFormat")
            override fun onLocationChanged(location: Location) {
                Log.e(TAG,"GPS TIME - "+location.time.toString())
                Log.e(TAG,"GPS TIME - "+location.time.toString())
                Log.e(TAG,"GPS TIME - "+location.time.toString())


                var  format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
var date = Date(location.time)

                var milis = location.elapsedRealtimeNanos / 1000000

                Log.e(TAG,"Altitude "+ location.altitude.toString())
                Log.e(TAG, "locatio"+location.accuracy.toShort())
                Log.e(TAG," ${milis}-Last bootime"+location.elapsedRealtimeNanos.toString()+" \n last boot time 2 ${SystemClock.elapsedRealtime()}\n")


                elapsedTime = milis.toString()


              var mydate =  Date(System.currentTimeMillis()
                        - milis
              )
                Log.e(TAG,"boot time - "+format.format(mydate)+"  current time - "+getCurrentTime())


                    try {







                    getAddress(location)
                    var mLongtitude = location.longitude.toString()
                    var mLatitude = location.latitude.toString()
                    tv_lat.text = mLatitude
                    tv_long.text = mLongtitude
                    lat = mLatitude
                    long = mLongtitude




                } catch (e: Exception) {
                    Log.e(TAG, "Exception raised $e")
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
            // locationManager.!!requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f,);

        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission
                        .ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            } else {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                // locationManager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                Log.e(TAG, "this part")
            }

        }
    }


    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")

        dismissDialogwhenGPSenabled()

        // mGPSDialog.dismiss()
    }

    override fun onPause() {
        super.onPause()
        //mGPSDialog.dismiss()
        Log.e(TAG, "onPause")

        dismissDialogwhenGPSenabled()

    }

    override fun onDestroy() {
        super.onDestroy()
        //mGPSDialog.dismiss()

        Log.e(TAG, "onDestroy")

        dismissDialogwhenGPSenabled()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

            }

        }

    }

    private fun checkGPSIsEnabled() {
        initGpsDisabledDialog(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDialog()
        } else {
            dissmissGPSDialog()
        }
    }

    private fun dismissDialogwhenGPSenabled() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dissmissGPSDialog()
        }
    }

    private fun showGPSDialog() {
        if (!mGPSDialog.isShowing()) {
            mGPSDialog.show()
        }
    }

    private fun dissmissGPSDialog() {
        mGPSDialog.dismiss()
    }

    internal fun initGpsDisabledDialog(context: Context) {

        val builder = AlertDialog.Builder(context)
        val action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Please turn on GPS to find current location."

        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "SETTINGS"
            ) { d, id ->
                startActivity(Intent(action))
                d.dismiss()
            }

        mGPSDialog = builder.create()
        mGPSDialog.setOnShowListener {
            val btnPossitive = mGPSDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            btnPossitive.setOnClickListener { startActivity(Intent(action)) }
        }
    }


    fun setupDigitalClock() {

        doAsync {
            var myDate = getCurrentDate()

            for (i in 0 until 9008941372) {


                Thread.sleep(1000)
                //  Log.e(TAG, getCurrentTime()+getCurrentDate())
                var myTime = getCurrentTime()
                var myString = " $myDate\n  $myTime "


                uiThread {


                    tv_digitalClock_loginDate.text = myDate
                    tv_digitalClock_loginTime.text = myTime

                }


            }

        }
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("h:mm:ss a")
        return df.format(c.time)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MMM dd, yyyy")
        return df.format(c.time)
    }

    fun getLoginData() {
        var myLoginDataList = myLOGINDATADBHELPER.getLoginData("wala lng")

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            globalSavePasswordBoolean = isChecked

        }

        if (myLoginDataList.isNotEmpty()) {
            var lastelement = myLoginDataList.size - 1
            et_username.setText(myLoginDataList[lastelement].userName)
            et_password.setText(myLoginDataList[lastelement].userPass)
//
//            iv_close_username.visibility = View.VISIBLE
//            iv_close_password.visibility = View.VISIBLE
            for (i in 0 until myLoginDataList.size) {
                Log.e(TAG, "credentials"+myLoginDataList[i].userName +" "+ myLoginDataList[i].userPass)
            }
            checkBox.isChecked = true
        } else
            checkBox.isChecked = false


    }
    fun displayNotification(myAttendance: Attendance) {
        Log.e("Notification test", "Succeed")
//        val intent = Intent(activity!!, HomeActivity::class.java).apply {
//            var flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
        notif_id += 1
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(activity!!, 0, intent, 0)
//

        val normal_layout = RemoteViews(packageName, R.layout.notif_offlinelog_normal)
        val expanded_layout = RemoteViews(packageName, R.layout.notif_offlinelog_expanded)

        expanded_layout.setImageViewBitmap(R.id.iv_notif_image,myAttendance.userBitmap)
        expanded_layout.setTextViewText(R.id.notif_id,myAttendance.userName)
        expanded_layout.setTextViewText(R.id.notif_action,myAttendance.userAction)
        expanded_layout.setTextViewText(R.id.notif_date_time,myAttendance.userLoginTime)

        var customChannel = myAttendance.userName+notif_id.toString()
        createNotificationChannel(customChannel)

        val builder = NotificationCompat.Builder(this, customChannel)
        builder.setSmallIcon(R.drawable.timeruler_the_logo)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(normal_layout)
        builder.setCustomBigContentView(expanded_layout)
      //  builder.setContentIntent(pendingIntent)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(myAttendance.userName.toInt()+notif_id, builder.build())

    }
    fun getPath(uri: Uri): String {
        var cursor = this.contentResolver.query(uri, null, null, null, null)
        cursor.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = this.contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf<String>(document_id),
            null
        )
        cursor.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor!!.moveToFirst()) {
                    result = cursor!!.getString(cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null) {
                    cursor!!.close()
                }
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)

        return Uri.parse(path)
    }

    @SuppressLint("NewApi")
    internal fun createNotificationChannel(Channel: String) {
        Log.e("CreateNotification", "Created")
        val name = "personal notification"
        val description = "include all notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Channel, name, importance)
        channel.description = description
        val notificationManager = getSystemService<NotificationManager>(NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)

    }
    private fun pushOfflineData(serverTime:Long) {
        var myOfflineDBHELPER = OFFLINELOGDBHELPER(this)

        var myOfflineDataList = myOfflineDBHELPER.offlineDataList
        Log.e(TAG,myOfflineDataList.toString())
        if(myOfflineDataList.size !=0) {

            for (i in 0 until myOfflineDataList.size) {

                trialFunc(myOfflineDataList[i],serverTime)
                Log.e(TAG, "OFFLINEDATA - " + myOfflineDataList.get(i).userElapsedTime + "  " + myOfflineDataList.get(i).userName + " " + myOfflineDataList.get(i).userLoginDate + " " + myOfflineDataList.get(i).userLoginTime + " " + myOfflineDataList.get(i).userBitmap
                )
            }

        }



    }
    fun trialFunc(myAttendance:Attendance,myServerTime:Long){
        Log.e(TAG,"trialFunc")
lateinit var myLogDate:String
        lateinit var myLogTime:String

        var difference =   SystemClock.elapsedRealtime().toInt()  -  myAttendance.userElapsedTime.toInt()
//        if(difference>1800000){
            Log.e(TAG,"IF")

        //    var  format = SimpleDateFormat("MMM dd, yyyy h:mm:ss a")
            var  format1 = SimpleDateFormat("MMM dd, yyyy")
            var  format2 = SimpleDateFormat("h:mm:ss a")

            var mydate =  Date(myServerTime - difference.toLong())


        myLogTime = format2.format(mydate)
        myLogDate = format1.format(mydate)

        Log.e(TAG,"Current time -"+myAttendance.userLoginTime+" comapre $myLogTime "+"\n Current Date -"+myAttendance.userLoginDate+" compare $myLogDate ")


        myAttendance.userLoginTime = myLogTime
        myAttendance.userLoginDate = myLogDate

push_userlog2(myAttendance)


    }



    private fun isNetworkAvailable(): Boolean {
        lateinit var connectivityManager: ConnectivityManager
        lateinit var activeNetworkInfo: NetworkInfo

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected


    }

    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.e(TAG,intent.action)
            doAsync {
             uiThread {
                 constraint_connection.setBackgroundColor(resources.getColor(R.color.green))
                 tv_connection.text = "Waiting for connection"
                 progressBar.visibility = View.VISIBLE
                 constraint_connection.visibility = View.VISIBLE

             }
            }
            Log.e(TAG,"wifi switched")
            val action = intent.action
            if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                var info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                val connected = info.isConnected
                //call your method
                if (connected) {
                    Log.e(TAG, "Have Wifi")
                    haveWifi = true
                    login_mode = "online"
                    doAsync {
                        Thread.sleep(2000)

                    }


                    doAsync {
                        try {
                            Thread.sleep(4000)
                            runOnUiThread {
                                constraint_connection.visibility = View.GONE
                            }
                        } catch (e: Exception) {

                        }

                    }
                } else {
                    haveWifi= false
                    doAsync {
                        Log.e(TAG, "No Wifi")
                        login_mode = "offline"


                        try {

                            Thread.sleep(3000)
                            runOnUiThread {
                                if(!haveWifi) {
                                    progressBar.visibility = View.GONE
                                    tv_connection.text = "No connection"
                                    constraint_connection.setBackgroundColor(resources.getColor(R.color.black))
                                    constraint_connection.visibility = View.VISIBLE
                                } }
                        } catch (e: Exception) {

                        }

                    }




                }
            }


        }
    }


    fun textListener() {
        et_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(mEdit: Editable) {
                if (et_username.text.toString()!=null) {

                    if (et_username.text.toString().isNotEmpty()) {
                        iv_close_username.visibility = View.VISIBLE
                        Log.e(TAG, "afterText")
                    } else if (et_username.text.toString().equals(""))
                        iv_close_username.visibility = View.GONE

                }

            }
        })

        et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(mEdit: Editable) {
                if (et_password.text.toString()!=null) {
                    if (et_password.text.toString().isNotEmpty()) {
                        iv_close_password.visibility = View.VISIBLE
                        Log.e(TAG, "afterText")
                    }
                }
            }

        })

    }
    fun stringToDate(){
        val currentServerDate = globalServerTime.message.date+" "+globalServerTime.message.time
        val format = SimpleDateFormat("MMM dd,yyyy h:mm:ss a")
        try {
            val date = format.parse(currentServerDate)
            System.out.println(date)
            Log.e(TAG,"current date server - ${date.toString()}"+"\n" +" milis - "+
                    date.time.toString()+"---"+System.currentTimeMillis())
                globalServertimemilis = date.time.toString()
            pushOfflineData(date.time)
        } catch (e: ParseException) {
            e.printStackTrace()

            Log.e(TAG,"Atay ra ahh")
        }

    }
    fun getServerTime() {


        var booleanSuccess:Boolean=false
        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/current_time"
            val mClient = OkHttpClient()
            val mRequest = Request.Builder()
                .url(url)
                .build()

            mClient.newCall(mRequest).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.e("error", "${e.toString()}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    try {
                        val mBody = response?.body()?.string()
                        val mGSON = GsonBuilder().create()
                        val currentTimeListFeed = mGSON.fromJson(mBody, CurrentTimeList::class.java)
                        //    val currentTIme = mGSON.fromJson(mBody,CurrentTime::class.java)
                        Log.e(TAG, " verify Login - ${currentTimeListFeed.message}"+ response!!.isSuccessful.toString())
                        // Log.e(TAG,currentTIme.toString()+response!!.isSuccessful)
                        globalServerTime = currentTimeListFeed
                        stringToDate()

                    } catch (e: java.lang.Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }
    fun getGeofences() {


        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/geofence_controller/geofence_entries"
            val mClient = OkHttpClient()
            val mRequest = Request.Builder()
                .url(url)
                .build()

            mClient.newCall(mRequest).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.e("error", "${e.toString()}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    try {
                        val mBody = response?.body()?.string()
                        val mGSON = GsonBuilder().create()
                        val geofenceListFeed = mGSON.fromJson(mBody, GeofenceModelList::class.java)
                        Log.e(TAG, " verify Login - ${geofenceListFeed.data.size}")
                        globalGeofenceList = geofenceListFeed

                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }


}
