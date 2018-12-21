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
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import december.timeruler.com.timeruler_december.DBHELPERS.LOGINDATADBHELPER
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.lang.Exception
import java.util.*
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_camera_source.*
import org.jetbrains.anko.uiThread
import java.net.URL
import java.text.SimpleDateFormat


class LoginActivity : AppCompatActivity() , SurfaceCamera.PassData {
    override fun passstring(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
    }

    val TAG = "LoginActivity"

    companion object {
        lateinit var username: String
        lateinit var password: String
    }
    lateinit var mGPSDialog: AlertDialog
    var globalSavePasswordBoolean:Boolean = false
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private lateinit var myLOGINDATADBHELPER: LOGINDATADBHELPER

    private var client: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupDigitalClock()
        myLOGINDATADBHELPER = LOGINDATADBHELPER(this)

         var intentFilter =  IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver,intentFilter)
        getLoginData()
        textListener()
//        savedInstanceState ?: supportFragmentManager.beginTransaction()
//            .replace(R.id.container, Camera2BasicFragment.newInstance())
//            .commit()
        checkGPSIsEnabled()
            askPermissions()
        getLocation()


        var javaLocation = JavaToKotlin()
        javaLocation.getLocationWhenOffline(this)
        //myJavaFunctions.getLocationWhenOffline(this)
        btn_login.setOnClickListener {
            username = et_username.text.toString()
            password = et_password.text.toString()
            if (globalSavePasswordBoolean)
            Log.e(TAG, myLOGINDATADBHELPER.addLoginDATA(username, password).toString())
            else
                myLOGINDATADBHELPER.dropTable2()
            var myIntent = Intent(this, SurfaceCamera::class.java)
            startActivityForResult(myIntent,1)

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG,"onActivity result")
        Log.e(TAG,"$requestCode $resultCode")
        if (resultCode==SurfaceCamera.REQUEST_CODE) {
            Log.e(TAG, "request")

            var userAttendance = data!!.getParcelableExtra<AttendanceParce>("userAttendance")
            Log.e(TAG,userAttendance.userAction + userAttendance.userLoginDate)
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
    fun getAddress(location:Location ){

      Log.e(TAG,"getting address")
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

            Log.e(
                TAG, "City: $city \n   " +
                        "state: $state \n" +
                        "country: $country \n" +
                        "postalCode: $postalCode \n" +
                        "knownName: $knownName \n" +
                        "locale: $locale \n" +
                        "subAdminArea: $subAdminArea \n" +
                        "premises: $premises \n" +
                        "countrycode: $countrycode\n" +
                        "subLocality: $subLocality" +
                        "adminArea: $adminArea"
            )
            var fulladress = addresses[0].toString()
            Log.e(TAG, "$addressline - Addressline  $fulladress ")
            if(postalCode == null)
                postalCode = " "

            var returnAddress = "$knownName, $city, $subAdminArea, $country, $postalCode"
            uiThread { tv_adress . text = returnAddress
        }
        }

    }
    fun getLocation() {

        Log.e(TAG, "ASDADASDAJDISAjDAJSIOSJDIASJDOAJDIAJGAGO kaba")
        Log.e(TAG, "yoyobarde")


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.e(TAG, location.toString())
                Log.e(TAG, "Longtitude - " + location.latitude + "  Latitude - " + location.longitude)

                try {
                    getAddress(location)
                    var mLongtitude = location.longitude.toString()
                    var mLatitude = location.latitude.toString()
                    tv_lat.text = mLatitude
                    tv_long.text = mLongtitude
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
        Log.e(TAG,"onResume")

        dismissDialogwhenGPSenabled()

        // mGPSDialog.dismiss()
    }

    override fun onPause() {
        super.onPause()
        //mGPSDialog.dismiss()
        Log.e(TAG,"onPause")

        dismissDialogwhenGPSenabled()

    }

    override fun onDestroy() {
        super.onDestroy()
        //mGPSDialog.dismiss()

        Log.e(TAG,"onDestroy")

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
    private fun dismissDialogwhenGPSenabled(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dissmissGPSDialog()
            Log.e(TAG,"dismissDialog")
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




    fun setupDigitalClock(){

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

    fun getLoginData(){
        var myLoginDataList = myLOGINDATADBHELPER.getLoginData("wala lng")

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            globalSavePasswordBoolean =isChecked

        }

        if(myLoginDataList.isNotEmpty()) {
            var lastelement = myLoginDataList.size - 1
            et_username.setText(myLoginDataList[lastelement].userName)
            et_password.setText(myLoginDataList[lastelement].userPass)

            iv_close_username.visibility = View.VISIBLE
            iv_close_password.visibility = View.VISIBLE
            for(i in 0 until myLoginDataList.size){
                Log.e(TAG,myLoginDataList[i].userName+myLoginDataList[i].userPass)
            }
            checkBox.isChecked = true
        }
        else
            checkBox.isChecked = false




    }

    private fun isNetworkAvailable(): Boolean {
        lateinit var connectivityManager:ConnectivityManager
        lateinit var activeNetworkInfo:NetworkInfo

             connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected



    }
    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //    val netInfo = conMan.activeNetworkInfo

             val action = intent.action
            Log.e(TAG,"wifiReceiver")

    if(action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
        var info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
//        var info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        val connected = info.isConnected
        //call your method
        if (connected) {

            Log.e(TAG, "Have wifi")
            constraint_connection.setBackgroundColor(resources.getColor(R.color.green))
            tv_connection.text = "Waiting for connection"
            progressBar.visibility = View.VISIBLE
            doAsync {
                try {
                    Thread.sleep(5000)
                    runOnUiThread {
                        constraint_connection.visibility = View.GONE
                    }
                } catch (e: Exception) {

                }

            }
        }
        else {

            Log.e(TAG, "No Wifi")

            progressBar.visibility = View.GONE
            tv_connection.text = "No connection"
            constraint_connection.setBackgroundColor(resources.getColor(R.color.black))
            constraint_connection.visibility = View.VISIBLE
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
                if (et_username.text.toString().isNotEmpty()) {
                    iv_close_username.visibility = View.VISIBLE
                    Log.e(TAG, "afterText")
                }
                else
                    iv_close_username.visibility = View.GONE

            }


        })

        et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(mEdit: Editable) {
                if (et_password.text.toString().isNotEmpty()) {
                    iv_close_password.visibility = View.VISIBLE
                    Log.e(TAG, "afterText")
                }
                else
                    iv_close_password.visibility = View.GONE

            }


        })

        et_username.setOnClickListener {

            et_username.setText("")
            iv_close_username.visibility = View.GONE

        }
        et_password.setOnClickListener {

            et_password.setText("")
            iv_close_password.visibility = View.GONE

        }
    }
}
