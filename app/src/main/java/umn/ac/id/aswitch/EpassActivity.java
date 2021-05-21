package umn.ac.id.aswitch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EpassActivity extends AppCompatActivity {
    EditText eNpass, econfNpass;
    AppCompatButton submit;
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epass);
        eNpass = findViewById(R.id.eNpass);
        econfNpass = findViewById(R.id.econfirmNpass);
        submit = findViewById(R.id.eSubmit2);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPassword();
            }
        });
    }

    private Boolean validNpass(){
        String val = eNpass.getText().toString();
        String dbPass = shad.getString("password","");

        if(val.isEmpty()){
            eNpass.setError("Field cannot be empty");
            return false;
        }else{
            if(val.equals(dbPass)){
                eNpass.setError(null);
                return true;
            }else{
                eNpass.setError("Password salah");
                return false;
            }
        }
    }

    private Boolean validConfpass(){
        String val = econfNpass.getText().toString();

        if(val.isEmpty()){
            econfNpass.setError("Field cannot be empty");
            return false;
        }else{
            econfNpass.setError(null);
            return true;
        }
    }

    public void editPassword(){
        if(!validNpass() | !validConfpass()){
            return;
        }else{
            goEpass();
        }
    }

    public void goEpass(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        String username = shad.getString("username","");
        String password = econfNpass.getText().toString();
        String email = shad.getString("email","");
        String telp = shad.getString("telp","");

        RegisHandler handle = new RegisHandler(username,password,email,telp);

        myRef.child(username).setValue(handle);

        shadEdit = shad.edit();
        shadEdit.putString("password", password);
        shadEdit.commit();

        Intent edit = new Intent(EpassActivity.this, ProfileActivity.class);
        startActivity(edit);
        setResult(RESULT_OK);
        finish();
    }
}