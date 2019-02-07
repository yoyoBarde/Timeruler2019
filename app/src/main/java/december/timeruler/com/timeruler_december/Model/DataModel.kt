package december.timeruler.com.timeruler_december.Model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


class DataModel {


}





data class GeofenceModelList(var data:ArrayList<GeofenceModelp>)
data class GeofenceModelp(
    var workplace_name:String,
    var workplace_address:String,
    var latitude: String,
    var longitude: String,
    var setby_admin:String,
    var workplace_id: String

)




data class GeofenceModel(
var workPlace:String,
var workAddress:String,
var latitude:String,
var longitude:String,
var admin:String,
var workplace_id:String
)




data class Login(val message:String)

data class PersonalInformation (val data: PersonalInfo)


data class PersonalInfo(val idno:String ,
                        val name:String ,
                        val position:String ,
                        val userlevel:String ,
                        val message:String)



data class UserList(val data: List<UserListData>)
data class UserListData(val idno:String ,
                        val name:String ,
                        val password:String)

data class UserLogsList(val data: List<UserLogs>)
data class UserLogs(
    var idno: String,
    var name:String,
    var date:String,
    var time:String,
    var action:String,
    var bitmap:Bitmap,
    var image:String,
    var latitude:String,
    var longitude:String
)






data class CurrentTimeList(val message: CurrentTime)
data class CurrentTime(
    var date:String,
    var time:String

)


data class APIMODEL(
    var companyIP:String,
    var companyName:String

)






data class Attendance(
    var userName: String,
    var userPass: String,
    var userLong: String,
    var userLat: String,
    var userLoginTime: String,
    var userLoginDate: String,
    var userAction: String,
    var userBitmap: Bitmap
){
     lateinit var userElapsedTime:String

}




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
             userName = parcel.readString()!!
             userPass = parcel.readString()!!
             userLong = parcel.readString()!!
             userLat = parcel.readString()!!
             userLoginTime = parcel.readString()!!
             userLoginDate = parcel.readString()!!
             userAction = parcel.readString()!!
             userBitmap = parcel.readParcelable(Bitmap::class.java.classLoader)!!
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


interface Dismisscallback{
    fun dismissListener()

}