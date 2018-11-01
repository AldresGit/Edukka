package com.javier.edukka.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.javier.edukka.R;

import java.util.Arrays;
import java.util.List;

public class SelectAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> options;
    private MyRecyclerViewAdapter adapter;
    private int cont = 0;

    public SelectAdapter(Context applicationContext, List<String> questions, List<String> options) {
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
        String str = "";
        if (adapter.getSelectedPos()>=0) {
            str = Arrays.asList(options.get(i).split(",")).get(adapter.getSelectedPos());
        }
        return str;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i != 0 || cont != questions.size()-1) {
            view = inflater.inflate(R.layout.play_recycler, viewGroup, false);
            TextView question = (TextView) view.findViewById(R.id.question);
            question.setText(questions.get(i));
            question.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorGeneral));
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
            adapter = new MyRecyclerViewAdapter(view.getContext(), Arrays.asList(options.get(i).split(",")));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
        }
        cont = i;
        return view;
    }

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private final LayoutInflater mInflater;
        private final List<String> mData;
        private int selectedPos = -1;

        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.myTextView.setText(mData.get(position));
            holder.myTextView.setBackgroundColor(selectedPos == position ? 0xFF90a4ae : 0xFFcfd8dc);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private int getSelectedPos() {
            return selectedPos;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = (TextView) itemView.findViewById(R.id.info_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(selectedPos);
                        selectedPos = getAdapterPosition();
                        notifyItemChanged(selectedPos);
                    }
                });
            }
        }
    }
}
