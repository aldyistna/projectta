package com.aldyistna.projectta.activity;

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.R;
import com.aldyistna.projectta.entity.Users;
import com.aldyistna.projectta.utils.SPManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    EditText edtUserName, edtPass;
    private ProgressBar progressBar;
    private RelativeLayout frameProgress;
    SPManager spManager;
    InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        edtUserName = findViewById(R.id.txt_user_name);
        edtPass = findViewById(R.id.txt_pass);

        progressBar = findViewById(R.id.progress_bar);
        frameProgress = findViewById(R.id.frame_progress);

        Button btnLogin = findViewById(R.id.btn_masuk);
        TextView tvRegister = findViewById(R.id.tv_daftar);
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        switch (v.getId()) {
            case R.id.btn_masuk:
                String userName = edtUserName.getText().toString();
                String pass = edtPass.getText().toString();
                if (userName.trim().length() > 0 && pass.trim().length() > 0) {

                    if (inputManager != null) {
                        if (getCurrentFocus() != null) {
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }

                    if (networkInfo != null && networkInfo.isConnected()) {
                        frameProgress.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        login(userName, pass);
                    } else {
                        makeToast("No Internet Connection");
                    }
                } else {
                    makeToast("Username and password cannot empty");
                }
                break;
            case R.id.tv_daftar:
//                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }

    }

    public void login(String username, String password) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        client.post(API_URL + "/login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject resObject = new JSONObject(result);
                    frameProgress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (resObject.getString("status").equals("0")) {
                        makeToast("Username or password is incorrect");
                    } else {
                        JSONObject val = resObject.getJSONObject("data");
                        Users users = new Users(val);
                        Gson gson = new Gson();
                        String json = gson.toJson(users);
                        spManager.saveString(SPManager.SP_USER, json);
                        spManager.saveString(SPManager.SP_USER_NAME, val.getString("username"));
                        spManager.saveInt(SPManager.SP_USER_ID, val.getInt("id"));
                        spManager.saveBoolean(SPManager.SP_LOGIN, true);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }
                } catch (JSONException e) {
                    frameProgress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    makeToast("Something's wrong");
                    Log.e(TAG + " JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                frameProgress.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                makeToast("Cannot login");
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