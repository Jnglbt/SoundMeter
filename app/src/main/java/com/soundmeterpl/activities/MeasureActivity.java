package com.soundmeterpl.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.soundmeterpl.R;
import com.soundmeterpl.adapters.MyMediaRecorder;
import com.soundmeterpl.utils.InfoDialog;
import com.soundmeterpl.utils.Meter;
import com.soundmeterpl.utils.Util;
import com.soundmeterpl.utils.World;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

public class MeasureActivity extends Activity
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
    long savedTime = 0;
    boolean isChart = false;
    boolean isMoney = false;
    /* Decibel */
    private boolean bListener = true;
    private boolean isThreadRun = true;
    private Thread thread;
    float volume = 10000;
    int refresh = 0;
    private MyMediaRecorder mRecorder;

    private static final int MY_PERMISSION_RECORD_AUDIO = 1;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    private Button  buttonAbort;

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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSION_RECORD_AUDIO);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_measure);
        tf = Typeface.createFromAsset(this.getAssets(), "fonts/Let_s go Digital Regular.ttf");
        minVal = (TextView) findViewById(R.id.minVal);
        minVal.setTypeface(tf);
        aveVal = (TextView) findViewById(R.id.aveVal);
        aveVal.setTypeface(tf);
        maxVal = (TextView) findViewById(R.id.maxVal);
        maxVal.setTypeface(tf);
        curVal = (TextView) findViewById(R.id.curVal);
        curVal.setTypeface(tf);
        infoButton = (ImageButton) findViewById(R.id.quest_bttn);
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
        refreshButton = (ImageButton) findViewById(R.id.refresh_bttn);
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

        meter = (Meter) findViewById(R.id.speed);
        mRecorder = new MyMediaRecorder();

        buttonAbort = (Button) findViewById(R.id.abort_bttn);
        buttonAbort.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
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
                            volume = mRecorder.getMaxAmplitude();  //Get the sound pressure value
                            if (volume > 0 && volume < 1000000)
                            {
                                World.setDbCount(20 * (float) (Math.log10(volume)));  //Change the sound pressure value to the decibel value
                                // Update with thread
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
        mRecorder.delete(); //Stop recording and delete the recording file
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
}