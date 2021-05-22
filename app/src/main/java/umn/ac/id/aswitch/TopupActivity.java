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

import java.text.SimpleDateFormat;
import java.util.Date;

public class TopupActivity extends AppCompatActivity {
    Spinner spEwallet;
    Double inNom;
    String ewallet;
    SharedPreferences shad;
    SharedPreferences.Editor shadEdit;
    AppCompatButton buy;
    EditText inpTelp, nominal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        inpTelp = findViewById(R.id.inputtelp_ewallet);
        nominal = findViewById(R.id.jumlahnominal);

        shad = getSharedPreferences("switchPref",MODE_PRIVATE);

        spEwallet = findViewById(R.id.sp_ewallet);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this, R.array.ewallet, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEwallet.setAdapter(spAdapter);
        spEwallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                ewallet = spEwallet.getItemAtPosition(position).toString();
                if(ewallet.equals("OVO")){
                    ewallet = "Top-up OVO";
                }else if(ewallet.equals("DANA")){
                    ewallet = "Top-up DANA";
                }else if(ewallet.equals("GOPAY")){
                    ewallet = "Top-up GOPAY";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buy = findViewById(R.id.buy_ewallet);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isiWallet();
            }
        });
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

    public Boolean nomEmpty(){
        String val = nominal.getText().toString();

        if(val.isEmpty()){
            nominal.setError("Field cannot be empty");
            return false;
        }else{
            nominal.setError(null);
            return true;
        }
    }

    public Boolean isEnough(){
        inNom = Double.parseDouble(nominal.getText().toString());
        Double pulsaNow = Double.parseDouble(shad.getString("saldo",""));

        if(pulsaNow >= inNom){
            return true;
        }else{
            Toast.makeText(this, "Maaf, saldo tidak mencukupi.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void isiWallet(){
        if(!telpEmpty() || !nomEmpty() || !isEnough()){
            return;
        }else{
            goWallet();
        }
    }

    public void goWallet(){
        Double inNominal = Double.parseDouble(nominal.getText().toString());
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
                    saldo -= inNominal;

                    RekHandler handle = new RekHandler(saldo, username);
                    myRef.child(rIdnow).setValue(handle);

                    shadEdit = shad.edit();
                    shadEdit.putString("saldo",saldo.toString());
                    shadEdit.commit();

                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("trans");

                    TransHandler handle2 = new TransHandler(ewallet, rIdnow, inpTelp.getText().toString(),inNominal,strDate);
                    myRef2.push().setValue(handle2);

                    Intent success = new Intent(TopupActivity.this, BerhasilActivity.class);
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