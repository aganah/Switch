package umn.ac.id.aswitch;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    AppCompatButton profile, saldo, pulsa, ewallet, trf;
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

        namaUser = findViewById(R.id.nama_user);
        saldonow = findViewById(R.id.saldo_kamu);

        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        namaUser.setText(shad.getString("username",""));

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        toRupiah();
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
}