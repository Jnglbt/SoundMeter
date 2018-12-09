package com.soundmeterpl.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.soundmeterpl.utils.InfoDialog;
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


    int i = 0;
    int count = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.info:
                InfoDialog.Builder builder = new InfoDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.activity_infobull));
                builder.setTitle(getString(R.string.activity_infotitle));
                builder.setNegativeButton(getString(R.string.activity_infobutton),
                        new android.content.DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
            }

            @Override
            public void onError(Status status)
            {

            }
        });

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
                //Intent intent = new Intent(MainActivity.this, MeasureActivity.class);
                //intent.putExtra("LATITUDE", latitude);
                //intent.putExtra("LONGITUDE", longitude);
                //startActivity(intent);
                startActivity(new Intent(MainActivity.this, MeasureActivity.class));
            }
        });
    }


    protected void addMark()
    {
        dbMeasureReference = FirebaseDatabase.getInstance().getReference();
        dbMeasureReference.child("measure").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    count++;
                }

                Marker marker[] = new Marker[count];
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

                    marker[i] = mMap.addMarker(new MarkerOptions().position(new LatLng(measure.latitude,
                            measure.longitude)).title("Sound level: " + measure.resultMeasure + "dB")
                            .snippet("Date: " + measure.time).icon(
                                    BitmapDescriptorFactory.defaultMarker(color)
                            ));
                    i++;
                }

                for (int j = 0; j < count; j++)
                {
                    double title1 = Double.parseDouble(marker[j].getTitle().substring(13, 17));
                    for (int k = j+1; k < count; k++)
                    {
                        double title2 = Double.parseDouble(marker[k].getTitle().substring(13, 17));

                        if (CalculationByDistance(marker[j].getPosition(), marker[k].getPosition()) <= 10.0)
                        {
                            marker[j].setTitle("Sound level: " + (title1 + title2) / 2 + "dB");
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(marker[j].getPosition())
                                    .radius(5)
                                    .strokeColor(Color.argb(40,255, 0,0))
                                    .fillColor(Color.argb(40,255, 0,0)));

                            marker[k].remove();
                            break;
                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, "Cant read");
            }
        });

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double meter = valueResult * 1000;
        return meter;
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

        addMark();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
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