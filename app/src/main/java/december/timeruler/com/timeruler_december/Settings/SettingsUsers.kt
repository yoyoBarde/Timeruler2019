package december.timeruler.com.timeruler_december.Settings

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import december.timeruler.com.timeruler_december.Adapters.UserListAdapter
import december.timeruler.com.timeruler_december.LoginActivity
import december.timeruler.com.timeruler_december.R
import december.timeruler.com.timeruler_december.UsersLogs
import kotlinx.android.synthetic.main.activity_settings_users.*

class SettingsUsers : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_users)


        users_view_logs.setOnClickListener {
            var myIntent = Intent(this, UsersLogs::class.java)
            UserListAdapter.userNameClicked = " John Doe"
            UserListAdapter.userIDclicked =
                    LoginActivity.usernamesettings
            startActivity(myIntent)


        }
    }
}
