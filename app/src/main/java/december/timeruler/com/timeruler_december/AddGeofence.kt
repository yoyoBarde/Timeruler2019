package december.timeruler.com.timeruler_december

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.GsonBuilder

import december.timeruler.com.timeruler_december.Adapters.GeofenceAdapter

import december.timeruler.com.timeruler_december.Model.GeofenceModel
import december.timeruler.com.timeruler_december.Model.GeofenceModelList
import december.timeruler.com.timeruler_december.Model.GeofenceModelp
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_add_geofence.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.HashMap
import android.view.Gravity
import android.view.WindowManager
import december.timeruler.com.timeruler_december.Adapters.PagerAdapter
import december.timeruler.com.timeruler_december.Fragments.FragmentLocationMethod1
import december.timeruler.com.timeruler_december.Model.Dismisscallback


class AddGeofence : AppCompatActivity(){


    var GeofenceList = ArrayList<GeofenceModelp>()
    val TAG = "AddGeofence"

    var count = 0
    var myAdapter:GeofenceAdapter ?= null
    lateinit var b:AlertDialog
    lateinit var newFragment:DialogAddGeofence
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_geofence)
//        getGeofences()
//        newFragment = DialogAddGeofence().newInstance()
//
//        imageView3.setOnClickListener {
//            finish()
//        }
        val viewPager = findViewById<ViewPager>(R.id.viewPager_payslip)
        val tabLayout =findViewById<TabLayout>(R.id.tabLayout_payslip)

        // attach tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager)

        val adapter = PagerAdapter(supportFragmentManager)
        // add your fragments
        adapter.addFragments(FragmentAddGeofence(), "Add Geofence")
        adapter.addFragments(FragmentLocationMethod1(), "Restrict User")
        viewPager.adapter = adapter



        button_add_geofence.setOnClickListener {
    button_add_geofence.setTextColor(resources.getColor(R.color.whiteop50))
    button_restrict_user.setTextColor(resources.getColor(R.color.white))
    supportFragmentManager.beginTransaction().replace(R.id.frameGeofence,FragmentAddGeofence()).commit()


}

button_restrict_user.setOnClickListener {

    button_add_geofence.setTextColor(resources.getColor(R.color.white))
    button_restrict_user.setTextColor(resources.getColor(R.color.whiteop50))
}




    }




//    override fun dismissListener() {
//        push_userlog(GeofenceModel(DialogAddGeofence.placec,DialogAddGeofence.addressc,DialogAddGeofence.latc,DialogAddGeofence.longc,"defaultAdmin"," gwapo"))
////        GeofenceList.add(GeofenceModelp(DialogAddGeofence.placec,DialogAddGeofence.addressc,DialogAddGeofence.latc,DialogAddGeofence.longc,"defaultAdmin","defaultID"))
////        myAdapter = GeofenceAdapter(this, GeofenceList)
////        val layoutManager = LinearLayoutManager(this)
////        recyclerViewGeofence.layoutManager = layoutManager
////        recyclerViewGeofence.itemAnimator = DefaultItemAnimator()
////        recyclerViewGeofence.adapter = myAdapter!!
////            myAdapter!!.notifyDataSetChanged()
//
//
//        Log.e("AddGeofence","DIsmiss "+GeofenceList.size )
//
//        if(newFragment!=null) {
//            newFragment.dismiss()
//
//        }
//
//    }
//
//    private fun showEditDialog() {
//
//        newFragment.show(supportFragmentManager,"dialog${count.toString()}")
//        newFragment.clearFindViewByIdCache()
//        count++
//    }
//    fun push_geofence(myGeoModel: GeofenceModel){
//        Log.e(TAG,"pushing geofence")
//
//        doAsync {
//            val urlInterested = "http://10.224.1.160/${SurfaceCamera.APINAME}/geofence_controller/geofence_entries"
//            val mClientInterested = OkHttpClient()
//            val params = HashMap<String, String>()
//
//            params["workplace"] = myGeoModel.workPlace
//            params["address"] = myGeoModel.workAddress
//            params["lat"] = myGeoModel.latitude
//            params["long"] = myGeoModel.longitude
//            params["admin"] = "ASD"
//
//
//            val jsonObject = JSONObject(params)
//            val JSON = MediaType.parse("application/json; charset=utf-8")
//            val body = RequestBody.create(JSON, jsonObject.toString())
//
//            val mRequestInterested = Request.Builder()
//                .url(urlInterested)
//                .post(body)
//                .build()
//            mClientInterested.newCall(mRequestInterested).enqueue(object : Callback{
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.e(TAG,e.toString())
//
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//
//                    var res = response?.body()?.toString()
//
//                    Log.e(TAG,"Responses - " +
//                            "${response.isSuccessful} " +
//                            "${response.body()} ")
//                    Log.e(TAG,"res " + res)
//
//
//                }
//            })
//
//
//
//
//        }
//
//    }
//    fun push_userlog(model:GeofenceModel){
//
//        Log.e(TAG,"${model.latitude}${model.workAddress}${model.longitude}")
//
//        doAsync {
//            val urlInterested = "http://10.224.1.160/payrulerupdated-api/geofence_controller/save_geofence"
//            val mClientInterested = OkHttpClient()
//            val params = HashMap<String, String>()
//
//
//            params["workplace"] =  model.workPlace
//            params["address"] = model.workAddress
//            params["lat"] =  model.latitude
//            params["long"] = model.longitude
//            params["admin"] = model.admin
//
//
//            val jsonObject = JSONObject(params)
//            val JSON = MediaType.parse("application/json; charset=utf-8")
//            val body = RequestBody.create(JSON, jsonObject.toString())
//
//            val mRequestInterested = Request.Builder()
//                .url(urlInterested)
//                .post(body)
//                .build()
//            mClientInterested.newCall(mRequestInterested).execute()
//
//            var response = mClientInterested.newCall(mRequestInterested).execute()
//            Log.e(TAG,"Responses - " +
//                    "${response.isSuccessful} " +
//                    "${response.body()} ")
//            getGeofences()
//
//
//
//
//
//
//
//
//
//
//        }
//
//    }
//
//    fun getGeofences() {
//
//
//        doAsync {
//
//            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/geofence_controller/geofence_entries"
//            val mClient = OkHttpClient()
//            val mRequest = Request.Builder()
//                .url(url)
//                .build()
//
//            mClient.newCall(mRequest).enqueue(object : Callback {
//                override fun onFailure(call: Call?, e: IOException?) {
//                    Log.e("error", "${e.toString()}")
//                }
//
//                override fun onResponse(call: Call?, response: Response?) {
//                    try {
//                        val mBody = response?.body()?.string()
//                        val mGSON = GsonBuilder().create()
//                        val geofenceListFeed = mGSON.fromJson(mBody, GeofenceModelList::class.java)
//                        Log.e(TAG, " verify Login - ${geofenceListFeed.data.size}")
//
//                        uiThread {
//                            GeofenceList = geofenceListFeed.data
//                            myAdapter = GeofenceAdapter(this@AddGeofence, geofenceListFeed.data)
//                            val layoutManager = LinearLayoutManager(this@AddGeofence)
//                            recyclerViewGeofence.layoutManager = layoutManager
//                            recyclerViewGeofence.itemAnimator = DefaultItemAnimator()
//                            recyclerViewGeofence.adapter = myAdapter!!
//
//
//                        }
//                    } catch (e: Exception) {
//                        Log.e("error", "$e")
//
//                    }
//                }
//
//            })
//
//        }
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        Log.e(TAG,"onResume")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.e(TAG,"onDestroy")
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//            Log.e(TAG,"onPause")
//
//    }
//





}
