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
    A fragment that displays the user's distance compared to the average distance of all users
    in a bar chart.
*/
public class DistanceFragment extends BaseChartFragment
{
    public DistanceFragment()
    {
        // Required empty public constructor
    }

    // Creates the bar chart for the distance fragment
    // that compares the user's distance to the average distance
    @Override
    protected BarChart createBarChart(Statistics statistics)
    {
        BarChart chart = new BarChart(requireContext());

        // Create a list of bar entries
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) statistics.getUserStats(username).getTotalDistance()));
        entries.add(new BarEntry(1, (float) (statistics.getAverageDistance())));

        List<String> labels = new ArrayList<>();
        labels.add("Total Distance");
        labels.add("Average Distance");

        // Create a dataset for the bar entries
        BarDataSet dataSet = new BarDataSet(entries, "");

        dataSet.setColors(Color.parseColor("#1577af"), Color.parseColor("#9e00ff"));
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

        // Set the description text
        Description description = new Description();
        description.setText("Distance Comparison");
        chart.setDescription(description);

        updateDistanceInfo(statistics);

        return chart;
    }

    // Update the text view with the distance information for the user
    // compared to the average distance
    private void updateDistanceInfo(Statistics statistics)
    {
        double userDistance = statistics.getUserStats(username).getTotalDistance();
        double averageDistance = statistics.getAverageDistance();

        // If the average distance is 0, then there is no data to compare to
        if (averageDistance == 0)
        {
            return;
        }

        if (statistics.getUserStats(username).getRoutesRecorded() == 0)
        {
            percentageView.setText("Record some activities to see your progress compared to other users!");
            return;
        }

        double percentage = (userDistance - averageDistance) / averageDistance * 100;

        String infoText;
        if (percentage > 0)
        {
            infoText = "Your overall distance recorded is " + String.format("%.2f", percentage) + "% greater than the average user's distance recorded!";
        }
        else if (percentage < 0)
        {
            infoText = "Your overall distance recorded is " + String.format("%.2f", Math.abs(percentage)) + "% below the average user's distance recorded.";
        }
        else
        {
            infoText = "Your overall distance is on par with the average.";
        }

        percentageView.setText(infoText);
    }

}
