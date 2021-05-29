package umn.ac.id.aswitch;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    SharedPreferences shad;
    TextView namaUser, saldonow;
    BottomNavigationView bottomapp;
    AppCompatButton profile, saldo, pulsa, ewallet, trf;
    AppCompatImageButton qrbtn;
    private static final int CAM_requestCode = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        finishActivity(2);
        profile = findViewById(R.id.prof_icon);
        saldo = findViewById(R.id.isi_saldo);
        pulsa = findViewById(R.id.pulsa_add);
        ewallet = findViewById(R.id.ewallet_add);
        trf = findViewById(R.id.transfer_to);
        bottomapp = findViewById(R.id.navview);
        namaUser = findViewById(R.id.nama_user);
        saldonow = findViewById(R.id.saldo_kamu);
        qrbtn = findViewById(R.id.qrbutton);

        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        namaUser.setText(shad.getString("username",""));
        bottomapp.setOnNavigationItemSelectedListener(navi);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilepage = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivityForResult(profilepage, 1);
            }
        });

        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saldo = new Intent(DashboardActivity.this, SaldoActivity.class);
                startActivity(saldo);
            }
        });

        pulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, PulsaActivity.class));
            }
        });

        ewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TopupActivity.class));
            }
        });

        trf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, GlobalActivity.class));
            }
        });

        qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CAMERA}, CAM_requestCode);
                }else{
                    Intent qr = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(qr);
                }
            }
        });

    }



    BottomNavigationView.OnNavigationItemSelectedListener navi =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            if (getApplicationContext() == DashboardActivity.this){
                                return true;
                            } return true;
                        case R.id.historyyy:

                            Intent goHistory = new Intent(DashboardActivity.this, HistoryActivity.class);
                            startActivity(goHistory);
                            return true;
                    } return false;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        toRupiah();
        bottomapp.setSelectedItemId(R.id.home);

    }

    private void toRupiah(){
        String username = shad.getString("username","");
        String rIdnow = username+shad.getString("telp","");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("rekening");

        Query checkrId = myRef.orderByKey().equalTo(rIdnow);

        checkrId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Double saldo;
                    if(snapshot.child(rIdnow).child("saldo").getValue() == null){
                        saldo = 0.0;
                    }else{
                        saldo = snapshot.child(rIdnow).child("saldo").getValue(Double.class);
                    }
                    Locale myIndonesianLocale = new Locale("in", "ID");
                    NumberFormat format = NumberFormat.getCurrencyInstance(myIndonesianLocale);
                    String saldoKonvert = format.format(saldo);

                    saldonow.setText(saldoKonvert);
                }else{
                    System.out.println("Data failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAM_requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {} else {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

}