package com.javier.edukka.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.app.Config;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.view.CreateRoomActivity;
import com.javier.edukka.view.SearchRoomActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
                    final Context context = v.getContext();
                    int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
                    String role = UserSingleton.getInstance().getUserModel().getRole();
                    if(getAdapterPosition() == 0) {
                        if(role.equals("student")){
                            UserModel user = UserSingleton.getInstance().getUserModel();
                            String data = user.getUsername() + ";" + user.getImage();

                            SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
                            String regId = pref.getString("regId", null);

                            RestInterface restInterface = RetrofitClient.getInstance();
                            Call<MultiplayerGameModel> request = restInterface.createRoom(Integer.parseInt(user.getId()), regId, 0, "",
                                    "","waiting", Integer.parseInt(user.getClassId()), 0, data);
                            request.enqueue(new Callback<MultiplayerGameModel>() {
                                @Override
                                public void onResponse(Call<MultiplayerGameModel> call, Response<MultiplayerGameModel> response) {
                                    Toast.makeText(context, "Creado con exito", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(context, CreateRoomActivity.class);
                                    i.putExtra("position", Integer.parseInt(response.body().getId()));
                                    i.putExtra("role", "host");
                                    context.startActivity(i);
                                }

                                @Override
                                public void onFailure(Call<MultiplayerGameModel> call, Throwable t) {
                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {

                        }
                    } else if(getAdapterPosition() == 1) {
                        if(role.equals("student")) {
                            Intent i = new Intent(context, SearchRoomActivity.class);
                            context.startActivity(i);
                        } else {

                        }
                    }
                }
            });
        }
    }

}
