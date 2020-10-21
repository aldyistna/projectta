package com.aldyistna.projectta.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Laporan {
    private String lap_id;
    private String location;
    private String keterangan;
    private String tanggal;
    private String status;
    private String gambar;
    private Users users;
    private boolean selected;

    public Laporan() {}

    public Laporan(JSONObject object) {
        try {
            String lap_id = object.getString("lap_id");
            String location = object.getString("location");
            String keterangan = object.getString("keterangan");
            String status = object.getString("status");
            String tanggal = "";
            switch (status) {
                case "Verification":
                    tanggal = object.getString("tanggal_dibuat");
                    break;
                case "Approve":
                    tanggal = object.getString("tanggal_approve");
                    break;
                case "Finish":
                    tanggal = object.getString("tanggal_finish");
                    break;
            }
            String gambar = object.getString("gambar");
            Users users = new Users(object.getJSONObject("user"));

            this.lap_id = lap_id;
            this.location = location;
            this.keterangan = keterangan;
            this.tanggal = tanggal;
            this.status = status;
            this.gambar = gambar;
            this.users = users;
            this.selected = false;
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
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

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
