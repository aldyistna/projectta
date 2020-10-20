package com.aldyistna.projectta.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

        checkPermission();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lapor");
        }

        Button btnProfil = findViewById(R.id.btn_profile);
        Button btnProfil2 = findViewById(R.id.btn_profile2);
        Button btnLapor = findViewById(R.id.btn_lapor);
        Button btnList = findViewById(R.id.btn_list);

        if (spManager.getSpUserRole().equals("admin")) {
            btnLapor.setVisibility(View.GONE);
            btnProfil.setVisibility(View.GONE);
            btnProfil2.setVisibility(View.VISIBLE);
        }

        btnProfil.setOnClickListener(this);
        btnProfil2.setOnClickListener(this);
        btnLapor.setOnClickListener(this);
        btnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (v.getId() == R.id.btn_profile2) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (v.getId() == R.id.btn_lapor) {
            startActivity(new Intent(MainActivity.this, AddLaporanActivity.class));
        } else if (v.getId() == R.id.btn_list) {
            startActivity(new Intent(MainActivity.this, ListLaporanActivity.class));
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermission: granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for(String permission: permissions){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    //denied
                    this.checkPermission();
                    Log.e("denied", permission);
                }else{
                    if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        Log.d("allowed", permission);
                    } else{
                        //set to never ask again
                        AlertDialog.Builder alBuilderBuilder = new AlertDialog.Builder(this);

                        alBuilderBuilder.setTitle("Warning");
                        alBuilderBuilder
                                .setMessage("Aplikasi ini membutuhkan izin akses kamera dan storage, silahkan izinkan aplikasi pada pengaturan perizinan")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alertDialog = alBuilderBuilder.create();
                        alertDialog.show();
                        Log.e("set to never ask again", permission);
                        //do something here.
                    }
                }
            }
        }
    }
}