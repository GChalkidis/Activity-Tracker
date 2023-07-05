package com.activity_tracker.frontend.activities;

import com.activity_tracker.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    // Tag for logging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login_button);


        loginButton.setOnClickListener(v ->
        {
            new Thread(() ->
            {
                TextView usernameTextView = (TextView) findViewById(R.id.username_title);

            }).start();

            Log.d(TAG, "onClick: Login button clicked");
            // Create a new socket to connect to the backend and verify the username
            TextView usernameTextView = findViewById(R.id.username_title);
            String username = usernameTextView.getText().toString().toLowerCase();

            launchMenuActivity(username);

        });
    }

    private void launchMenuActivity(String username)
    {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("EXTRA_USERNAME", username);
        // Popup message to show that the login is successful
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }


}