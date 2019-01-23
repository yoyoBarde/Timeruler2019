package december.timeruler.com.timeruler_december

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_settings_work_location.*

class SettingsWorkLocation : AppCompatActivity() {
   lateinit var manager:FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_work_location)

        workFrameLayout


        manager = supportFragmentManager.beginTransaction()
        manager!!.replace(R.id.workFrameLayout,FragmentLocationMethod1())
        manager!!.commit()
    }
}
