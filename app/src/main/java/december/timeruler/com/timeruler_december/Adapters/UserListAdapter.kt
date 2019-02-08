package december.timeruler.com.timeruler_december.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import december.timeruler.com.timeruler_december.R
import december.timeruler.com.timeruler_december.Model.UserListData
import december.timeruler.com.timeruler_december.UsersLogs
import kotlinx.android.synthetic.main.model_user.view.*


class UserListAdapter(private val context:Context, private var userlist : List<UserListData>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>(){
companion object {
    lateinit var userIDclicked:String
    lateinit var userNameClicked:String
}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):

            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_user,parent,false))


    override fun getItemCount(): Int  = userlist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val user = userlist[position]

        holder.idno.text = user.idno
        holder.nameuser.text = user.name
        if(position%2==0){
            holder.backgroundColor.setBackgroundColor(context.resources.getColor(R.color.white))

        }
        else
            holder.backgroundColor.setBackgroundColor(context.resources.getColor(R.color.row_color))


            holder.viewLog.setOnClickListener {
                var userLogIntent = Intent(context,UsersLogs::class.java)
                userIDclicked = user.idno
                userNameClicked = user.name
                userLogIntent.putExtra("username",user.idno)
                context.startActivity(userLogIntent)


            }



    }

    fun filterList(filterdNames: ArrayList<UserListData>) {
        userlist = filterdNames
        notifyDataSetChanged()
    }


    inner class ViewHolder (view:View): RecyclerView.ViewHolder(view){

        val idno = view.model_idno!!
        val nameuser = view.model_name!!
        val viewLog = view.tv_viewLogs!!
        val backgroundColor = view.model_constraint


    }







}








