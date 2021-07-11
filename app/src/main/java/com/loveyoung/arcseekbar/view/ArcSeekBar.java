package com.loveyoung.arcseekbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.loveyoung.arcseekbar.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Author:created  By Walt-zhong at 2021/4/28 12:06
 * e-Mail:2511255880@qq.com
 */
public class ArcSeekBar extends View {
    private static final String TAG = "ArcSeekBar";
    private Drawable mIndicator;
    private Drawable mResetIcon;
    private Drawable mAutoIcon;
    private Paint mArcIndicatorPaint;
    private float sweepAngle;
    private float lastAngle = 75;
    private float startX;
    private float startY;
    private Paint mValuePaint = null;

    private String[] mValueArray;

    private final float START_ANGLE = 75;
    private final float SWEEP_ANGLE = 30;
    private final float END_ANGLE = START_ANGLE + SWEEP_ANGLE;
    private final float ALPHA_255 = 255;

    private final int ANGLE_180 = 180;
    private final int INT_2 = 2;
    private final int INT_3 = 3;

    private final float ANGLE_90_DIV_START_ANGLE = (90 - START_ANGLE);


    private float mStep = SWEEP_ANGLE / 51f;

    private float mStepN = 0;

    private int mPadding = 30;
    private float mRadius = 500;
    private float rad = 80;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcSeekBar(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParam(context, attrs);
    }

    public void init(String[] valueArray){
        int length = valueArray.length;
        int dotCount = Math.round(SWEEP_ANGLE/length);
        mStep = SWEEP_ANGLE/((dotCount + 1)*length);
        mStepN = mStep * (dotCount+1);


        if (length > 30) {
            mStep = SWEEP_ANGLE/length;
            mStepN = mStep;
        }
        Log.d(TAG,"mstep: " + mStep + ",radius: " + mRadius + ",mStepN: " + mStepN + ", dotCount: " + dotCount
        +" ,length: " + length);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initParam(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcSeekBar);
        int leftPadding = typedArray.getDimensionPixelSize(R.styleable.ArcSeekBar_left_padding, -1);
        int rightPadding = typedArray.getDimensionPixelSize(R.styleable.ArcSeekBar_right_padding, -1);
        float bgAlpha = typedArray.getFloat(R.styleable.ArcSeekBar_arc_seek_bar_alpha, 0f);
        int indicatorColor = typedArray.getColor(R.styleable.ArcSeekBar_indicator_color, -1);

        typedArray.recycle();

        mValueArray = getResources().getStringArray(R.array.shutter_list);
        init(mValueArray);

        mIndicator = getResources().getDrawable(R.drawable.ic_manual_pointer);
        mResetIcon = getResources().getDrawable(R.drawable.ic_manual_auto_inactive);
        mAutoIcon = getResources().getDrawable(R.drawable.ic_manual_auto_location);


        mPadding = leftPadding + rightPadding;
        sweepAngle = START_ANGLE;
        mArcIndicatorPaint = new Paint();
        mArcIndicatorPaint.setStyle(Paint.Style.FILL);
        mArcIndicatorPaint.setAntiAlias(true);
        mArcIndicatorPaint.setColor(indicatorColor);

        mValuePaint = new Paint();
        mValuePaint.setStyle(Paint.Style.STROKE);
        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(Color.GREEN);
        mValuePaint.setTextSize(120);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = (float) (((getWidth() - mPadding) / INT_2) / Math.sin(Math.toRadians(ANGLE_90_DIV_START_ANGLE)));
        mRadius = radius;
        float cx1 = getWidth() >> 1;
        float cy1 = (float) (mRadius * Math.cos(Math.toRadians(ANGLE_90_DIV_START_ANGLE)) +
                ((getWidth() / INT_2) * Math.sin(Math.toRadians(ANGLE_90_DIV_START_ANGLE))));
        float cx = (float) (cx1 - mRadius * Math.cos(Math.toRadians(sweepAngle)));
        float cy = (float) (cy1 - mRadius * Math.sin(Math.toRadians(sweepAngle)));

        drawResetIcon(canvas, cx1, cy1, radius);
        drawAutoIcon(canvas, cx1, cy1, radius);
        drawArcGradition(canvas, cx1, cy1, radius);
        drawIndicator(canvas, cx, cy);
    }

    private void drawAutoIcon(Canvas canvas, float cx1, float cy1, float radius) {
        canvas.save();
        float cx = (float) (cx1 - (radius * Math.cos(Math.toRadians(START_ANGLE))));
        float cy = (float) (cy1 - (radius * Math.sin(Math.toRadians(START_ANGLE))));

        mAutoIcon.setBounds((int) cx - 30, (int) cy - 30, (int) cx + 30, (int) cy + 30);
        canvas.rotate(-15, cx, cy);
        mAutoIcon.draw(canvas);
        canvas.restore();
    }

