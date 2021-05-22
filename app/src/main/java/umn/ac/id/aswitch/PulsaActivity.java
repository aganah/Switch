package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PulsaActivity extends AppCompatActivity{
    Spinner spPulsa;
    Double inPulsa;
    String pulsa;
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;
    AppCompatButton buy;
    EditText inpTelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsa);

        inpTelp = findViewById(R.id.inputtelp);
        shad = getSharedPreferences("switchPref", MODE_PRIVATE);

        spPulsa = findViewById(R.id.dropdown_pulsa);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this, R.array.nominalpulsa, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPulsa.setAdapter(spAdapter);
        spPulsa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                pulsa = spPulsa.getItemAtPosition(position).toString().replace("Rp.","").replace(".","");
                inPulsa = Double.parseDouble(pulsa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buy = findViewById(R.id.buypulsabtn);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isiPulsa();
            }
        });
    }

    public Boolean enoughSaldo(){
        Double pulsaNow = Double.parseDouble(shad.getString("saldo",""));

        if(pulsaNow >= inPulsa){
            return true;
        }else{
            Toast.makeText(this, "Maaf, saldo tidak mencukupi.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public Boolean telpEmpty(){
        String val = inpTelp.getText().toString();

        if(val.isEmpty()){
            inpTelp.setError("Field cannot be empty");
            return false;
        }else{
            inpTelp.setError(null);
            return true;
        }
    }

    public void isiPulsa(){
        if(!telpEmpty() || !enoughSaldo()){
            return;
        }else{
            goPulsa();
        }
    }

    public void goPulsa(){
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
                    Double saldo = snapshot.child(rIdnow).child("saldo").getValue(Double.class);
                    saldo -= inPulsa;

                    RekHandler handle = new RekHandler(saldo, username);
                    myRef.child(rIdnow).setValue(handle);

                    shadEdit = shad.edit();
                    shadEdit.putString("saldo",saldo.toString());
                    shadEdit.commit();

                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("trans");
                    String jenis = "Top-up Pulsa";

                    TransHandler handle2 = new TransHandler(jenis, rIdnow, inpTelp.getText().toString(),inPulsa,strDate);
                    myRef2.push().setValue(handle2);

                    Intent success = new Intent(PulsaActivity.this, BerhasilActivity.class);
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