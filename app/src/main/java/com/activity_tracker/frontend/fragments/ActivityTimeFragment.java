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
    A fragment that displays the user's activity time compared to
    the average activity time of all users in a bar chart.
 */
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
        entries.add(new BarEntry(1, (float) statistics.getAverageActivityTime()));

        BarDataSet dataSet = new BarDataSet(entries, "");
        // Set the colors of the bars
        dataSet.setColors(Color.parseColor("#1577af"), Color.parseColor("#9e00ff"));

        // Create labels for the legend
        List<String> labels = new ArrayList<>();
        labels.add("Your Activity Time");
        labels.add("Average Activity Time");

        // Create a LegendEntry for each color
        List<LegendEntry> legendEntries = new ArrayList<>();
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
        double averageActivityTime = statistics.getAverageActivityTime();

        // If the average activity time is 0, then there is no data to compare to
        if (averageActivityTime == 0)
        {
            return;
        }

        if (statistics.getUserStats(username).getRoutesRecorded() == 0)
        {
            percentageView.setText("Record some activities to see your progress compared to other users!");
            return;
        }

        double percentage = (userActivityTime - averageActivityTime) / averageActivityTime * 100;

        String infoText;
        if (percentage > 0)
        {
            infoText = "Your overall activity time is " + String.format("%.2f", percentage) + "% greater than the average user's activity time!";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall activity time is " + String.format("%.2f", Math.abs(percentage)) + "% below the average user's activity time.";
        }
        else
        {
            infoText = "Your overall activity time is on par with the average.";
        }

        percentageView.setText(infoText);
    }


}
