package com.example.mohmohhtun.mapapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mohmohhtun on 9/2/16.
 */
public class LocationList extends Activity {


    ListView listView;
    LocationAdapter adapter;
    TextView txtNoLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        listView = (ListView)findViewById(R.id.list);
        Bundle bundle = getIntent().getExtras();
        final ArrayList<Place> places = (ArrayList<Place>)bundle.get("extra");

        adapter= new LocationAdapter(getApplicationContext(),places);
        txtNoLoc = (TextView)findViewById(R.id.noLoc);
        if (places != null  && places.size() == 0){
            txtNoLoc.setVisibility(View.VISIBLE);
        }else {
            txtNoLoc.setVisibility(View.GONE);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place place = places.get(i);

                Intent intent = new Intent(LocationList.this,ShowLocation.class);
                intent.putExtra("extra", (android.os.Parcelable) place);

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                final AlertDialog.Builder builder = new AlertDialog.Builder(LocationList.this);
                builder.setTitle(R.string.app_name).setMessage("Delete this location ?").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Place place = places.get(position);

                        new PlaceHandler(LocationList.this).removePlace(place.getName(),place.latt,place.lon);

                        adapter.getList().remove(position);
                        adapter.notifyDataSetChanged();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                if (adapter != null  && adapter.getList().size() == 0){
                    txtNoLoc.setVisibility(View.VISIBLE);
                }else {
                    txtNoLoc.setVisibility(View.GONE);
                }
                return true;
            }
        });

    }


}
