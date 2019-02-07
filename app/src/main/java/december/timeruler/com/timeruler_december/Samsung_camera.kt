package december.timeruler.com.timeruler_december

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import december.timeruler.com.timeruler_december.Fragments.Camera2BasicFragment

class Samsung_camera : AppCompatActivity() {
    lateinit var manager: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samsung_camera)

//
//
//        manager = supportFragmentManager.beginTransaction()
//        manager!!.replace(R.id.samsung_frame, Camera2BasicFragment())
//        manager!!.commit()
//

        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(R.id.samsung_frame, Camera2BasicFragment.newInstance())
            .commit()
    }
}
