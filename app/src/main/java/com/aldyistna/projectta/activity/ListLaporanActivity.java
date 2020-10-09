package com.aldyistna.projectta.activity;

import android.content.Intent;
import android.os.Bundle;

import com.aldyistna.projectta.activity.ui.ApproveFragment;
import com.aldyistna.projectta.activity.ui.FinishFragment;
import com.aldyistna.projectta.activity.ui.VerificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.aldyistna.projectta.R;

public class ListLaporanActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = ListLaporanActivity.class.getSimpleName();

    Fragment verifFragment = null;
    Fragment approveFragment = null;
    Fragment finishFragment = null;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    BottomNavigationView btnNav;
    FrameLayout frameLayout;
    Fragment active = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_laporan);

        frameLayout = findViewById(R.id.frame_layout);
        btnNav = findViewById(R.id.bottom_nav);
        btnNav.setOnNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Daftar Laporan");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListLaporanActivity.this, AddLaporanActivity.class));
            }
        });

        verifFragment = new VerificationFragment();
        approveFragment = new ApproveFragment();
        finishFragment = new FinishFragment();
        fragmentManager.beginTransaction().add(R.id.frame_layout, verifFragment, "Verification").hide(verifFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, approveFragment, "Approve").hide(approveFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, finishFragment, "Finish").hide(finishFragment).commit();
        btnNav.setSelectedItemId(R.id.navigation_verification);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_verification:
                fragmentManager.beginTransaction().hide(approveFragment).hide(finishFragment).show(verifFragment).commit();
                return true;
            case R.id.navigation_approve:
                fragmentManager.beginTransaction().hide(verifFragment).hide(finishFragment).show(approveFragment).commit();
                return true;
            case R.id.navigation_finish:
                fragmentManager.beginTransaction().hide(verifFragment).hide(approveFragment).show(finishFragment).commit();
                return true;
            case R.id.navigation_close:
                onBackPressed();
                return true;
        }
        return false;
    }
}