package com.example.abhinab.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Group9";
    //public static final String TABLE_NAME = "";
    public static final String COL_1 = "Timestamp";
    public static final String COL_2 = "X";
    public static final String COL_3 = "Y";
    public static final String COL_4 = "Z";
    public static final String packageName = "CSE535_ASSIGNMENT2";


    public DatabaseHelper(Context context) {
        super(context, "/sdcard/Android/data/"+packageName+"/"+DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean createAndInsertTable(String table_name, List<DataPoint> xArray,List<DataPoint> yArray,List<DataPoint> zArray){
        boolean errorFlag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println(db.getPath());
        String dropTable = "DROP TABLE IF EXISTS "+ table_name;
        String createTable = "CREATE TABLE "+ table_name +" (Timestamp DOUBLE, X DOUBLE, Y DOUBLE, Z DOUBLE)";
        db.execSQL(dropTable);
        db.execSQL(createTable);

        //Inserting values
        for(int i = 0; i<xArray.size();i++){

            double time = xArray.get(i).getX();
            double xVal = xArray.get(i).getY();
            double yVal = yArray.get(i).getY();
            double zVal = zArray.get(i).getY();


            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,time);
            contentValues.put(COL_2,xVal);
            contentValues.put(COL_3,yVal);
            contentValues.put(COL_4,zVal);

            long result = db.insert(table_name,null,contentValues);

            if (result == -1) {
                errorFlag = true;
                break;
            }




        }

        if(errorFlag)
            return false;
        else
            return true;

    }

    public ArrayList<ArrayList<DataPoint>> showData(SQLiteDatabase db, String table_name){
        //SQLiteDatabase db = this.getReadableDatabase();
        //ArrayList<DataPoint>[] returnArray = new ArrayList<DataPoint>[3];
        //ArrayList<DataPoint>[] returnArray = (ArrayList<DataPoint>[]) new Object[3];
        ArrayList<ArrayList<DataPoint>> returnArray = new ArrayList<ArrayList<DataPoint>>();
        DataPoint newPoint1;
        DataPoint newPoint2;
        DataPoint newPoint3;
        ArrayList<DataPoint> x_array = new ArrayList<DataPoint>();
        ArrayList<DataPoint> y_array = new ArrayList<DataPoint>();
        ArrayList<DataPoint> z_array = new ArrayList<DataPoint>();
        String[] columns = {COL_1,COL_2,COL_3,COL_4};
        Cursor cursor = db.query(table_name,columns,null,null,null,null,null);
        cursor.moveToFirst();
        System.out.println(COL_1+"\t\t\t"+COL_2+"\t\t\t"+COL_3+"\t\t\t"+COL_4);
        do {
            System.out.println(cursor.getString(0)+"\t\t\t\t\t"+cursor.getString(1)+"\t"+cursor.getString(2)+"\t"+cursor.getString(3));
            newPoint1 = new DataPoint(Integer.parseInt(cursor.getString(0)) , Double.parseDouble(cursor.getString(1)));

            newPoint2 = new DataPoint(Integer.parseInt(cursor.getString(0)) , Double.parseDouble(cursor.getString(2)));

            newPoint3 = new DataPoint(Integer.parseInt(cursor.getString(0)) , Double.parseDouble(cursor.getString(3)));

            x_array.add(newPoint1);
            y_array.add(newPoint2);
            z_array.add(newPoint3);

        }while (cursor.moveToNext());

            returnArray.add(x_array);
            returnArray.add(y_array);
            returnArray.add(z_array);


            return returnArray;


    }

    public SQLiteDatabase openDatabase() {
        File databaseFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Downloaded_Group-9.db");
        databaseFile.setWritable(true);
        return SQLiteDatabase
                .openDatabase(databaseFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

    }


}