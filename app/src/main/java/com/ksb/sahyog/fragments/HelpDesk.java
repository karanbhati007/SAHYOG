package com.ksb.sahyog.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ksb.sahyog.R;
import com.ksb.sahyog.adapter.HomeRecylAdapter;
import com.ksb.sahyog.model.AdminMessage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;

public class HelpDesk extends Fragment {

    private SharedPreferences sharedPreferences;
    private int PERMISSION_ID=44;
    private boolean onTimeToast=false;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference mPostDbReference;
    private HomeRecylAdapter adapter;
    // private StorageReference mStorageRef;
    private AlertDialog alertDialog;
    private static final String TAG = "TAG";
    private List<AdminMessage> adminList;
    private RecyclerView recyclerView;
    private List<String> timeSpam;
    private Boolean isEmpty=true;
    private View contextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         return inflater.inflate(R.layout.help_desk, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        adminList = new ArrayList<>();
        timeSpam = new ArrayList<>();
        contextView = view.findViewById(android.R.id.content);
        recyclerView = view.findViewById(R.id.recyclerListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        sharedPreferences = getActivity().getSharedPreferences("com.ksb.sahyogapp",MODE_PRIVATE);

        if(sharedPreferences.getString("postalCode",null)==null) {
            getLastLocation();
        }
        else
        {
            fetchPinCode();
        }


        alertDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
        //mStorageRef = FirebaseStorage.getInstance().getReference();

        //SharedPreferences sharedPreferences = this.getSharedPreferences("com.ksb.covid_19admin",MODE_PRIVATE);



        alertDialog.setMessage("Please Wait...");
        alertDialog.show();

        /// AlertDialog
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(isEmpty)
                {
                    //Snackbar.make((AppCompatActivity)getActivity(), "No New post by Admin", Snackbar.LENGTH_SHORT).show();
                     Toast.makeText(getActivity(), "No New post by Admin", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        }, 6000);

        //  getLastLocation();

    }



    ////////// Fetching Post

    private void fetchPostByAdmin() {
        mPostDbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                AdminMessage message = dataSnapshot.getValue(AdminMessage.class);
                timeSpam.add(dataSnapshot.getKey());
                adminList.add(message);
                Collections.reverse(adminList);
                Collections.reverse(timeSpam);
                isEmpty=false;

               // Log.i(TAG,dataSnapshot.getKey());

                adapter = new HomeRecylAdapter(getActivity(),adminList,timeSpam);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });

    }


    public void fetchPinCode()
    {
        String postalCode = sharedPreferences.getString("postalCode",null);
        // Log.i(TAG,postalCode);

        if(postalCode!=null)
            mPostDbReference  = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child(postalCode);
        else
            mPostDbReference = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child("00000");

        mPostDbReference.keepSynced(true);

        fetchPostByAdmin();

    }

    //////////////// LOCATION !!

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
                getLastLocation();
            }
            else{
                if(!onTimeToast)
                {
                    Toast.makeText(getActivity(), "Plz Give Permission", Toast.LENGTH_SHORT).show();
                    onTimeToast = true;
                }
                getLastLocation();
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                System.out.println("GOT THE LOCATIONN !!!");
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    //latt.setText(location.getLatitude()+"");
                                    // longg.setText(location.getLongitude()+"");

                                    getInfoFromLatLong(location);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                //latt.setText(mLastLocation.getLatitude()+"");
                // longg.setText(mLastLocation.getLongitude()+"");

                getInfoFromLatLong(mLastLocation);

            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    public void getInfoFromLatLong(Location location)
    {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude()
                    ,location.getLongitude(),1);

            if(addresses.get(0).getPostalCode() != null && addresses.get(0).getPostalCode().length()>0 && sharedPreferences.getString("postalCode",null)==null)
            {
                sharedPreferences.edit().putString("postalCode",addresses.get(0).getPostalCode()).apply();
                Log.i("Postal Code Saved :: ",addresses.get(0).getPostalCode());
                Toast.makeText(getActivity(), addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
                fetchPinCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        if (checkPermissions() && sharedPreferences.getString("postalCode",null)==null) {
            getLastLocation();
        }

    }
}
