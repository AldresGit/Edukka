package com.javier.edukka.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.view.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListContentFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;
    private ArrayList<UserModel> mArrayList;
    private static Integer size;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        loadJSON();
        return mRecyclerView;
    }

    public static Integer getSize() {
        return size;
    }

    public static void setSize(int i) {
        size = i;
    }

    private void loadJSON(){
        RestInterface restInterface = RetrofitClient.getInstance();
        //Call<List<UserModel>> call = restInterface.getAllUsers();
        Integer id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        Call<List<UserModel>> call = restInterface.getUserClass(id);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                List<UserModel> jsonResponse = response.body();
                mArrayList = (ArrayList<UserModel>) jsonResponse;
                mAdapter = new ContentAdapter(mArrayList);
                mRecyclerView.setAdapter(mAdapter);
                size = mArrayList.size();
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final ArrayList<UserModel> mArrayList;

        private ContentAdapter(ArrayList<UserModel> arrayList) {
            mArrayList = arrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            int resourceId = getResources().getIdentifier(mArrayList.get(i).getImage(), "drawable", getContext().getPackageName());
            holder.avatar.setImageResource(resourceId);
            holder.name.setText(mArrayList.get(i).getUsername());
            holder.description.setText(mArrayList.get(i).getName());
            if (mArrayList.get(i).getRole().equals("student") && UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
                holder.score.setText(mArrayList.get(i).getScore());
            }

            switch (i) {
                case 0:
                    holder.medal.setImageDrawable(getResources().getDrawable(R.drawable.medal_first));
                    holder.medal.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    holder.medal.setImageDrawable(getResources().getDrawable(R.drawable.medal_second));
                    holder.medal.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    holder.medal.setImageDrawable(getResources().getDrawable(R.drawable.medal_third));
                    holder.medal.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final ImageView medal;
        private final TextView name;
        private final TextView description;
        private final TextView score;

        private ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            avatar = (ImageView) itemView.findViewById(R.id.list_avatar);
            medal = (ImageView) itemView.findViewById(R.id.list_medal);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            score = (TextView) itemView.findViewById(R.id.list_score);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(mArrayList.get(getAdapterPosition()).getId());
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.EXTRA_POSITION, id);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJSON();
    }
}
