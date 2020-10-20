package com.aldyistna.projectta.activity;

import android.content.Intent;
import android.os.Bundle;

import com.aldyistna.projectta.activity.ui.ApproveFragment;
import com.aldyistna.projectta.activity.ui.FinishFragment;
import com.aldyistna.projectta.activity.ui.VerificationFragment;
import com.aldyistna.projectta.utils.SPManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    Fragment verifFragment = null;
    Fragment approveFragment = null;
    Fragment finishFragment = null;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    BottomNavigationView btnNav;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_laporan);

        SPManager spManager = new SPManager(this);

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
        if (spManager.getSpUserRole().equals("admin")) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

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
        if (item.getItemId() == R.id.navigation_verification) {
            fragmentManager.beginTransaction().hide(approveFragment).hide(finishFragment).show(verifFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_approve) {
            fragmentManager.beginTransaction().hide(verifFragment).hide(finishFragment).show(approveFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_finish) {
            fragmentManager.beginTransaction().hide(verifFragment).hide(approveFragment).show(finishFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_close) {
            startActivity(new Intent(ListLaporanActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return true;
        }
        return false;
    }
}