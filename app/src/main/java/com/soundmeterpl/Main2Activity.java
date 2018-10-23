package com.soundmeterpl;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener
{
    private FirebaseAuth firebaseAuth;
    private TextView buttonLogout;
    private Button  buttonMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        firebaseAuth = FirebaseAuth.getInstance();

        /*if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivities(new Intent(this, Main2Activity.class));
        }*/

        //FirebaseAuth user = firebaseAuth.getCurrentUser();

        buttonLogout = (TextView) findViewById(R.id.buttonLogout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);



        buttonLogout.setOnClickListener(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap)
    {

    }

    @Override
    public void onClick(View v) {

    }
}
