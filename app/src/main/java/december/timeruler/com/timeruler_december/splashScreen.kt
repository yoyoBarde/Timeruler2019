package december.timeruler.com.timeruler_december

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.DBHELPERS.LoginData
import december.timeruler.com.timeruler_december.DBHELPERS.OFFLINELOGINDATADBHELPER
import december.timeruler.com.timeruler_december.Model.UserList
import kotlinx.android.synthetic.main.activity_splash_screen.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.lang.Exception

class splashScreen : AppCompatActivity() {
    val TAG = "SlapshScreen"

    companion object {

        lateinit var globalUserList:List<LoginData>

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        var myDBHELPER =   OFFLINELOGINDATADBHELPER(this)
        Log.e(TAG,"LOGINDATA "+myDBHELPER.loginData)
        globalUserList  = myDBHELPER.loginData

        for(i in 0 until myDBHELPER.loginData.size){
           // Log.e(TAG,"username: "+myDBHELPER.loginData[i].userName + "password: "+myDBHELPER.loginData[i].userPass)

        }
        if (myDBHELPER.loginData.isEmpty()) {
            putCached()
            Log.e(TAG,"put cashed")
        }

        var myIntent = Intent(this,LoginActivity::class.java)

        doAsync {
            uiThread {
                var fronBottom = AnimationUtils.loadAnimation(this@splashScreen, R.anim.uptodown)
                var backBottom = AnimationUtils.loadAnimation(this@splashScreen, R.anim.downtoup)
                tv_timeruler_splash.animation = fronBottom
                logo_splash.animation = backBottom
            }

            try {
                Thread.sleep(3000)
                uiThread {
                    startActivity(myIntent)

                }
       //         Log.e(TAG," asd")

            } catch (e: Exception) {

            }


        }
    }

    fun putToLocal(userList: UserList){

        var myDBHELPER =   OFFLINELOGINDATADBHELPER(this)
        for (i in 0 until userList.data.size) {

            Log.e(TAG,"Data saved = "+myDBHELPER.addLoginDATA(userList.data[i].idno,userList.data[i].password))
        }


    }
    fun putCached() {


        var booleanSuccess:Boolean=false
        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/userlist"
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
                        val userListFeed = mGSON.fromJson(mBody, UserList::class.java)
                        Log.e(TAG, " verify Login - ${userListFeed.data.size}")
                        putToLocal(userListFeed)
                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }


}