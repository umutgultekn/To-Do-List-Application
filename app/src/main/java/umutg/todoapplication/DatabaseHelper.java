package umutg.todoapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todolist.db";
    public static final String TABLE_NAME_1 = "registeruser";
    public static final String TABLE_NAME_2 = "itemtodoumut";
    public static final String TABLE_NAME_3 = "itemtodogultekin";
    public static final String COL_1 = "userid";
    public static final String COL_2 = "username";
    public static final String COL_3 = "password";
    public static final String todo_id = "todoid";
    public static final String todo_name = "itemname";
    public static final String todo_createtime = "createtime";
    public static final String todo_userid = "todouserid";
    public static final String todo_status = "status";
    public static final String todoitem_id = "todoitem_id";
    public static final String todoitem_name = "todoitem_name";
    public static final String todoitem_description = "todoitem_description";
    public static final String todoitem_deadline = "todoitem_deadline";
    public static final String todoitem_deadlineint = "todoitem_deadlineint";
    public static final String todoitem_deadlinestatus = "todoitem_deadlinestatus";
    public static final String todoitem_createdate = "todoitem_createdate";
    public static final String todoitem_expired = "todoitem_expired";
    public static final String todoitem_status = "todoitem_status";
    public static final String todoitem_todoid = "todoitem_todoid";
    public static final String todoitem_userid = "todoitem_userid";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(" CREATE TABLE "
                + TABLE_NAME_1 + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_2 + " TEXT,"
                + COL_3 + " TEXT)");

        db.execSQL(" CREATE TABLE "
                + TABLE_NAME_2 +"("
                + todo_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + todo_name + " TEXT,"
                + todo_createtime + " TEXT,"
                + todo_status + " TEXT,"
                + todo_userid + " TEXT)");

        db.execSQL(" CREATE TABLE "
                + TABLE_NAME_3 +"("
                + todoitem_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + todoitem_name + " TEXT,"
                + todoitem_description + " TEXT,"
                + todoitem_deadline + " TEXT,"
                + todoitem_deadlineint + " INTEGER,"
                + todoitem_deadlinestatus + " TEXT,"
                + todoitem_createdate + " TEXT,"
                + todoitem_expired + " TEXT,"
                + todoitem_status + " TEXT,"
                + todoitem_todoid + " TEXT,"
                + todoitem_userid + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_NAME_1);
        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_NAME_2);
        db.execSQL(" DROP TABLE IF EXISTS " +TABLE_NAME_3);
        onCreate(db);
    }

    public long addUser(String user , String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,user);
        contentValues.put(COL_3,password);
        long res = db.insert(TABLE_NAME_1,null, contentValues);
//        db.close();
        return res;
    }

    public long addTodoItem(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(todo_name , name);
//        contentValues.put(todo_createtime , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        long res = db.insert(TABLE_NAME_2,null, contentValues);
        db.close();
        return res;
    }

    public boolean checkUser(String username, String password ) {
        String[] columns = {COL_1};

        SQLiteDatabase db = getReadableDatabase();

        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";

        String[] selectionArgs = {username,password};

        Cursor cursor = db.query(TABLE_NAME_1,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean registerCheckUser(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from registeruser where username=?",new String[] {name});
        if(cursor.getCount()>0){
            return false;
        }else{
            return true;
        }
    }

    public long VeriEkle(String name){
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(todo_name, name);
            cv.put(todo_createtime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            long asd = db.insert(TABLE_NAME_2, null,cv);

        db.close();
        return asd;
    }

    public List<String> VeriListele(){
        List<String> veriler = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] stunlar = {todo_id,todo_name,todo_createtime};
            Cursor cursor = db.query(TABLE_NAME_2, stunlar,null,null,null,null,null);
            while (cursor.moveToNext()){
                veriler.add(cursor.getInt(0)
                        + " - "
                        + cursor.getString(1)
                        + " - "
                        + cursor.getString(2)
                        );
            }
        }catch (Exception e){
        }
        db.close();
        return veriler;
    }
}
