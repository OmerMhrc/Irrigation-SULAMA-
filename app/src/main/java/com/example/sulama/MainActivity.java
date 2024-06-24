package com.example.sulama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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

    TextView zaman; // sulama süresi/zamani

    Switch sw;
    SeekBar sb;

    Button bt;

    CountDownTimer cdt;
    boolean isCountingDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tn = findViewById(R.id.topraknemi);
        ts = findViewById(R.id.topraksicakligi);
        hn = findViewById(R.id.havanemi);
        hs = findViewById(R.id.havasicakligi);
        sw = findViewById(R.id.switch1);
        sb = findViewById(R.id.seekBar3);
        zaman = findViewById(R.id.duration);
        bt = findViewById(R.id.button);

        sb.setVisibility(SeekBar.INVISIBLE);
        zaman.setVisibility(TextView.INVISIBLE);
        bt.setVisibility(Button.INVISIBLE);

        // Switch durumuna göre SeekBar ve zaman TextView'ını kontrol et
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch açıkken SeekBar ve zaman görünür yap
                sb.setVisibility(SeekBar.VISIBLE);
                zaman.setVisibility(TextView.VISIBLE);
                bt.setVisibility(Button.VISIBLE);
                startCountDown(sb.getProgress() * 1000); // Geri sayımı başlat
            } else {
                // Switch kapalıyken SeekBar ve zamanı görünmez yap
                sb.setVisibility(SeekBar.INVISIBLE);
                zaman.setVisibility(TextView.INVISIBLE);
                bt.setVisibility(Button.INVISIBLE);
                stopCountDown(); // Geri sayımı durdur
            }
        });

        // Firebase veri tabanından verileri oku ve TextView'lara yaz
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

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Kullanıcı SeekBar'ı hareket ettirmeye başladığında yapılacak işlemler
                if (cdt != null) {
                    cdt.cancel(); // Eğer mevcut bir geri sayım varsa iptal et
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Kullanıcı SeekBar'ı bıraktığında yapılacak işlemler
                startCountDown(seekBar.getProgress() * 1000); // Geri sayımı başlat
            }
        });

        // Butona tıklandığında geri sayımı durdur
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCountDown();
                sb.setProgress(0);
            }
        });
    }

    // Geri sayımı başlatan metod
    private void startCountDown(long duration) {
        sb.setEnabled(false); // SeekBar'ı devre dışı bırak
        cdt = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Geri sayım her saniye olduğunda zaman TextView'ını güncelle
                updateTime(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                sb.setEnabled(true); // Geri sayım tamamlandığında SeekBar'ı tekrar etkinleştir
                zaman.setText("Sulama süresi: 00:00"); // Geri sayım bittiğinde zamanı sıfırla
                isCountingDown = false; // Geri sayım bittiğinde durumu güncelle
            }
        };
        cdt.start(); // Geri sayımı başlat
        isCountingDown = true; // Geri sayım başladığında durumu güncelle
    }

    // Geri sayımı durduran metod
    private void stopCountDown() {
        if (cdt != null) {
            cdt.cancel(); // Geri sayımı iptal et
            sb.setEnabled(true); // SeekBar'ı tekrar etkinleştir
            zaman.setText("Sulama süresi: 00:00"); // Zamanı sıfırla
            isCountingDown = false; // Geri sayım durumunu güncelle
        }
    }

    // Zamanı güncelleyen metod
    private void updateTime(long seconds) {
        long min = seconds / 60;
        long sec = seconds % 60;
        String minute = String.format("%02d", min);
        String second = String.format("%02d", sec);
        zaman.setText("Sulama süresi: " + minute + ":" + second);
        sb.setProgress((int) seconds);
    }
}
