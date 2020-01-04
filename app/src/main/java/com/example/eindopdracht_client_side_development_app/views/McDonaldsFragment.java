package com.example.eindopdracht_client_side_development_app.views;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.google.android.gms.maps.model.LatLng;

public class McDonaldsFragment extends Fragment
{
    private McDonalds mcDonalds;
    private TextView address;
    private TextView phoneNumber;
    private TextView location;

    private OnFragmentInteractionListener mListener;

    public McDonaldsFragment()
    {
        // Required empty public constructor
    }

    public static McDonaldsFragment newInstance(McDonalds mcDonalds)
    {
        McDonaldsFragment fragment = new McDonaldsFragment();
        Bundle args = new Bundle();
        args.putSerializable("mcdonalds", mcDonalds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            this.mcDonalds = (McDonalds)getArguments().getSerializable("mcdonalds");
//            this.address.setText(this.mcDonalds.getAddress());
//            this.phoneNumber.setText(this.mcDonalds.getPhoneNumber());
//            LatLng location = this.mcDonalds.getLocation();
//            this.location.setText("Lat " + location.latitude + " : Lon " + location.longitude);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_mc_donalds, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setMcDonalds(McDonalds mcDonalds)
    {
        this.address = getActivity().findViewById(R.id.lbl_Address);
        this.phoneNumber = getActivity().findViewById(R.id.lbl_PhoneNumber);
        this.location = getActivity().findViewById(R.id.lbl_Location);

        this.mcDonalds = mcDonalds;
        this.address.setText(this.mcDonalds.getAddress());
        this.phoneNumber.setText(this.mcDonalds.getPhoneNumber());
        LatLng location = this.mcDonalds.getLocation();
        this.location.setText(location.latitude + " : " + location.longitude);
    }
}
