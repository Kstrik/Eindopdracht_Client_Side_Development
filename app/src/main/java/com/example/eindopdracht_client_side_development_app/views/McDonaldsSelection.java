package com.example.eindopdracht_client_side_development_app.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.example.eindopdracht_client_side_development_app.util.DatabaseHandler;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIListener;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIManager;
import com.example.eindopdracht_client_side_development_app.util.MapUtils;
import com.example.eindopdracht_client_side_development_app.util.animations.ScaleBounceAnimationSequence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class McDonaldsSelection extends AppCompatActivity implements LocationAPIListener
{
    private RecyclerView recentView;
    private ConstraintLayout recentLayout;
    private McDonaldsAdapter recentAdapter;

    private RecyclerView recyclerView;
    private McDonaldsAdapter mcDonaldsAdapter;

    private DatabaseHandler databaseHandler;
    private ArrayList<McDonalds> mcDonaldsList;

    private EditText searchMcDonaldsEditText;
    private LocationAPIManager locationAPIManager;

    private ScaleBounceAnimationSequence scaleBounceAnimationSequenceSearchButton;
    private ScaleBounceAnimationSequence scaleBounceAnimationSequenceSFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc_donalds_selection);

        this.recyclerView = findViewById(R.id.rcv_McDonaldsView);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        this.searchMcDonaldsEditText = findViewById(R.id.txb_SearchMcDonalds);

        this.databaseHandler = DatabaseHandler.getInstance(getApplicationContext(), "McDonaldsDB");
        this.mcDonaldsList = this.databaseHandler.getAllMcDonalds();
        this.mcDonaldsAdapter = new McDonaldsAdapter(this.mcDonaldsList, this, false);

        this.recyclerView.setAdapter(this.mcDonaldsAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            this.recentView = findViewById(R.id.rcv_RecentView);
            this.recentView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
            this.recentLayout = findViewById(R.id.cns_RecentLayout);

            this.recentLayout.setVisibility(View.GONE);
            McDonalds recent = getRecentIfExists(this.mcDonaldsList);
            if(recent != null)
            {
                this.recentLayout.setVisibility(View.VISIBLE);
                ArrayList<McDonalds> recentList = new ArrayList<McDonalds>();
                recentList.add(recent);
                this.recentAdapter = new McDonaldsAdapter(recentList, this, true);
                this.recentView.setAdapter(this.recentAdapter);
            }
        }

        this.locationAPIManager = LocationAPIManager.getInstance(this);
        this.locationAPIManager.setLocationAPIListener(this);
        this.locationAPIManager.requestLastLocation();

        this.scaleBounceAnimationSequenceSearchButton = new ScaleBounceAnimationSequence(findViewById(R.id.btn_SearchInRadius), 0.8f, 1.2f, 500, 1);
        this.scaleBounceAnimationSequenceSFavoritesButton = new ScaleBounceAnimationSequence(findViewById(R.id.btn_ShowFavorites), 0.8f, 1.2f, 500, 1);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.mcDonaldsAdapter.notifyDataSetChanged();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && this.recentAdapter != null)
        {
            McDonalds recent = getRecentIfExists(this.mcDonaldsList);
            ArrayList<McDonalds> recentList = new ArrayList<McDonalds>();
            recentList.add(recent);
            this.recentAdapter.setDataset(recentList);
            this.recentAdapter.notifyDataSetChanged();
        }
    }

    public void onSearchInRangeClick(View view)
    {
        try
        {
            this.scaleBounceAnimationSequenceSearchButton.start();
            String searchText = this.searchMcDonaldsEditText.getText().toString();

            if(searchText.equals(""))
                this.mcDonaldsAdapter.setDataset(this.mcDonaldsList);
            else
            {
                ArrayList<McDonalds> mcDonaldsInRangeList = new ArrayList<McDonalds>();
                double range = Double.parseDouble(searchText) * 1000;

                LatLng currentLocation = this.locationAPIManager.getLastLocation();
                for(McDonalds mcDonalds : this.mcDonaldsList)
                {
                    if(MapUtils.getDistance(currentLocation, mcDonalds.getLocation()) <= range)
                        mcDonaldsInRangeList.add(mcDonalds);
                }

                this.mcDonaldsAdapter.setDataset(sortMcDonaldsByDistance(mcDonaldsInRangeList));
            }

            //this.recyclerView.removeAllViewsInLayout();
            this.mcDonaldsAdapter.notifyDataSetChanged();
        }
        catch(Exception e)
        {
            Toast.makeText(this, getText(R.string.incorrect_input_notification), Toast.LENGTH_LONG).show();
        }
    }

    public void onShowFavoritesClick(View view)
    {
        this.scaleBounceAnimationSequenceSFavoritesButton.start();

        ArrayList<McDonalds> mcDonaldsFavoritesList = new ArrayList<McDonalds>();
        for(McDonalds mcDonalds : this.mcDonaldsList)
        {
            if(mcDonalds.isFavorite())
                mcDonaldsFavoritesList.add(mcDonalds);
        }

        this.mcDonaldsAdapter.setDataset(sortMcDonaldsByDistance(mcDonaldsFavoritesList));
        this.mcDonaldsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLocationReceived(LatLng location)
    {
        Log.d("onLocationReceived", "Location: " + location.latitude + " : " + location.longitude);
        ArrayList<McDonalds> mcDonaldsList = new ArrayList<McDonalds>();
        mcDonaldsList.addAll(this.mcDonaldsList);
        this.mcDonaldsAdapter.setDataset(sortMcDonaldsByDistance(mcDonaldsList));
        this.recyclerView.removeAllViewsInLayout();
        this.mcDonaldsAdapter.notifyDataSetChanged();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && this.recentAdapter != null)
            this.recentAdapter.notifyDataSetChanged();
    }

    private ArrayList<McDonalds> sortMcDonaldsByDistance(ArrayList<McDonalds> mcDonaldsList)
    {
        ArrayList<McDonalds> mcDonaldsListSorted = new ArrayList<McDonalds>();

        LatLng lastLocation = this.locationAPIManager.getLastLocation();
        while(mcDonaldsList.size() != 0)
        {
            McDonalds closest = mcDonaldsList.get(0);
            double closestDistacne = MapUtils.getDistance(lastLocation, closest.getLocation());

            for(McDonalds mcDonalds : mcDonaldsList)
            {
                double distance = MapUtils.getDistance(lastLocation, mcDonalds.getLocation());
                if(distance < closestDistacne)
                {
                    closest = mcDonalds;
                    closestDistacne = distance;
                }
            }
            mcDonaldsListSorted.add(closest);
            mcDonaldsList.remove(closest);
        }

        return mcDonaldsListSorted;
    }

    private McDonalds getRecentIfExists(ArrayList<McDonalds> mcDonaldsList)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String recentMcDonaldsAddress = sharedPreferences.getString("recent", "");

        McDonalds recent = null;
        if(!recentMcDonaldsAddress.equals(""))
        {
            for(McDonalds mcDonalds : mcDonaldsList)
            {
                if(mcDonalds.getAddress().equals(recentMcDonaldsAddress))
                    recent = mcDonalds.clone();
            }
        }
        return recent;
    }
}
