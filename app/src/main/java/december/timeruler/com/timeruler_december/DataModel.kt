package december.timeruler.com.timeruler_december

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


class DataModel {


}    data class Attendance(
    var userName: String,
    var userPass: String,
    var userLong: String,
    var userLat: String,
    var userLoginTime: String,
    var userLoginDate: String,
    var userAction: String,
    var userBitmap: Bitmap
)

     class AttendanceParce() :Parcelable {

         lateinit var userName:String
         lateinit var userPass: String
         lateinit var userLong:String
         lateinit var userLat:String
         lateinit var userLoginTime:String
         lateinit var userLoginDate: String
         lateinit var userAction:String
         lateinit var userBitmap: Bitmap

         constructor(parcel: Parcel) : this() {
             userName = parcel.readString()
             userPass = parcel.readString()
             userLong = parcel.readString()
             userLat = parcel.readString()
             userLoginTime = parcel.readString()
             userLoginDate = parcel.readString()
             userAction = parcel.readString()
             userBitmap = parcel.readParcelable(Bitmap::class.java.classLoader)
         }

         override fun writeToParcel(parcel: Parcel, flags: Int) {
             parcel.writeString(userName)
             parcel.writeString(userPass)
             parcel.writeString(userLong)
             parcel.writeString(userLat)
             parcel.writeString(userLoginTime)
             parcel.writeString(userLoginDate)
             parcel.writeString(userAction)
             parcel.writeParcelable(userBitmap, flags)
         }

         override fun describeContents(): Int {
             return 0
         }

         companion object CREATOR : Parcelable.Creator<AttendanceParce> {
             override fun createFromParcel(parcel: Parcel): AttendanceParce {
                 return AttendanceParce(parcel)
             }

             override fun newArray(size: Int): Array<AttendanceParce?> {
                 return arrayOfNulls(size)
             }
         }



     }
