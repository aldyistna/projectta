package com.aldyistna.projectta.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Laporan {
    private String lap_id;
    private String location;
    private String keterangan;
    private String tanggal_dibuat;
    private String status;
    private String gambar;

    public Laporan() {}

    public Laporan(JSONObject object) {
        try {
            String lap_id = object.getString("lap_id");
            String location = object.getString("location");
            String keterangan = object.getString("keterangan");
            String tanggal_dibuat = object.getString("tanggal_dibuat");
            String status = object.getString("status");
            String gambar = object.getString("gambar");

            this.lap_id = lap_id;
            this.location = location;
            this.keterangan = keterangan;
            this.tanggal_dibuat = tanggal_dibuat;
            this.status = status;
            this.gambar = gambar;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLap_id() {
        return lap_id;
    }

    public void setLap_id(String lap_id) {
        this.lap_id = lap_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal_dibuat() {
        return tanggal_dibuat;
    }

    public void setTanggal_dibuat(String tanggal_dibuat) {
        this.tanggal_dibuat = tanggal_dibuat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
