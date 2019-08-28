package umutg.todoapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static umutg.todoapplication.DatabaseHelper.TABLE_NAME_2;
import static umutg.todoapplication.DatabaseHelper.TABLE_NAME_3;
import static umutg.todoapplication.DatabaseHelper.todo_createtime;
import static umutg.todoapplication.DatabaseHelper.todo_id;
import static umutg.todoapplication.DatabaseHelper.todo_name;
import static umutg.todoapplication.DatabaseHelper.todo_status;
import static umutg.todoapplication.DatabaseHelper.todo_userid;
import static umutg.todoapplication.DatabaseHelper.todoitem_id;
import static umutg.todoapplication.DatabaseHelper.todoitem_status;

public class HomepageActivity extends AppCompatActivity implements RcAdapter.OnItemListener {
    SQLiteDatabase mDatabase;
    DatabaseHelper dbHelper;
    RcAdapter mAdapter;
    FloatingActionButton floatingActionButton,fbuttonRefresh;
    RecyclerView recyclerView;
    String user_id,itemuser_id;
    private static final String TAG = "HomepageActivity";
    private static final String SHARED_PREFS= "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        dbHelper = new DatabaseHelper(HomepageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        try{
            user_id = getIntent().getExtras().getString("id");
            itemuser_id = getIntent().getExtras().getString("itemuser_id");


        }catch (Exception e){

        }


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RcAdapter(this,getAllItems(),this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RcAdapter.OnItemClickListener() {
            @Override
            public void onCheckboxClick(long position, boolean isChecked) {
                if(isChecked){
                    Log.d(TAG, "onCheckboxClick: checkbox checked" + position);
//                    updateStatus1(position);
                }
                else{
                    Log.d(TAG, "onCheckboxClick: checkbox unchecked " + position);
//                    updateStatus0(position);
                }
            }
        });


        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialogHomepage();
            }
        });

        fbuttonRefresh = (FloatingActionButton) findViewById(R.id.floatbuttonRefresh);

        fbuttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.swapCursor(getAllItems());
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

                removeItem((long) viewHolder.itemView.getTag());
                Toast.makeText(HomepageActivity.this,"Task successfully deleted!",Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

    }

    public void addItem(String name,String userid){

            ContentValues cv = new ContentValues();
            cv.put(todo_name, name);
            cv.put(todo_createtime, new SimpleDateFormat("HH:mm:ss  yyyy-MM-dd").format(new Date()));
            cv.put(todo_userid, userid);
            cv.put(todo_status, "0");
            mDatabase.insert(TABLE_NAME_2, null,cv);
            mAdapter.swapCursor(getAllItems());

    }

    public void updateStatus0(long id){

        dbHelper = new DatabaseHelper(HomepageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todo_status,"0");
        mDatabase.update(TABLE_NAME_2,cv,todo_id+"="+ id ,null);
    }
    public void updateStatus1(long id){

        dbHelper = new DatabaseHelper(HomepageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todo_status,"1");
        mDatabase.update(TABLE_NAME_2,cv,todo_id+"="+ id ,null);

    }

    public void showCustomAlertDialogHomepage() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomepageActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.customdialog_homepage,null);
        final EditText mTask = (EditText) mView.findViewById(R.id.todo_name);
        Button mAdd = (Button) mView.findViewById(R.id.button_add);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mAdapter.swapCursor(getAllItems());

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!mTask.getText().toString().isEmpty()){
                    // Buraya db insert atıcaz
                    String task = mTask.getText().toString();
                    addItem(task,user_id);
                    dialog.dismiss();
                    Toast.makeText(HomepageActivity.this,task +" saved",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HomepageActivity.this, "Task name is empty!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Cursor getAllItems(){

        String td = "todouserid";
        String selection = td +"=?";
        String x;
//        if(user_id !=null){
//            x = user_id;
//        }else{
//            x = itemuser_id;
//        }
        String[] selectionArgs = {user_id};
        return mDatabase.query(TABLE_NAME_2,null,selection,selectionArgs,null,null,null);
    }

    public void removeItem(long id){

        mDatabase.delete(TABLE_NAME_2,todo_id+"="+id ,null);

        mAdapter.swapCursor(getAllItems());
    }

    boolean doubleBackToExitPressedOnce = false;

    public void closeApp(){
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please tap again to exit!", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

    }

    @Override
    public void onBackPressed() {
        closeApp();
    }


    @Override
    public void onItemClick(long position) {

            Log.d(TAG,"onItemClick: clicked." + position);

            Intent i = new Intent(this,ItempageActivity.class);
            String mIntent = String.valueOf(position);
            i.putExtra("itemindex",mIntent);
            i.putExtra("itemuserid",user_id);
            startActivity(i);


    }
}
