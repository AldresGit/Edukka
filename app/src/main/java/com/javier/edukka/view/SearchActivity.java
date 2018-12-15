package com.javier.edukka.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.javier.edukka.R;
import com.javier.edukka.adapter.GameAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.HelperClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    public static final String SUBJECT_NAME = "subject";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ArrayList<GameModel> mArrayList;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private GameAdapter mAdapter;

    private int classId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        getSupportActionBar().setTitle(getIntent().getStringExtra(SUBJECT_NAME));

        loadJSON();

        //--------------Lineas nuevas-----------------
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(classId == 1) {
                    infoDialog1();
                } else {
                    Intent intent = new Intent(SearchActivity.this, GameCreateActivity.class);
                    intent.putExtra(SUBJECT_NAME, getIntent().getStringExtra(SUBJECT_NAME));
                    intent.putExtra("id", 0);
                    startActivity(intent);
                }
            }
        });
        //--------------Lineas nuevas-----------------

        refresh();

    }

    protected void onRestart() {
        super.onRestart();
        loadJSON();
    }

    private void loadJSON(){
        String search = getIntent().getStringExtra(SUBJECT_NAME);
        if (Locale.getDefault().getLanguage().equals("es")) {
            search = HelperClient.subjectTranslateEn(search);
        }


        if(UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
            classId = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        }


        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<GameModel>> call = restInterface.getSubjectGames(search);
        call.enqueue(new Callback<List<GameModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<GameModel>> call, @NonNull Response<List<GameModel>> response) {
                List<GameModel> jsonResponse = response.body();
                if (jsonResponse.get(0).getId()==null) {
                    mArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    ArrayList<GameModel> list = new ArrayList<>();
                    for (GameModel game : jsonResponse) {
                        if (game.getLocale().equals(Locale.getDefault().getLanguage()) && game.getClassId().equals(UserSingleton.getInstance().getUserModel().getClassId())) {
                            list.add(game);                                                 //Modificar para solo agregar con el id de la clase
                        }
                    }
                    mArrayList = list;
                    if (mArrayList.isEmpty()) {
                        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                        findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                    }
                }
                mAdapter = new GameAdapter(mArrayList);
                mRecyclerView.setAdapter(mAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<GameModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });


    }

    public void infoDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.action_not_available);
        builder.setMessage(R.string.must_create_class);
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            return super.onOptionsItemSelected(item);
        } else {
            finish();
            return true;
        }
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
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
}
