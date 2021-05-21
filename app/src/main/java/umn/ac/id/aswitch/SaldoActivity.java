package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaldoActivity extends AppCompatActivity {
    private static final String TAG = "SaldoActivity";
    AppCompatButton isi, button10, button25, button50, button100;
    EditText nominal;
    SharedPreferences shad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo);

        isi = findViewById(R.id.buttonisi);
        button10 = findViewById(R.id.button10k);
        button25 = findViewById(R.id.button25k);
        button50 = findViewById(R.id.button50k);
        button100 = findViewById(R.id.button100k);
        nominal = findViewById(R.id.nominal_saldo);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nominal.setText("10000");
            }
        });

        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nominal.setText("25000");
            }
        });

        button50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nominal.setText("50000");
            }
        });

        button100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nominal.setText("100000");
            }
        });

        isi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isiSaldo();
            }
        });
    }

    public Boolean isKosong(){
        String val = nominal.getText().toString();

        if(val.isEmpty()){
            nominal.setError("Field cannot be empty");
            return false;
        }else{
            nominal.setError(null);
            return true;
        }
    }

    public void isiSaldo(){
        if(!isKosong()){
            return;
        }else{
            goIsi();
        }
    }

    public void goIsi(){
        String val = nominal.getText().toString();
        Double nom = Double.parseDouble(val);
        Date date = new Date();
        String username = shad.getString("username","");
        String rIdnow = username+shad.getString("telp","");
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = formatter.format(date);
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
                    System.out.println(saldo);
                    saldo += nom;

                    RekHandler handle = new RekHandler(saldo, username);
                    myRef.child(rIdnow).setValue(handle);


                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("trans");
                    String jenis = "isi";

                    TransHandler handle2 = new TransHandler(jenis, rIdnow,"",nom,strDate);
                    myRef2.push().setValue(handle2);

                    Intent success = new Intent(SaldoActivity.this, BerhasilActivity.class);
                    startActivity(success);
                    finish();
                }else{
                    System.out.println("Data failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}