package com.example.activityrecognition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private SQLiteDatabase mySQLiteDatabase = null;
    private DBHelper dbHelper = null;
    private String DBName = "Record.db";

    private Context myContext = null;

    public DBManager (Context context){
        myContext = context;
    }

    //close DB method
    public void closeDB(){
        mySQLiteDatabase.close();
        dbHelper.close();
    }

    public void openDB(){
        try{
            dbHelper = new DBHelper(myContext,DBName,null,1);
            if(dbHelper == null){
                Log.v("Error: ", "Can not open DB");
                return;
            }
            mySQLiteDatabase = dbHelper.getWritableDatabase();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //Insert data to Table
    public long insert (String actName,String startTime){
        long tag = -1;
        try{
            ContentValues contentvalue = new ContentValues();
            contentvalue.put("ActName", actName);
            contentvalue.put("StartTime", startTime);
            tag = mySQLiteDatabase.insert("ActRecord",null,contentvalue);
        }catch (Exception e){
            tag = -1;
            e.printStackTrace();
        }
        return tag;
    }

    //Delete data in Table
    public int delete(long id){
        int tag = 0;
        try{
            tag = mySQLiteDatabase.delete("ActRecord","_Id=?", new String[] {id + ""});

        }catch (Exception e){
            e.printStackTrace();
            tag = -1;
        }
        return tag;
    }

    //Update data in Table
    public int update(int id,String actName,String startTime){
        int tag = 0;
        try{
            ContentValues contentvalue = new ContentValues();
            contentvalue.put("ActName",actName);
            contentvalue.put("StartTime",startTime);
            tag = mySQLiteDatabase.update("ActRecord", contentvalue, "_Id=?",new String [] {id + ""});
        }catch (Exception e){
            e.printStackTrace();
            tag = -1;
        }
        return tag;
    }

    //Get all
    public Cursor getAll(){
        Cursor c = null;
        try{
            String SQL = "select * from actrecord";
            c = mySQLiteDatabase.rawQuery(SQL,null);
        }catch (Exception e){
            e.printStackTrace();
            c = null;
        }
        return c;
    }

    //Get by Id
    public Cursor getById(int id){
        Cursor c = null;
        try{
            String SQL = "select * from actrecord where _id='" + id +"'";
            c = mySQLiteDatabase.rawQuery(SQL,null);
        }catch (Exception e){
            e.printStackTrace();
            c = null;
        }
        return c;
    }

}
