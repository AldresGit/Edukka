package com.javier.edukka.view;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.javier.edukka.R;
import com.javier.edukka.adapter.HistoryAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.ActivityModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_EDITION = "edition";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<ActivityModel> mArrayList;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.history);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refresh();
        if (getIntent().getStringExtra(EXTRA_EDITION).equals("student")) {
            loadJSON1();
        } else {
            loadJSON2();
        }
    }

    private void loadJSON1(){
        int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        //int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<ActivityModel>> call = restInterface.getUserActivity(id);
        call.enqueue(new Callback<List<ActivityModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityModel>> call, @NonNull Response<List<ActivityModel>> response) {
                List<ActivityModel> jsonResponse = response.body();
                if (jsonResponse.get(0).getStudentId()==null) {
                    mArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    mArrayList = (ArrayList<ActivityModel>) jsonResponse;
                    findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                }
                mAdapter = new HistoryAdapter(mArrayList,getIntent().getStringExtra(EXTRA_EDITION));
                mRecyclerView.setAdapter(mAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void loadJSON2(){
        //int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<ActivityModel>> call = restInterface.getClassActivity(id);
        call.enqueue(new Callback<List<ActivityModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityModel>> call, @NonNull Response<List<ActivityModel>> response) {
                List<ActivityModel> jsonResponse = response.body();
                if (jsonResponse.get(0).getStudentId()==null) {
                    mArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    mArrayList = (ArrayList<ActivityModel>) jsonResponse;
                    findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                }
                mAdapter = new HistoryAdapter(mArrayList,getIntent().getStringExtra(EXTRA_EDITION));
                mRecyclerView.setAdapter(mAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void refresh() {
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getIntent().getStringExtra(EXTRA_EDITION).equals("student")) {
                            loadJSON1();
                        } else {
                            loadJSON2();
                        }
                    }
                }
        );
    }

}
