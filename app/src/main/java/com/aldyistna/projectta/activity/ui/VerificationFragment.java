package com.aldyistna.projectta.activity.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.aldyistna.projectta.R;
import com.aldyistna.projectta.adapter.LaporanAdapter;
import com.aldyistna.projectta.entity.Laporan;
import com.aldyistna.projectta.entity.Users;
import com.aldyistna.projectta.model.LaporanViewModel;
import com.aldyistna.projectta.utils.ListClickSupport;
import com.aldyistna.projectta.utils.SPManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class VerificationFragment extends Fragment {

    private static final int PDF_NOTIF_ID = 11;

    private ProgressBar progressBar;
    private RecyclerView rvVerif;
    private LaporanViewModel viewModel;
    private LaporanAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    SPManager spManager;
    Laporan laporan;

    static Paint paint = new Paint();
    static int y = 30;

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

        spManager = new SPManager(requireActivity());

        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel.class);

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
                    txtEmpty.setVisibility(View.GONE);
                    rvVerif.setVisibility(View.VISIBLE);
                    adapter.setData(laporans);
                    adapter.notifyDataSetChanged();
                    rvVerif.setAdapter(adapter);
                    if (spManager.getSpUserRole().equals("admin")) {
                        ListClickSupport.addTo(rvVerif).setOnItemClickListener(new ListClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int pos, View view) {
                                laporan = laporans.get(pos);
//                                ListLaporanActivity.showDialog(laporans.get(pos), requireActivity());
                                showDialog();
                            }
                        });
                    }
                } else {
                    String sMatch = "Tidak ada laporan diverifikasi";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvVerif.setVisibility(View.GONE);
                }
            } else {
                String sMatch = "Terjadi kesalahan, silahkan refresh halaman";
                txtEmpty.setText(sMatch);
                txtEmpty.setVisibility(View.VISIBLE);
                rvVerif.setVisibility(View.GONE);
            }
            showLoading(false);
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

    public void showDialog() {
        AlertDialog.Builder alBuilderBuilder = new AlertDialog.Builder(requireActivity());
        Users users = laporan.getUsers();

        alBuilderBuilder.setTitle("Download");
        alBuilderBuilder
                .setMessage("Download Laporan \"" + laporan.getKeterangan() + "\" dari \"" + users.getName() + "\"")
                .setCancelable(false)
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createFile(laporan);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alBuilderBuilder.create();
        alertDialog.show();
    }

    private void createFile(Laporan lap) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Laporan_" + lap.getLap_id() + ".pdf");
        startActivityForResult(intent, 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 12) {
            if (data != null) {
                Uri uri = data.getData();
                createPDF(requireActivity(), uri, laporan);
            }
        }
    }

    private static void createPDF(final Context context, final Uri uri, final Laporan laporan) {
        final ProgressDialog progress = new ProgressDialog(context);
        class CreatePDF extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setTitle("Processing");
                progress.setMessage("Please Wait...");
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                Glide.with(context)
                        .asBitmap()
                        .load(laporan.getGambar())
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .error(R.drawable.error_image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Users users = laporan.getUsers();

                                Locale locale = context.getResources().getConfiguration().locale;
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
                                String newDate = "";
                                try {
                                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault()).parse(laporan.getTanggal_dibuat());
                                    assert date != null;
                                    newDate = df.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                PdfDocument pdf = new PdfDocument();
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595,842,1).create();
                                PdfDocument.Page page = pdf.startPage(pageInfo);

                                paint.setTextSize(16);
                                Canvas canvas = page.getCanvas();
//                                canvas.drawText("Judul Laporan: " + laporan.getKeterangan(), 16, y, paint);
                                canvas.drawText("Judul Laporan", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                canvas = drawText(canvas, laporan.getKeterangan(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Lokasi: " + laporan.getLocation(), 16, y, paint);
                                canvas.drawText("Lokasi", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                canvas = drawText(canvas, laporan.getLocation(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Tanggal: " + newDate, 16, y, paint);
                                canvas.drawText("Tanggal", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                canvas = drawText(canvas,newDate, 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Status: " + laporan.getStatus(), 16, y, paint);
                                canvas.drawText("Status", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                canvas = drawText(canvas, laporan.getStatus(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Pelapor: " + users.getName(), 16, y, paint);
                                canvas.drawText("Pelapor", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                canvas = drawText(canvas, users.getName(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

                                resource = Bitmap.createScaledBitmap(resource, 563, 842 - y - 16, false);
                                canvas.drawBitmap(resource, 16, y, paint);

                                /*try {
                                    y += paint.descent() - paint.ascent() + 16;
                                    Bitmap bitmap = Picasso.get().load(laporan.getGambar()).get();
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 563, 842 - y - 16, false);
                                    canvas.drawBitmap(bitmap, 16, y, paint);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/

                                pdf.finishPage(page);

                                try {
                                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                                    pdf.writeTo(outputStream);
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                paint = new Paint();
                                y = 30;
                                pdf.close();

                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                final String CHANNEL_ID = "Channel_1";
                final String CHANNEL_NAME = "PDF channel";
                if(progress.isShowing()){
                    progress.dismiss();
                }

                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(uri, "application/pdf");
                openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Laporan_" + laporan.getLap_id())
                        .setContentText(context.getResources().getString(R.string.notif_message))
                        .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                        .setVibrate(new long[]{ 0 })
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

                    channel.enableVibration(false);

                    builder.setChannelId(CHANNEL_ID);

                    if (manager != null) {
                        manager.createNotificationChannel(channel);
                    }
                }

                Notification notification = builder.build();

                if (manager != null) {
                    manager.notify(PDF_NOTIF_ID, notification);
                }
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
        }

        CreatePDF createPDF = new CreatePDF();
        createPDF.execute();
    }

    public static Canvas drawText(Canvas canvas, String text, int x, Paint paint) {
        int length = text.length();
        if (text.length() > 50) {
            String newText1 = text.substring(0, text.lastIndexOf(' ', 50));
            canvas.drawText(newText1, x, y, paint);
            y += paint.descent() - paint.ascent() + 8;
            String newText2 = text.substring(text.lastIndexOf(' ', 50)+1, length);
            drawText(canvas, newText2, x, paint);
        } else {
            canvas.drawText(text, x, y, paint);
        }
        return canvas;
    }
}