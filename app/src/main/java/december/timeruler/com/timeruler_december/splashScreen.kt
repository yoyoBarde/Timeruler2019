package december.timeruler.com.timeruler_december

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class splashScreen : AppCompatActivity() {
val TAG = "SlapshScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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
            Log.e(TAG," asd")

            } catch (e: Exception) {

            }


        }
    }
}
