package com.aldyistna.projectta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.entity.Laporan;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Laporan> listLaporan = new ArrayList<>();

    public LaporanAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Laporan> items) {
        listLaporan.clear();
        listLaporan.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(listLaporan.get(position));
    }

    @Override
    public int getItemCount() {
        return listLaporan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLaporan;
        TextView txtStatus, txtLocation, txtTgl, txtKet;
        RelativeLayout layoutStatus;
        ConstraintLayout itemList, layoutCheck;
        public ViewHolder(@NonNull View view) {
            super(view);

            imgLaporan = view.findViewById(R.id.img_lapor);
            txtStatus = view.findViewById(R.id.txt_status);
            txtLocation = view.findViewById(R.id.txt_alamat_lapor);
            txtTgl = view.findViewById(R.id.txt_tgl_lapor);
            txtKet = view.findViewById(R.id.txt_ket_lapor);
            layoutStatus = view.findViewById(R.id.status);
            itemList = view.findViewById(R.id.item_list);
            layoutCheck = view.findViewById(R.id.layout_check);
        }

        public void bind(Laporan laporan) {
            if (laporan.isSelected()) {
                layoutCheck.setVisibility(View.VISIBLE);
                itemList.setBackgroundResource(R.drawable.card_list_selected);
            } else {
                layoutCheck.setVisibility(View.GONE);
                itemList.setBackgroundResource(R.drawable.card_list);
            }
            Glide.with(context)
                    .load(laporan.getGambar())
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.error_image)
                    .into(imgLaporan);

            txtStatus.setText(laporan.getStatus());
            switch (laporan.getStatus()) {
                case "Verification":
                    layoutStatus.setBackgroundResource(R.drawable.card_status_verif);
                    break;
                case "Approve":
                    layoutStatus.setBackgroundResource(R.drawable.card_status_approve);
                    break;
                case "Finish":
                    layoutStatus.setBackgroundResource(R.drawable.card_status_finish);
                    break;
            }

            txtLocation.setText(laporan.getLocation());
            txtKet.setText(laporan.getKeterangan());

            Locale locale = context.getResources().getConfiguration().locale;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault()).parse(laporan.getTanggal());
                assert date != null;
                String newDate = df.format(date);
                txtTgl.setText(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
