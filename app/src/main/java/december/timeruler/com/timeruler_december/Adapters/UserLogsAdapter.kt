package december.timeruler.com.timeruler_december.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import december.timeruler.com.timeruler_december.*
import december.timeruler.com.timeruler_december.Model.UserLogs
import kotlinx.android.synthetic.main.model_userlogs.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.uiThread


class UserLogsAdapter(private val context: Context, private val userlogslist : List<UserLogs>) : RecyclerView.Adapter<UserLogsAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):

            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_userlogs,parent,false))


    override fun getItemCount(): Int  = userlogslist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var userLog = userlogslist[position]

        Log.e("Adapter atay",userLog.image)
        holder.userAction.text = userLog.action
        holder.userLogDate.text = userLog.time +"  "+userLog.date



        holder.constraintBackground.setOnClickListener {


            showLoginDialogSuccess(userLog)
        }
        if(position%2==0){
            holder.constraintBackground.setBackgroundColor(context.resources.getColor(R.color.white))

        }
        else
            holder.constraintBackground.setBackgroundColor(context.resources.getColor(R.color.light_gray))

    }



    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){

        val userAction = view.userAction!!
        val userLogDate = view.userLogData!!
        val constraintBackground = view.constraint_background!!


    }



    fun showLoginDialogSuccess(myLogs: UserLogs) {


        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_dialog_log_success, null)

        val mLogPhoto = dialogView.findViewById(R.id.logPhoto) as ImageView
        val mLogName = dialogView.findViewById(R.id.log_name) as TextView
        val mLogAction = dialogView.findViewById(R.id.log_action) as TextView
        val mLogTime = dialogView.findViewById(R.id.log_time) as TextView

        val mLogDate = dialogView.findViewById(R.id.log_date) as TextView
        val mLogLat = dialogView.findViewById(R.id.log_lat) as TextView
        val mLogLong = dialogView.findViewById(R.id.log_long) as TextView
        val mDialogTitle = dialogView.findViewById(R.id.dialogTitle) as TextView

doAsync {
    val srt = java.net.URL(myLogs.image).openStream()
    var bitmaps = BitmapFactory.decodeStream(srt)
uiThread {

    mLogPhoto.setImageBitmap(bitmaps)
}
}
      //  mLogPhoto.setImageBitmap(myAttendanceParce.userBitmap)
 mDialogTitle.text = "Logs"
        mLogName.text = myLogs.name
        mLogAction.text =myLogs.action
        mLogTime.text = myLogs.time
        mLogDate.text = myLogs.date
        mLogLat.text = myLogs.latitude
        mLogLong.text = myLogs.longitude
//        mLogLat.text = myAttendanceParce.userLat
//        mLogLong.text = myAttendanceParce.userLong

        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton("Close") { dialog, whichButton ->
        }
        dialogBuilder.setNegativeButton("Delete"){dialog,whichButton ->

        }
        val b = dialogBuilder.create()

        b.show()


    }




}


