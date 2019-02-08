package december.timeruler.com.timeruler_december.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import december.timeruler.com.timeruler_december.LoginActivity
import december.timeruler.com.timeruler_december.Model.GeofenceModel
import december.timeruler.com.timeruler_december.Model.GeofenceModelp
import december.timeruler.com.timeruler_december.Model.UserLogs
import december.timeruler.com.timeruler_december.R
import kotlinx.android.synthetic.main.fragment_add_geofence.view.*
import kotlinx.android.synthetic.main.model_geofences.view.*
import kotlinx.android.synthetic.main.model_userlogs.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.util.HashMap


class GeofenceAdapter( var userlogslist : ArrayList<GeofenceModelp>) : RecyclerView.Adapter<GeofenceAdapter.ViewHolder>(){

val TAG ="GeofenceAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):

            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_geofences,parent,false))


    override fun getItemCount(): Int  = userlogslist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var myGeofence = userlogslist[position]

        holder.lat.text = myGeofence.latitude
        holder.long.text = myGeofence.longitude
        holder.workPlacename.text = myGeofence.workplace_name
        holder.address.text=myGeofence.workplace_address
        holder.buttonClose.setOnClickListener {
            deleteGeofenceDialog(position,holder)



        }





    }

    fun deleteGeofenceDialog(positioned: Int,holder: ViewHolder) {

        val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
        dialogBuilder.setCancelable(false)


        dialogBuilder.setTitle("Are you sure?")
        dialogBuilder.setMessage("Delete Geofence")

        dialogBuilder.setNegativeButton("Cancel") { dialog, whichButton ->
        }
        dialogBuilder.setPositiveButton("Delete"){dialog,whichButton ->
            deleteGeofence(userlogslist[positioned].workplace_id)

            userlogslist.removeAt(positioned)
            notifyDataSetChanged()
            notifyItemRemoved(positioned)
            notifyItemChanged(positioned)
            Log.e("deleteGeofenceDialog","removed $positioned")
        //    deleteGeofence(userlogslist[positioned].workplace_id)

        }
        val b = dialogBuilder.create()
        b.show()



    }
fun notifySetCHange(myModel:GeofenceModelp){

    this.userlogslist.add(myModel)
    notifyDataSetChanged()

}

    fun deleteGeofence(idno:String){
        Log.e(TAG,idno+" --- ID delete geofence")

        doAsync {
            val urlInterested = "http://10.224.1.160/payrulerupdated-api/geofence_controller/delete_geofence"
            val mClientInterested = OkHttpClient()
            val params = HashMap<String, String>()
            params["id"] =  idno
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






        }

    }



    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){

//        val userAction = view.userAction!!
//        val userLogDate = view.userLogData!!
//        val constraintBackground = view.constraint_background!!

        var lat = view.geoLat5!!
        var long = view.geoLong!!
        var buttonClose = view.button_delete_geofence!!
        var address = view.textView46!!
        var workPlacename = view.textView45!!

    }







}


