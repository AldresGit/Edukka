package com.javier.edukka.view;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.javier.edukka.R;
import com.javier.edukka.adapter.ScoreAdapter;
import com.javier.edukka.controller.GameSingleton;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameLookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> questions;
    private List<String> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_look);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(GameSingleton.getInstance().getGameModel().getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        questions = new ArrayList<>();
        answers = new ArrayList<>();
        loadJSON();
    }

    private void loadJSON(){
        int id = Integer.parseInt(GameSingleton.getInstance().getGameModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<QuizModel>> call = restInterface.getGameQuiz(id);
        call.enqueue(new Callback<List<QuizModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<QuizModel>> call, @NonNull Response<List<QuizModel>> response) {
                List<QuizModel> jsonResponse = response.body();
                for (QuizModel quizModel : jsonResponse) {
                    questions.add(quizModel.getQuestion());
                    answers.add(quizModel.getAnswer());
                }
                ScoreAdapter scoreAdapter = new ScoreAdapter(questions,answers,new ArrayList<String>());
                recyclerView.setAdapter(scoreAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<QuizModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
