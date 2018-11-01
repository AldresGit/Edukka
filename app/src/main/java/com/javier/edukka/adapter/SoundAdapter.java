package com.javier.edukka.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SoundAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> options;
    private MyRecyclerViewAdapter adapter;
    private int cont = 0;

    public SoundAdapter(Context applicationContext, List<String> questions, List<String> options) {
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
            view = inflater.inflate(R.layout.play_sound, viewGroup, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(viewGroup.getContext(), 2));
            adapter = new MyRecyclerViewAdapter(view.getContext(), Arrays.asList(options.get(i).split(",")));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            String url = "http://docs.google.com/uc?export=download&id="+questions.get(i).split(",")[1];
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(view.getContext(), R.string.sound, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            ImageView play = (ImageView) view.findViewById(R.id.btnPlay);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.start();
                    Toast.makeText(view.getContext(), R.string.sound, Toast.LENGTH_SHORT).show();
                }
            });
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
            holder.myTextView.setBackgroundColor(selectedPos == position ? 0xFF4caf50 : 0xFF8bc34a);
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

