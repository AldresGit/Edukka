package com.javier.edukka.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.javier.edukka.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CheckboxAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> options;
    private final List<String> values;
    private int cont = 0;

    public CheckboxAdapter(Context applicationContext, List<String> questions, List<String> options) {
        this.inflater = (LayoutInflater.from(applicationContext));
        this.questions = questions;
        this.options = options;
        this.values = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int i) {
        Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        StringBuilder res = new StringBuilder();
        res.append(values.toString());
        res.deleteCharAt(res.length()-1);
        res.deleteCharAt(0);
        return res.toString();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i != 0 || cont != questions.size()-1) {
            view = inflater.inflate(R.layout.play_recycler, viewGroup, false);
            values.clear();
            TextView question = (TextView) view.findViewById(R.id.question);
            question.setText(questions.get(i));
            question.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorBioGeo));
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(viewGroup.getContext(), 2));
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(view.getContext(), Arrays.asList(options.get(i).split(",")));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        }
        cont = i;
        return view;
    }

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private final LayoutInflater mInflater;
        private final List<String> mData;

        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_multi, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.myCheckBox.setText(mData.get(position));
            holder.myCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (values.contains(holder.myCheckBox.getText().toString())) {
                        values.remove(holder.myCheckBox.getText().toString());
                    } else {
                        values.add(holder.myCheckBox.getText().toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final CheckBox myCheckBox;

            ViewHolder(View itemView) {
                super(itemView);
                myCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            }
        }
    }
}
