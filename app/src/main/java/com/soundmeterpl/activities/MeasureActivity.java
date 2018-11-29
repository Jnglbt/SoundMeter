package com.soundmeterpl.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.soundmeterpl.R;
import com.soundmeterpl.adapters.MyMediaRecorder;
import com.soundmeterpl.utils.InfoDialog;
import com.soundmeterpl.utils.Measure;
import com.soundmeterpl.utils.Meter;
import com.soundmeterpl.utils.Util;
import com.soundmeterpl.utils.World;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class MeasureActivity extends AppCompatActivity
{
    boolean refreshed = false;
    Meter meter;
    public static Typeface tf;
    ImageButton infoButton;
    ImageButton refreshButton;
    TextView minVal;
    TextView maxVal;
    TextView aveVal;
    TextView curVal;
    long currentTime = 0;
    boolean isChart = false;
    private boolean bListener = true;
    private boolean isThreadRun = true;
    private Thread thread;
    float volume = 10000;
    int refresh = 0;
    private MyMediaRecorder mRecorder;

    private static final int MY_PERMISSION_RECORD_AUDIO = 1;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    private Button buttonAbort;
    private Button buttonAdd;

    private DatabaseReference databaseValue;


    final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            DecimalFormat df1 = new DecimalFormat("####.0");
            if (msg.what == 1)
            {
                meter.refresh();
                minVal.setText(df1.format(World.minDB));
                aveVal.setText(df1.format((World.minDB + World.maxDB) / 2));
                maxVal.setText(df1.format(World.maxDB));
                curVal.setText(df1.format(World.dbCount));
                if (refresh == 1)
                {
                    long now = new Date().getTime();
                    now = now - currentTime;
                    now = now / 1000;
                    refresh = 0;
                } else
                {
                    refresh++;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        databaseValue = FirebaseDatabase.getInstance().getReference("measure");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSION_RECORD_AUDIO);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/Let_s go Digital Regular.ttf");
        minVal = findViewById(R.id.minVal);
        minVal.setTypeface(tf);
        aveVal = findViewById(R.id.aveVal);
        aveVal.setTypeface(tf);
        maxVal = findViewById(R.id.maxVal);
        maxVal.setTypeface(tf);
        curVal = findViewById(R.id.curVal);
        curVal.setTypeface(tf);
        infoButton = findViewById(R.id.quest_bttn);
        infoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InfoDialog.Builder builder = new InfoDialog.Builder(MeasureActivity.this);
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
            }
        });
        refreshButton = findViewById(R.id.refresh_bttn);
        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                refreshed = true;
                World.minDB = 100;
                World.dbCount = 0;
                World.lastDbCount = 0;
                World.maxDB = 0;
            }
        });

        meter = findViewById(R.id.speed);
        mRecorder = new MyMediaRecorder();

        buttonAbort = findViewById(R.id.abort_bttn);
        buttonAbort.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MeasureActivity.this, MainActivity.class));
            }
        });
        buttonAdd = findViewById(R.id.add_bttn);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buttonAdd.setEnabled(false);
            buttonAdd.setText("Turn on GPS");
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddValue();
                startActivity(new Intent(MeasureActivity.this, MainActivity.class));
            }
        });
    }

    private void startListenAudio()
    {
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (isThreadRun)
                {
                    try
                    {
                        if (bListener)
                        {
                            volume = mRecorder.getMaxAmplitude();
                            if (volume > 0)
                            {
                                World.setDbCount(20 * (float) (Math.log10(volume)));
                                //World.setDbCount(volume);
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        }
                        if (refreshed)
                        {
                            Thread.sleep(1200);
                            refreshed = false;
                        } else
                        {
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        bListener = false;
                    }
                }
            }
        });
        thread.start();
    }

    public void startRecord(File fFile)
    {
        try
        {
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder())
            {
                startListenAudio();
            } else
            {
                Toast.makeText(this, getString(R.string.activity_recStartErr), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            Toast.makeText(this, getString(R.string.activity_recBusyErr), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        File file = Util.createFile("temp.amr");
        if (file != null)
        {
            startRecord(file);
        } else
        {
            Toast.makeText(getApplicationContext(), getString(R.string.activity_recFileErr), Toast.LENGTH_LONG).show();
        }
        bListener = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bListener = false;
        mRecorder.delete();
        thread = null;
        isChart = false;
    }

    @Override
    protected void onDestroy()
    {
        if (thread != null)
        {
            isThreadRun = false;
            thread = null;
        }
        mRecorder.delete();
        super.onDestroy();
    }
    private void AddValue(){
        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("LATITUDE");
        double longitude = bundle.getDouble("LONGITUDE");
        Date time = Calendar.getInstance().getTime();

        String measureString = curVal.getText().toString();
        String FinMeasureString = measureString.replace(',', '.');
        if(!TextUtils.isEmpty(measureString)){
            String id = databaseValue.push().getKey();
            Measure measure = new Measure(id, FinMeasureString, longitude, latitude, time);
            databaseValue.child(id).setValue(measure);
            Toast.makeText(this, "Measurment was added", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Unable to measure", Toast.LENGTH_LONG).show();
        }
    }
}