package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit,shadEdit1;
    EditText username, password;
    AppCompatButton login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.input_uname);
        password = findViewById(R.id.input_password);
        login_btn = findViewById(R.id.login_button);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);
        isLogin();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    public void isLogin(){
        if(shad == null){
            shad = getSharedPreferences("switchPref", MODE_PRIVATE);
        }

        String username = shad.getString("username", "");

        if(username != null && !username.equals("")){
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
            finishActivity(2);
        }
    }

    private Boolean validUser(){
        String val = username.getText().toString();

        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    private Boolean validPassword(){
        String val = password.getText().toString();

        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    public void loginUser(){
        if(!validUser() | !validPassword()){
            return;
        }else{
            isUser();
        }
    }

    public void isUser(){
        String enterUsername = username.getText().toString().trim();
        String enterPassword = password.getText().toString().trim();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = myRef.orderByChild("username").equalTo(enterUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    username.setError(null);
                    String dbPass = snapshot.child(enterUsername).child("password").getValue(String.class);
                    String dbNotelp = snapshot.child(enterUsername).child("telp").getValue(String.class);
                    String dbEmail = snapshot.child(enterUsername).child("email").getValue(String.class);
                    if(dbPass.equals(enterPassword)){
                        password.setError(null);

                        String idRek = enterUsername+dbNotelp;
                        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("rekening");
                        Query checkRek = myRef1.orderByKey().equalTo(idRek);
                        checkRek.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Double saldoUser = snapshot.child(enterUsername+dbNotelp).child("saldo").getValue(Double.class);

                                    shadEdit = shad.edit();
                                    shadEdit.putString("username", enterUsername);
                                    shadEdit.putString("password", dbPass);
                                    shadEdit.putString("email", dbEmail);
                                    shadEdit.putString("telp", dbNotelp);
                                    shadEdit.putString("saldo", saldoUser.toString());
                                    shadEdit.commit();

                                    Intent dashboard = new Intent(LoginActivity.this, DashboardActivity.class);
                                    startActivity(dashboard);
                                    setResult(RESULT_OK);
                                    finish();
                                }else{
                                    System.out.println("Data failed");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }else{
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                }else{
                    username.setError("Username not found");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}