package december.timeruler.com.timeruler_december.DBHELPERS;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import december.timeruler.com.timeruler_december.Model.APIMODEL;

import java.util.ArrayList;
import java.util.List;


public class APIATAY extends SQLiteOpenHelper {
    private static final String COL1 = "userID";
    private static final String COL2 = "companyName";
    private static final String COL3 = "companyIP";
    private static final String COL4 = "userLong";


    private static final String TABLE_NAME = "OFFLINEAPITABLEasddasads";
    private static final String TAG = "OfflineDBHELPER";



    public APIATAY(Context context) {
        super(context, TABLE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String CREATE_TABLE_API_ATAY= "CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE_API_ATAY);
        Log.e(TAG,"onCREATE");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);


    }


    public boolean addOFFlineDATA( String companyIP,String companyName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,1101);
        contentValues.put(COL2, companyIP);
        contentValues.put(COL3, companyName);
        contentValues.put(COL4, "asdds");


        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean updateTable(String companyIP,String companyName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, companyIP);
        contentValues.put(COL3, companyName);

        String query = COL1+ " = "+1101;
       long result =  db.update(TABLE_NAME,contentValues,query,null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

//    public Boolean deleteFrom(String username,String userlogtime){
//
//        String equals = " = '";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "DELETE FROM " + TABLE_NAME + " WHERE "+COL2+equals+ username+" ' "+" AND " +COL6+equals+userlogtime+"'";
//        Log.e(TAG,"delete from"+"\n"+query);
//        String whereClause = COL2+equals+ username+"'"+" AND "+COL6+equals+userlogtime+"'";
//        return db.delete(TABLE_NAME,whereClause,null   )>0;
//    }


    public List<APIMODEL> getAPILIST() {
        List<APIMODEL> myAPILIST = new ArrayList<APIMODEL>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        if (data.moveToFirst()) {
            do {

                APIMODEL myAPIMODEL = new APIMODEL(data.getString(1), data.getString(2));
             //   Log.e(TAG,myAPIMODEL.toString());
                myAPILIST.add(myAPIMODEL);
            }

            while (data.moveToNext());


        }
        return  myAPILIST;
    }



    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + TABLE_NAME;
        db.execSQL(query);
    }






}
