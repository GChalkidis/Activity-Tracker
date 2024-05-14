package com.activity_tracker.frontend.fragments;

import android.graphics.Color;

import com.activity_tracker.backend.calculations.Statistics;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/*
    A fragment that displays the user's elevation compared to the average elevation of all users
    in a bar chart.
 */
public class ElevationFragment extends BaseChartFragment
{

    public ElevationFragment()
    {
        // Required empty public constructor
    }
    // Creates the bar chart for the elevation fragment
    // that compares the user's elevation to the average elevation
    @Override
    protected BarChart createBarChart(Statistics statistics)
    {
        BarChart chart = new BarChart(requireContext());

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) statistics.getUserStats(username).getTotalElevation()));
        entries.add(new BarEntry(1, (float) statistics.getAverageElevation()));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#1577af"), Color.parseColor("#9e00ff"));

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

    // Updates the elevation info for the user
    // compared to the average elevation
    private void updateElevationInfo(Statistics statistics)
    {
        double userElevation = statistics.getUserStats(username).getTotalElevation();
        double averageElevation = statistics.getAverageElevation();

        // If the average elevation is 0, then there is no data to compare to
        if (averageElevation == 0)
        {
            return;
        }

        if (statistics.getUserStats(username).getRoutesRecorded() == 0)
        {
            percentageView.setText("Record some activities to see your progress compared to other users!");
            return;
        }

        double percentage = (userElevation - averageElevation) / averageElevation * 100;

        String infoText;

        if (percentage > 0)
        {
            infoText = "Your overall elevation recorded is " + String.format("%.2f", percentage) + "% greater than the average user's elevation recorded!";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall elevation recorded is " + String.format("%.2f", Math.abs(percentage)) + "% below the average user's distance recorded.";
        }
        else
        {
            infoText = "Your overall elevation recorded is on par with the average.";
        }
        percentageView.setText(infoText);
    }

}
