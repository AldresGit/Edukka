package com.javier.edukka.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.adapter.GeneralAdapter;
import com.javier.edukka.adapter.MultiScoreAdapter;
import com.javier.edukka.app.Config;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiPlayActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "position";
    public static final String QUIZZES = "quizzes";
    public static final String RIVAL_ID = "rival_id";
    public static final String EXTRA_POINTS = "extra";
    private AdapterViewFlipper flipper;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView progressText1, progressText2;
    private ImageView avatar1, avatar2;
    private FloatingActionButton fab;
    private List<String> questions;
    private List<String> types;
    private List<String> options;
    private List<String> answers;
    private List<String> players;
    private List<String> images;

    private int step = 0;
    private int score1 = 0;
    private int score2 = 0;
    int correctAnswer = 10;
    int wrongAnswer = -5;

    private String rivalId = "";
    private boolean end = false;
    private BaseAdapter baseAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_play);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.quiz_duel);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        avatar1 = (ImageView) findViewById(R.id.avatar1);
        avatar2 = (ImageView) findViewById(R.id.avatar2);

        progressText1 = (TextView) findViewById(R.id.progress_text1);
        progressText2 = (TextView) findViewById(R.id.progress_text2);

        questions = new ArrayList<>();
        options = new ArrayList<>();
        answers = new ArrayList<>();
        types = new ArrayList<>();
        players = new ArrayList<>();
        images = new ArrayList<>();

        loadJSON();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    switch (message.split(";")[0]) {
                        case "data" :
                            String image = message.split(";")[1];
                            images.add(image);
                            players.add(message.split(";")[2]);
                            int resourceId = getResources().getIdentifier(image, "drawable", getPackageName());
                            avatar2.setImageDrawable(getResources().getDrawable(resourceId));
                            break;
                        case "scoredown" :
                            score2 = Integer.parseInt(message.split(";")[1]);
                            updateScores(score1, score2);
                            break;
                        case "next" :
                            score2 = Integer.parseInt(message.split(";")[1]);
                            updateScores(score1, score2);
                            nextQuestion();
                            break;
                    }
                }
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answers.get(step).equals(baseAdapter.getItem(step).toString())) {
                    //Pregunta fallada
                    score1 += wrongAnswer;
                    updateScores(score1, score2);
                    RestInterface restInterface = RetrofitClient.getInstance();
                    Call<Void> messageRequest = restInterface.sendMessage(rivalId, "scoredown;" + String.valueOf(score1));
                    messageRequest.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(MultiPlayActivity.this, getResources().getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                } else {
                    //Pregunta acertada
                    score1 += correctAnswer;
                    updateScores(score1, score2);
                    RestInterface restInterface = RetrofitClient.getInstance();
                    Call<Void> messageRequest = restInterface.sendMessage(rivalId, "next;" + String.valueOf(score1));
                    messageRequest.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            nextQuestion();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { }
                    });

                }
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Animation up = AnimationUtils.loadAnimation(MultiPlayActivity.this, R.anim.scale_up);
                Animation down = AnimationUtils.loadAnimation(MultiPlayActivity.this, R.anim.scale_down);
                if (!end) {
                    if (scrollY == 0) {
                        fab.startAnimation(up);
                        fab.show();
                    } else {
                        fab.startAnimation(down);
                        fab.hide();
                    }
                }
            }
        });
    }

    private void loadJSON() {
        progressBar.setProgress(0);
        int resourceId = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
        avatar1.setImageDrawable(getResources().getDrawable(resourceId));

        rivalId = getIntent().getStringExtra(RIVAL_ID);
        players.add(UserSingleton.getInstance().getUserModel().getName());
        images.add(UserSingleton.getInstance().getUserModel().getImage());

        loadGame();
    }

    private void loadGame() {
        boolean extra = getIntent().getBooleanExtra(EXTRA_POINTS, false);
        if(extra) {
            correctAnswer = 20;
            wrongAnswer = -10;
        }

        String quizzes = getIntent().getStringExtra(QUIZZES);
        int id1 = Integer.parseInt(quizzes.split(",")[0]);
        int id2 = Integer.parseInt(quizzes.split(",")[1]);
        int id3 = Integer.parseInt(quizzes.split(",")[2]);
        int id4 = Integer.parseInt(quizzes.split(",")[3]);
        int id5 = Integer.parseInt(quizzes.split(",")[4]);

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> messageRequest = restInterface.sendMessage(rivalId, "data;" + UserSingleton.getInstance().getUserModel().getImage()
                + ";" + UserSingleton.getInstance().getUserModel().getName());
        Call<List<QuizModel>> quizzesRequest = restInterface.getQuizzesById(id1, id2, id3, id4, id5);
        messageRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

        quizzesRequest.enqueue(new Callback<List<QuizModel>>() {
            @Override
            public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                List<QuizModel> jsonResponse = response.body();
                for (QuizModel quizModel : jsonResponse) {
                    questions.add(quizModel.getQuestion());
                    answers.add(quizModel.getAnswer());
                    options.add(quizModel.getOptions());
                    types.add(quizModel.getType());
                }

                flipper = (AdapterViewFlipper) findViewById(R.id.adapter_view);

                baseAdapter = new GeneralAdapter(getApplication(), questions, options, answers, types);
                flipper.setAdapter(baseAdapter);

            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void updateScores(int score1, int score2) {

        progressText1.setText(String.valueOf(score1));
        progressText2.setText(String.valueOf(score2));

        if(score1 == score2) {
            progressText1.setTextColor(Color.WHITE);
            progressText2.setTextColor(Color.WHITE);
        } else if(score1 > score2) {
            progressText1.setTextColor(getResources().getColor(R.color.colorMusic));
            progressText2.setTextColor(getResources().getColor(R.color.colorMaths));
        } else {
            progressText1.setTextColor(getResources().getColor(R.color.colorMaths));
            progressText2.setTextColor(getResources().getColor(R.color.colorMusic));
        }
    }

    private void nextQuestion() {
        step++;

        progressBar.setProgress(progressBar.getProgress() + 100/baseAdapter.getCount());
        if (step == baseAdapter.getCount()) {
            end = true;
        }

        InputMethodManager inputManager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isAcceptingText()) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        flipper.showNext();

        if(end) {
            flipper.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);


            MultiScoreAdapter multiScoreAdapter = new MultiScoreAdapter(players, images, score1, score2, this.getApplicationContext());
            recyclerView.setAdapter(multiScoreAdapter);

            finishGame();
        }
    }

    private void finishGame() {
        if(score1 == score2) {
            getSupportActionBar().setTitle(R.string.draw);
        } else if(score1 < score2) {
            getSupportActionBar().setTitle(R.string.defeat);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorMaths));
        } else {
            getSupportActionBar().setTitle(R.string.victory);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorMusic));
        }

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<UserModel> scoreRequest = restInterface.updateUserScore(score1, Integer.parseInt(UserSingleton.getInstance().getUserModel().getId()));
        scoreRequest.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                UserModel jsonResponse = response.body();
                UserSingleton.getInstance().setUserModel(jsonResponse);
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {

            }
        });

        //----------------Manejar el resto de acciones---------------------

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
