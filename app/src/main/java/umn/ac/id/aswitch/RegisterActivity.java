package umn.ac.id.aswitch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText username_input, password_input, email_input, telp_input;
    AppCompatButton regis_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username_input = findViewById(R.id.regis_input_uname);
        password_input = findViewById(R.id.regis_input_password);
        email_input = findViewById(R.id.regis_input_email);
        telp_input = findViewById(R.id.regis_input_telp);
        regis_btn = findViewById(R.id.regis_button);

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regisUser();
            }
        });
    }

    private Boolean validUser(){
        String val = username_input.getText().toString();

        if(val.isEmpty()){
            username_input.setError("Field cannot be empty");
            return false;
        }else{
            username_input.setError(null);
            return true;
        }
    }

    private Boolean validPassword(){
        String val = password_input.getText().toString();

        if(val.isEmpty()){
            password_input.setError("Field cannot be empty");
            return false;
        }else{
            password_input.setError(null);
            return true;
        }
    }

    private Boolean validEmail(){
        String val = email_input.getText().toString();

        if(val.isEmpty()){
            email_input.setError("Field cannot be empty");
            return false;
        }else{
            email_input.setError(null);
            return true;
        }
    }

    private Boolean validTelp(){
        String val = telp_input.getText().toString();

        if(val.isEmpty()){
            telp_input.setError("Field cannot be empty");
            return false;
        }else{
            telp_input.setError(null);
            return true;
        }
    }

    public void regisUser(){
        if(!validUser() | !validPassword() | !validEmail() | !validTelp()){
            return;
        }else{
            goRegis();
        }
    }

    public void goRegis(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        String email = email_input.getText().toString();
        String telp = telp_input.getText().toString();

        RegisHandler handle = new RegisHandler(username,password,email,telp);

        myRef.child(username).setValue(handle);

        DatabaseReference myRef2 = database.getReference("rekening");
        String rId = username+telp;
        Double saldo = 0.0;

        RekHandler handle2 = new RekHandler(saldo, username);
        myRef2.child(rId).setValue(handle2);
        finish();
    }
}