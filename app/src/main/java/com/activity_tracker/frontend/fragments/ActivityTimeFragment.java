package com.activity_tracker.frontend.fragments;

import android.graphics.Color;

import com.activity_tracker.R;
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

public class ActivityTimeFragment extends BaseChartFragment
{

    public ActivityTimeFragment()
    {
        // Required empty public constructor
    }

    // Creates the bar chart for the activity time fragment
    // that compares the user's activity time to the average activity time
    @Override
    protected BarChart createBarChart(Statistics statistics)
    {
        BarChart chart = new BarChart(requireContext());

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) statistics.getUserStats(username).getTotalActivityTime()));
        entries.add(new BarEntry(1, (float) statistics.getAverageActivityTimes()));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(Color.BLUE, Color.GREEN); // Set the colors of the bars

        // Create labels for the legend
        List<String> labels = new ArrayList<>();
        labels.add("Your Activity Time");
        labels.add("Average Activity Time");

        List<LegendEntry> legendEntries = new ArrayList<>();
        for (int i = 0; i < dataSet.getColors().size(); i++)
        {
            LegendEntry entry = new LegendEntry();
            entry.formColor = dataSet.getColors().get(i);
            entry.label = labels.get(i);
            legendEntries.add(entry);
        }

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
        description.setText("Activity Time Comparison");
        chart.setDescription(description);

        updateActivityTimeInfo(statistics);

        return chart;
    }

    // Updates the text view that displays information about the user's activity time
    // compared to the average activity time
    private void updateActivityTimeInfo(Statistics statistics)
    {
        double userActivityTime = statistics.getUserStats(username).getTotalActivityTime();
        double averageActivityTime = statistics.getAverageActivityTimes();

        double percentage = (userActivityTime - averageActivityTime) / averageActivityTime * 100;

        String infoText;
        if (percentage > 0)
        {
            infoText = "Your overall activity time is above average by " + String.format("%.2f", percentage) + "%.";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall activity time is " + String.format("%.2f", Math.abs(percentage)) + "% below the average.";
        }
        else
        {
            infoText = "Your overall activity time is on par with the average.";
        }

        percentageView.setText(infoText);
    }


}
