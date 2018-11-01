package com.javier.edukka.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> options;
    private MyRecyclerViewAdapter adapter;
    private int cont = 0;

    public ImageAdapter(Context applicationContext, List<String> questions, List<String> options) {
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
            str = Arrays.asList(options.get(i).split(";")).get(adapter.getSelectedPos()).split(",")[0];
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
            question.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.colorSports));
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(viewGroup.getContext(), 2));
            adapter = new MyRecyclerViewAdapter(view.getContext(), Arrays.asList(options.get(i).split(";")));
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
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_picture, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyRecyclerViewAdapter.ViewHolder holder, int position) {
            Picasso.with(inflater.getContext()).load(mData.get(position).split(",")[1]).into(holder.myImageView);
            holder.myImageView.setBackgroundColor(selectedPos == position ? 0xFFFFA000: 0xFFFFC107);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private int getSelectedPos() {
            return selectedPos;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView myImageView;

            ViewHolder(View itemView) {
                super(itemView);
                myImageView = (ImageView) itemView.findViewById(R.id.image);
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
