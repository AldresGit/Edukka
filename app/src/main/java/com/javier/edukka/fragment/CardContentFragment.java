package com.javier.edukka.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.view.HistoryActivity;
import com.javier.edukka.view.StatisticsActivity;

public class CardContentFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    private static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LENGTH = 2;
        private final String[] activity;
        private final String[] activityDesc;
        private final Drawable[] activityPictures;

        private ContentAdapter(Context context) {
            Resources resources = context.getResources();
            activity = resources.getStringArray(R.array.activity);
            activityDesc = resources.getStringArray(R.array.activity_desc);
            TypedArray a = resources.obtainTypedArray(R.array.activity_pictures);
            activityPictures = new Drawable[a.length()];
            for (int i = 0; i < activityPictures.length; i++) {
                activityPictures[i] = a.getDrawable(i);
            }
            a.recycle();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.picture.setImageDrawable(activityPictures[position % activityPictures.length]);
            holder.name.setText(activity[position % activity.length]);
            holder.description.setText(activityDesc[position % activityDesc.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final TextView description;

        private ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_card, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            name = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
                    String role = UserSingleton.getInstance().getUserModel().getRole();
                    if (getAdapterPosition()==0) {
                        Intent intent = new Intent(context, HistoryActivity.class);
                        intent.putExtra(HistoryActivity.EXTRA_POSITION, id);
                        intent.putExtra(HistoryActivity.EXTRA_EDITION, role);
                        context.startActivity(intent);
                    } else if (getAdapterPosition()==1) {
                        Intent intent = new Intent(context, StatisticsActivity.class);
                        intent.putExtra(StatisticsActivity.EXTRA_POSITION, id);
                        intent.putExtra(StatisticsActivity.EXTRA_EDITION, role);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
