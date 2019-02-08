package december.timeruler.com.timeruler_december


import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.Adapters.GeofenceAdapter
import december.timeruler.com.timeruler_december.Adapters.PagerAdapter
import december.timeruler.com.timeruler_december.Fragments.FragmentLocationMethod1
import december.timeruler.com.timeruler_december.Model.Dismisscallback
import december.timeruler.com.timeruler_december.Model.GeofenceModel
import december.timeruler.com.timeruler_december.Model.GeofenceModelList
import december.timeruler.com.timeruler_december.Model.GeofenceModelp
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_fragment_add_geofence.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.IOException


class FragmentAddGeofence : Fragment(),Dismisscallback {


    var GeofenceList = ArrayList<GeofenceModelp>()
    val TAG = "AddGeofence"

    var count = 0
    lateinit var b: AlertDialog
    lateinit var newFragment:DialogAddGeofence
    lateinit var recyclerView:RecyclerView
    lateinit var button_add_geofence: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
               newFragment = DialogAddGeofence().newInstance()



        recyclerView = view.findViewById(R.id.recyclerViewGeofence) as RecyclerView


        getGeofences()

        button_add_geofence = view.findViewById(R.id.button_addGeofence) as Button

        button_add_geofence.setOnClickListener {

            showEditDialog()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_add_geofence, container, false)
    }



        fun getGeofences() {
            var myLayoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL,false)

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

                        uiThread {
                            GeofenceList = geofenceListFeed.data
                           var  myAdapter = GeofenceAdapter( geofenceListFeed.data)
                          //  val layoutManager = myLayoutManager
                            recyclerView!!.layoutManager = myLayoutManager
                            recyclerView!!.itemAnimator = DefaultItemAnimator()
                            recyclerView!!.adapter = myAdapter!!


                        }
                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }
    override fun dismissListener() {
        push_userlog(GeofenceModel(DialogAddGeofence.placec,DialogAddGeofence.addressc,DialogAddGeofence.latc,DialogAddGeofence.longc,"defaultAdmin","defaultID"))
    }
        private fun showEditDialog() {

        newFragment.show(activity!!.supportFragmentManager,"dialog${count.toString()}")
        newFragment.clearFindViewByIdCache()
        count++

    }

        fun push_userlog(model: GeofenceModel){

        Log.e(TAG,"${model.latitude}${model.workAddress}${model.longitude}")

        doAsync {
            val urlInterested = "http://10.224.1.160/payrulerupdated-api/geofence_controller/save_geofence"
            val mClientInterested = OkHttpClient()
            val params = HashMap<String, String>()


            params["workplace"] =  model.workPlace
            params["address"] = model.workAddress
            params["lat"] =  model.latitude
            params["long"] = model.longitude
            params["admin"] = model.admin


            val jsonObject = JSONObject(params)
            val JSON = MediaType.parse("application/json; charset=utf-8")
            val body = RequestBody.create(JSON, jsonObject.toString())

            val mRequestInterested = Request.Builder()
                .url(urlInterested)
                .post(body)
                .build()
            mClientInterested.newCall(mRequestInterested).execute()

            var response = mClientInterested.newCall(mRequestInterested).execute()
            Log.e(TAG,"Responses - " +
                    "${response.isSuccessful} " +
                    "${response.body()} ")


                getGeofences()

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

    override fun onResume() {
        super.onResume()
        Log.e(TAG,"onResume")

    }


}
