package december.timeruler.com.timeruler_december.DBHELPERS

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import december.timeruler.com.timeruler_december.Model.APIMODEL

import java.util.ArrayList

class APIDBHELPER(context: Context) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {

    val apilista: List<APIMODEL>
        get() {
            val OFFLINEAPI = ArrayList<APIMODEL>()
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_NAME"
            val data = db.rawQuery(query, null)

            if (data.moveToFirst()) {
                do {


                    val myOFFLINEAPI =
                        APIMODEL(data.getString(2), data.getString(3))

                    OFFLINEAPI.add(myOFFLINEAPI)
                } while (data.moveToNext())
            }
            Log.e(TAG, OFFLINEAPI.toString())
            return OFFLINEAPI
        }

    override fun onCreate(db: SQLiteDatabase) {


        val CREATE_TABLE_API_DATA = (" CREATE TABLE " + TABLE_NAME + " ( "
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT , "
                + COL3 + " TEXT , "
                + COL4 + " TEXT ) ")

        db.execSQL(CREATE_TABLE_API_DATA)
        Log.e(TAG, "onCREATE")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP IF TABLE EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun setAPI(companyIP: String, companyName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
         contentValues.put(COL1,"oneID");
        contentValues.put(COL2, companyIP)
        contentValues.put(COL3, companyName)

        val result = db.insert(TABLE_NAME, null, contentValues)
        //Log.e(TAG,apilista.toString())
        return !result.equals(-1)

    }


    fun dropTable() {
        val db = this.writableDatabase
        val query = "DROP TABLE $TABLE_NAME"
        db.execSQL(query)
    }

    companion object {

        private val COL1 = " IDKONO "
        private val COL2 = " companyIP "
        private val COL3 = " companyName "
        private val COL4 = " companyFlower"
        private val TABLE_NAME = " APITABLE "
        private val TAG = " APIDBHELPER "
    }


}
