package com.activity_tracker.frontend.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class ElevationFragment extends BaseChartFragment
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

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) statistics.getUserStats(username).getTotalElevation()));
        entries.add(new BarEntry(1, (float) statistics.getAverageElevations()));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(Color.BLUE, Color.GREEN);

        // Set labels for the legend
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Your Elevation");
        labels.add("Average Elevation");

        // Create a LegendEntry for each color
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        for (int i = 0; i < dataSet.getColors().size(); i++)
        {
            LegendEntry entry = new LegendEntry();
            entry.formColor = dataSet.getColors().get(i);
            entry.label = labels.get(i);
            legendEntries.add(entry);
        }

        // Set the custom legend entries
        Legend legend = chart.getLegend();
        legend.setCustom(legendEntries);

        BarData data = new BarData(dataSet);
        chart.setData(data);

        chart.setFitBars(true);
        chart.setDrawValueAboveBar(true);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);

        // Set a description for the chart
        Description description = new Description();
        description.setText("Elevation Comparison");
        chart.setDescription(description);

        updateElevationInfo(statistics);

        return chart;
    }


    private void updateElevationInfo(Statistics statistics)
    {
        double userElevation = statistics.getUserStats(username).getTotalElevation();
        double averageElevation = statistics.getAverageElevations();

        double percentage = (userElevation - averageElevation) / averageElevation * 100;

        String infoText;
        if (percentage > 0)
        {
            infoText = "Your overall elevation is greater than " + String.format("%.2f", percentage) + "% of users!";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall elevation is " + String.format("%.2f", Math.abs(percentage)) + "% below the average.";
        }
        else
        {
            infoText = "Your overall elevation is on par with the average.";
        }
        percentageView.setText(infoText);
    }

}
