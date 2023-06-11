package com.activity_tracker.frontend.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import com.activity_tracker.R;
import com.activity_tracker.backend.calculations.Statistics;
import com.activity_tracker.frontend.ProfileActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.util.ArrayList;
import java.util.List;

public class DistanceFragment extends BaseChartFragment
{

    @Override
    protected int getLayoutResource()
    {
        return R.layout.fragment_chart_base;
    }

    @Override
    protected BarChart createBarChart(Statistics statistics)
    {
        BarChart chart = new BarChart(requireContext());

        // Create a list of bar entries
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) statistics.getUserStats(username).getTotalDistance()));
        entries.add(new BarEntry(1, (float) (statistics.getAverageDistances())));

        List<String> labels = new ArrayList<>();
        labels.add("Total Distance");
        labels.add("Average Distance");

        // Create a dataset for the bar entries
        BarDataSet dataSet = new BarDataSet(entries, "");

        dataSet.setColors(Color.BLUE, Color.GREEN);
        dataSet.setLabel("Distance");
        dataSet.setValueTextColor(Color.BLACK);

        // Set labels for the legend
        ArrayList<String> legendLabels = new ArrayList<>();
        legendLabels.add("Your Distance");
        legendLabels.add("Average Distance");

        // Create a LegendEntry for each color
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        for (int i = 0; i < dataSet.getColors().size(); i++)
        {
            LegendEntry entry = new LegendEntry();
            entry.formColor = dataSet.getColors().get(i);
            entry.label = legendLabels.get(i);
            legendEntries.add(entry);
        }

        // Set the custom legend entries
        Legend legend = chart.getLegend();
        legend.setCustom(legendEntries);

        // Create a BarData object with the dataset
        BarData data = new BarData(dataSet);
        chart.setData(data);

        chart.setFitBars(true);
        chart.setDrawValueAboveBar(true);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);

        Description description = new Description();
        description.setText("Distance Comparison");
        chart.setDescription(description);

        updateDistanceInfo(statistics);

        return chart;
    }


    private void updateDistanceInfo(Statistics statistics)
    {
        double userDistance = statistics.getUserStats(username).getTotalDistance();
        double averageDistance = statistics.getAverageDistances();

        double percentage = (userDistance - averageDistance) / averageDistance * 100;

        String infoText;
        if (percentage > 0)
        {
            infoText = "Your overall distance is greater than " + String.format("%.2f", percentage) + "% of users!";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall distance is " + String.format("%.2f", Math.abs(percentage)) + "% below the average.";
        }
        else
        {
            infoText = "Your overall distance is on par with the average.";
        }

        percentageView.setText(infoText);
    }

}
