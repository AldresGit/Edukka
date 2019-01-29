package com.javier.edukka.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomContentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContentAdapter mAdapter;
    private ArrayList<MultiplayerGameModel> mArrayList;
    private static Integer size;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        int padding = getResources().getDimensionPixelSize(R.dimen.md_keylines);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        loadJSON();
        return recyclerView;
    }

    public static Integer getSize() {
        return size;
    }

    public static void setSize(int i) {
        size = i;
    }

    private void loadJSON() {
        RestInterface restInterface = RetrofitClient.getInstance();
        int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        Call<List<MultiplayerGameModel>> request = restInterface.searchRoom(id);
        request.enqueue(new Callback<List<MultiplayerGameModel>>() {
            @Override
            public void onResponse(Call<List<MultiplayerGameModel>> call, Response<List<MultiplayerGameModel>> response) {
                List<MultiplayerGameModel> jsonResponse = response.body();
                mArrayList = (ArrayList<MultiplayerGameModel>) jsonResponse;
                mAdapter = new ContentAdapter(mArrayList);
                recyclerView.setAdapter(mAdapter);
                size = mArrayList.size();
            }

            @Override
            public void onFailure(Call<List<MultiplayerGameModel>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final ArrayList<MultiplayerGameModel> mArrayList;

        private ContentAdapter (ArrayList arrayList) {
            mArrayList = arrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {
            int resourceId = getResources().getIdentifier(mArrayList.get(i).getData().split(";")[1], "drawable", getContext().getPackageName());
            holder.image.setImageResource(resourceId);
            String text = getResources().getString(R.string.game_room) + mArrayList.get(i).getData().split(";")[0];
            holder.name.setText(text);
            if(mArrayList.get(i).getExtra().equals("0")) {
                holder.extraPoints.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView name;
        private final ImageView extraPoints;

        private ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_room, parent, false));
            image = (ImageView) itemView.findViewById(R.id.user_image);
            name = (TextView) itemView.findViewById(R.id.user_name);
            extraPoints = (ImageView) itemView.findViewById(R.id.extra_points);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    //PA LUEGO


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
