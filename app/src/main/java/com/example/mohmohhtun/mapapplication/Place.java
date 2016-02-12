package com.example.mohmohhtun.mapapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohmohhtun on 2/2/16.
 */
public class Place implements Parcelable{
    String name;
    String latt;
    String lon;
    String inout;


    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>()
    {
        @Override
        public Place createFromParcel(Parcel source)
        {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size)
        {
            return new Place[size];
        }
    };

    public Place(Parcel in) {
        setName(in.readString());
        setLatt(in.readString());
        setLon(in.readString());
        setInout(in.readString());
    }

    public Place(String name, String latt, String lon, String inout) {
        this.name = name;
        this.latt = latt;
        this.lon = lon;
        this.inout = inout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatt() {
        return latt;
    }

    public void setLatt(String latt) {
        this.latt = latt;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(latt);
        parcel.writeString(lon);
        parcel.writeString(inout);
    }
}
