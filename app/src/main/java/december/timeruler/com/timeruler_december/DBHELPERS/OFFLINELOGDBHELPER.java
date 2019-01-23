package december.timeruler.com.timeruler_december.DBHELPERS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import december.timeruler.com.timeruler_december.Attendance;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class OFFLINELOGDBHELPER extends SQLiteOpenHelper {
    private static final String COL1 = "userID";
    private static final String COL2 = "userName";
    private static final String COL3 = "userPassword";
    private static final String COL4 = "userLong";
    private static final String COL5 = "userLat";
    private static final String COL6 = "userLoginTime";
    private static final String COL7 ="userLoginDate" ;
    private static final String COL8 = "userAction";
    private static final String COL9 = "userImage";
    private static final String COL10 = "flower";

    private static final String TABLE_NAME = "OFFLINEDATATABLE";
    private static final String TAG = "OfflineDBHELPER";



    public OFFLINELOGDBHELPER(Context context) {
        super(context, TABLE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String CREATE_TABLE_OFFLINE_DATA = "CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY  ,"
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT, "
                + COL5 + " TEXT, "
                + COL6 + " TEXT, "
                + COL7 + " TEXT, "
                + COL8 + " TEXT, "
                + COL9 + " TEXT, "
                + COL10 + " TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE_OFFLINE_DATA);
        Log.e(TAG,"onCREATE");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);


    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public boolean addOFFlineDATA(Attendance offlineAttendance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL2, offlineAttendance.getUserName());
        contentValues.put(COL3, offlineAttendance.getUserPass());
        contentValues.put(COL4, offlineAttendance.getUserLong());
        contentValues.put(COL5, offlineAttendance.getUserLat());
        contentValues.put(COL6, offlineAttendance.getUserLoginTime());
        contentValues.put(COL7, offlineAttendance.getUserLoginDate());
        contentValues.put(COL8,offlineAttendance.getUserAction());
        contentValues.put(COL9,getBytes(offlineAttendance.getUserBitmap()));
        Log.e(TAG,"byteArray - "+getBytes(offlineAttendance.getUserBitmap()).toString());

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public List<Attendance> getOfflineDataList() {
        List<Attendance> OfflineList = new ArrayList<Attendance>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        if (data.moveToFirst()) {
            do {

                Attendance myOfflineAttendance = new Attendance(data.getString(1),data.getString(2),data.getString(3),data.getString(4),
                        data.getString(5)
                        ,data.getString(6)
                        ,data.getString(7),
                        getImage(data.getBlob(8)));
                OfflineList.add(myOfflineAttendance);
            } while (data.moveToNext());
        }

        return OfflineList;
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + TABLE_NAME;
        db.execSQL(query);
    }



    public Boolean deleteFrom(String username,String userlogtime){

        String equals = " = '";

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "+COL2+equals+ username+" ' "+" AND " +COL6+equals+userlogtime+"'";
        Log.e(TAG,"delete from"+"\n"+query);
        String whereClause = COL2+equals+ username+"'"+" AND "+COL6+equals+userlogtime+"'";
        return db.delete(TABLE_NAME,whereClause,null   )>0;
    }





    public void deleteName() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP";


    }

}
