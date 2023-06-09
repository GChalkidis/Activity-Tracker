package com.activity_tracker.frontend;

import com.activity_tracker.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.activity_tracker.backend.calculations.UserStatistics;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = "ProfileActivity";

    private String username;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        // Receive the username from the previous activity
        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        TextView usernameTextView = findViewById(R.id.profileText);
        usernameTextView.setText("Profile\n\n" + "Statistics for " + username);
        this.handler = new Handler(Looper.getMainLooper());


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
            UserStatistics userStatistics = null;

            try
            {
                connection = new Socket("192.168.1.19", 8890);
                out = new ObjectOutputStream(connection.getOutputStream());
                // Write the username to the server
                out.writeObject(username);
                out.flush();
                // Request the leaderboard data
                out.writeObject("STATISTICS");
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());

                // Receive the leaderboard data
                Object object = in.readObject();
                Log.e(TAG, "onCreate: " + object.getClass().getName());
                if (object instanceof UserStatistics)
                {
                    userStatistics = (UserStatistics) object;

                }
                else
                {
                    throw new RuntimeException("Received object is not of type UserStatistics");
                }



            }
            catch (IOException e)
            {
                // Handle exceptions
                e.printStackTrace();

            }
            catch (ClassNotFoundException e)
            {
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


            final UserStatistics finalUserStatistics = userStatistics;
            handler.post(() ->
            {
                updateUI(finalUserStatistics);
            });

        }).start();
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