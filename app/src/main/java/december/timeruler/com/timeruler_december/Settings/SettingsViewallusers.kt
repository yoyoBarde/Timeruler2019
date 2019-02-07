package december.timeruler.com.timeruler_december.Settings

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.Adapters.UserListAdapter
import kotlinx.android.synthetic.main.activity_settings_viewallusers.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList
import android.view.inputmethod.InputMethodManager
import december.timeruler.com.timeruler_december.Model.UserList
import december.timeruler.com.timeruler_december.Model.UserListData
import december.timeruler.com.timeruler_december.R
import december.timeruler.com.timeruler_december.SurfaceCamera


class SettingsViewallusers : AppCompatActivity() {
lateinit var myRecyclerView: RecyclerView
    lateinit var mySearch:EditText

    val TAG= "SettingsViewallusers"
    var counter = 0
    fun by_name_desc(user: UserListData):String = user.name

     var myAdapter:UserListAdapter ?= null
companion object {
    lateinit var myBtnSearch:ImageView
    lateinit var myIvIconUsers:ImageView

    lateinit var mytv_toolbar:TextView

    lateinit var myConstraintSearch:ConstraintLayout





}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewallusers)

        myBtnSearch = findViewById<ImageView>(
            R.id.btn_search
        )
        myIvIconUsers = findViewById<ImageView>(
            R.id.iv_icon_users
        )
        mytv_toolbar = findViewById<TextView>(
            R.id.tv_toolbar_Users
        )
        myConstraintSearch = findViewById<ConstraintLayout>(
            R.id.constraint_search
        )

myRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
         mySearch = findViewById<EditText>(R.id.searchAutocomplete)
        // Get the input method manager

        imageView3.setOnClickListener { finish() }
putCached()

        btn_search.setOnClickListener {
            mySearch.requestFocus()
            mySearch.showKeyboard()
            btn_search.visibility = View.GONE
            iv_icon_users.visibility = View.GONE
            tv_toolbar_Users.visibility = View.GONE
            constraint_search.visibility = View.VISIBLE

        }





        frameLayout.setOnClickListener {
        Log.e(TAG,"Frame layout clicked")
            myBtnSearch.visibility = View.VISIBLE
            myIvIconUsers.visibility = View.VISIBLE
            mytv_toolbar.visibility = View.VISIBLE
            myConstraintSearch.visibility = View.GONE
            mySearch.hideKeyboard()
        }

    }
    fun View.showKeyboard() {


        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        //imm.toggleSoftInputFromWindow(rootView.windowToken, 0,0);

    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

fun hideSearch(hideit:Boolean){
            doAsync {
                if (hideit) {
                    counter=25

                    for (i in 1 until 4) {
                    Thread.sleep(1000)
                Log.e(TAG,"Counter"+counter)
                }

                uiThread {
                        if (counter!=0){
                            Log.e(TAG,counter.toString()+"code execution")
                            myBtnSearch.visibility = View.VISIBLE
                            myIvIconUsers.visibility = View.VISIBLE
                            mytv_toolbar.visibility = View.VISIBLE
                            myConstraintSearch.visibility = View.GONE
                            mySearch.hideKeyboard()

                        }

                }

            }
    else{
            counter=0
            Log.e(TAG,counter.toString()+" lain 0")

        }
            }


}

    private fun filter(text: String,name:List<UserListData>) {
        //new array list that will hold the filtered data
        val filterdNames = ArrayList<UserListData>()
        //looping through existing elements

//        var nameList = arrayListOf<String>("john DOe")
//        nameList.remove(0)
//
//        for (i in 0 until name.size) {
//        nameList.add(name[i].name)
//        }


        for (s in name) {
            //if the existing elements contains the search input
            val nameee = s.name
            val idnoo = s.idno
            if (nameee.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
            else if(idnoo.contains(text)){
                filterdNames.add(s)

            }


        }



            //calling a method of the adapter class and passing the filtered list
        myAdapter!!.filterList(filterdNames)
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
                        Log.e(TAG, " verify Login - ${userListFeed.data}")

                        //var sortedList = list.sortedWith(compareBy({ it.customProperty }))
                     var sortedList =    userListFeed.data.sortedBy {  by_name_desc(it)}
                        mySearch!!.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                            }

                            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                            }

                            override fun afterTextChanged(editable: Editable) {
                                //after the change calling the method and passing the search input

                                Log.e(TAG,"afterTextChanged = " +editable.toString())
                                filter(editable.toString(),sortedList)
                                if(editable.toString() == ""){
                                    Log.e(TAG,"afterTextChanged =very true so true " +editable.toString())
                                    hideSearch(true)
                                }
                                else{


                                    hideSearch(false)

                                }


                            }
                        })


                        //     var sortedUserlist = userListFeed.s
            uiThread {


                myAdapter = UserListAdapter(this@SettingsViewallusers, sortedList)
                val layoutManager = LinearLayoutManager(this@SettingsViewallusers)
                myRecyclerView.layoutManager = layoutManager
                myRecyclerView.itemAnimator = DefaultItemAnimator()
                myRecyclerView.adapter = myAdapter!!




            }
                    } catch (e: Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }

}
