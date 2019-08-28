package umutg.todoapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static umutg.todoapplication.DatabaseHelper.TABLE_NAME_2;
import static umutg.todoapplication.DatabaseHelper.TABLE_NAME_3;
import static umutg.todoapplication.DatabaseHelper.todo_id;
import static umutg.todoapplication.DatabaseHelper.todo_status;
import static umutg.todoapplication.DatabaseHelper.todoitem_createdate;
import static umutg.todoapplication.DatabaseHelper.todoitem_deadline;
import static umutg.todoapplication.DatabaseHelper.todoitem_deadlineint;
import static umutg.todoapplication.DatabaseHelper.todoitem_deadlinestatus;
import static umutg.todoapplication.DatabaseHelper.todoitem_description;
import static umutg.todoapplication.DatabaseHelper.todoitem_expired;
import static umutg.todoapplication.DatabaseHelper.todoitem_id;
import static umutg.todoapplication.DatabaseHelper.todoitem_name;
import static umutg.todoapplication.DatabaseHelper.todoitem_status;
import static umutg.todoapplication.DatabaseHelper.todoitem_todoid;
import static umutg.todoapplication.DatabaseHelper.todoitem_userid;

public class ItempageActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Date dateDeadline,dateAddItem;
    long diff;
    private static final String TAG = "ItempageActivity";
    SQLiteDatabase mDatabase;
    DatabaseHelper dbHelper;
    RcItemAdapter mAdapter;
    RecyclerView recyclerView;
    EditText mTaskName,mDescription,mDeadline;
    FloatingActionButton itemfloatingActionButton;
    Button button_deadline;
    String taskid;
    String user_id;
    Spinner mFilter, mOrder;
    private String[] Filter ={"All","Completed","Not Complete","Expired"};
    private String[] Order ={"Name(ASC)","Name(DESC)","Create Time","Deadline","Status"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itempage);
        Log.d(TAG, "onCreate: called.");

        dbHelper = new DatabaseHelper(ItempageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        try{
            taskid = getIntent().getExtras().getString("itemindex");
            user_id = getIntent().getExtras().getString("itemuserid");

            Log.d(TAG, "onCreate: TASK ID"+taskid);

        }catch (Exception e){

        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RcItemAdapter(this,getAllItems());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RcItemAdapter.OnItemClickListener() {
            @Override
            public void onCheckboxClick(long position, boolean isChecked) {





                if(isChecked){
                    Log.d(TAG, "onCheckboxClick: checkbox checked" + position);
                    updateStatus1(position);

                    String[] selectionArgs2 = {taskid,user_id};
                    Cursor cursor1 = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs2);

                    int count = cursor1.getCount();
                    int countstatus;

                    Log.d(TAG, "Count: "+count);

                    String[] selectionArgs1 = {taskid,user_id,"1"};
                    Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_status+" = ?" ,selectionArgs1);

                    countstatus = cursor.getCount();

                    Log.d(TAG, "CountStatus: " + countstatus);

                    if(countstatus == count){

                        int task_id = Integer.valueOf(taskid);
                        updatetodoStatus1(task_id);
//                        mAdapter.swapCursor(getAllItems());
                        Log.d(TAG, "TodoitemStatus Working _1_  "+task_id);

                    }
                    else{

                        int task_id = Integer.valueOf(taskid);
                        updatetodoStatus0(task_id);
//                        mAdapter.swapCursor(getAllItems());
                        Log.d(TAG, "TodoitemStatus Working _0_ "+task_id);
                    }

                }
                else{
                    Log.d(TAG, "onCheckboxClick: checkbox unchecked " + position);
                    updateStatus0(position);

                    String[] selectionArgs2 = {taskid,user_id};
                    Cursor cursor1 = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs2);

                    int count = cursor1.getCount();
                    int countstatus;

                    Log.d(TAG, "Count: "+count);

                    String[] selectionArgs1 = {taskid,user_id,"1"};
                    Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_status+" = ?" ,selectionArgs1);

                    countstatus = cursor.getCount();

                    Log.d(TAG, "CountStatus: " + countstatus);

                    if(countstatus == count){

                        int task_id = Integer.valueOf(taskid);
                        updatetodoStatus1(task_id);
//                        mAdapter.swapCursor(getAllItems());
                        Log.d(TAG, "TodoitemStatus Working _1_  "+task_id);

                    }
                    else{

                        int task_id = Integer.valueOf(taskid);
                        updatetodoStatus0(task_id);
//                        mAdapter.swapCursor(getAllItems());
                        Log.d(TAG, "TodoitemStatus Working _0_ "+task_id);
                    }

                }
            }

        });

        mFilter = (Spinner) findViewById(R.id.spinner_filter);
        mOrder = (Spinner) findViewById(R.id.spinner_order);

        //Creating the ArrayAdapter instance having the filter and order list
        ArrayAdapter adapterFilter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Filter);
        ArrayAdapter adapterOrder = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Order);

        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterOrder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        mFilter.setAdapter(adapterFilter);
        mOrder.setAdapter(adapterOrder);

        mFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (parent.getSelectedItemPosition()) {
                    case 0:
                        if(mOrder.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsAZ());
                        }
                        else if(mOrder.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsZA());
                        }
                        else if (mOrder.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsCreateTime());
                        }
                        else if(mOrder.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsDeadline());
                        }
                        else if(mOrder.getSelectedItemPosition()==4){
                            mAdapter.swapCursor(getAllItemsStatus());
                        }

                        break;
                    case 1:
                        if(mOrder.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsCompletedASC());
                        }
                        else if(mOrder.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsCompletedDESC());
                        }
                        else if (mOrder.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsCompleted());
                        }
                        else if(mOrder.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsCompletedDeadline());
                        }
                        else if(mOrder.getSelectedItemPosition()==4){
                            mAdapter.swapCursor(getAllItemsStatusCompleted());
                        }

                        break;
                    case 2:
                        if(mOrder.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsNotCompletedASC());
                        }
                        else if(mOrder.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsNotCompletedDESC());
                        }
                        else if (mOrder.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsNotComplete());
                        }
                        else if(mOrder.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsNotCompletedDeadline());
                        }
                        else if(mOrder.getSelectedItemPosition()==4){
                            mAdapter.swapCursor(getAllItemsStatusNotCompleted());
                        }

                        break;
                    case 3:
                        if(mOrder.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsExpiredASC());
                        }
                        else if(mOrder.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsExpiredDESC());
                        }
                        else if (mOrder.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsExpired());
                        }
                        else if(mOrder.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsExpiredDeadline());
                        }
                        else if(mOrder.getSelectedItemPosition()==4){
                            mAdapter.swapCursor(getAllItemsExpiredStatus());
                        }
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (parent.getSelectedItemPosition()) {
                    case 0:

                        if(mFilter.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsAZ());
                        }
                        else if(mFilter.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsCompletedASC());
                        }
                        else if(mFilter.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsNotCompletedASC());
                        }
                        else {
                            mAdapter.swapCursor(getAllItemsExpiredASC());
                        }
                        break;
                    case 1:

                        if(mFilter.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsZA());
                        }
                        else if(mFilter.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsCompletedDESC());
                        }
                        else if(mFilter.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsNotCompletedDESC());
                        }
                        else if(mFilter.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsExpiredDESC());
                        }
                        break;
                    case 2:

                        if(mFilter.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItems());
                        }
                        else if(mFilter.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsCompleted());
                        }
                        else if(mFilter.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsNotComplete());
                        }
                        else if(mFilter.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsExpired());
                        }
                        break;
                    case 3:
                        if(mFilter.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsDeadline());
                        }
                        else if(mFilter.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsCompletedDeadline());
                        }
                        else if(mFilter.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsNotCompletedDeadline());
                        }
                        else if(mFilter.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsExpiredDeadline());
                        }
                        break;
                    case 4:

                        if(mFilter.getSelectedItemPosition()==0){
                            mAdapter.swapCursor(getAllItemsStatus());
                        }
                        else if(mFilter.getSelectedItemPosition()==1){
                            mAdapter.swapCursor(getAllItemsStatusCompleted());
                        }
                        else if(mFilter.getSelectedItemPosition()==2){
                            mAdapter.swapCursor(getAllItemsStatusNotCompleted());
                        }
                        else if(mFilter.getSelectedItemPosition()==3){
                            mAdapter.swapCursor(getAllItemsExpiredStatus());
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        itemfloatingActionButton = (FloatingActionButton) findViewById(R.id.floatbuttonItem);
        itemfloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCustomAlertDialogItempage();
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
                Toast.makeText(ItempageActivity.this,"Task successfully deleted!",Toast.LENGTH_SHORT).show();
            }

        }).attachToRecyclerView(recyclerView);
        
    }
    

    public void addItem(String name,String description,String deadline,int deadlineint,String deadlinestatus,String taskitemid,String itemuserid){


        ContentValues cv = new ContentValues();
        cv.put(todoitem_name, name);
        cv.put(todoitem_description, description);
        cv.put(todoitem_deadline, deadline);
        cv.put(todoitem_deadlineint, deadlineint);
        cv.put(todoitem_deadlinestatus, deadlinestatus);
        cv.put(todoitem_createdate, new SimpleDateFormat("HH:mm:ss  yyyy-MM-dd").format(new Date()));
        cv.put(todoitem_status, "0");
        cv.put(todoitem_expired, "0");
        cv.put(todoitem_todoid, taskitemid);
        cv.put(todoitem_userid, itemuserid);
        mDatabase.insert(TABLE_NAME_3, null,cv);

        String[] selectionArgs2 = {taskid,user_id};
        Cursor cursor1 = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs2);

        int count = cursor1.getCount();
        int countstatus;

        Log.d(TAG, "Count: "+count);

        String[] selectionArgs1 = {taskid,user_id,"1"};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_status+" = ?" ,selectionArgs1);

        countstatus = cursor.getCount();

        Log.d(TAG, "CountStatus: " + countstatus);

        if(countstatus == count){

            int task_id = Integer.valueOf(taskid);
            updatetodoStatus1(task_id);
//            mAdapter.swapCursor(getAllItems());
            Log.d(TAG, "TodoitemStatus Working _1_  "+task_id);

        }
        else{

            int task_id = Integer.valueOf(taskid);
            updatetodoStatus0(task_id);
//            mAdapter.swapCursor(getAllItems());
            Log.d(TAG, "TodoitemStatus Working _0_ "+task_id);
        }


        mAdapter.swapCursor(getAllItems());

    }

    public void removeItem(long id){

        mDatabase.delete(TABLE_NAME_3,todoitem_id+"="+id ,null);

        String[] selectionArgs2 = {taskid,user_id};
        Cursor cursor1 = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs2);

        int count = cursor1.getCount();
        int countstatus;

        Log.d(TAG, "Count: "+count);

        String[] selectionArgs1 = {taskid,user_id,"1"};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_status+" = ?" ,selectionArgs1);

        countstatus = cursor.getCount();

        Log.d(TAG, "CountStatus: " + countstatus);

        if(countstatus == count){

            int task_id = Integer.valueOf(taskid);
            updatetodoStatus1(task_id);
            mAdapter.swapCursor(getAllItems());
            Log.d(TAG, "TodoitemStatus Working _1_  "+task_id);

        }
        else{

            int task_id = Integer.valueOf(taskid);
            updatetodoStatus0(task_id);
            mAdapter.swapCursor(getAllItems());
            Log.d(TAG, "TodoitemStatus Working _0_ "+task_id);
        }

        mAdapter.swapCursor(getAllItems());
    }

    public void updateDeadlineStatus(int id){

        ContentValues values = new ContentValues();
        values.put(todoitem_deadlinestatus, "1");
        mDatabase.update(TABLE_NAME_3,values,todoitem_id+"="+ id ,null);
//        mAdapter.swapCursor(getAllItems());

    }


    public void updatetodoStatus0(int id){


        dbHelper = new DatabaseHelper(ItempageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todo_status,"0");
        mDatabase.update(TABLE_NAME_2,cv,todo_id+"="+ id ,null);
        mAdapter.swapCursor(getAllItems());
    }

    public void updatetodoStatus1(int id){


        dbHelper = new DatabaseHelper(ItempageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todo_status,"1");
        mDatabase.update(TABLE_NAME_2,cv,todo_id+"="+ id ,null);
//        mAdapter.swapCursor(getAllItems());
    }


    public void updateStatus0(long id){


        dbHelper = new DatabaseHelper(ItempageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todoitem_status,"0");
        mDatabase.update(TABLE_NAME_3,cv,todoitem_id+"="+ id ,null);
//        mAdapter.swapCursor(getAllItems());

    }
    public void updateStatus1(long id){


        dbHelper = new DatabaseHelper(ItempageActivity.this);
        mDatabase = dbHelper.getWritableDatabase();
        mDatabase = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(todoitem_status,"1");
        mDatabase.update(TABLE_NAME_3,cv,todoitem_id+"="+ id ,null);
//        mAdapter.swapCursor(getAllItems());
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();

    }

    public void showCustomAlertDialogItempage() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItempageActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.customdialog_itempage,null);

         mTaskName = (EditText) mView.findViewById(R.id.todoitem_name);
         mDescription = (EditText) mView.findViewById(R.id.todoitem_description);
         mDeadline = (EditText) mView.findViewById(R.id.todoitem_deadline);

        button_deadline = (Button) mView.findViewById(R.id.button_deadline);
        mDeadline.setEnabled(false);

        button_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button mAdd = (Button) mView.findViewById(R.id.button_additem);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mTaskName.getText().toString().isEmpty()){
                    if(!mDescription.getText().toString().isEmpty()){
                        if(!mDeadline.getText().toString().isEmpty()){
                            // Buraya db insert atıcaz
                            String taskname = mTaskName.getText().toString();
                            String taskDescription = mDescription.getText().toString();
                            String taskDeadline = mDeadline.getText().toString();
                            int taskDeadlineint;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date now = new Date();
                            String simdi = dateFormat.format(now);

                            try {
                                now = dateFormat.parse(simdi);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                dateAddItem = dateFormat.parse(taskDeadline);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            taskDeadlineint =(int) dateAddItem.getTime();

                            long diff = dateAddItem.getTime() - now.getTime();
                            long diffDays = diff / (24 * 60 * 60 * 1000);
                            int x = (int) diffDays+1;
                            Log.d(TAG, "long diffDays: "+diffDays + "   int diffDays:  "+x);
                            String i;
                            if(x<=0){
                                i="1";
                                Log.d(TAG, "diff<0 : " + diff);
                            }
                            else{
                                i="0";
                                Log.d(TAG, "diff>0: " + diff);
                            }
                            addItem(taskname,taskDescription,taskDeadline,x,i,taskid,user_id);
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }else{
                            Toast.makeText(ItempageActivity.this, "Task deadline is empty!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ItempageActivity.this, "Task description is empty!",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ItempageActivity.this, "Task name is empty!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Cursor getAllItems(){
        String[] selectionArgs = {taskid,user_id};

        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";

        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,null);

    }


    private Cursor getAllItemsAZ(){

        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";
        String[] selectionArgs = {taskid,user_id};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" ASC");

    }
    private Cursor getAllItemsZA(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";
        String[] selectionArgs = {taskid,user_id};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" DESC");

    }
    private Cursor getAllItemsCompleted(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,null);
    }

    private Cursor getAllItemsCompletedASC(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" ASC");
    }

    private Cursor getAllItemsCompletedDESC(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" DESC");
    }

    private Cursor getAllItemsCompletedDeadline(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_deadlineint+" ASC");
    }

    private Cursor getAllItemsNotComplete(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"0"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,null);
    }

    private Cursor getAllItemsNotCompletedASC(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"0"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" ASC");
    }

    private Cursor getAllItemsNotCompletedDESC(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"0"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_name+" DESC");
    }

    private Cursor getAllItemsNotCompletedDeadline(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"0"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_deadlineint+" ASC");
    }

    private Cursor getAllItemsCreateTime(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";
        String[] selectionArgs = {taskid,user_id};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,null);

    }

    private Cursor getAllItemsDeadline(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";
        String[] selectionArgs = {taskid,user_id};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_deadlineint+" ASC");
    }

    private Cursor getAllItemsStatus(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?";
        String[] selectionArgs = {taskid,user_id};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null, todoitem_status+" ASC");
    }

    private Cursor getAllItemsStatusCompleted(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_deadlineint+" ASC");
    }
    private Cursor getAllItemsStatusNotCompleted(){
        String selection = todoitem_todoid + "=?" + " and " + todoitem_userid + "=?" + " and " + todoitem_status + " =?";
        String[] selectionArgs = {taskid,user_id,"0"};
        return mDatabase.query(TABLE_NAME_3,null,selection,selectionArgs,null,null,todoitem_deadlineint+" ASC");
    }

    private Cursor getAllItemsExpiredStatus(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String simdi = dateFormat.format(now);
        String[] selectionArgs1 = {taskid,user_id};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs1);
        try {
            now = dateFormat.parse(simdi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while(cursor.moveToNext()){
            int index;

            index = cursor.getColumnIndexOrThrow(todoitem_deadline);
            String deadlineStr = cursor.getString(index);
            try {
                dateDeadline = dateFormat.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diff = dateDeadline.getTime() - now.getTime();

            int i = (int) diff;

            if(i<0){
                updateDeadlineStatus(index);
            }
        }

        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_deadlinestatus+" = ?" ,selectionArgs);
    }


    private Cursor getAllItemsExpired(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String simdi = dateFormat.format(now);
        String[] selectionArgs1 = {taskid,user_id};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs1);
        try {
            now = dateFormat.parse(simdi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while(cursor.moveToNext()){
            int index;
            index = cursor.getColumnIndexOrThrow(todoitem_deadline);
            String deadlineStr = cursor.getString(index);
            Log.d(TAG, "deadlineStr: "+ deadlineStr);
            try {
                dateDeadline = dateFormat.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diff = dateDeadline.getTime() - now.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            int x = (int) diffDays+1;
            int i = (int) diff;
            Log.d(TAG, "diff : "+ diff +"   i : " +i + "   diffDays: "+x  +"   index : "+index);

            if(x<0){
                updateDeadlineStatus(index);
            }
        }

        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_deadlinestatus+" = ?" ,selectionArgs);
    }

    private Cursor getAllItemsExpiredASC(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String simdi = dateFormat.format(now);
        String[] selectionArgs1 = {taskid,user_id};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs1);
        try {
            now = dateFormat.parse(simdi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while(cursor.moveToNext()){
            int index;
            index = cursor.getColumnIndexOrThrow(todoitem_deadline);
            String deadlineStr = cursor.getString(index);
            Log.d(TAG, "deadlineStr: "+ deadlineStr);
            try {
                dateDeadline = dateFormat.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diff = dateDeadline.getTime() - now.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            int x = (int) diffDays+1;
            int i = (int) diff;
            Log.d(TAG, "diff : "+ diff +"   i : " +i + "   diffDays: "+x  +"   index : "+index);

            if(x<0){
                updateDeadlineStatus(index);
            }
        }

        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_deadlinestatus+" = ? ORDER BY "+ todoitem_name +" ASC" ,selectionArgs);
    }

    private Cursor getAllItemsExpiredDESC(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String simdi = dateFormat.format(now);
        String[] selectionArgs1 = {taskid,user_id};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs1);
        try {
            now = dateFormat.parse(simdi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while(cursor.moveToNext()){
            int index;

            index = cursor.getColumnIndexOrThrow(todoitem_deadline);
            String deadlineStr = cursor.getString(index);
            try {
                dateDeadline = dateFormat.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diff = dateDeadline.getTime() - now.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            int x = (int) diffDays+1;
            int i = (int) diff;

            if(x<0){
                updateDeadlineStatus(index);
            }
        }

        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_deadlinestatus+" = ?  ORDER BY "+todoitem_name +" DESC" ,selectionArgs);
    }

    private Cursor getAllItemsExpiredDeadline(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String simdi = dateFormat.format(now);
        String[] selectionArgs1 = {taskid,user_id};
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ?" ,selectionArgs1);
        try {
            now = dateFormat.parse(simdi);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while(cursor.moveToNext()){
            int index;

            index = cursor.getColumnIndexOrThrow(todoitem_deadline);
            String deadlineStr = cursor.getString(index);
            try {
                dateDeadline = dateFormat.parse(deadlineStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diff = dateDeadline.getTime() - now.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            int x = (int) diffDays+1;
            int i = (int) diff;

            if(x<0){
                updateDeadlineStatus(index);
            }
        }

        String[] selectionArgs = {taskid,user_id,"1"};
        return mDatabase.rawQuery("SELECT * FROM "+TABLE_NAME_3+" WHERE "+todoitem_todoid+" = ? AND "+todoitem_userid+" = ? AND "+todoitem_deadlinestatus+" = ?  ORDER BY "+todoitem_deadlineint +" ASC" ,selectionArgs);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String deadline="";
//        String deadline = dayOfMonth + "-" + month + "-" +year;
        int mMonth =month+1;

        if(mMonth<10 && dayOfMonth<10){
            deadline = year + "-" + "0"+mMonth + "-" +"0"+dayOfMonth;
        }
        else if(mMonth<10 && dayOfMonth>=10){
            deadline = year + "-" + "0"+mMonth + "-" + dayOfMonth;
        }
        else if(mMonth>=10 && dayOfMonth<10){
            deadline = year + "-" + mMonth + "-" +"0"+dayOfMonth;
        }
        else if(mMonth>=10 && dayOfMonth>=10){
            deadline = year + "-" + mMonth + "-" +dayOfMonth;
        }

        mDeadline.setText(deadline);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();


    }

}
