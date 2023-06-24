package com.activity_tracker.frontend.misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.activity_tracker.R;
import com.activity_tracker.backend.calculations.UserSegmentStatistics;

import java.util.ArrayList;
import java.util.Collection;

public class SegmentLeaderboardAdapter extends RecyclerView.Adapter<SegmentLeaderboardAdapter.ViewHolder>
{
    Context context;
    ArrayList<UserSegmentStatistics> userSegmentStatisticsList;

    public SegmentLeaderboardAdapter(Context context)
    {
        this.context = context;
        this.userSegmentStatisticsList = new ArrayList<>();
    }

    public SegmentLeaderboardAdapter(Context context, Collection<UserSegmentStatistics> userSegmentStatistics)
    {
        this.context = context;
        this.userSegmentStatisticsList = new ArrayList<>(userSegmentStatistics);
    }

    public void setUserSegmentStatisticsLeaderboard(Collection<UserSegmentStatistics> userSegmentStatistics)
    {
        this.userSegmentStatisticsList = new ArrayList<>(userSegmentStatistics);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView rankTextview;
        TextView usernameTextview;
        TextView timeTextview;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            rankTextview = itemView.findViewById(R.id.leaderboard_rank_textview);
            usernameTextview = itemView.findViewById(R.id.leaderboard_username_textview);
            timeTextview = itemView.findViewById(R.id.leaderboard_time_textview);
        }
    }

    @NonNull
    @Override
    public SegmentLeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull SegmentLeaderboardAdapter.ViewHolder holder, int position)
    {
        if (userSegmentStatisticsList == null || userSegmentStatisticsList.size() == 0)
        {
            return;
        }

        UserSegmentStatistics userSegmentStatistics = userSegmentStatisticsList.get(position);
        holder.rankTextview.setText(String.valueOf(position + 1));
        holder.usernameTextview.setText(userSegmentStatistics.getUsername());
        holder.timeTextview.setText(String.format("%.3f minutes", userSegmentStatistics.getTime()));
     }

    @Override
    public int getItemCount()
    {
        return this.userSegmentStatisticsList.size();
    }
}
