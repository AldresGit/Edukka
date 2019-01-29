package com.javier.edukka.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.javier.edukka.R;
import com.javier.edukka.adapter.RoomAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRoomActivity extends AppCompatActivity {
    public static final String NAME_PLAYER1 = "name1";
    public static final String IMAGE_PLAYER1 = "image1";
    public static final String ID_ROOM = "room_id";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<MultiplayerGameModel> modelArrayList;
    private RecyclerView mRecyclerView;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        int padding = getResources().getDimensionPixelSize(R.dimen.md_keylines);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.md_keylines_x3);
        mRecyclerView.setPadding(padding, paddingTop, padding, padding);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadJSON();

        refresh();
    }

    protected void onRestart() {
        super.onRestart();
        loadJSON();
    }

    private void loadJSON() {
        int classId = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<MultiplayerGameModel>> request = restInterface.searchRoom(classId);
        request.enqueue(new Callback<List<MultiplayerGameModel>>() {
            @Override
            public void onResponse(Call<List<MultiplayerGameModel>> call, Response<List<MultiplayerGameModel>> response) {

                List<MultiplayerGameModel> jsonResponse = response.body();
                if(jsonResponse.get(0).getId() == null) {
                    modelArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    modelArrayList = (ArrayList<MultiplayerGameModel>) jsonResponse;
                    findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                }

                roomAdapter = new RoomAdapter(modelArrayList, getApplicationContext());
                mRecyclerView.setAdapter(roomAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<MultiplayerGameModel>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void refresh() {
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadJSON();
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