    private void drawIndicator(Canvas canvas, float cx, float cy) {
        canvas.save();
        mIndicator.setBounds((int) cx - 30, (int) cy - 30, (int) cx + 30, (int) cy + 30);
        mIndicator.draw(canvas);

        canvas.drawText(mValueArray[getIndex(sweepAngle)], getWidth() >> 1, getHeight() >> 1, mValuePaint);
        canvas.restore();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawArcGradition(Canvas canvas, float cx1, float cy1, float radius) {
        canvas.save();

        float angle;
            for (angle = START_ANGLE + mStep; angle <= (END_ANGLE - mStepN); angle += mStep) {
                float cx = (float) (cx1 - radius * Math.cos(Math.toRadians(angle)));
                float cy = (float) (cy1 - radius * Math.sin(Math.toRadians(angle)));
                canvas.drawCircle(cx, cy, 1, mArcIndicatorPaint);
            }


        for (angle = START_ANGLE + mStepN; angle <= END_ANGLE; angle += mStepN) {
            float cx = (float) (cx1 - radius * Math.cos(Math.toRadians(angle)));
            float cy = (float) (cy1 - radius * Math.sin(Math.toRadians(angle)));
            canvas.drawCircle(cx, cy, 5, mArcIndicatorPaint);
        }

        canvas.restore();
    }


    private void drawResetIcon(Canvas canvas, float cx1, float cy1, float radius) {
        canvas.save();
        float cx = (float) (cx1 - (radius * Math.cos(Math.toRadians(END_ANGLE))));
        float cy = (float) (cy1 - (radius * Math.sin(Math.toRadians(END_ANGLE))));

        mResetIcon.setBounds((int) cx - 30, (int) cy - 30, (int) cx + 30, (int) cy + 30);
        mResetIcon.draw(canvas);
        canvas.restore();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                if (event.getX() > (END_ANGLE - mStepN)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - startX;
                sweepAngle = (float) (lastAngle + ((distance * ANGLE_180) / (Math.PI * mRadius)));
                Log.d(TAG,"distanch: " + distance + "sweepAngle: " + sweepAngle);
                if (sweepAngle > (END_ANGLE - mStepN)) {
                    sweepAngle = END_ANGLE - mStepN;
                }

                if (sweepAngle < START_ANGLE) {
                    sweepAngle = START_ANGLE;
                }
                int index = getIndex(sweepAngle);
                android.util.Log.d(TAG, "zhongxj: index====>: " + index);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                float cx1 = getWidth() >> 1;
                float cy1 = (float) (mRadius * Math.cos(Math.toRadians(ANGLE_90_DIV_START_ANGLE)) +
                        ((mRadius / INT_2) * Math.sin(Math.toRadians(ANGLE_90_DIV_START_ANGLE))));
                float cx = (float) (cx1 - (mRadius * Math.cos(Math.toRadians(END_ANGLE))));
                float cy = (float) (cy1 - (mRadius * Math.sin(Math.toRadians(END_ANGLE))));


                Log.d(TAG,"zhongxj: startX: " +startX+",startY: " +startY+",cx: "+cx+",cy: " +cy);
                if (startX >= (cx- rad) &&(startX<=cx+ rad)
                        &&startY >= cy- rad && startY<=cy+ rad) {
                    resetValue2Default();
                    invalidate();
                    return true;
                }

                float angle = (float) Math.toRadians(sweepAngle - START_ANGLE);
                float step3Radius = (float) Math.toRadians(mStepN);
                int count = (int) (angle / step3Radius);
                float mod = angle % step3Radius;
                if (mod > step3Radius / INT_2) {
                    sweepAngle = START_ANGLE + (count + 1) * mStepN;
                } else {
                    sweepAngle = START_ANGLE + (count) * mStepN;
                }

                if (sweepAngle > (END_ANGLE - mStepN)) {
                    sweepAngle = END_ANGLE - mStepN;
                }

                if (sweepAngle < START_ANGLE) {
                    sweepAngle = START_ANGLE;
                }

                lastAngle = sweepAngle;
                startY = 0;
                startX = 0;
                invalidate();
                break;
        }

        return true;
    }

    private void resetValue2Default() {
        sweepAngle = START_ANGLE;
        lastAngle = START_ANGLE;
        startX = 0;
        startY = 0;
    }

    private int getIndex(float sweepAngle) {
        int index = 0;
        float step = mStepN == 0 ? 1 : mStepN;
        index = Math.round(((sweepAngle - START_ANGLE) / step));
        return index;
    }


}
