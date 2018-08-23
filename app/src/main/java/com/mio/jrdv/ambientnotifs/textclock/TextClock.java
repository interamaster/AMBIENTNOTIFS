package com.mio.jrdv.ambientnotifs.textclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mio.jrdv.ambientnotifs.R;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by joseramondelgado on 23/08/18.
 */


public class TextClock extends LinearLayout {
    private Calendar mTime;
    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvDay;
    private TextView tvMonth;
    private TextView tvSecond;
    private TextView tvMeridian;
    private boolean is24HourFormat;
    private boolean showSecond;

    private static final int DALEY = 1000;

    public TextClock(Context context) {
        super(context);
    }

    public TextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        createTime(TimeZone.getDefault().getID());

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.TextClock, defStyleAttr, 0);

        LayoutInflater.from(context).inflate(R.layout.text_clock_layout, this);

        tvHour = (TextView) findViewById(R.id.hour);
        tvMinute = (TextView) findViewById(R.id.minute);
        tvDay = (TextView) findViewById(R.id.day);
        tvMonth = (TextView) findViewById(R.id.month);
        tvMeridian = (TextView) findViewById(R.id.meridian);
        tvSecond = (TextView) findViewById(R.id.second);
        int color;

        try {
            color = a.getColor(R.styleable.TextClock_color, Color.BLACK);
            is24HourFormat = a.getBoolean(R.styleable.TextClock_format24Hour, false);
            showSecond = a.getBoolean(R.styleable.TextClock_showSecond, true);
        } finally {
            a.recycle();
        }

        setColor(color);

        if (!showSecond)
            tvSecond.setVisibility(GONE);

        setTime();

        //TODO pte de quitar si no necesito
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTime();
                if (getHandler() != null)
                    getHandler().postDelayed(this, DALEY);
            }
        }, DALEY);
    }

    public void setColor(int color) {
        tvHour.setTextColor(color);
        tvMinute.setTextColor(color);
        tvDay.setTextColor(color);
        tvMonth.setTextColor(color);
        tvMeridian.setTextColor(color);
        tvSecond.setTextColor(color);
        findViewById(R.id.separador).setBackgroundColor(color);
    }

    private void createTime(String timeZone) {
        if (timeZone != null)
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        else
            mTime = Calendar.getInstance();
    }

    private void setTime() {
        mTime.setTimeInMillis(System.currentTimeMillis());

        tvHour.setText(String.valueOf(is24HourFormat ?
                mTime.get(Calendar.HOUR_OF_DAY) :
                mTime.get(Calendar.HOUR)));

        tvMinute.setText(DateFormat.format("mm", mTime));
        tvSecond.setText(String.valueOf(mTime.get(Calendar.SECOND)));
        tvMeridian.setText(DateFormat.format("a", mTime));

        tvDay.setText(String.valueOf(mTime.get(Calendar.DAY_OF_MONTH)));
        tvMonth.setText(DateFormat.format("MMM", mTime));
    }
}