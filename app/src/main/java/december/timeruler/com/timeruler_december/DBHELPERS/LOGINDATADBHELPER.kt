package december.timeruler.com.timeruler_december.DBHELPERS


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


import java.util.ArrayList

class LOGINDATADBHELPER(context: Context) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {
    val loginData: List<LoginData>
        get() {
            val myLoginDataList = ArrayList<LoginData>()
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_NAME"
            val data = db.rawQuery(query, null)

            if (data.moveToFirst()) {
                do {

                    val myLoginData = LoginData("  ", "  ")
                    myLoginData.setUserName(data.getString(1))
                    myLoginData.setUserPass(data.getString(2))
                    myLoginDataList.add(myLoginData)

                } while (data.moveToNext())
            }

            return myLoginDataList

        }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        val CREATE_TABLE_LOGIN_DATA = ("CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT , "
                + COL3 + " TEXT , "
                + COL8 + " TEXT )")



        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN_DATA)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)
    }


    fun addLoginDATA(userName: String, userPass: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()


        //contentValues.put(COL1, 2018);
        contentValues.put(COL2, userName)
        contentValues.put(COL3, userPass)
        val result = db.insert(TABLE_NAME, null, contentValues)

        return if (result.toInt() == -1) {
            false
        } else {
            true
        }

    }

    fun dropTable2() {
        val db = this.writableDatabase
        val query = " DELETE FROM $TABLE_NAME"
        db.execSQL(query)
    }

    companion object {

        private val TABLE_NAME = "LOGINDATA"
        private val COL1 = "ID"
        private val COL2 = "userName"
        private val COL3 = "userPass"
        private val COL8 = "waylabot"
    }


}
