package december.timeruler.com.timeruler_december

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.FillEventHistory
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.Adapters.UserListAdapter
import december.timeruler.com.timeruler_december.DBHELPERS.APIATAY
import kotlinx.android.synthetic.main.activity_settings_admin.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.w3c.dom.Text
import java.io.IOException
import java.lang.Exception

class SettingsAdmin : AppCompatActivity() {
lateinit var toolbar:Toolbar
    val TAG = "SettingsAdmin"

    lateinit var tv_companyIP:TextView
    lateinit var tv_companyName:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_admin)

    toolbar = findViewById<Toolbar>(R.id.settingsToolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Settings"
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        toolbar.setLogo(R.mipmap.settings_timeruler_icon)

        var myAPIDB = APIATAY(this)
        textView30.text = myAPIDB.apilist.get(0).companyIP
        textView29.text = myAPIDB.apilist.get(0).companyName

        tv_companyIP = findViewById<TextView>(R.id.textView30)
        tv_companyName = findViewById<TextView>(R.id.textView29)
        tv_setApi.setOnClickListener { showIPentry() }
        tv_viewAllusers.setOnClickListener {

            var myIntent = Intent(this,SettingsViewallusers::class.java)
            startActivity(myIntent)
        }

        tv_setLocation.setOnClickListener {
            var myIntent = Intent (this,SettingsWorkLocation::class.java)
            startActivity(myIntent)

        }
        adminLogs.setOnClickListener {
            var myIntent = Intent(this,UsersLogs::class.java)
            UserListAdapter.userNameClicked = " John Doe"
            UserListAdapter.userIDclicked = LoginActivity.usernamesettings
            startActivity(myIntent)


        }
        settings_logout.setOnClickListener { finish()
        }

    }
    fun showIPentry() {

        var myAPIATAY = APIATAY(this)
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_api, null)

        val myUserIP= dialogView.findViewById(R.id.dialog_et_ip) as EditText
        val myUserFolder = dialogView.findViewById(R.id.dialog_et_folder) as EditText
        val mySubmitIP = dialogView.findViewById(R.id.submit_ip) as Button


        myUserIP.setText( myAPIATAY.apilist[0].companyIP)
        myUserFolder.setText( myAPIATAY.apilist[0].companyName)
        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)

        val b = dialogBuilder.create()

        b.show()


        mySubmitIP.setOnClickListener {



            myAPIATAY.addOFFlineDATA(myUserFolder.text.toString(), myUserIP.text.toString())
            Log.e(TAG,myAPIATAY.updateTable(myUserIP.text.toString(),myUserFolder.text.toString()).toString())

            tv_companyName.text = myUserFolder.text.toString()
            tv_companyIP.text = myUserIP.text.toString()
            Log.e(TAG, myAPIATAY.apilist.toString())

            b.dismiss()

        }


        try {

            Log.e(TAG, myAPIATAY.apilist.toString())
        }
        catch (e:Exception){

            print(e)
        }

    }

    override fun onBackPressed() {
        System.exit(0)
    }

}
