package com.example.digitalacademy;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InfoPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivDevLogo = findViewById(R.id.ivDevLogo);
        TextView tvDevelopers = findViewById(R.id.tvDevelopers);

        int[] imgArray = {
                R.drawable.ashfaq,
                R.drawable.rohith,
                R.drawable.sarath,
                R.drawable.sathish
        };
        String[] nameArray = {
                getString(R.string.ashfaq),
                getString(R.string.rohith),
                getString(R.string.sarath),
                getString(R.string.sathish)
        };

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            int i = 0;
            public void run() {
                ivDevLogo.setImageResource(imgArray[i]);
                tvDevelopers.setText(nameArray[i]);
                i++;
                if (i >= imgArray.length) {
                    i = 0;
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(r, 2000);
    }
}
