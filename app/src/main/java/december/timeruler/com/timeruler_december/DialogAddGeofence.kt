package december.timeruler.com.timeruler_december

import android.app.Dialog
import android.net.wifi.WifiConfiguration
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import december.timeruler.com.timeruler_december.Model.Dismisscallback
import android.view.InflateException
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_add_geofence.*
import org.jetbrains.anko.support.v4.toast


class DialogAddGeofence : DialogFragment() {
    lateinit var lat:TextView
    lateinit var long:TextView
    lateinit var tv_place:TextView
    lateinit var buttonSet:Button
    lateinit var workPlace:EditText
    lateinit var placeAutocompletesd:PlaceAutocompleteFragment
    val TAG = "DialogAddGeofence"
    var viewss:View? = null



    companion object {

        lateinit var latc:String
        lateinit var longc:String
        lateinit var placec:String
        lateinit var addressc:String
    }






    fun newInstance(): DialogAddGeofence {
        return DialogAddGeofence()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


                if(viewss!=null){

                  var  parentt = viewss?.parent as ViewGroup
                    if (parentt != null)
                        parentt.removeView(viewss)

                }
        try {

            viewss = inflater.inflate(R.layout.fragment_add_geofence, container,false)
        }
        catch (e:InflateException){

        }




          var   my_button= viewss?.findViewById(R.id.button_close_dialog) as ImageView
        my_button.setOnClickListener {

            dismiss()
        }


            lat = viewss?.findViewById(R.id.geoLat3) as TextView
            tv_place= viewss?.findViewById(R.id.placesText) as TextView
            long = viewss?.findViewById(R.id.geoLong3) as TextView
            buttonSet = viewss?.findViewById(R.id.button_set_geofence) as Button
        workPlace = viewss?.findViewById(R.id.et_workPlacename) as EditText




            val mDismisscallback = (activity) as Dismisscallback

            buttonSet.setOnClickListener {


                if (workPlace.text.isEmpty()) {
                    workPlace.error = "Invalid"
                }
                else {
                    placec = workPlace.text.toString()
                    workPlace.text.clear()

                    mDismisscallback.dismissListener()
                }


            }

        placeAutocompletesd = activity!!.fragmentManager!!.findFragmentById(R.id.google_search) as PlaceAutocompleteFragment
        placeAutocompletesd?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {


                lat.text = place.latLng.latitude.toString()
                long.text = place.latLng.longitude.toString()
                tv_place.text = place.name.toString()

                latc=lat.text.toString()
                longc = long.text.toString()
                addressc = tv_place.text.toString()
                Log.e(TAG,"atay ra ahh")
                toast("Success")
            }

            override fun onError(status: Status) {
                toast("Error")
            }
        })

    return  viewss


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Sample)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!
            .attributes.windowAnimations = R.style.DialogAnimation

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      //  dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return dialog
    }

    override fun onDestroy() {
        Log.e(TAG,"destroy")
            super.onDestroy()

        workPlace.text.clear()
        placeAutocompletesd.setText(" ")
        lat.text ="0.0"
        long.text = "0.0"
        tv_place.text= " "

        // var myFragment =
    }

    override fun onResume() {
        super.onResume()
//
        Log.e(TAG,"resume")



    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG,"onPause")

    }


    }

