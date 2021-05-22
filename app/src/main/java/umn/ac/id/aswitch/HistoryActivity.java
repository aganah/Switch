package umn.ac.id.aswitch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    SharedPreferences shad;
    ArrayList<TransHandler> history;
    BottomNavigationView bottomapp;
    RecyclerView rv;
    HistoryAdapter adapter;
    TextView nohist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        nohist = findViewById(R.id.no_history);

        rv = findViewById(R.id.recyclerview);
        shad = getSharedPreferences("switchPref",MODE_PRIVATE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        bottomapp = findViewById(R.id.navview);
        String username = shad.getString("username","");
        String rIdnow = username+shad.getString("telp","");

        history = new ArrayList<TransHandler>();

        bottomapp.setSelectedItemId(R.id.historyyy);
        bottomapp.setOnNavigationItemSelectedListener(navi);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("trans");
        Query checkrId = myRef.orderByChild("rId").equalTo(rIdnow);

        checkrId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()){
                        TransHandler handle = data.getValue(TransHandler.class);
                        history.add(handle);
                    }
                    adapter = new HistoryAdapter(history);
                    rv.setAdapter(adapter);
                }else{
                    nohist.setText("Tidak ada transaksi.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    BottomNavigationView.OnNavigationItemSelectedListener navi =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            finish();
                            return true;
                        case R.id.historyyy:
                            if (getApplicationContext() == HistoryActivity.this){
                                return true;
                            }
                            return true;
                    }return false;
                }
            };
}

