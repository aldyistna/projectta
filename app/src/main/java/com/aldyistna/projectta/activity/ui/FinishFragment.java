package com.aldyistna.projectta.activity.ui;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.adapter.LaporanAdapter;
import com.aldyistna.projectta.entity.Laporan;
import com.aldyistna.projectta.model.LaporanViewModel;
import com.aldyistna.projectta.utils.ListClickSupport;
import com.aldyistna.projectta.utils.SPManager;

import java.util.ArrayList;

public class FinishFragment extends Fragment implements LaporanViewModel.handleFailureFinish {

    private ProgressBar progressBar;
    private RecyclerView rvFinish;
    private LaporanViewModel viewModel;
    private LaporanAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    SPManager spManager;

    ArrayList<Laporan> listLaporan = new ArrayList<>();
    Laporan laporan = null;
    private String searchValue = "";
    private boolean itemSelected = false;

    public onItemClickedInterface onClick;

    public FinishFragment(onItemClickedInterface onItemClickedInterface) {
        onClick = onItemClickedInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_finish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedStateInstance) {
        super.onViewCreated(view, savedStateInstance);

        spManager = new SPManager(requireActivity());

        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel.class);
        viewModel.LaporanViewModelFinish(this);

        adapter = new LaporanAdapter(getActivity());

        rvFinish = view.findViewById(R.id.rv_finish);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvFinish.setLayoutManager(layoutManager);

        txtEmpty = view.findViewById(R.id.empty_text);
        swipe = view.findViewById(R.id.swipe);
        progressBar = view.findViewById(R.id.progress_bar);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                getData(true);
            }
        });

        viewModel.getLaporanFinish().observe(requireActivity(), getLaporan);
        if (savedStateInstance == null) {
            getData(true);
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
                    rvFinish.setVisibility(View.VISIBLE);
                    setData();
                } else {
                    String sMatch = "Tidak ada laporan selesai";
                    if (!searchValue.equals("")) {
                        sMatch = "Tidak ada pelapor bernama " + searchValue;
                    }
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvFinish.setVisibility(View.GONE);
                }
                showLoading(false);
            }
        }
    };

    public void getData(boolean state) {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected()) {
                txtEmpty.setVisibility(View.GONE);
                rvFinish.setVisibility(View.VISIBLE);
                viewModel.setLaporan("Finish", getActivity(), "");
                showLoading(state);
            } else {
                txtEmpty.setText(getString(R.string.no_internet));
                txtEmpty.setVisibility(View.VISIBLE);
                rvFinish.setVisibility(View.GONE);
                showLoading(false);
            }

        }
    }

    public void getDataSearch(String term) {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected()) {
                txtEmpty.setVisibility(View.GONE);
                rvFinish.setVisibility(View.VISIBLE);
                viewModel.setLaporan("Finish", getActivity(), term);
            } else {
                txtEmpty.setText(getString(R.string.no_internet));
                txtEmpty.setVisibility(View.VISIBLE);
                rvFinish.setVisibility(View.GONE);
            }

        }
    }

    private void setData() {
        adapter.setData(listLaporan);
        adapter.notifyDataSetChanged();
        rvFinish.setAdapter(adapter);
        if (spManager.getSpUserRole().equals("admin")) {
            ListClickSupport.addTo(rvFinish).setOnItemClickListener(new ListClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int pos, View view) {
                    if (laporan == null) {
                        laporan = listLaporan.get(pos);
                        laporan.setSelected(true);
                        onClick.postClicked(laporan, true, true);
                        itemSelected = true;
                    } else {
                        if (listLaporan.get(pos).isSelected()) {
                            laporan = null;
                            listLaporan.get(pos).setSelected(false);
                            onClick.postClicked(laporan, false, true);
                            itemSelected = false;
                        } else {
                            for (int i = 0; i < listLaporan.size(); i++) {
                                listLaporan.get(i).setSelected(false);
                            }
                            laporan = listLaporan.get(pos);
                            laporan.setSelected(true);
                            onClick.postClicked(laporan, true, true);
                            itemSelected = true;
                        }
                    }
                    requireActivity().invalidateOptionsMenu();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            MenuItem item = menu.findItem(R.id.icon_search);
            final SearchView searchView = (SearchView) item.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.cari));
            ImageView clearBtn = searchView.findViewById(R.id.search_close_btn);

            item.setVisible(spManager.getSpUserRole().equals("admin") && !itemSelected);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    (menu.findItem(R.id.icon_search)).collapseActionView();
                    searchValue = s;
                    getDataSearch(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!s.isEmpty()) {
                        searchValue = s;
                        getDataSearch(s);
                    }
                    return false;
                }
            });

            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText et = searchView.findViewById(R.id.search_src_text);
                    et.setText("");
                    searchValue = "";
                    getData(false);
                }
            });
        }
    }

    public void getFinish(Laporan lap) {
        listLaporan.add(0, lap);
        laporan = null;
        setData();
    }

    public void clearSelected() {
        if (laporan != null) {
            itemSelected = false;
            requireActivity().invalidateOptionsMenu();
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
        rvFinish.setVisibility(View.GONE);
        showLoading(false);
    }

    public interface onItemClickedInterface {
        void postClicked(Laporan lap, boolean clicked, boolean isFinishFragment);
    }
}