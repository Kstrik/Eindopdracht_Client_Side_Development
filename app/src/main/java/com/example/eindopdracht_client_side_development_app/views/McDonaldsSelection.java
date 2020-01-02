package com.example.eindopdracht_client_side_development_app.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIListener;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIManager;
import com.example.eindopdracht_client_side_development_app.util.MapUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class McDonaldsSelection extends AppCompatActivity implements LocationAPIListener
{
    private RecyclerView recyclerView;
    private McDonaldsAdapter mcDonaldsAdapter;

    private ArrayList<McDonalds> mcDonaldsList;

    private EditText searchMcDonaldsEditText;
    private LocationAPIManager locationAPIManager;

    private LatLng lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc_donalds_selection);

        this.recyclerView = findViewById(R.id.rcv_McDonaldsView);
        this.searchMcDonaldsEditText = findViewById(R.id.txb_SearchMcDonalds);
        //this.databaseHandler = DatabaseHandler.getInstance(getApplicationContext(), "GameDB");
        //this.mcDonaldsList = this.databaseHandler.getAllGames();

        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        this.mcDonaldsAdapter = new McDonaldsAdapter(this.mcDonaldsList);

        this.recyclerView.setAdapter(this.mcDonaldsAdapter);

        this.locationAPIManager = LocationAPIManager.getInstance(this);
        this.locationAPIManager.setLocationAPIListener(this);
    }

    public void onSearchInRangeClick(View view)
    {
        try
        {
            double range = Double.parseDouble(this.searchMcDonaldsEditText.getText().toString()) / 1000;

            LatLng currentLocation = new LatLng(this.lastLocation.latitude, this.lastLocation.longitude);
            ArrayList<McDonalds> mcDonaldsInRangeList = new ArrayList<McDonalds>();
            for(McDonalds mcDonalds : this.mcDonaldsList)
            {
                if(MapUtils.getDistance(currentLocation, mcDonalds.getLocation()) <= range)
                    mcDonaldsInRangeList.add(mcDonalds);
            }

            //this.recyclerView.removeAllViewsInLayout();
            this.mcDonaldsAdapter.setDataset(mcDonaldsInRangeList);
            this.mcDonaldsAdapter.notifyDataSetChanged();
        }
        catch(Exception e)
        {
            Toast.makeText(this, getText(R.string.incorrect_input_notification), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onLocationReceived(LatLng location)
    {
        Log.d("onLocationReceived", "Location: " + location.latitude + " : " + location.longitude);
        this.lastLocation = location;
    }
}
