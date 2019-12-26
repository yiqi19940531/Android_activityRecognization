package com.example.activityrecognition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private String ActivityRecordTableName = "ActRecord";
    private Context myContext = null;
    private String SQL = "create table if not exists " + ActivityRecordTableName +
            "(_Id integer primary key autoincrement, " +
            "ActName varchar," +
            "StartTime varchar)";


    public DBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
