package com.activity_tracker.frontend;

import com.activity_tracker.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button) findViewById(R.id.login_button);

        handler = new Handler(Looper.getMainLooper());

        loginButton.setOnClickListener(v ->
        {
            new Thread(() ->
            {
                TextView usernameTextView = (TextView) findViewById(R.id.username_title);
                String username = usernameTextView.getText().toString().toLowerCase();

                Log.d(TAG, "onClick: Username: " + username);
            }).start();

            Log.d(TAG, "onClick: Login button clicked");
            // Create a new socket to connect to the backend and verify the username
            TextView usernameTextView = (TextView) findViewById(R.id.username_title);
            String username = usernameTextView.getText().toString().toLowerCase();

            Log.d(TAG, "onClick: Username: " + username);

            launchMenuActivity(username);

        });
    }

    private void launchMenuActivity(String username)
    {
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("username", username);
        // Popup message to show that the login is successful
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }


}