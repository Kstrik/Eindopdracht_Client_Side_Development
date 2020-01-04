package com.example.eindopdracht_client_side_development_app.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.example.eindopdracht_client_side_development_app.util.DatabaseHandler;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIManager;
import com.example.eindopdracht_client_side_development_app.util.MapUtils;
import com.example.eindopdracht_client_side_development_app.util.animations.ScaleBounceAnimationSequence;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class McDonaldsAdapter extends RecyclerView.Adapter<McDonaldsAdapter.McDonaldsViewHolder>
{
    private ArrayList<McDonalds> dataset;
    private Context context;

    public McDonaldsAdapter(ArrayList<McDonalds> dataset, Context context)
    {
        this.dataset = dataset;
        this.context = context;
    }

    private ViewGroup parent;

    @NonNull
    @Override
    public McDonaldsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.parent = parent;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mcdonalds_list_item, parent, false);
        return new McDonaldsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull McDonaldsViewHolder holder, int position)
    {
        final McDonalds mcDonalds = dataset.get(position);
        holder.address.setText(mcDonalds.getAddress());
        holder.phoneNumber.setText(this.context.getString(R.string.phonenumber) + " " + mcDonalds.getPhoneNumber());

        final FloatingActionButton favoriteButton = holder.favoriteButton;
        favoriteButton.setImageResource((mcDonalds.isFavorite()) ?  R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);

        final ScaleBounceAnimationSequence scaleBounceAnimationSequence = new ScaleBounceAnimationSequence(holder.favoriteButton, 0.8f, 1.2f, 500, 1);

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                scaleBounceAnimationSequence.start();
                mcDonalds.setFavorite(!mcDonalds.isFavorite());
                favoriteButton.setImageResource((mcDonalds.isFavorite()) ?  R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);

                DatabaseHandler.getInstance().updateMcDonalds(mcDonalds);
            }
        });

        LatLng lastLocation = LocationAPIManager.getInstance().getLastLocation();
        if(lastLocation != null)
        {
            double distance = MapUtils.getDistance(lastLocation, mcDonalds.getLocation());
            holder.distance.setText(Integer.toString((int)Math.floor(distance)) + " m");
        }
        else
            holder.distance.setText("");
    }

    @Override
    public int getItemCount()
    {
        return dataset.size();
    }

    public class McDonaldsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView address;
        public TextView phoneNumber;
        public TextView distance;
        public FloatingActionButton favoriteButton;

        public McDonaldsViewHolder(View itemView)
        {
            super(itemView);
            this.address = (TextView)itemView.findViewById(R.id.lbl_Address);
            this.phoneNumber = (TextView)itemView.findViewById(R.id.lbl_PhoneNumber);
            this.distance = (TextView)itemView.findViewById(R.id.lbl_Distance);
            this.favoriteButton = (FloatingActionButton)itemView.findViewById(R.id.btn_Favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    McDonalds mcDonalds = dataset.get(McDonaldsViewHolder.super.getAdapterPosition());

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("recent", mcDonalds.getAddress());
                    editor.commit();

                    Intent intent = new Intent(view.getContext(), MapsActivity.class).putExtra("mcdonalds", mcDonalds);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    public void setDataset(ArrayList<McDonalds> dataset)
    {
        if(dataset != null)
            this.dataset = dataset;
    }
}
