package december.timeruler.com.timeruler_december

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.Adapters.UserListAdapter
import december.timeruler.com.timeruler_december.Adapters.UserLogsAdapter
import kotlinx.android.synthetic.main.activity_users_logs.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException

class UsersLogs : AppCompatActivity() {
    val TAG = "UsersLogs"
    lateinit var mysRecyclerView: RecyclerView
    fun by_date_recent(userLogs:UserLogs):String = userLogs.date
    lateinit var myUsername:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_logs)


        mysRecyclerView = findViewById<RecyclerView>(R.id.giatayRecyckerVuew)

        val intent = intent
        val bundle = intent.extras
        putCached(UserListAdapter.userIDclicked)


        users_log_id.text = UserListAdapter.userIDclicked
        users_log_name.text = UserListAdapter.userNameClicked
        myUsername = findViewById<TextView>(R.id.users_log_name)
        imageIcon22.setOnClickListener { finish() }
    }


    fun putCached(userID: String) {


        doAsync {
            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/logs_controller/userlogs_ID/$userID"
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
                        val userLogsListFeed = mGSON.fromJson(mBody, UserLogsList::class.java)
                        var sortedList =  userLogsListFeed.data.sortedWith(compareByDescending<UserLogs>{it.date }.thenByDescending { it.time })

                        Log.e(TAG, " verify Login - ${userLogsListFeed.data}")
                        uiThread {

                            if (sortedList.size>0)
                                myUsername.text = sortedList[0].name


                                var adapter = UserLogsAdapter(this@UsersLogs, sortedList)

                            val layoutManager = LinearLayoutManager(this@UsersLogs,LinearLayout.VERTICAL,false)
                            mysRecyclerView.layoutManager = layoutManager

                            mysRecyclerView.adapter = adapter!!

                        }
                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }


}
