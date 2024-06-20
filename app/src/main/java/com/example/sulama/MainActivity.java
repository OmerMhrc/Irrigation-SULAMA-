package com.example.sulama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

//import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView tn;
    TextView ts;
    TextView hn;
    TextView hs;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tn = findViewById(R.id.topraknemi);
        ts = findViewById(R.id.topraksicakligi);
        hn = findViewById(R.id.havanemi);
        hs = findViewById(R.id.havasicakligi);

        button = findViewById(R.id.button);


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference hNem = db.getReference("DHT11/hava nemi");
        hNem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer hnem = snapshot.getValue(Integer.class);
                hn.setText("Hava nemi: " + hnem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseData", "Veri okuma iptal edildi.");
            }
        });

        DatabaseReference tNem = db.getReference("toprak nem sensoru/toprak nemi");
        tNem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer tnem = snapshot.getValue(Integer.class);
                tn.setText("Toprak Nemi: " + tnem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseData", "Veri okuma iptal edildi.");
            }
        });

        DatabaseReference hSicaklik = db.getReference("DHT11/hava sıcaklıgı");
        hSicaklik.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer hsicaklik = snapshot.getValue(Integer.class);
                hs.setText("Hava Sıcaklığı: " + hsicaklik);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseData", "Veri okuma iptal edildi.");
            }
        });

        DatabaseReference tSicaklik = db.getReference("toprak sıcaklık/toprak sıcaklıgı");
        tSicaklik.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer tsicaklik = snapshot.getValue(Integer.class);
                ts.setText("Toprak Sıcaklığı: " + tsicaklik);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseData", "Veri okuma iptal edildi.");
            }
        });


    }

}