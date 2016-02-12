package com.example.mohmohhtun.mapapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by mohmohhtun on 2/2/16.
 */
public class PlaceHandler extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "PLACE";
    private static final String ID = "ID";
    private static final String TABLE_PLACE = "TB_PLACE";
    private static final String PLACE_NAME = "PLACE_NAME";
    private static final String LATT = "LATT";
    private static final String LON = "LONG";
    private static final String INOUT = "INOUT";

    public PlaceHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_HIGHLIGHT_TABLE = "CREATE TABLE " + TABLE_PLACE + "("
                + ID + " INTEGER, " + PLACE_NAME + " TEXT, "+LATT + " TEXT, "+LON + " TEXT, "+INOUT +" TEXT, PRIMARY KEY("+ID+", "+PLACE_NAME+")"+
                ")";
        sqLiteDatabase.execSQL(CREATE_HIGHLIGHT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
        onCreate(sqLiteDatabase);
    }

    public void removePlace(String name,String latt,String longi){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PLACE,String.format("%s = ? AND %s = ? AND %s=?",PLACE_NAME,LATT,LON),new String[]{name,latt,longi});

        //db.execSQL(query);
    }

    public void addPlace(String placename,String latt,String lon,String inout){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO "+TABLE_PLACE +"("+PLACE_NAME+","+LATT+","+LON+","+INOUT+") VALUES('"+placename+"','"+latt+"','"+lon+"','"+inout+"')";

        db.execSQL(query);

        //db.insert(TABLE_SHOP,null,values);
    }


    public ArrayList<Place> getPlaces(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Place> list = new ArrayList<>();
        String[] arr= new String[1];
        String countQuery = "SELECT  * FROM " + TABLE_PLACE;
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            for (int i = 0 ;i < cursor.getCount() ; i ++){
                Place p = new Place(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
                list.add(p);
                cursor.moveToNext();
            }

            cursor.close();
            return list;
        }
        else{
            cursor.close();
            return list;
        }
    }

}
