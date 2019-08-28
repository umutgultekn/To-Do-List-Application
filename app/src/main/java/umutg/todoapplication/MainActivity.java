package umutg.todoapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText login_username,login_password;
    Button login;
    TextView register;
    DatabaseHelper dbHelper;
    TextInputLayout inputUsername,inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        inputUsername = (TextInputLayout) findViewById(R.id.login_input_username);
        inputPassword= (TextInputLayout) findViewById(R.id.login_input_password);
        login_username =(EditText) findViewById(R.id.login_username);
        login_password = (EditText)findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.button_login);
        register = (TextView) findViewById(R.id.register);

        register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent i = new Intent(MainActivity.this,RegisterActivity.class  );
                startActivity(i);
                return true;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = login_username.getText().toString().trim();
                String password = login_password.getText().toString().trim();
                boolean res = dbHelper.checkUser(user,password);
                if(res == true){
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.rawQuery("Select userid from registeruser where username=?", new String[]{user});
                    cursor.moveToNext();

                    Toast.makeText(MainActivity.this,"Succesfully Logged In",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this,HomepageActivity.class);
                    String x = cursor.getString(cursor.getColumnIndex("userid"));
                    i.putExtra("id",x);
                    startActivity(i);
//                    finish();

                }
                else{
                    Toast.makeText(MainActivity.this,"Username/Password is incorrect.",Toast.LENGTH_SHORT).show();
                }



            }
        });
    }


}
