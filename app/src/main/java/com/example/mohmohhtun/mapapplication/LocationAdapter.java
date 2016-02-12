package com.example.mohmohhtun.mapapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mohmohhtun on 9/2/16.
 */
public class LocationAdapter extends BaseAdapter {
    ArrayList<Place> places;
    Context ctx;
    LayoutInflater inflater;
    public LocationAdapter(Context ctx,ArrayList<Place> places){
        this.ctx = ctx;
        this.places = places;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int i) {
        return places.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.cell_location,null,false);
        TextView txtName = (TextView)view.findViewById(R.id.name);
        TextView txtLoc = (TextView)view.findViewById(R.id.loc);


        txtName.setText(places.get(i).getName());
        txtLoc.setText(places.get(i).getLatt()+"/"+places.get(i).getLon());

        return view;
    }

    public ArrayList<Place> getList(){
        return places;
    }
}
