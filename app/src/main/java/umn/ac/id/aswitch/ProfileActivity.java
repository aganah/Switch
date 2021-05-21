package umn.ac.id.aswitch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    AppCompatButton profile, pass, logout;
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;
    TextView namaprof;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile = findViewById(R.id.change_profile);
        pass = findViewById(R.id.change_pass);
        namaprof = findViewById(R.id.nama_profile);
        logout = findViewById(R.id.logout_btn);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        namaprof.setText(shad.getString("username",""));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profDetail = new Intent(ProfileActivity.this, EprofActivity.class);
                startActivityForResult(profDetail,3);
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passDetail = new Intent(ProfileActivity.this, EpassActivity.class);
                startActivityForResult(passDetail, 3);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadEdit = shad.edit();
                shadEdit.clear();
                shadEdit.commit();

                Intent backtoMain = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(backtoMain);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }
}