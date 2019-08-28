package umutg.todoapplication;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText username,password,repassword;
    Button register;
    TextInputLayout inputUsername,inputPassword,inputRepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        inputUsername = (TextInputLayout)findViewById(R.id.register_username);
        inputPassword = (TextInputLayout) findViewById(R.id.input_password);
        inputRepassword = (TextInputLayout) findViewById(R.id.input_repassword);
        username =(EditText) findViewById(R.id.username);
        password =(EditText) findViewById(R.id.password);
        repassword =(EditText) findViewById(R.id.repassword);
        register = (Button) findViewById(R.id.button_register);




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                String repwd = repassword.getText().toString().trim();

//                if(pwd.equals(repwd) && db.registerCheckUser(user)){
//                    long val = db.addUser(user,pwd);
//
//                    if(val >0){
//                        Toast.makeText(RegisterActivity.this,"You have registered",Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
//                    else{
//                        Toast.makeText(RegisterActivity.this,"Registeration Error",Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//                else {
//                    Toast.makeText(RegisterActivity.this,"Password is not matching",Toast.LENGTH_SHORT).show();
//                }

                if(db.registerCheckUser(user)){
                    if(pwd.equals(repwd)){

                        long val = db.addUser(user,pwd);

                        if(val >0){
                            Toast.makeText(RegisterActivity.this,"You have registered",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Registeration Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Password is not matching",Toast.LENGTH_SHORT).show();
                    }


                }
                else {
                    Toast.makeText(RegisterActivity.this,"This username already exists.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
