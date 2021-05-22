package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalActivity extends AppCompatActivity {
    Double inSaldo;
    String pulsa;
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;
    AppCompatButton send;
    EditText nominal, idRek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);

        nominal = findViewById(R.id.inp_sentto);
        idRek = findViewById(R.id.inp_rek);
        send = findViewById(R.id.globalbtn);

        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimSaldo();
            }
        });
    }

    public Boolean nominalEmpty(){
        String val = nominal.getText().toString();

        if(val.isEmpty()){
            nominal.setError("Field cannot be empty");
            return false;
        }else{
            nominal.setError(null);
            return true;
        }
    }

    public Boolean idrekEmpty(){
        String val = idRek.getText().toString();

        if(val.isEmpty()){
            idRek.setError("Field cannot be empty");
            return false;
        }else{
            idRek.setError(null);
            return true;
        }
    }

    public Boolean enoughSaldo(){
        inSaldo = Double.parseDouble(nominal.getText().toString());
        Double pulsaNow = Double.parseDouble(shad.getString("saldo",""));

        if(pulsaNow >= inSaldo){
            return true;
        }else{
            Toast.makeText(this, "Maaf, saldo tidak mencukupi.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public  Boolean selfRek(){
        String val = idRek.getText().toString();
        String username = shad.getString("username","");
        String rIdnow = username+shad.getString("telp","");

        if(val.equals(rIdnow)){
            idRek.setError("Tidak dapat transfer ke rekening anda sendiri");
            return false;
        }else{
            idRek.setError(null);
            return true;
        }
    }

    public void kirimSaldo(){
        if(!nominalEmpty() || !idrekEmpty() || !enoughSaldo() || !selfRek() ){
            return;
        }else{
            goKirim();
        }
    }

    public void goKirim(){
        Double inNominal = Double.parseDouble(nominal.getText().toString());
        Date date = new Date();

        String penerima = idRek.getText().toString();
        String username = shad.getString("username","");
        String rIdnow = username+shad.getString("telp","");

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = formatter.format(date);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("rekening");

        Query checkrId = myRef.orderByKey().equalTo(penerima);
        checkrId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    idRek.setError(null);
                    Query checkUser = myRef.orderByKey().equalTo(rIdnow);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Double saldo = snapshot.child(rIdnow).child("saldo").getValue(Double.class);
                            saldo -= inNominal;

                            RekHandler handle = new RekHandler(saldo, username);
                            myRef.child(rIdnow).setValue(handle);

                            shadEdit = shad.edit();
                            shadEdit.putString("saldo",saldo.toString());
                            shadEdit.commit();

                            DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("trans");
                            String jenis = "Transfer";

                            TransHandler handle2 = new TransHandler(jenis, rIdnow, penerima,inNominal,strDate);
                            myRef2.push().setValue(handle2);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    String uPenerima = snapshot.child(penerima).child("username").getValue(String.class);

                    Double saldopenerima;
                    if(snapshot.child(penerima).child("saldo").getValue() == null){
                        saldopenerima = 0.0;
                    }else{
                        saldopenerima = snapshot.child(penerima).child("saldo").getValue(Double.class);
                    }

                    saldopenerima += inNominal;
                    RekHandler handle1 = new RekHandler(saldopenerima, uPenerima);
                    myRef.child(penerima).setValue(handle1);

                    Intent success = new Intent(GlobalActivity.this, BerhasilActivity.class);
                    startActivity(success);
                    finish();
                }else{
                    idRek.setError("ID Rekening not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}