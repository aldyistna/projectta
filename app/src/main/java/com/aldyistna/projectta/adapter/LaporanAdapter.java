package com.aldyistna.projectta.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private Context context;
    private ArrayList<Laporan> listLaporan = new ArrayList<>();

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        public ViewHolder(@NonNull View view) {
            super(view);

            imgLaporan = view.findViewById(R.id.img_lapor);
            txtStatus = view.findViewById(R.id.txt_status);
            txtLocation = view.findViewById(R.id.txt_alamat_lapor);
            txtTgl = view.findViewById(R.id.txt_tgl_lapor);
            txtKet = view.findViewById(R.id.txt_ket_lapor);
            layoutStatus = view.findViewById(R.id.status);
        }

        public void bind(Laporan laporan) {
            Glide.with(context)
                    .load(laporan.getGambar())
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.error_image)
                    .into(imgLaporan);

            txtStatus.setText(laporan.getStatus());
            switch (laporan.getStatus()) {
                case "Verification":
                    layoutStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.card_status_verif));
                    break;
                case "Approve":
                    layoutStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.card_status_approve));
                    break;
                case "Finish":
                    layoutStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.card_status_finish));
                    break;
            }

            txtLocation.setText(laporan.getLocation());
            txtKet.setText(laporan.getKeterangan());

            Locale locale = context.getResources().getConfiguration().locale;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale); ;

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault()).parse(laporan.getTanggal_dibuat());
                assert date != null;
                String newDate = df.format(date);
                txtTgl.setText(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
