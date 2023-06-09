package com.activity_tracker.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.activity_tracker.R;
import androidx.appcompat.app.AppCompatActivity;

import com.activity_tracker.backend.calculations.SegmentLeaderboard;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class LeaderboardActivity extends AppCompatActivity
{
    private static final String TAG = "LeaderboardActivity";
    private Handler handler;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.leaderboard);

        // Receive the username from the previous activity
        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        Log.e(TAG, "Username: " + username);

        handler = new Handler(Looper.getMainLooper());

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
                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("username", username);
                startActivity(i);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if (R.id.leaderboard == id)
            {
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
            ArrayList<SegmentLeaderboard> leaderboards = null;

            try
            {
                connection = new Socket("192.168.1.19", 8890);
                out = new ObjectOutputStream(connection.getOutputStream());
                // Write the username to the server
                out.writeObject(username);
                out.flush();
                // Request the leaderboard data
                out.writeObject("LEADERBOARD");
                out.flush();


                in = new ObjectInputStream(connection.getInputStream());
                // Receive the leaderboard data
                Object object = in.readObject();

                // if the object is an ArrayList of SegmentLeaderboards, then cast it to that type
                // else, the leaderboards is null and will be handled in the updateUI method
                if (object instanceof ArrayList)
                {
                    leaderboards = (ArrayList<SegmentLeaderboard>) object;
                }

            }
            catch (IOException e)
            {
                Log.e(TAG, "Error connecting to server");
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            catch (ClassNotFoundException e)
            {
                Log.e(TAG, "Error casting object to ArrayList<SegmentLeaderboard>");
                throw new RuntimeException(e);
            }
            finally
            {
                Log.e(TAG, "Closing resources");
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

            ArrayList<SegmentLeaderboard> finalLeaderboards = leaderboards;
            handler.post(() ->
            {
                // TODO: Update the UI with the leaderboard data
                updateUI(finalLeaderboards);
            });

        }).start();
    }

    private void updateUI(ArrayList<SegmentLeaderboard> leaderboards)
    {
        if (leaderboards != null)
        {

        }
        else
        {
            // TODO: Display a message to the user that there are no leaderboards
        }

    }

}