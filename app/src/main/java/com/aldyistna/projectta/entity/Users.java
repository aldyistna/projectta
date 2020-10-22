package com.aldyistna.projectta.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Users implements Parcelable {
    private String id;
    private String nik;
    private String name;
    private String pob;
    private String dob;
    private String alamat;
    private String jekel;
    private String username;
    private String password;
    private String role;
    private String foto;
    private String phone;

    public Users() {}

    public Users(JSONObject object) {
        try {
            String id = object.getString("id");
            String nik = object.getString("nik");
            String name = object.getString("name");
            String pob = object.getString("pob");
            String dob = object.getString("dob");
            String alamat = object.getString("alamat");
            String jekel = object.getString("jekel");
            String username = object.getString("username");
            String password = object.getString("password");
            String role = object.getString("role");
            String foto = object.getString("foto");
            String phone = object.getString("no_telp");

            this.id = id;
            this.nik = nik;
            this.name = name;
            this.pob = pob;
            this.dob = dob;
            this.alamat = alamat;
            this.jekel = jekel;
            this.username = username;
            this.password = password;
            this.role = role;
            this.foto = foto;
            this.phone = phone;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Users(Parcel in) {
        id = in.readString();
        nik = in.readString();
        name = in.readString();
        pob = in.readString();
        dob = in.readString();
        alamat = in.readString();
        jekel = in.readString();
        username = in.readString();
        password = in.readString();
        role = in.readString();
        foto = in.readString();
        phone = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nik);
        dest.writeString(name);
        dest.writeString(pob);
        dest.writeString(dob);
        dest.writeString(alamat);
        dest.writeString(jekel);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(role);
        dest.writeString(foto);
        dest.writeString(phone);
    }
}
