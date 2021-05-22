package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("rekening");

        String rIdnow = username+shad.getString("telp","");
        Query checkrId = myRef2.orderByKey().equalTo(rIdnow);

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

                    DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("trans");
                    Query checkTrans = myRef4.orderByChild("rId").equalTo(rIdnow);

                    checkTrans.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()){
                                TransHandler handle = data.getValue(TransHandler.class);
                                handle.setrId(username+telp);
                                myRef4.child(data.getKey()).removeValue();
                                myRef4.push().setValue(handle);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    DatabaseReference myRef5 = FirebaseDatabase.getInstance().getReference("trans");
                    Query checkTransPenerima = myRef5.orderByChild("penerima").equalTo(rIdnow);
                    checkTransPenerima.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()){
                                TransHandler handle = data.getValue(TransHandler.class);
                                handle.setPenerima(username+telp);
                                myRef5.child(data.getKey()).removeValue();
                                myRef5.push().setValue(handle);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("rekening");
                    myRef3.child(rIdnow).removeValue();

                    shadEdit = shad.edit();
                    shadEdit.putString("email", email);
                    shadEdit.putString("telp", telp);
                    shadEdit.commit();
                    String rIdnow = username+shad.getString("telp","");

                    RekHandler handle2 = new RekHandler(saldo, username);
                    myRef2.child(rIdnow).setValue(handle2);
                }else{
                    System.out.println("Data failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        setResult(RESULT_OK);
        finish();
    }
}