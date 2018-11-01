package com.javier.edukka.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private final List<String> questions;
    private final List<String> answers;
    private final List<String> results;

    public ScoreAdapter(List<String> questions, List<String> answers, List<String> results) {
        this.questions = questions;
        this.answers = answers;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.question.setText(questions.get(i).split(",")[0]);
        viewHolder.answer.setText(answers.get(i));

        if (UserSingleton.getInstance().getUserModel().getRole().equals("student")) {
            if (results.get(i).equals("true")) {
                viewHolder.result.setColorFilter(Color.GREEN);
                viewHolder.result.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            } else {
                viewHolder.result.setColorFilter(Color.RED);
                viewHolder.result.setImageResource(R.drawable.ic_thumb_down_black_24dp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView question;
        private final TextView answer;
        private final ImageView result;

        private ViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.list_question);
            answer = (TextView) view.findViewById(R.id.list_answer);
            result = (ImageView) view.findViewById(R.id.list_result);
        }
    }

}