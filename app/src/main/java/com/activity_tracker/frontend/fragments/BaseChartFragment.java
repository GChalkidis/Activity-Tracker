package com.activity_tracker.frontend.fragments;

import android.content.Intent;
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

/*
 * Base class for all fragments that display a chart of the user's statistics.
 * TODO: Check how the charts behave when values have a large deviation
 */
public abstract class BaseChartFragment extends Fragment
{
    private static final String ARG_STATISTICS = "statistics";
    private static final String ARG_USERNAME = "username";

    // The statistics object that contains the data to be displayed
    private Statistics statistics;
    // The username of the user whose data is being displayed
    protected String username;

    // The TextView that displays the percentage
    protected TextView percentageView;

    public BaseChartFragment()
    {
        // Required empty public constructor
    }

    // Creates a new instance of the fragment with the given statistics and fragment class
    public static Fragment newInstance(Statistics statistics, Class<? extends BaseChartFragment> fragmentClass, String username)
    {
        try
        {
            BaseChartFragment fragment = fragmentClass.newInstance();
            Bundle args = new Bundle();
            args.putSerializable(ARG_STATISTICS, statistics);
            args.putString(ARG_USERNAME, username);
            fragment.setArguments(args);
            return fragment;
        }
        catch (IllegalAccessException | InstantiationException | java.lang.InstantiationException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            statistics = (Statistics) getArguments().getSerializable(ARG_STATISTICS);
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutResource(), container, false);

        percentageView = view.findViewById(R.id.percentageView);

        AppCompatImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> navigateToProfile());

        // Create the chart
        BarChart chart = createBarChart(statistics);

        // Get the reference to the ViewGroup in the layout where you want to add the chart
        ViewGroup chartContainer = view.findViewById(R.id.chartContainer);

        // Clear any existing views in the chart container
        chartContainer.removeAllViews();

        // Add the chart to the chart container
        chartContainer.addView(chart);

        return view;
    }


    // Navigates to the profile activity when the back button is pressed
    private void navigateToProfile()
    {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("EXTRA_USERNAME", username);
        startActivity(intent);
        getActivity().finish();
    }

    // Abstract method to get the layout resource, can also be implemented here
    protected int getLayoutResource()
    {
        return R.layout.fragment_chart_base;
    }


    // Abstract method to create the chart, implemented by subclasses to create different charts
    protected abstract BarChart createBarChart(Statistics statistics);
}
