package com.aldyistna.projectta.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.R;
import com.aldyistna.projectta.utils.SPManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    SPManager spManager;
    InputMethodManager inputManager;
    ProgressDialog progressDialog;

    DatePickerDialog picker;
    EditText edtNIK, edtNama, edtPOB, edtDOB, edtAlamat, edtUsername, edtPass;
    Spinner edtJekel;
    ImageView imgFoto;

    private final String[] listJekel = {"- Pilih Jenis Kelamin -", "Laki-laki", "Perempuan"};
    File file = null;
    Uri selectedImage;
    String dob = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar");
        }

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        final List<String> jekelList = new ArrayList<>(Arrays.asList(listJekel));
        edtJekel = findViewById(R.id.txt_edt_jekel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, jekelList) {
            @Override
            public boolean isEnabled(int pos) {
                return pos != 0;
            }

            @Override
            public View getDropDownView(int pos, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(pos, convertView, parent);
                TextView tv = (TextView) view;
                if (pos == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        edtJekel.setAdapter(adapter);

        edtNIK = findViewById(R.id.txt_edt_nik);
        edtNama = findViewById(R.id.txt_edt_name);
        edtPOB = findViewById(R.id.txt_edt_pob);
        edtDOB = findViewById(R.id.txt_edt_dob);
        edtAlamat = findViewById(R.id.txt_edt_adr);
        edtUsername = findViewById(R.id.txt_edt_username);
        edtPass = findViewById(R.id.txt_edt_pass);

        progressDialog = new ProgressDialog(this);

        edtDOB.setInputType(InputType.TYPE_NULL);
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                if (!dob.isEmpty()) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dob);
                        assert date != null;
                        calendar.setTime(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month +1;
                        String sMonth = month < 10 ? "0" + month : String.valueOf(month);
                        String sDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        String text = year + "-" + sMonth + "-" + sDay;
                        dob = text;
                        edtDOB.setText(text);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        imgFoto = findViewById(R.id.img_profile);
        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        Button btnBack = findViewById(R.id.btn_back);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                file = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 2) {
            selectedImage = data.getData();
            imgFoto.setImageURI(selectedImage);
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
    public void onClick(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (v.getId() == R.id.btn_back) {
            onBackPressed();
            finish();
        } else if (v.getId() == R.id.btn_save) {
            EditText[] fields = {edtNIK, edtNama, edtPOB, edtDOB, edtAlamat, edtUsername, edtPass};
            for (EditText current : fields) {
                String strCurrent = current.getText().toString();
                if (!(strCurrent.trim().length() > 0)) {
                    makeToast("Field cannot empty");
                    return;
                }
            }

            if (edtJekel.getSelectedItem() == listJekel[0]) {
                makeToast("Field cannot empty");
                return;
            }

            if (inputManager != null) {
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
            if (networkInfo != null && networkInfo.isConnected()) {
                progressDialog.setTitle("Proses");
                progressDialog.setMessage("Silahkan tunggu...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                registerUser();
            } else {
                makeToast(getString(R.string.no_internet));
            }
        }
    }

    private void registerUser() {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("nik", edtNIK.getText());
        params.put("name", edtNama.getText());
        params.put("pob", edtPOB.getText());
        params.put("dob", edtDOB.getText());
        params.put("alamat", edtAlamat.getText());
        params.put("jekel", edtJekel.getSelectedItem());
        params.put("username", edtUsername.getText());
        params.put("password", edtPass.getText());
        params.put("role", "user");

        if (selectedImage != null) {
            OutputStream outStream;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
                params.put("files", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        client.post(API_URL + "/users", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject resObject = new JSONObject(result);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    if (resObject.getString("status").equals("NU01")) {
                        makeToast("Username sudah digunakan");
                    } else {
                        if (file != null) {
                            if (file.exists()) {
                                Boolean deleted = file.delete();
                                Log.e(TAG + " onSuccess", String.valueOf(deleted));
                            }
                        }
                        makeToast("Berhasil daftar, silahkan login");
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }

                } catch (JSONException e) {
                    if (file != null) {
                        if (file.exists()) {
                            Boolean deleted = file.delete();
                            Log.e(TAG + " JSONException", String.valueOf(deleted));
                        }
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    makeToast("Something's wrong");
                    Log.e(TAG + " JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (file != null) {
                    if (file.exists()) {
                        Boolean deleted = file.delete();
                        Log.e(TAG + " onFailure", String.valueOf(deleted));
                    }
                }
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                makeToast("Terjadi kesalahan, silahkan coba lagi");
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