package com.mio.jrdv.ambientnotifs.views;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.mio.jrdv.ambientnotifs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esq00931 on 07/09/2018.
 */

public class HorizontalBarPreference extends Preference {

    int  numWhats,numGmail,numutlook,numTelegram;


    public HorizontalBarPreference(Context context) {
        super(context);
    }

    public HorizontalBarPreference(Context context, AttributeSet attrs) {
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






        HorizontalBarChart horizontalbar=(HorizontalBarChart) v.findViewById(R.id.horbarchar1);



        horizontalbar.animateXY(2000, 2000);
        horizontalbar.invalidate();


        // create BarEntry for Bar 1

        ArrayList<BarEntry> yValues = new ArrayList<>();

        yValues.add(new BarEntry((float)numWhats,0));
        yValues.add(new BarEntry((float)numGmail,1));
        yValues.add(new BarEntry((float)numutlook,2));
        yValues.add(new BarEntry((float)numTelegram,3));

        // creating dataset for group 1
        BarDataSet dataset = new BarDataSet(yValues,"hi");
        dataset.setColors(MIS_COLORS);



        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Whatsapp");
        xAxis.add("Gmail");
        xAxis.add("Outlook");
        xAxis.add("Telegram");



        ArrayList<IBarDataSet> dataSets = new ArrayList<>();  // combined all dataset into an arraylist
        dataSets.add(dataset);
        BarData data = new BarData( dataSets); // initialize the Bardata with argument labels and dataSet
        horizontalbar.setData(data);


        data.setBarWidth(9f);
        horizontalbar.setData(data);

        horizontalbar.getDescription().setText("");
         horizontalbar.getDescription().setEnabled(true);
      //  horizontalbar.setExtraOffsets(5,10,5,5);
       // horizontalbar.setDragDecelerationFrictionCoef(0.9f);

      //  horizontalbar.animateY(1000, Easing.EasingOption.EaseInOutCubic);


        horizontalbar.getXAxis().setDrawGridLines(false);
        horizontalbar.getAxisLeft().setDrawGridLines(false);
       /*
        ArrayList<BarEntry> yValues = new ArrayList<>();

        yValues.add(new BarEntry((float)numWhats,0));
        yValues.add(new BarEntry((float)numGmail,1));
        yValues.add(new BarEntry((float)numutlook,2));
        yValues.add(new BarEntry((float)numTelegram,3));



        BarDataSet dataSet = new BarDataSet(yValues, "");

        dataSet.setColors(MIS_COLORS);
        BarData barData = new BarData( dataSet);
        barData.setValueTextSize(10f);
        barData.setValueTextColor(Color.YELLOW);

        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Whatsapp");
        xAxis.add("Gmail");
        xAxis.add("Outlook");
        xAxis.add("Telegram");


        //PieChart Ends Here

*/


        return v;
    }



    private BarDataSet getDataSet() {
        // create BarEntry for Bar 1

        ArrayList<BarEntry> yValues = new ArrayList<>();

        yValues.add(new BarEntry((float)numWhats,0));
        yValues.add(new BarEntry((float)numGmail,1));
        yValues.add(new BarEntry((float)numutlook,2));
        yValues.add(new BarEntry((float)numTelegram,3));

        // creating dataset for group 1
        BarDataSet dataset = new BarDataSet(yValues,"hi");
        dataset.setColors(MIS_COLORS);

        return dataset;
    }
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Whatsapp");
        xAxis.add("Gmail");
        xAxis.add("Outlook");
        xAxis.add("Telegram");
        return xAxis;
    }
}
