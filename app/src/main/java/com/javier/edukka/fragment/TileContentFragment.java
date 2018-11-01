package com.javier.edukka.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.view.SearchActivity;

public class TileContentFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        int tilePadding = getResources().getDimensionPixelSize(R.dimen.md_keylines);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        return recyclerView;
    }

    private static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LENGTH = 10;
        private final String[] subjects;
        private final Drawable[] subjectPictures;

        private ContentAdapter(Context context) {
            Resources resources = context.getResources();
            subjects = resources.getStringArray(R.array.subjects);
            TypedArray a = resources.obtainTypedArray(R.array.subject_pictures);
            subjectPictures = new Drawable[a.length()];
            for (int i = 0; i < subjectPictures.length; i++) {
                subjectPictures[i] = a.getDrawable(i);
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
            holder.picture.setImageDrawable(subjectPictures[position % subjectPictures.length]);
            holder.name.setText(subjects[position % subjects.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;

        private ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_tile, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.tile_picture);
            name = (TextView) itemView.findViewById(R.id.tile_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra(SearchActivity.SUBJECT_NAME, name.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}

