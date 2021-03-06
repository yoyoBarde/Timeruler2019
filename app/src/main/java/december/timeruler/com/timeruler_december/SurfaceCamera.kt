package december.timeruler.com.timeruler_december

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.gson.GsonBuilder
import december.timeruler.com.timeruler_december.DBHELPERS.OFFLINELOGDBHELPER
import december.timeruler.com.timeruler_december.Model.Attendance
import december.timeruler.com.timeruler_december.Model.AttendanceParce
import december.timeruler.com.timeruler_december.Model.CurrentTimeList
import kotlinx.android.synthetic.main.activity_camera_source.*
import okhttp3.*
import org.jetbrains.anko.*
import java.io.ByteArrayOutputStream
import java.io.File

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SurfaceCamera : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 11111
        const val APINAME= "payrulerupdated-api"

    }
    lateinit var globalServerTime: CurrentTimeList
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    lateinit var filepath:File
    lateinit var filename:String
    lateinit var surfaceView: SurfaceView
    lateinit var cameraSource: CameraSource
    lateinit var mBtn_in: Button
    lateinit var mBtn_out: Button
    var globalCounter = 0
    var smileSettoWhite = true
    val TAG = "SurfaceCamera"
    lateinit var my_iv_preview: ImageView
    lateinit var my_ic_face: ImageView
    lateinit var globalUserBitmap: Bitmap
    lateinit var myOFFLINELOGDBHELPER: OFFLINELOGDBHELPER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_source)

        getServerTime()
        my_ic_face = findViewById(R.id.iv_facedetected_ic)
        my_iv_preview = findViewById<ImageView>(R.id.iv_photoPreview)
        mBtn_in = findViewById<Button>(R.id.btn_in)
        mBtn_out = findViewById<Button>(R.id.btn_out)
        setupDigitalClock()
        requestPermissionTakePhoto()
        myOFFLINELOGDBHELPER = OFFLINELOGDBHELPER(this)

        if(LoginActivity.login_mode.equals("offline")){

            timeruler_mode.visibility = View.VISIBLE
        }
        else{
            timeruler_mode.visibility = View.GONE

        }
        askPermissions()
        onCreateDoables()



        mBtn_in.setOnClickListener {

                transferData("IN")

        }
        mBtn_out.setOnClickListener { transferData("OUT") }

        iv_retake_pic.setOnClickListener {

            recreate()
        }

    }


    private fun onCreateDoables() {
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        surfaceView = findViewById<SurfaceView>(R.id.cameraPreview)
        val detector = FaceDetector.Builder(applicationContext)
            .setProminentFaceOnly(true)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setTrackingEnabled(true)
            .build()

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(15.0f)
            .build()


        Log.e("ScreenSize", (width + height).toString())
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        //mGraphicOverlay = GraphicOverlay(this,attr())

//        //mGraphicOverlay = GraphicOverlay(applicationContext,null)
//        mGraphicOverlay = GraphicOverlay(applicationContext)
//        detector.setProcessor(LargestFaceFocusingProcessor(detector, GraphicFaceTracker(mGraphicOverlay)))

        detector.setProcessor(object : Detector.Processor<Face> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Face>) {
                doAsync {
                    val faces = detections.detectedItems

                    if (faces.size() != 0) {


                        globalCounter += 1
                        if (globalCounter == 1) {
                            uiThread {
                                my_ic_face.setImageResource(R.mipmap.ic_face)
                            }
                        }

                        if (globalCounter == 27) {
                            goVibrate()


                            uiThread {
                                my_ic_face.setImageResource(R.mipmap.ic_face_green)
                            }


                        }
                        if (globalCounter == 30) {

                            Log.e(TAG, "gwapo ka")
                            cameraSource.takePicture(mShutterCallback, mPictureCallback)
                            globalCounter = -20


                        }

                    } else {
                        uiThread {
                            my_ic_face.setImageResource(R.mipmap.ic_face_red)
                        }
                        globalCounter = 0


                    }
                }
            }
        })


    }











    fun getPath(uri: Uri): String {
        var cursor = this.contentResolver.query(uri, null, null, null, null)
        cursor.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()

        cursor = this.contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf<String>(document_id),
            null
        )
        cursor.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()

        return path
    }
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor!!.moveToFirst()) {
                    result = cursor!!.getString(cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null) {
                    cursor!!.close()
                }
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }



    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)

        return Uri.parse(path)
    }






    private val mShutterCallback = CameraSource.ShutterCallback {


    }

    private fun requestPermissionTakePhoto() {
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                return
            }

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return
            } else {

                Log.e(TAG, "this part")
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
              //  locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

            }

        }

    }

    private val mPictureCallback = CameraSource.PictureCallback { bytes ->
        // val orientation = Exif.getOrientation(bytes)


        doAsync {
            var userBitmap = getResizedBitmap(detect(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))!!, 100)
            cameraSource.release()
            enableButton()
            Log.e(TAG, userBitmap.toString())
            globalUserBitmap = userBitmap
            //globalByte = bytes
            var size = (globalUserBitmap.height * globalUserBitmap.width) * 4
            Log.e(TAG, "image size" + globalUserBitmap.byteCount + size)

            uiThread {
                iv_photoPreview.visibility = View.VISIBLE
                iv_photoPreview.setImageBitmap(userBitmap)
            }


        }
    }

    fun setupDigitalClock() {

        doAsync {
            var myDate = getCurrentDate()

            for (i in 0 until 9008941372) {


                Thread.sleep(1000)
                //  Log.e(TAG, getCurrentTime()+getCurrentDate())
                var myTime = getCurrentTime()
                var myString = " $myDate \n $myTime "


                uiThread {


                    digitalClock2.text = myString

                }


            }

        }
    }

    fun goVibrate() {

        var v: Vibrator = applicationContext!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
// Vibrate for 500 milliseconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }


    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("h:mm:ss a")
        return df.format(c.time)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MMM dd,yyyy")
        return df.format(c.time)
    }

    fun askPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
