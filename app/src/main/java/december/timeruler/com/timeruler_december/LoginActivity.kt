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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import december.timeruler.com.timeruler_december.DBHELPERS.LOGINDATADBHELPER
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"
    companion object {
        lateinit var username:String
        lateinit var password:String
    }

    private lateinit var myLOGINDATADBHELPER: LOGINDATADBHELPER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        myLOGINDATADBHELPER = LOGINDATADBHELPER(this)
//        savedInstanceState ?: supportFragmentManager.beginTransaction()
//            .replace(R.id.container, Camera2BasicFragment.newInstance())
//            .commit()
        askPermissions()
        var myJavaFunctions = JavaFunctions()
        //myJavaFunctions.getLocationWhenOffline(this)
        btn_login.setOnClickListener {
            username = et_username.text.toString()
            password = et_password.text.toString()

            Log.e(TAG,myLOGINDATADBHELPER.addLoginDATA(username, password).toString())
            var myIntent = Intent(this, SurfaceCamera::class.java)
            startActivity(myIntent)

        }


    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

        Log.e(TAG,"ASDASD")

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG,"$requestCode $resultCode")
        Log.e(TAG,"ASDASD")
        if (resultCode==SurfaceCamera.REQUEST_CODE) {
         //   OfflineData myOfflineData = data.getExtras().getParcelable("OFFLINEDATA");
            var resultAttendance:AttendanceParce = data!!.extras.getParcelable("userAttendance")

            Log.e(TAG,"result attendance - ${resultAttendance.userName} ${resultAttendance.userAction}")

        }

    }
    fun askPermissions(){

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
                        //Manifest.permission.ACCESS,
                        Manifest.permission.CAMERA

                    ), 0
                )
            }
        }
    }

}
