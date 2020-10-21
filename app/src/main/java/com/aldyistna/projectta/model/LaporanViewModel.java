package com.aldyistna.projectta.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.entity.Laporan;
import com.aldyistna.projectta.utils.SPManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class LaporanViewModel extends ViewModel {
    private static final String TAG = LaporanViewModel.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;
    private final MutableLiveData<ArrayList<Laporan>> listLaporanVerif = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Laporan>> listLaporanApprove = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Laporan>> listLaporanFinish = new MutableLiveData<>();

    public handleFailureVerification handleFailureVerification;
    public handleFailureApprove handleFailureApprove;
    public handleFailureFinish handleFailureFinish;

    public void LaporanViewModelVerification(handleFailureVerification fail) {
        handleFailureVerification = fail;
    }

    public void LaporanViewModelApprove(handleFailureApprove fail) {
        handleFailureApprove = fail;
    }

    public void LaporanViewModelFinish(handleFailureFinish fail) {
        handleFailureFinish = fail;
    }

    public void setLaporan(final String status, Context context) {
        SPManager spManager = new SPManager(context);
        String paramQuery = "";
        AsyncHttpClient client = new AsyncHttpClient();
        final ArrayList<Laporan> listLap = new ArrayList<>();

        if (spManager.getSpUserRole().equals("user")) {
            String userName = spManager.getSpUserName();
            paramQuery = "&dibuat_oleh=" + userName;
        }

        client.get(API_URL + "laporan?status=" + status + paramQuery, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resObject = new JSONObject(result);
                    JSONArray list = resObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Laporan laporan = new Laporan(jsonObject);
                        listLap.add(laporan);
                    }

                    switch (status) {
                        case "Verification":
                            listLaporanVerif.postValue(listLap);
                            break;
                        case "Approve":
                            listLaporanApprove.postValue(listLap);
                            break;
                        case "Finish":
                            listLaporanFinish.postValue(listLap);
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG + " Exception", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                switch (status) {
                    case "Verification":
                        handleFailureVerification.onFailureLoad();
                        break;
                    case "Approve":
                        handleFailureApprove.onFailureLoad();
                        break;
                    case "Finish":
                        handleFailureFinish.onFailureLoad();
                        break;
                }
                Log.e(TAG + " onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });
    }

    public LiveData<ArrayList<Laporan>> getLaporanVerification() {
        return listLaporanVerif;
    }

    public LiveData<ArrayList<Laporan>> getLaporanApprove() {
        return listLaporanApprove;
    }

    public LiveData<ArrayList<Laporan>> getLaporanFinish() {
        return listLaporanFinish;
    }

    public interface handleFailureVerification {
        void onFailureLoad();
    }

    public interface handleFailureApprove {
        void onFailureLoad();
    }

    public interface handleFailureFinish {
        void onFailureLoad();
    }
}
