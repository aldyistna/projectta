package com.aldyistna.projectta.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.R;
import com.aldyistna.projectta.entity.Users;
import com.aldyistna.projectta.utils.SPManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    DatePickerDialog picker;
    EditText edtNIK, edtNama, edtPOB, edtDOB, edtAlamat, edtPhone;
    Spinner edtJekel;
    SPManager spManager;
    InputMethodManager inputManager;
    Users users;
    ProgressDialog progressDialog;

    private final String[] listJekel = {"- Pilih Jenis Kelamin -", "Laki-laki", "Perempuan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Data Pelapor");
        }

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        Gson gson = new Gson();
        String json = spManager.getSpUser();
        users = gson.fromJson(json, Users.class);

        final List<String> jekelList = new ArrayList<>(Arrays.asList(listJekel));
        edtJekel = findViewById(R.id.input_jekel);
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

        edtNIK = findViewById(R.id.input_nik);
        edtNama = findViewById(R.id.input_nama);
        edtPOB = findViewById(R.id.input_tempat_lahir);
        edtDOB = findViewById(R.id.input_tanggal_lahir);
        edtAlamat = findViewById(R.id.input_alamat);
        edtPhone = findViewById(R.id.input_phone);

        progressDialog = new ProgressDialog(this);

        edtNIK.setText(users.getNik());
        edtNama.setText(users.getName());
        edtPOB.setText(users.getPob());
        edtDOB.setText(users.getDob());
        edtAlamat.setText(users.getAlamat());
        edtPhone.setText(users.getPhone());
        edtJekel.setSelection(Arrays.asList(listJekel).indexOf(users.getJekel()));

        edtNIK.setTextColor(Color.parseColor("#C4C4C4"));
        edtDOB.setInputType(InputType.TYPE_NULL);
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(users.getDob());
                    assert date != null;
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(EditProfileActivity.this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month +1;
                        String sMonth = month < 10 ? "0" + month : String.valueOf(month);
                        String sDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                        String text = year + "-" + sMonth + "-" + sDay;
                        users.setDob(text);
                        edtDOB.setText(text);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (v.getId() == R.id.btn_next) {
            EditText[] fields = {edtNIK, edtNama, edtPOB, edtDOB, edtAlamat, edtPhone};
            for (EditText current : fields) {
                String strCurrent = current.getText().toString();
                if (!(strCurrent.trim().length() > 0)) {
                    makeToast("Field " + current.getResources().getResourceName(current.getId()).split("/")[1] + " tidak boleh kosong");
                    return;
                }
            }
            if (edtJekel.getSelectedItem() == listJekel[0]) {
                makeToast("Field jenis kelamin tidak boleh kosong");
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
                editProfile();
            } else {
                makeToast(getString(R.string.no_internet));
            }
        }
    }

    private void editProfile() {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("nik", users.getNik());
        params.put("name", edtNama.getText());
        params.put("pob", edtPOB.getText());
        params.put("dob", edtDOB.getText());
        params.put("alamat", edtAlamat.getText());
        params.put("phone", edtPhone.getText());
        params.put("jekel", edtJekel.getSelectedItem());
        params.put("username", users.getUsername());
        params.put("password", users.getPassword());
        params.put("role", users.getRole());

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
                        makeToast(resObject.getString("message"));
                    } else {
                        JSONObject val = resObject.getJSONObject("data");
                        Users users = new Users(val);
                        Gson gson = new Gson();
                        String json = gson.toJson(users);
                        spManager.saveString(SPManager.SP_USER, json);
                        startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }

                } catch (JSONException e) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    makeToast("Something's wrong");
                    Log.e(TAG + " JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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