package com.activity_tracker.frontend;

import com.activity_tracker.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.activity_tracker.backend.calculations.ActivityStats;
import com.activity_tracker.backend.misc.GPXData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;


public class Menu extends AppCompatActivity {
    private static final String TAG = "MENU";
    private Handler handler;
    private String username;
    private View activityCard;
    private ActivityStats activityStats;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item ->
        {
            final int id = item.getItemId();
            if (R.id.home == id)
            {
                return true;
            } else if (R.id.profile == id)
            {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (R.id.leaderboard == id)
            {
                Intent intent = new Intent(this, LeaderboardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            return false;
        });

        // Getting the username from the previous activity
        Bundle username = getIntent().getExtras();
        if (username != null)
        {
            final String usernameString = username.getString("username");
            Log.e(TAG, "onCreate: " + usernameString);
            this.username = usernameString;

            final TextView welcomeMessage = findViewById(R.id.welcome_text);
            final String welcomeMessageString = "Welcome, " + usernameString;
            welcomeMessage.setText(welcomeMessageString);
        }

        handler = new Handler(Looper.getMainLooper());
        ImageView uploadData = findViewById(R.id.upload_data);
        uploadData.setOnClickListener(v -> OpenFileChooser());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        saveActivityStats();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadActivityStats();
        updateUI(activityStats,username);
    }

    private void saveActivityStats()
    {
        SharedPreferences mPrefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(activityStats);
        Log.d(TAG, "Saving activity stats: " + json);
        prefsEditor.putString("StatsObject", json);
        Log.d(TAG, "Username to be saved: " + username); // Print the username to be saved
        prefsEditor.putString("username", username);
        prefsEditor.apply();
    }

    private void loadActivityStats()
    {
        // Retrieve the object from the shared preferences
        SharedPreferences mPrefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("StatsObject", "");
        // Check if the username that was saved is the same as the one that is currently logged in
        String savedUsername = mPrefs.getString("username", "");
        activityStats = gson.fromJson(json, ActivityStats.class);
        // Condition 1: A new user has logged in and there is no saved activity stats
        if ((savedUsername != null && !savedUsername.equals(username)) && (username != null && !username.equals("") ))
        {
            Log.e(TAG, "No saved activity stats");
            this.username = username;
            activityStats = null;
            return;
        }

        // Condition 2: The user has logged in again and there are saved activity stats
        if (savedUsername != null && savedUsername.equals(username) && (username != null && !username.equals("")))
        {
            Log.e(TAG, "Loading saved activity stats");
            this.username = savedUsername;
            activityStats = gson.fromJson(json, ActivityStats.class);
            return;
        }

        // Condition 3: The user just switched to another activity and came back to this one, so load the activity stats and the username
        if (savedUsername != null && (username == null))
        {
            Log.e(TAG, "Loading from previous view activity stats");
            this.username = savedUsername;
            Log.e(TAG, "Loaded username: " + username);
            activityStats = gson.fromJson(json, ActivityStats.class);
        }
        Log.e(TAG, "Loaded username: " + username);
        Log.e(TAG, "Loading the activity stats");
    }


    private void setActivityStatsData(ActivityStats stats)
    {
        TextView speedView = activityCard.findViewById(R.id.averageSpeedTextView);
        TextView elevationView = activityCard.findViewById(R.id.elevationTextView);
        TextView timeView = activityCard.findViewById(R.id.activityTimeTextView);
        TextView distanceView = activityCard.findViewById(R.id.totalDistanceTextView);

        speedView.setText(String.format("%.2f", stats.getSpeed()) + " km/h");

        elevationView.setText(String.format("%.2f", stats.getElevation()));
        elevationView.append(" meters");
        elevationView.setGravity(Gravity.CENTER_VERTICAL);

        timeView.setText(String.format("%.2f", stats.getTime()) + " minutes");

        distanceView.setText(String.format("%.2f", stats.getDistance()) + " km");
    }

    private void updateUI(ActivityStats stats,String username)
    {
        Log.e(TAG, "Updating the UI");
        if (activityCard == null)
        {
            LinearLayout cardLayout = findViewById(R.id.linearLayout);
            activityCard = getLayoutInflater().inflate(R.layout.gpxstats_card, null);
            cardLayout.addView(activityCard);
        }

        if (stats != null)
        {
            setActivityStatsData(stats);
        }

        if (username != null)
        {
            TextView welcomeMessage = findViewById(R.id.welcome_text);
            String welcomeMessageString = "Welcome, " + username;
            welcomeMessage.setText(welcomeMessageString);
        }
    }

    public void onActivityResult(int REQUEST_CODE, int RESULT_CODE, Intent data)
    {
        super.onActivityResult(REQUEST_CODE, RESULT_CODE, data);

        if (RESULT_CODE == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            byte[] fileData = getFileDataFromUri(uri);
            Log.e(TAG, data.getData().toString());

            Log.e(TAG, "Now sending the file to the server");

            new Thread(() -> {
                // TODO: Open a new socket and send the gpxData to the server.
                GPXData gpxdata = new GPXData(fileData);
                Socket connection = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;
                final ActivityStats[] stats = new ActivityStats[1];

                try
                {
                    connection = new Socket("192.168.1.19", 8890);
                    out = new ObjectOutputStream(connection.getOutputStream());
                    // Send the username to the server
                    out.writeObject(username);
                    out.flush();
                    // Send the gpxData to the server
                    out.writeObject(gpxdata);
                    out.flush();

                    in = new ObjectInputStream(connection.getInputStream());
                    stats[0] = (ActivityStats) in.readObject();

                    // Process the received stats here

                } catch (IOException | ClassNotFoundException e) {
                    // Handle exceptions
                    e.printStackTrace();
                } finally {
                    // Close resources in the finally block
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                final ActivityStats finalStats = stats[0];
                activityStats = finalStats;

                handler.post(() ->
                {
                    // Creating a notification to inform the user that the data is ready
                    sendNotification(username);
                    updateUI(finalStats,username);
                });

            }).start();
        }
    }

    private void OpenFileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        final int REQUEST_CODE = 1;
        startActivityForResult(intent, REQUEST_CODE);
    }

    private byte[] getFileDataFromUri(Uri uri)
    {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }

    private void sendNotification(String usernameString)
    {
        // Create the notification channel if the SDK version is 26 or higher
        {
            // Create the notification channel
            CharSequence name = "channelData";
            String description = "Inform the user that the data is ready";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelData", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Menu.this, "channelData")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Data Ready")
                .setContentText("The data is ready for " + usernameString)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();

        // Get permission to post the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Menu.this);
        if (ActivityCompat.checkSelfPermission(Menu.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Post the notification
        notificationManager.notify(0, notification);
    }
}
