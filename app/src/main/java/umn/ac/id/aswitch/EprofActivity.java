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

public class EprofActivity extends AppCompatActivity {
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;
    EditText eEmail, eNotelp;
    AppCompatButton submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eprof);
        eEmail = findViewById(R.id.eEmail);
        eNotelp = findViewById(R.id.eNotelp);
        submit = findViewById(R.id.eSubmit);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);
        eEmail.setText(shad.getString("email",""));
        eNotelp.setText(shad.getString("telp", ""));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
    }

    private Boolean validEmail(){
        String val = eEmail.getText().toString();

        if(val.isEmpty()){
            eEmail.setError("Field cannot be empty");
            return false;
        }else{
            eEmail.setError(null);
            return true;
        }
    }

    private Boolean validNotelp(){
        String val = eNotelp.getText().toString();

        if(val.isEmpty()){
            eNotelp.setError("Field cannot be empty");
            return false;
        }else{
            eNotelp.setError(null);
            return true;
        }
    }

    public void editProfile(){
        if(!validEmail() | !validNotelp()){
            return;
        }else{
            goEdit();
        }
    }

    public void goEdit(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        String username = shad.getString("username","");
        String password = shad.getString("password","");
        String email = eEmail.getText().toString();
        String telp = eNotelp.getText().toString();

        RegisHandler handle = new RegisHandler(username,password,email,telp);

        myRef.child(username).setValue(handle);


        shadEdit = shad.edit();
        shadEdit.putString("email", email);
        shadEdit.putString("telp", telp);
        shadEdit.commit();

        Intent edit = new Intent(EprofActivity.this, ProfileActivity.class);
        startActivity(edit);
        setResult(RESULT_OK);
        finish();
    }
}