package com.aldyistna.projectta.activity.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.adapter.LaporanAdapter;
import com.aldyistna.projectta.entity.Laporan;
import com.aldyistna.projectta.model.LaporanViewModel;

import java.util.ArrayList;

public class VerificationFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView rvVerif;
    private LaporanViewModel viewModel;
    private LaporanAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;

    public VerificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedStateInstance) {
        super.onViewCreated(view, savedStateInstance);

        viewModel = new ViewModelProvider(requireActivity()).get(LaporanViewModel.class);

        adapter = new LaporanAdapter(getActivity());
        adapter.notifyDataSetChanged();
        
        rvVerif = view.findViewById(R.id.rv_verif);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvVerif.setLayoutManager(layoutManager);

        txtEmpty = view.findViewById(R.id.empty_text);
        swipe = view.findViewById(R.id.swipe);
        progressBar = view.findViewById(R.id.progress_bar);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                getData();
            }
        });

        viewModel.getLaporanVerification().observe(requireActivity(), getLaporan);
        if (savedStateInstance == null) {
            getData();
        }
    }

    private void showLoading(boolean state) {
        if (state) {
            swipe.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            swipe.setVisibility(View.VISIBLE);
        }
    }

    private Observer<ArrayList<Laporan>> getLaporan = new Observer<ArrayList<Laporan>>() {
        @Override
        public void onChanged(ArrayList<Laporan> laporans) {
            if (laporans != null) {
                if (laporans.size() > 0) {
                    txtEmpty.setVisibility(View.GONE);
                    rvVerif.setVisibility(View.VISIBLE);
                    adapter.setData(laporans);
                    rvVerif.setAdapter(adapter);
                    showLoading(false);
                } else {
                    String sMatch = "Tidak ada laporan diverifikasi";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvVerif.setVisibility(View.GONE);
                    showLoading(false);
                }
            }
        }
    };

    private void getData() {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected()) {
                txtEmpty.setVisibility(View.GONE);
                rvVerif.setVisibility(View.VISIBLE);
                viewModel.setLaporan("Verification", getActivity());
                showLoading(true);
            } else {
                txtEmpty.setText(getString(R.string.no_internet));
                txtEmpty.setVisibility(View.VISIBLE);
                rvVerif.setVisibility(View.GONE);
                showLoading(false);
            }

        }
    }
}