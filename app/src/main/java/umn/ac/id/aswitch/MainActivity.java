package umn.ac.id.aswitch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    AppCompatButton login_btn, regis_btn;
    SharedPreferences shad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_btn = findViewById(R.id.login_btn);
        regis_btn = findViewById(R.id.regis_btn);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);
        isLogin();

       regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regis = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(regis);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent( getApplicationContext(), LoginActivity.class);
                startActivityForResult(login,2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    public void isLogin(){
        if(shad == null){
            shad = getSharedPreferences("switchPref", MODE_PRIVATE);
        }

        String username = shad.getString("username", "");

        if(username != null && !username.equals("")){
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
    }
}