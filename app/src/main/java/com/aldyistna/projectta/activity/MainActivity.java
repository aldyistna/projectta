package com.aldyistna.projectta.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.utils.SPManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SPManager spManager = new SPManager(this);

        if (!spManager.getSPLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lapor");
        }

        Button btnProfil = findViewById(R.id.btn_profile);
        Button btnLapor = findViewById(R.id.btn_lapor);
        Button btnList = findViewById(R.id.btn_list);
        btnProfil.setOnClickListener(this);
        btnLapor.setOnClickListener(this);
        btnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.btn_lapor:
                startActivity(new Intent(MainActivity.this, AddLaporanActivity.class));
                break;
            case R.id.btn_list:
                startActivity(new Intent(MainActivity.this, ListLaporanActivity.class));
                break;
        }
    }
}