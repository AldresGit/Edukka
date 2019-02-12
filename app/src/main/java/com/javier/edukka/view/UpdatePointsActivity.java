package com.javier.edukka.view;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.javier.edukka.R;
import com.javier.edukka.adapter.PointsAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePointsActivity extends AppCompatActivity {
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<UserModel> modelArrayList;
    private RecyclerView mRecyclerView;
    private PointsAdapter pointsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_points);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //int padding = getResources().getDimensionPixelSize(R.dimen.md_keylines);
        //int paddingTop = getResources().getDimensionPixelSize(R.dimen.md_keylines_x3);
        //mRecyclerView.setPadding(padding, paddingTop, padding, padding);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadJSON();

        refresh();
    }

    private void loadJSON() {
        int classId = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<UserModel>> request = restInterface.getUserClass(classId);
        request.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                List<UserModel> jsonResponse = response.body();
                if(jsonResponse.get(0).getId() == null) {
                    modelArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    modelArrayList = (ArrayList<UserModel>) jsonResponse;
                    findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                }

                Activity activity = UpdatePointsActivity.this;

                pointsAdapter = new PointsAdapter(modelArrayList, getApplicationContext(), activity);
                mRecyclerView.setAdapter(pointsAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

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

    protected void onRestart() {
        super.onRestart();
        loadJSON();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJSON();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}
