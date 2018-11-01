package com.javier.edukka.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.javier.edukka.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> options;
    private MaterialBetterSpinner spinner;
    private int cont = 0;

    public SpinnerAdapter(Context applicationContext, List<String> questions, List<String> options) {
        this.inflater = (LayoutInflater.from(applicationContext));
        this.questions = questions;
        this.options = options;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int i) {
        spinner.setAdapter(null);
        return spinner.getText().toString();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i != 0 || cont != questions.size()-1) {
            view = inflater.inflate(R.layout.play_spinner, viewGroup, false);
            TextView question1 = (TextView) view.findViewById(R.id.question1);
            TextView question2 = (TextView) view.findViewById(R.id.question2);
            TextView question3 = (TextView) view.findViewById(R.id.question3);
            question1.setText(questions.get(i).split(",")[1]);
            question2.setText(questions.get(i).split(",")[0].split("_")[0]);
            question3.setText(questions.get(i).split(",")[0].split("_")[1]);

            spinner = (MaterialBetterSpinner) view.findViewById(R.id.answer);
            String[] options_list = options.get(i).split(",");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_dropdown_item_1line, options_list);
            spinner.setAdapter(arrayAdapter);
        }
        cont = i;
        return view;
    }
}
