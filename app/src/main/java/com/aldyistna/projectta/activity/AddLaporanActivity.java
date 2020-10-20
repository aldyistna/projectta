package com.aldyistna.projectta.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.R;
import com.aldyistna.projectta.utils.SPManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class AddLaporanActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    SPManager spManager;
    InputMethodManager inputManager;
    ImageView imgFoto;
    EditText edtLoc, edtKet;
    Button btnNext;
    private ProgressBar progressBar;
    private RelativeLayout frameProgress;
    File images = null;
    String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_laporan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Lapor");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        edtLoc = findViewById(R.id.txt_loc);
        edtKet = findViewById(R.id.txt_ket);

        progressBar = findViewById(R.id.progress_bar);
        frameProgress = findViewById(R.id.frame_progress);

        imgFoto = findViewById(R.id.img_foto);
        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                images = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (images != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aldyistna.projectta.fileprovider", images);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 1) {
            if (images != null) {
                currentPhotoPath = images.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aldyistna.projectta.fileprovider", images);
                imgFoto.setBackgroundResource(R.drawable.background);
                imgFoto.setImageURI(photoURI);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_next) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (images == null) {
                makeToast("Tidak ada foto untuk dilaporkan");
                return;
            }
            String loc = edtLoc.getText().toString();
            String ket = edtKet.getText().toString();
            if (loc.trim().length() > 0 && ket.trim().length() > 0) {
                if (inputManager != null) {
                    if (getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
                if (networkInfo != null && networkInfo.isConnected()) {
                    frameProgress.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    postLaporan(loc, ket);
                } else {
                    makeToast("No Internet Connection");
                }
            } else {
                makeToast("Lokasi dan Keterangan tidak boleh kosong");
            }
        }
    }

    private void postLaporan(String loc, String ket) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("location", loc);
        params.put("keterangan", ket);
        params.put("status", "Verification");
        params.put("username", spManager.getSpUserName());
        File file = null;
        try {
            file = new File(currentPhotoPath);
            params.put("file", file);
        } catch (FileNotFoundException e) {
            makeToast("Silahkan masukkan foto");
        }

        final File finalFile = file;
        client.post(API_URL + "/laporan", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (images != null) {
                    if (images.exists()) {
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onSuccess", String.valueOf(deleted));
                    }
                }
                if (finalFile != null) {
                    if (finalFile.exists()) {
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onSuccess", String.valueOf(deleted));
                    }
                }
                frameProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                makeToast("Success add laporan");
                startActivity(new Intent(AddLaporanActivity.this, ListLaporanActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (images != null) {
                    if (images.exists()) {
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onFailure", String.valueOf(deleted));
                    }
                }
                if (finalFile != null) {
                    if (finalFile.exists()) {
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onFailure", String.valueOf(deleted));
                    }
                }
                frameProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                makeToast("Post failed");
                Log.e(TAG + " onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });

    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}