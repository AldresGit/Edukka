package com.javier.edukka.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiScoreAdapter extends RecyclerView.Adapter<MultiScoreAdapter.ViewHolder>{
    private final List<String> players;
    private final List<String> images;
    private final List<Integer> score;
    Context context;

    public MultiScoreAdapter(List<String> players, List<String> images, int score1, int score2, Context context) {
        this.players = players;
        this.images = images;
        score = new ArrayList<>();
        score.add(score1);
        score.add(score2);
        if(score1 < score2) {
            Collections.reverse(this.players);
            Collections.reverse(this.images);
            Collections.reverse(this.score);
        }
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multi_result_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        int resourceId = context.getResources().getIdentifier(images.get(i), "drawable", context.getPackageName());
        viewHolder.avatar.setImageResource(resourceId);
        viewHolder.name.setText(players.get(i));
        viewHolder.score.setText(String.valueOf(score.get(i)));
        if(score.get(i) > 0) {
            viewHolder.score.setTextColor(context.getResources().getColor(R.color.colorMusic));
        } else if(score.get(i) < 0) {
            viewHolder.score.setTextColor(context.getResources().getColor(R.color.colorMaths));
        }
        switch(i) {
            case 0 :
                viewHolder.medal.setImageResource(R.drawable.medal_first);
                break;
            case 1 :
                viewHolder.medal.setImageResource(R.drawable.medal_second);
                break;
            case 2 :
                viewHolder.medal.setImageResource(R.drawable.medal_third);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView name;
        private final TextView score;
        private final ImageView medal;

        private ViewHolder(View view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.name);
            score = (TextView) view.findViewById(R.id.score);
            medal = (ImageView) view.findViewById(R.id.medal);
        }
    }
}
