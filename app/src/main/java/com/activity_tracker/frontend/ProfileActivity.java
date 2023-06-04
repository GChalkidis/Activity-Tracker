package com.activity_tracker.frontend;

import com.activity_tracker.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
                startActivity(new Intent(getApplicationContext(), LeaderboardActivity.class));
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
                connection = new Socket("192.168.1.19", 5554);
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
                Log.d(TAG,object.toString());


            }
            catch (IOException e)
            {
                // Handle exceptions
                e.printStackTrace();

            } catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            } finally
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


            handler.post(() ->
            {
                // TODO: Update the UI with the profile data
            });

        }).start();
    }

    private void updateUI()
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}