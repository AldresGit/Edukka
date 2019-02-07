package com.javier.edukka.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.adapter.RoomAdapter;
import com.javier.edukka.app.Config;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRoomActivity extends AppCompatActivity {
    public static final String NAME_PLAYER1 = "name1";
    public static final String IMAGE_PLAYER1 = "image1";
    public static final String RIVAL_ID = "firebase_id";
    public static final String ID_ROOM = "position";
    public static final String ROLE = "role";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<MultiplayerGameModel> modelArrayList;
    private RecyclerView mRecyclerView;
    private RoomAdapter roomAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        int padding = getResources().getDimensionPixelSize(R.dimen.md_keylines);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.md_keylines_x3);
        mRecyclerView.setPadding(padding, paddingTop, padding, padding);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadJSON();

        refresh();

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    switch (message.split(";")[0]) {
                        case "come" :
                            String myUser = UserSingleton.getInstance().getUserModel().getId();

                            SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
                            String myregId = pref.getString("regId", null);

                            final int roomId = Integer.parseInt(message.split(";")[1]);
                            final String rivalId = message.split(";")[2];
                            RestInterface restInterface = RetrofitClient.getInstance();
                            Call<MultiplayerGameModel> request = restInterface.joinRoom(Integer.parseInt(myUser), myregId, "connected", roomId);
                            request.enqueue(new Callback<MultiplayerGameModel>() {
                                @Override
                                public void onResponse(Call<MultiplayerGameModel> call, Response<MultiplayerGameModel> response) {
                                    MultiplayerGameModel model = response.body();
                                    if (model.getId()==null) { //AQUI SE RECIBE NULL
                                        Toast.makeText(getApplicationContext(), R.string.time_limit_error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent i = new Intent(getApplicationContext(), CreateRoomActivity.class);
                                        i.putExtra(SearchRoomActivity.ID_ROOM, roomId);
                                        i.putExtra(SearchRoomActivity.NAME_PLAYER1, model.getData().split(";")[0]);
                                        i.putExtra(SearchRoomActivity.IMAGE_PLAYER1, model.getData().split(";")[1]);
                                        i.putExtra(SearchRoomActivity.RIVAL_ID, rivalId);
                                        i.putExtra(SearchRoomActivity.ROLE, "guest");
                                        startActivity(i);
                                    }
                                }

                                @Override
                                public void onFailure(Call<MultiplayerGameModel> call, Throwable t) {
                                    Log.d("Error",t.getMessage());
                                }
                            });
                            break;
                        case "full" :
                            Toast.makeText(getApplicationContext(), R.string.time_limit_error, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };
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

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }
}
