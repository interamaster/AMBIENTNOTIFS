package com.mio.jrdv.ambientnotifs.views;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mio.jrdv.ambientnotifs.R;

import java.util.ArrayList;

/**
 * Created by esq00931 on 07/09/2018.
 */



public class PiegraphPreference  extends Preference{

    int  numWhats,numGmail,numutlook,numTelegram;


    public PiegraphPreference(Context context) {
        super(context);
    }

    public PiegraphPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void SetDatas(int numWhatsfromPref, int numGmailfromPref, int numutlookfromPref, int numTelegramfromPref)

    {

        numWhats=Math.abs(numWhatsfromPref);
        numGmail=Math.abs(numGmailfromPref);
        numutlook=Math.abs(numutlookfromPref);
        numTelegram=Math.abs(numTelegramfromPref);

    }


    public static final int[] MIS_COLORS = {
            rgb("#25d366"), rgb("#D44638"), rgb("#0072C6"), rgb("#0088cc")
    };


    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public View getView(View convertView, ViewGroup parent)
    {
        View v = super.getView(convertView, parent);






        PieChart pieChart=(PieChart)v.findViewById(R.id.piechart_1);





        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(true);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry((float)numWhats,"Whastapp"));
        yValues.add(new PieEntry((float)numGmail,"Gmail"));
        yValues.add(new PieEntry((float)numutlook,"Outlook"));
        yValues.add(new PieEntry((float)numTelegram,"Telegram"));



        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(MIS_COLORS);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);
        pieChart.setData(pieData);
        pieChart.getDescription().setText("");
        //PieChart Ends Here






        return v;
    }

}
