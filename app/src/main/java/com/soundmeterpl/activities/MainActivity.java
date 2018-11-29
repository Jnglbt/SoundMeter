package com.soundmeterpl.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soundmeterpl.R;
import com.soundmeterpl.utils.Measure;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private TextView buttonLogout;
    private Button buttonMeasure;
    private Button login;
    private Button signup;
    private LinearLayout vert;
    private LinearLayout hor;
    private Button measureButton;

    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private CameraPosition mCameraPosition;

    private Location mLastKnownLocation;
    private double latitude;
    private double longitude;
    private float color = BitmapDescriptorFactory.HUE_CYAN;

    private boolean mLocationPermissionGranted;

    private static final String TAG = MainActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbMeasureReference;

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hor = findViewById(R.id.hor_layout);
        vert = findViewById(R.id.vert_layout);
        measureButton = findViewById(R.id.measureButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null)
        {
            hor.setVisibility(View.GONE);
            vert.setVisibility(View.VISIBLE);
        } else
        {
            hor.setVisibility(View.VISIBLE);
            vert.setVisibility(View.GONE);
        }

        if (savedInstanceState != null)
        {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);

        login = findViewById(R.id.login_bttn);
        login.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        signup = findViewById(R.id.signup_bttn);
        signup.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, MainActivity.class));

            }
        });

        buttonMeasure = findViewById(R.id.measureButton);
        buttonMeasure.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, MeasureActivity.class);
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                startActivity(intent);
            }
        });
    }

    protected void addMark()
    {
        dbMeasureReference = FirebaseDatabase.getInstance().getReference("measure");
        dbMeasureReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    Measure measure = childSnapshot.getValue(Measure.class);

                    float val = Float.valueOf(measure.resultMeasure);
                    if (val < 40)
                    {
                        color = BitmapDescriptorFactory.HUE_GREEN;
                    } else if (val >= 40 && val < 75)
                    {
                        color = BitmapDescriptorFactory.HUE_CYAN;
                    } else if (val >= 75 && val < 125)
                    {
                        color = BitmapDescriptorFactory.HUE_ORANGE;
                    } else if (val >= 125)
                    {
                        color = BitmapDescriptorFactory.HUE_RED;
                    }

                    mMap.addMarker(new MarkerOptions().position(new LatLng(measure.latitude,
                            measure.longitude)).title("Sound level: " + measure.resultMeasure + "dB")
                            .snippet("Date: " + measure.time).icon(
                            BitmapDescriptorFactory.defaultMarker(color)
                    ));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, "Cant read");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (mMap != null)
        {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                return null;
            }
        });

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        addMark();
    }

    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        } else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation()
    {
        try
        {
            if (mLocationPermissionGranted)
            {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Location> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));


                            latitude = mLastKnownLocation.getLatitude();
                            longitude = mLastKnownLocation.getLongitude();
                        } else
                        {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI()
    {
        if (mMap == null)
        {
            return;
        }
        try
        {
            if (mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}