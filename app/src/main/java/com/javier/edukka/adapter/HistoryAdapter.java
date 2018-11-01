package com.javier.edukka.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.model.ActivityModel;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final ArrayList<ActivityModel> mArrayList;
    private final String mRole;

    public HistoryAdapter(ArrayList<ActivityModel> arrayList, String role) {
        mArrayList = arrayList;
        mRole = role;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.history_date.setText(mArrayList.get(i).getDate());
        viewHolder.history_rating.setRating(Float.parseFloat(mArrayList.get(i).getResult()));

        int id = Integer.parseInt(mArrayList.get(i).getGameId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<GameModel> call = restInterface.getGame(id);
        call.enqueue(new Callback<GameModel>() {
            @Override
            public void onResponse(@NonNull Call<GameModel> call, @NonNull Response<GameModel> response) {
                GameModel jsonResponse = response.body();
                viewHolder.history_name.setText(jsonResponse.getTitle());
            }

            @Override
            public void onFailure(@NonNull Call<GameModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

        if (mRole.equals("teacher")) {
            int id2 = Integer.parseInt(mArrayList.get(i).getStudentId());
            Call<UserModel> call2 = restInterface.getUser(id2);
            call2.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                    UserModel jsonResponse = response.body();
                    viewHolder.history_user.setText(jsonResponse.getUsername());
                }

                @Override
                public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView history_name;
        private final TextView history_date;
        private final TextView history_user;
        private final RatingBar history_rating;

        private ViewHolder(View view) {
            super(view);
            history_name = (TextView) view.findViewById(R.id.list_game);
            history_date = (TextView) view.findViewById(R.id.list_date);
            history_user = (TextView) view.findViewById(R.id.list_user);
            history_rating = (RatingBar) view.findViewById(R.id.ratingBar);
        }
    }

}
