package com.aldyistna.projectta.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.entity.Users;
import com.aldyistna.projectta.utils.SPManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private SPManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profil");
        }

        spManager = new SPManager(this);

        Gson gson = new Gson();
        String json = spManager.getSpUser();
        Users users = gson.fromJson(json, Users.class);

        setView(users);

        Button btnEdit = findViewById(R.id.btn_edit);
        Button btnLogout = findViewById(R.id.btn_logout);
        btnEdit.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    private void setView(Users users) {
        ImageView imgProfilePic = findViewById(R.id.img_profile);

        if (users.getFoto() == null) {
            imgProfilePic.setImageResource(R.drawable.no_photo);
        } else {
            Glide.with(this)
                    .load(users.getFoto())
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.error_image)
                    .into(imgProfilePic);
        }

        TextView txtNIK = findViewById(R.id.txt_nik);
        txtNIK.setText(users.getNik());

        TextView txtName = findViewById(R.id.txt_nama);
        txtName.setText(users.getName());

        TextView txtPOB = findViewById(R.id.txt_pob);
        txtPOB.setText(users.getPob());

        TextView txtDOB = findViewById(R.id.txt_dob);
        txtDOB.setText(users.getDob());

        TextView txtAddress = findViewById(R.id.txt_address);
        txtAddress.setText(users.getAlamat());

        TextView txtJekel = findViewById(R.id.txt_jekel);
        txtJekel.setText(users.getJekel());

        TextView txtUsername = findViewById(R.id.txt_username);
        txtUsername.setText(users.getUsername());

        TextView txtPass = findViewById(R.id.txt_pass);
        txtPass.setText(users.getPassword().replaceAll(".", "*"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_edit) {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        } else if (v.getId() == R.id.btn_logout) {
            spManager.saveString(SPManager.SP_USER, "");
            spManager.saveString(SPManager.SP_USER_NAME, "");
            spManager.saveInt(SPManager.SP_USER_ID, 0);
            spManager.saveBoolean(SPManager.SP_LOGIN, false);
            finish();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}