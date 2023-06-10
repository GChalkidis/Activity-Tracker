package com.activity_tracker.frontend;

import com.activity_tracker.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.activity_tracker.backend.calculations.Statistics;
import com.activity_tracker.backend.calculations.UserStatistics;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = "ProfileActivity";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private String username;
    private Handler handler;

    private Statistics statistics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        // Receive the username from the previous activity.
        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        TextView usernameTextView = findViewById(R.id.profileText);
        usernameTextView.setText("Profile\n\n" + "Statistics for " + username);
        this.handler = new Handler(Looper.getMainLooper());

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                final int id = item.getItemId();
                if (R.id.activity_time_item == id)
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Toast.makeText(ProfileActivity.this, "Activity Time", Toast.LENGTH_SHORT).show();
                    fragmentR(new ActivityTimeFragment());
                }
                if (R.id.elevation_item == id)
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Toast.makeText(ProfileActivity.this, "Elevation Time", Toast.LENGTH_SHORT).show();
                    fragmentR(new ElevationFragment());
                }
                if (R.id.distance_item == id)
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Toast.makeText(ProfileActivity.this, "Distance Time", Toast.LENGTH_SHORT).show();
                    fragmentR(new DistanceFragment());
                }
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item ->
        {
            final int id = item.getItemId();
            if (R.id.home == id)
            {
                startActivity(new Intent(getApplicationContext(), Menu.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if (R.id.profile == id)
            {
                return true;
            }
            else if (R.id.leaderboard == id)
            {
                Intent i = new Intent(this, LeaderboardActivity.class);
                i.putExtra("username", username);
                startActivity(i);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            return false;
        });

        new Thread(() ->
        {
            // Create a new socket connection to the server and ask for the leaderboard data
            Socket connection = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try
            {
                connection = new Socket("192.168.1.19", 8890);
                out = new ObjectOutputStream(connection.getOutputStream());
                // Write the username to the server.
                out.writeObject(username);
                out.flush();
                // Request the leaderboard data.
                out.writeObject("STATISTICS");
                out.flush();
                // Read the leaderboard data.
                in = new ObjectInputStream(connection.getInputStream());

                Object object = in.readObject();
                Log.e(TAG, "onCreate: " + object.getClass().getName());
                if (object instanceof Statistics)
                {
                    Log.e(TAG, "onCreate: " + "Received statistics");
                    this.statistics = (Statistics) object;
                }
                else
                {
                    throw new RuntimeException("Received object is not of type UserStatistics");
                }
                Log.e(TAG, "onCreate: " + "???");


            }
            catch (IOException e)
            {
                // Handle exceptions
                Log.e(TAG, e.getMessage());
                e.printStackTrace();

            }
            catch (ClassNotFoundException e)
            {
                Log.e(TAG, e.getMessage());

                throw new RuntimeException(e);
            }
            finally
            {
                // Close resources in the finally block
                try
                {
                    if (in != null)
                    {
                        in.close();
                    }
                    if (out != null)
                    {
                        out.close();
                    }
                    if (connection != null)
                    {
                        connection.close();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "onCreate: " + username);
            final UserStatistics finalUserStatistics = statistics.getUserStats(username);
            handler.post(() ->
            {
                updateUI(finalUserStatistics);
            });

        }).start();
    }

    private void fragmentR(Fragment fragment)
    {
        // Hide other elements before replacing the fragment
        LinearLayout linearLayout = findViewById(R.id.profileStatsContainer);
        TextView noStatsTextView = findViewById(R.id.noStatsTextView);
        linearLayout.setVisibility(View.GONE);
        noStatsTextView.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    private void updateUI(UserStatistics finalUserStatistics)
    {
        LinearLayout linearLayout = findViewById(R.id.profileStatsContainer);
        TextView noStatsTextView = findViewById(R.id.noStatsTextView);

        if (finalUserStatistics != null)
        {
            // UserStatistics object is not null, inflate and add the profile stats view
            View profileStats = getLayoutInflater().inflate(R.layout.profilestats, null);
            linearLayout.addView(profileStats);

            setProfileStats(finalUserStatistics);
            noStatsTextView.setVisibility(View.GONE);
        } else
        {
            Log.e(TAG, "updateUI: UserStatistics object is null");
            // UserStatistics object is null, display the "No statistics available" message
            noStatsTextView.setVisibility(View.VISIBLE);
        }
    }




    private void setProfileStats(UserStatistics userStatistics)
    {
        TextView textViewRoutesRecorded = findViewById(R.id.textViewRoutesRecorded);
        textViewRoutesRecorded.setText(String.valueOf(userStatistics.getRoutesRecorded()));

        TextView textViewDistance = findViewById(R.id.textViewDistance);
        textViewDistance.setText(String.format("%.2f", userStatistics.getTotalDistance()));

        TextView textViewElevation = findViewById(R.id.textViewElevation);
        textViewElevation.setText(String.format("%.2f", userStatistics.getTotalElevation()));

        TextView textViewWorkoutTime = findViewById(R.id.textViewWorkoutTime);
        textViewWorkoutTime.setText(String.format("%.2f", userStatistics.getTotalActivityTime()));

        TextView textViewAverageDistance = findViewById(R.id.textViewAverageDistance);
        textViewAverageDistance.setText(String.format("%.2f", userStatistics.getAverageDistance()));

        TextView textViewAverageElevation = findViewById(R.id.textViewAverageElevation);
        textViewAverageElevation.setText(String.format("%.2f", userStatistics.getAverageElevation()));

        TextView textViewAverageWorkoutTime = findViewById(R.id.textViewAverageWorkoutTime);
        textViewAverageWorkoutTime.setText(String.format("%.2f", userStatistics.getAverageActivityTime()));

    }



}