///method to get Images

            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(
                        applicationContext,
                        "Your Permission is needed to get access the camera",
                        Toast.LENGTH_LONG
                    ).show()
                }
                requestPermissions(
                    arrayOf(
                        // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        // Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), 0
                )
            }
        }
    }

    fun disableButtons() {
        doAsync {
            var myFloat: Float = 0.5.toFloat()
            uiThread {
                mBtn_in.isClickable = false
                mBtn_out.isClickable = false
                mBtn_in.alpha = myFloat
                mBtn_out.alpha = myFloat
            }
        }
    }

    fun enableButton() {
        doAsync {
            Log.e(TAG, "enableButtons")
            var myFloat: Float = .99.toFloat()
            uiThread {
                mBtn_in.textColor = ContextCompat.getColor(applicationContext, R.color.black)
                mBtn_out.textColor = ContextCompat.getColor(applicationContext, R.color.black)
                mBtn_in.isClickable = true
                mBtn_out.isClickable = true
                mBtn_in.alpha = myFloat
                mBtn_out.alpha = myFloat
            }
        }

    }

    private fun detect(bitmap: Bitmap): Bitmap? {

        val detector = FaceDetector.Builder(applicationContext)
            .setProminentFaceOnly(true)
            .setTrackingEnabled(false)
            .build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val faces = detector.detect(frame)
        detector.release()

        Log.e(
            TAG, "detect \n" +
                    "bitmap width:" + bitmap.width + " height:" + bitmap.height + "\n" +
                    "face width:" + " height:"
        )
        Log.e(TAG, "face size:" + faces.size())
        if (faces.size() > 0) {
            val face = faces.valueAt(0)
            //            int x = Math.max((int) (face.getPosition().x - FaceGraphic.ID_X_OFFSET), 0);
            //            int y = Math.max((int) (face.getPosition().y - FaceGraphic.ID_Y_OFFSET), 0);
            //            int width = Math.min((int) (face.getWidth() - FaceGraphic.ID_X_OFFSET), bitmap.getWidth() - x);
            //            int height = Math.min((int) (face.getHeight() + FaceGraphic.ID_Y_OFFSET), bitmap.getHeight() - y);

            var x = face.position.x.toInt()
            var y = face.position.y.toInt()
            var width = Math.min(face.width.toInt(), bitmap.width - x)
            var height = Math.min(face.height.toInt(), bitmap.height - y)
            if (x < 0) {
                width += x // += x is negative
                x = 0
            }
            if (y < 0) {
                height += y // += y is negative
                y = 0

            }
//            Log.e(TAG, "Y = " + y + " height " + height + " = " + (y + height) + " bH " + bitmap.height)
//            Log.e(TAG, "x = " + x + " width " + width + " = " + (x + width) + " bW " + bitmap.width)

            if (y + height > bitmap.height) {// to avoid java.lang.IllegalArgumentException: y + height must be <= bitmap.height()
                val difference =
                    Math.abs(y + height - bitmap.height) // Get the difference of bitmap height and y+height
                val divide = difference / 2                              // divide it to 2
                y += divide                                            // add it to y and subtract it to height
                height -= divide                                       // so we can get the center of the fucking face

            }

            if (x + width > bitmap.width) {// to avoid java.lang.IllegalArgumentException: x + width must be <= bitmap.getWidth()
                val difference =
                    Math.abs(y + height - bitmap.height) // Get the difference of bitmap width and x + width
                val divide = difference / 2                              // divide it to 2
                x += divide                                            // add it to y and subtract it to height
                width -= divide                                       // so we can get the center of the fucking face
            }


            return Bitmap.createBitmap(bitmap, x, y, width, height)
        }
        return null
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        val width = image.width
        val height = image.height
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxSize
            newHeight = height * newWidth / width
        } else {
            newHeight = maxSize
            newWidth = width * newHeight / height
        }
        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }


    fun push_userlog2(myAttendance: Attendance){
            Log.e(TAG,"giataykudasai")
        doAsync {
            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/save_userlog"
            val mClient = OkHttpClient()
            val formBodyBuilder = MultipartBody.Builder()

            formBodyBuilder.setType(MultipartBody.FORM)
            formBodyBuilder.addFormDataPart("idno", myAttendance.userName)
            formBodyBuilder.addFormDataPart("action",myAttendance.userAction )
            formBodyBuilder.addFormDataPart("time", globalServerTime.message.time)
            formBodyBuilder.addFormDataPart("date", globalServerTime.message.date)
            formBodyBuilder.addFormDataPart("long", myAttendance.userLong)
            formBodyBuilder.addFormDataPart("lat", myAttendance.userLat)
            filepath = File(getPath(getImageUri(this@SurfaceCamera, globalUserBitmap)))
            filename = getFileName(this@SurfaceCamera, getImageUri(this@SurfaceCamera, globalUserBitmap))

            val requestBody = RequestBody.create(MediaType.parse("image/*"), filepath)
            val fileToUpload = MultipartBody.Part.createFormData("file", filename, requestBody)
            formBodyBuilder.addPart(fileToUpload)

            val formBody = formBodyBuilder.build()

            var builder = Request.Builder()
            builder = builder.url(url)
            builder = builder.post(formBody)
            val request = builder.build()

            mClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val mBody = response?.body()?.string()
                    val mGSON = GsonBuilder().create()

                    uiThread {
                        filepath.delete()
                        Log.i("file", filename)
                        Log.i("mBody", mBody)
                        Log.i("mGSON", mGSON.toString())
                    }
                    Log.e(TAG,"push is successful - "+response.isSuccessful.toString())

                }
            })

            Log.e(TAG,"Ahakdesu")
        }

    }


    fun transferData(InOrOut: String) {

        Log.e(TAG, "transferData")
        //disableButtons()
        var myuserName = LoginActivity.username
        var myuserPass = LoginActivity.password
        var myuserPic = globalUserBitmap
        var myuserLong = LoginActivity.long
        var myuserLat = LoginActivity.lat
        var myuserLoginTime = getCurrentTime()
        var myuserLoginDate = getCurrentDate()
        var myuserAction = InOrOut


        var myAttendance = Attendance(
            myuserName,
            myuserPass,
            myuserLong,
            myuserLat,
            myuserLoginTime,
            myuserLoginDate,
            myuserAction,
            myuserPic
        )
        myAttendance.userElapsedTime = LoginActivity.elapsedTime


            var myAttendanceParce = AttendanceParce()
        myAttendanceParce.userName = myuserName
        myAttendanceParce.userPass = myuserPass
        myAttendanceParce.userBitmap = myuserPic
        myAttendanceParce.userLong = myuserLong
        myAttendanceParce.userLat = myuserLat
        myAttendanceParce.userLoginTime = myuserLoginTime
        myAttendanceParce.userLoginDate = myuserLoginDate
        myAttendanceParce.userAction = myuserAction

        Log.e(
            TAG,
            "$myuserName $myuserPass $myuserLong $myuserLat $myuserLoginTime $myuserLoginDate $myuserAction ${myuserPic.toString()}"


        )
        if(LoginActivity.login_mode.equals("offline"))
        {
           var ifsuccess =  myOFFLINELOGDBHELPER.addOFFlineDATA(myAttendance)

            Log.e(TAG,"Offline data is saved = "+ifsuccess.toString())
        }
        else{

        }

       // push_userlog(myAttendance)
        push_userlog2(myAttendance)
        var gagongIntent: Intent = Intent()
        gagongIntent.putExtra("surface_mode",LoginActivity.login_mode)
        gagongIntent.putExtra("somedata", "gwapo ka")
        gagongIntent.putExtra("userAttendance",myAttendanceParce)
        setResult(REQUEST_CODE, gagongIntent)
        finish()


        }


    interface PassData {
        fun passstring(string: String)
    }


    fun getServerTime() {


        var booleanSuccess:Boolean=false
        doAsync {

            val url = "http://10.224.1.160/${SurfaceCamera.APINAME}/user_controller/current_time"
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
                    val currentTimeListFeed = mGSON.fromJson(mBody, CurrentTimeList::class.java)
                    //    val currentTIme = mGSON.fromJson(mBody,CurrentTime::class.java)
                       Log.e(TAG, " verify Login - ${currentTimeListFeed.message}"+ response!!.isSuccessful.toString())
                       // Log.e(TAG,currentTIme.toString()+response!!.isSuccessful)
                            globalServerTime = currentTimeListFeed

                    } catch (e: java.lang.Exception) {
                        Log.e("error", "$e")

                    }
                }

            })

        }
    }

}



