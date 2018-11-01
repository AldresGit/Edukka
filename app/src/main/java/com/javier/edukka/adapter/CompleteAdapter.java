package com.javier.edukka.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.javier.edukka.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CompleteAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> answers;
    private EditText answer;
    private int cont = 0;

    public CompleteAdapter(Context applicationContext, List<String> questions, List<String> answers) {
        this.inflater = (LayoutInflater.from(applicationContext));
        this.questions = questions;
        this.answers = answers;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int i) {
        return answer.getText().toString();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (i != 0 || cont != questions.size()-1) {
            view = inflater.inflate(R.layout.play_complete, viewGroup, false);
            ImageView question = (ImageView) view.findViewById(R.id.image);
            Picasso.with(inflater.getContext()).load(questions.get(i).split(",")[1]).into(question);
            answer = (EditText) view.findViewById(R.id.answer);
            answer.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            answer.setText(answers.get(i).substring(0,1));
            answer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (answer.length() == 0) {
                        answer.setText(answers.get(i).substring(0,1));
                        answer.setSelection(1);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        cont = i;
        return view;
    }
}
