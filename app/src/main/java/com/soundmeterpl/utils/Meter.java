package com.soundmeterpl.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.soundmeterpl.R;
import com.soundmeterpl.activities.MeasureActivity;

public class Meter extends AppCompatImageView
{
    private float scaleWidth, scaleHeight;
    private int newWidth, newHeight;
    private Matrix mMatrix = new Matrix();
    private Bitmap indicatorBitmap;
    private Paint paint = new Paint();
    static final long ANIMATION_INTERVAL = 5;

    public Meter(Context context)
    {
        super(context);
    }

    public Meter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init()
    {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noise_index);
        int bitmapWidth = myBitmap.getWidth();
        int bitmapHeight = myBitmap.getHeight();
        newWidth = getWidth();
        newHeight = getHeight();
        scaleWidth = ((float) newWidth) / (float) bitmapWidth;
        scaleHeight = ((float) newHeight) / (float) bitmapHeight;
        mMatrix.postScale(scaleWidth, scaleHeight);
        indicatorBitmap = Bitmap.createBitmap(myBitmap, 0, 0, bitmapWidth, bitmapHeight, mMatrix, true);

        paint = new Paint();
        paint.setTextSize(80);
        paint.setTypeface(MeasureActivity.tf);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
    }

    public void refresh()
    {
        postInvalidateDelayed(ANIMATION_INTERVAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (indicatorBitmap == null)
        {
            init();
        }
        mMatrix.setRotate(getAngle(World.dbCount), newWidth / 2, newHeight * 215 / 460);
        canvas.drawBitmap(indicatorBitmap, mMatrix, paint);
        canvas.drawText((int) World.dbCount + " dB", newWidth / 2, newHeight * 36 / 46, paint);
    }

    private float getAngle(float db)
    {
        return (db - 85) * 5 / 3;
    }
}
