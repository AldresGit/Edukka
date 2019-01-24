package com.javier.edukka.fragment;


import android.content.Context;
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


public class ButtonContentFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        ContentAdapter contentAdapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    private static class ContentAdapter extends RecyclerView.Adapter<ViewHolder>{
        private static final int LENGTH = 2;
        private final String[] multiplayer;
        private final String[] multiplayerDesc;
        private final Drawable[] multiplayerPictures;

        private ContentAdapter(Context context) {
            Resources resources = context.getResources();
            if(UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
                multiplayer = resources.getStringArray(R.array.multiplayer_teacher);
                multiplayerDesc = resources.getStringArray(R.array.multiplayer_teacher_desc);
                TypedArray a = resources.obtainTypedArray(R.array.multiplayer_pictures_teacher);
                multiplayerPictures = new Drawable[a.length()];
                for (int i = 0; i < multiplayerPictures.length; i++) {
                    multiplayerPictures[i] = a.getDrawable(i);
                }
                a.recycle();
            } else {
                multiplayer = resources.getStringArray(R.array.multiplayer_student);
                multiplayerDesc = resources.getStringArray(R.array.multiplayer_student_desc);
                TypedArray a = resources.obtainTypedArray(R.array.multiplayer_pictures_student);
                multiplayerPictures = new Drawable[a.length()];
                for (int i = 0; i < multiplayerPictures.length; i++) {
                    multiplayerPictures[i] = a.getDrawable(i);
                }
                a.recycle();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.picture.setImageDrawable(multiplayerPictures[position % multiplayerPictures.length]);
            holder.name.setText(multiplayer[position % multiplayer.length]);
            holder.description.setText(multiplayerDesc[position % multiplayerDesc.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView picture;
        private final TextView name;
        private final TextView description;

        private ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_button, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.button_image);
            name = (TextView) itemView.findViewById(R.id.button_title);
            description = (TextView) itemView.findViewById(R.id.button_text);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Rellenar
                }
            });
        }
    }

}
