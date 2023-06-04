package com.activity_tracker.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.activity_tracker.R;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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

            try
            {
                connection = new Socket("192.168.1.19", 5554);
                out = new ObjectOutputStream(connection.getOutputStream());
                // Write the username to the server
                out.writeObject(username);
                // Request the leaderboard data
                out.writeObject("LEADERBOARD");
                in = new ObjectInputStream(connection.getInputStream());

                // Receive the leaderboard data
                Object object = in.readObject();


            }
            catch (IOException e)
            {
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

            handler.post(() ->
            {
                // TODO: Update the UI with the leaderboard data
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