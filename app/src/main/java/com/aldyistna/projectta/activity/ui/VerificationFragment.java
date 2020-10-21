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
import com.aldyistna.projectta.utils.ListClickSupport;
import com.aldyistna.projectta.utils.SPManager;

import java.util.ArrayList;

public class VerificationFragment extends Fragment implements LaporanViewModel.handleFailureVerification{

    private ProgressBar progressBar;
    private RecyclerView rvVerif;
    private LaporanViewModel viewModel;
    private LaporanAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    SPManager spManager;

    ArrayList<Laporan> listLaporan = new ArrayList<>();
    Laporan laporan = null;

    public onItemClickedInterface onClick;

    public VerificationFragment(onItemClickedInterface onItemClickedInterface) {
        onClick = onItemClickedInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedStateInstance) {
        super.onViewCreated(view, savedStateInstance);

        spManager = new SPManager(requireActivity());

        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel.class);
        viewModel.LaporanViewModelVerification(this);

        adapter = new LaporanAdapter(getActivity());
        
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

    private final Observer<ArrayList<Laporan>> getLaporan = new Observer<ArrayList<Laporan>>() {
        @Override
        public void onChanged(final ArrayList<Laporan> laporans) {
            if (laporans != null) {
                if (laporans.size() > 0) {
                    listLaporan = laporans;
                    txtEmpty.setVisibility(View.GONE);
                    rvVerif.setVisibility(View.VISIBLE);
                    setData();
                } else {
                    String sMatch = "Tidak ada laporan diverifikasi";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvVerif.setVisibility(View.GONE);
                }
                showLoading(false);
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

    private void setData() {
        adapter.setData(listLaporan);
        adapter.notifyDataSetChanged();
        rvVerif.setAdapter(adapter);
        if (spManager.getSpUserRole().equals("admin")) {
            ListClickSupport.addTo(rvVerif).setOnItemClickListener(new ListClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int pos, View view) {
                    if (laporan == null) {
                        laporan = listLaporan.get(pos);
                        laporan.setSelected(true);
                        onClick.postClicked(laporan, true, false);
                    } else {
                        if (listLaporan.get(pos).isSelected()) {
                            laporan = null;
                            listLaporan.get(pos).setSelected(false);
                            onClick.postClicked(laporan, false, false);
                        } else {
                            for (int i = 0; i < listLaporan.size(); i++) {
                                listLaporan.get(i).setSelected(false);
                            }
                            laporan = listLaporan.get(pos);
                            laporan.setSelected(true);
                            onClick.postClicked(laporan, true, false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void approveLaporan(Laporan lap) {
        listLaporan.remove(lap);
        laporan = null;
        setData();
    }

    public void clearSelected() {
        if (laporan != null) {
            laporan.setSelected(false);
            laporan = null;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailureLoad() {
        String sMatch = "Terjadi kesalahan, silahkan refresh halaman";
        txtEmpty.setText(sMatch);
        txtEmpty.setVisibility(View.VISIBLE);
        rvVerif.setVisibility(View.GONE);
        showLoading(false);
    }

    public interface onItemClickedInterface {
        void postClicked(Laporan lap, boolean clicked, boolean isFinishFragment);
    }
}