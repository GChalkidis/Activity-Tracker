package com.activity_tracker.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.activity_tracker.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity_tracker.backend.calculations.SegmentLeaderboard;
import com.activity_tracker.frontend.misc.SegmentLeaderboardAdapter;
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

    // the recycler view that represents the leaderboard table
    private RecyclerView leaderboardRecyclerView;

    // the adapter for our recycler view
    private SegmentLeaderboardAdapter adapter;

    // all the leaderboards of the segments the user has registered
    private ArrayList<SegmentLeaderboard> segmentLeaderboardsForUser = new ArrayList<>();

    // represents the index of the leaderboard the user is currently viewing, among the segmentLeaderboardsForUser indices
    private int currentSegmentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.leaderboard);

        // Receive the username from the previous activity
        Intent intent = getIntent();
        this.username = intent.getStringExtra("EXTRA_USERNAME");
        Log.e(TAG, "Username: " + username);

        // set the leaderboard's visibility to gone until the user has at least 1 segment registered
        this.leaderboardRecyclerView = findViewById(R.id.leaderboard_table_recycler_view);
        leaderboardRecyclerView.setVisibility(View.GONE);
        TableLayout leaderboardTableLayout = findViewById(R.id.leaderboard_tablelayout);
        leaderboardTableLayout.setVisibility(View.GONE);

        handler = new Handler(Looper.getMainLooper());

        bottomNavigationView.setOnItemSelectedListener(item ->
        {
            final int id = item.getItemId();
            if (R.id.home == id)
            {
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }
            else if (R.id.profile == id)
            {
                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("EXTRA_USERNAME", username);
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

            try
            {
                connection = new Socket("192.168.1.10", 8890);
                out = new ObjectOutputStream(connection.getOutputStream());
                // Write the username to the server
                out.writeObject(username);
                out.flush();
                // Request the leaderboard data
                out.writeObject("LEADERBOARDS");
                out.flush();

                in = new ObjectInputStream(connection.getInputStream());
                out.flush();

                // Receive the leaderboard data
                Object object = in.readObject();

                // if the object is an ArrayList of SegmentLeaderboards, then update segmentLeaderboardsForUser
                if (object instanceof ArrayList)
                {
                    segmentLeaderboardsForUser = (ArrayList<SegmentLeaderboard>) object;
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
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            handler.post(this::updateUI);
        }).start();
    }

    private void updateUI()
    {
        /* if the segment leaderboards is empty (meaning the user hasn't registered a segment yet)
         * there is no point in doing anything further.         */
        if (segmentLeaderboardsForUser.isEmpty())
        {
            return;
        }

        /* if the user has registered leaderboards and this is their first time
         * viewing the leaderboard (hence the no segments text is still visible)          */
        if (findViewById(R.id.no_segments_text).getVisibility() == View.VISIBLE)
        {
            Log.e("LeaderboardActivity", "Updating UI");

            // initialising currentSegmentIndex to 0
            currentSegmentIndex = 0;

            // updated the related visibilities
            View noSegmentsText = findViewById(R.id.no_segments_text);
            noSegmentsText.setVisibility(View.GONE);

            View leaderboardLayout = findViewById(R.id.leaderboard_tablelayout);
            leaderboardLayout.setVisibility(View.VISIBLE);
            leaderboardRecyclerView.setVisibility(View.VISIBLE);

            View leftArrow = findViewById(R.id.left_arrow);
            leftArrow.setVisibility(View.VISIBLE);

            View rightArrow = findViewById(R.id.right_arrow);
            rightArrow.setVisibility(View.VISIBLE);

            TextView segmentNameTextview = findViewById(R.id.segment_name);
            segmentNameTextview.setVisibility(View.VISIBLE);
            segmentNameTextview.setText(segmentLeaderboardsForUser.get(currentSegmentIndex).getTrimmedFileName());

            // set up the recyclerview accordingly
            setUpRecyclerView();
        }

        View leftArrow = findViewById(R.id.left_arrow);
        View rightArrow = findViewById(R.id.right_arrow);
        TextView segmentNameTextview = findViewById(R.id.segment_name);

        // set up 2 OnClickListeners for the 2 arrow TextViews, to navigate through the user's segment leaderboards
        leftArrow.setOnClickListener(v ->
        {
            if (currentSegmentIndex == 0)
            {
                currentSegmentIndex = segmentLeaderboardsForUser.size() - 1;
            }
            else
            {
                currentSegmentIndex--;
            }

            adapter.setUserSegmentStatisticsList(segmentLeaderboardsForUser.get(currentSegmentIndex).getLeaderboard());
            adapter.notifyDataSetChanged();
            segmentNameTextview.setText(segmentLeaderboardsForUser.get(currentSegmentIndex).getTrimmedFileName());
        });

        rightArrow.setOnClickListener(v ->
        {
            if (currentSegmentIndex == segmentLeaderboardsForUser.size() - 1)
            {
                currentSegmentIndex = 0;
            }
            else
            {
                currentSegmentIndex++;
            }
            adapter.setUserSegmentStatisticsList(segmentLeaderboardsForUser.get(currentSegmentIndex).getLeaderboard());
            adapter.notifyDataSetChanged();
            segmentNameTextview.setText(segmentLeaderboardsForUser.get(currentSegmentIndex).getTrimmedFileName());
        });

    }

    private void setUpRecyclerView()
    {
        leaderboardRecyclerView.setHasFixedSize(false);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SegmentLeaderboardAdapter(this, segmentLeaderboardsForUser.get(currentSegmentIndex).getLeaderboard());
        leaderboardRecyclerView.setAdapter(adapter);
    }
}