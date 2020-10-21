package com.aldyistna.projectta.activity;

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

import com.aldyistna.projectta.BuildConfig;
import com.aldyistna.projectta.activity.ui.ApproveFragment;
import com.aldyistna.projectta.activity.ui.FinishFragment;
import com.aldyistna.projectta.activity.ui.VerificationFragment;
import com.aldyistna.projectta.entity.Laporan;
import com.aldyistna.projectta.entity.Users;
import com.aldyistna.projectta.utils.SPManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aldyistna.projectta.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class ListLaporanActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        VerificationFragment.onItemClickedInterface, ApproveFragment.onItemClickedInterface, FinishFragment.onItemClickedInterface {
    private static final String TAG = ListLaporanActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;
    private static final int PDF_NOTIF_ID = 11;

    VerificationFragment verifFragment = null;
    ApproveFragment approveFragment = null;
    FinishFragment finishFragment = null;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    SPManager spManager;
    BottomNavigationView btnNav, btnNav2, btnNav3;
    FrameLayout frameLayout;
    ProgressDialog progressDialog;
    boolean clicked = false;
    Laporan laporan = null;

    static Paint paint = new Paint();
    static int y = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_laporan);

        spManager = new SPManager(this);
        verifFragment = new VerificationFragment(this);
        approveFragment = new ApproveFragment(this);
        finishFragment = new FinishFragment(this);

        frameLayout = findViewById(R.id.frame_layout);

        btnNav = findViewById(R.id.bottom_nav);
        btnNav2 = findViewById(R.id.bottom_nav2);
        btnNav3 = findViewById(R.id.bottom_nav3);
        btnNav.setOnNavigationItemSelectedListener(this);
        btnNav2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_save) {
                    createFile();
                    return true;
                } else if (item.getItemId() == R.id.navigation_done) {
                    showDialog();
                    return true;
                }
                return false;
            }
        });
        btnNav3.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_save) {
                    createFile();
                    return true;
                }
                return false;
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Daftar Laporan");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListLaporanActivity.this, AddLaporanActivity.class));
            }
        });
        if (spManager.getSpUserRole().equals("admin")) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

        progressDialog = new ProgressDialog(this);

        fragmentManager.beginTransaction().add(R.id.frame_layout, verifFragment, "Verification").hide(verifFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, approveFragment, "Approve").hide(approveFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, finishFragment, "Finish").hide(finishFragment).commit();
        btnNav.setSelectedItemId(R.id.navigation_verification);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_verification) {
            handleBackPressed();
            fragmentManager.beginTransaction().hide(approveFragment).hide(finishFragment).show(verifFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_approve) {
            handleBackPressed();
            fragmentManager.beginTransaction().hide(verifFragment).hide(finishFragment).show(approveFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_finish) {
            handleBackPressed();
            fragmentManager.beginTransaction().hide(verifFragment).hide(approveFragment).show(finishFragment).commit();
            return true;
        } else if (item.getItemId() == R.id.navigation_close) {
            handleBackPressed();
            startActivity(new Intent(ListLaporanActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return true;
        }
        return false;
    }

    public void showDialog() {
        AlertDialog.Builder alBuilderBuilder = new AlertDialog.Builder(this);
        Users users = laporan.getUsers();
        String status = "";
        if (laporan.getStatus().equals("Verification")) {
            status = "Approve";
        } else if (laporan.getStatus().equals("Approve")) {
            status = "Finish";
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        alBuilderBuilder.setTitle(status);
        final NetworkInfo finalNetworkInfo = networkInfo;
        final String finalStatus = status;
        alBuilderBuilder
                .setMessage("Anda yakin untuk " + status + " Laporan \"" + laporan.getKeterangan() + "\" dari \"" + users.getName() + "\" ?")
                .setCancelable(false)
                .setPositiveButton(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finalNetworkInfo != null && finalNetworkInfo.isConnected()) {
                            laporan.setStatus(finalStatus);
                            progressDialog.setTitle("Proses");
                            progressDialog.setMessage("Silahkan tunggu...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            changeStatusLaporan(laporan);
                        } else {
                            makeToast(getString(R.string.no_internet));
                        }
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

    public void changeStatusLaporan(final Laporan laporan) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("lap_id", laporan.getLap_id());
        params.put("status", laporan.getStatus());
        params.put("username", spManager.getSpUserName());

        client.post(API_URL + "/laporan", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                laporan.setSelected(false);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                makeToast("Success");
                if (laporan.getStatus().equals("Approve")) {
                    verifFragment.approveLaporan(laporan);
                    approveFragment.getApprove(laporan);
                    btnNav.setSelectedItemId(R.id.navigation_approve);
                } else if (laporan.getStatus().equals("Finish")) {
                    approveFragment.finishLaporan(laporan);
                    finishFragment.getFinish(laporan);
                    btnNav.setSelectedItemId(R.id.navigation_finish);
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

    private void createFile() {
        String timeStamp = new SimpleDateFormat("dMMMyyyy--HH-mm-ss", Locale.getDefault()).format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Laporan_" + laporan.getLap_id() + "_" + timeStamp + ".pdf");
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
                createPDF(this, uri, laporan);
                handleBackPressed();
            }
        }
    }

    private static void createPDF(final Context context, final Uri uri, final Laporan laporan) {
        final ProgressDialog progress = new ProgressDialog(context);
        class CreatePDF extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setTitle("Proses");
                progress.setMessage("Silahkan tunggu...");
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
                                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault()).parse(laporan.getTanggal());
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
                                drawText(canvas, laporan.getKeterangan(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Lokasi: " + laporan.getLocation(), 16, y, paint);
                                canvas.drawText("Lokasi", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                drawText(canvas, laporan.getLocation(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Tanggal: " + newDate, 16, y, paint);
                                canvas.drawText("Tanggal", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                drawText(canvas, newDate, 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Status: " + laporan.getStatus(), 16, y, paint);
                                canvas.drawText("Status", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                drawText(canvas, laporan.getStatus(), 135, paint);
                                y += paint.descent() - paint.ascent() + 8;

//                                canvas.drawText("Pelapor: " + users.getName(), 16, y, paint);
                                canvas.drawText("Pelapor", 16, y, paint);
                                canvas.drawText(":", 125, y, paint);
                                drawText(canvas, users.getName(), 135, paint);
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
                                    Objects.requireNonNull(outputStream).close();
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

    public static void drawText(Canvas canvas, String text, int x, Paint paint) {
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
    }

    public void handleBackPressed() {
        clicked = false;
        verifFragment.clearSelected();
        approveFragment.clearSelected();
        finishFragment.clearSelected();
        laporan = null;
        btnNav.setVisibility(View.VISIBLE);
        btnNav2.setVisibility(View.GONE);
        btnNav3.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (clicked) {
            handleBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void postClicked(Laporan lap, boolean isClicked, boolean isFinishFragment) {
        clicked = isClicked;
        if (isClicked) {
            laporan = lap;
            if (isFinishFragment) {
                btnNav3.setVisibility(View.VISIBLE);
            } else {
                btnNav2.setVisibility(View.VISIBLE);
            }
        } else {
            laporan = null;
            btnNav.setVisibility(View.VISIBLE);
            btnNav2.setVisibility(View.GONE);
            btnNav3.setVisibility(View.GONE);
        }
    }
}