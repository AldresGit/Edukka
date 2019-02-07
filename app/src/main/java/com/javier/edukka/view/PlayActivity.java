package com.javier.edukka.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.adapter.CheckboxAdapter;
import com.javier.edukka.adapter.CompleteAdapter;
import com.javier.edukka.adapter.DragDropAdapter;
import com.javier.edukka.adapter.DragImageAdapter;
import com.javier.edukka.adapter.DragNameAdapter;
import com.javier.edukka.adapter.GeneralAdapter;
import com.javier.edukka.adapter.ImageAdapter;
import com.javier.edukka.adapter.PickerAdapter;
import com.javier.edukka.adapter.ScoreAdapter;
import com.javier.edukka.adapter.SelectAdapter;
import com.javier.edukka.adapter.SoundAdapter;
import com.javier.edukka.adapter.SpinnerAdapter;
import com.javier.edukka.controller.GameSingleton;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.ActivityModel;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayActivity extends AppCompatActivity {

    private AdapterViewFlipper flipper;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView progressText;
    private ImageView avatarView;
    private FloatingActionButton fab, back;
    private List<String> questions;
    private List<String> types;
    private List<String> options;
    private List<String> answers;
    private List<String> values;
    private List<String> results;
    private int step = 0;
    private int correct = 0;
    private boolean end = false;
    private BaseAdapter baseAdapter;

    private RingProgressBar ringProgressBar;
    private int progress = 0;
    private int progressMax = 100;

    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                if(progress < progressMax) {
                    progress++;
                    ringProgressBar.setProgress(progress);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(GameSingleton.getInstance().getGameModel().getTitle());
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        avatarView = (ImageView) findViewById(R.id.avatar);
        progressText = (TextView) findViewById(R.id.progress_text);

        //timerCount = (TextView) findViewById(R.id.timer_count);

        ringProgressBar = (RingProgressBar) findViewById(R.id.ring_progress_bar);

        questions = new ArrayList<>();
        options = new ArrayList<>();
        answers = new ArrayList<>();
        types = new ArrayList<>();
        results = new ArrayList<>();
        values = new ArrayList<>();
        loadJSON();


        ringProgressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {

                if(end) {

                } else {
                    for(int i = step; i < baseAdapter.getCount(); i++) {
                        results.add("false");
                        values.add("TIMEOUT!!");
                        step++;
                    }

                    end = true;

                    flipper.setVisibility(View.INVISIBLE); //Linea Mágica


                    RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                    ratingBar.setRating((correct*5.0f) / questions.size());
                    ratingBar.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.INVISIBLE);
                    ringProgressBar.setVisibility(View.INVISIBLE);

                    ScoreAdapter scoreAdapter = new ScoreAdapter(questions, values, results);
                    recyclerView.setAdapter(scoreAdapter);

                    finishGame();


                    Toast.makeText(PlayActivity.this, "TIMEOUT!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < progressMax; i++) {
                    try {
                        Thread.sleep(1000);
                        myHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answers.get(step).equals(baseAdapter.getItem(step).toString())) {
                    results.add("true");
                    correct++;
                } else {
                    results.add("false");
                }

                values.add(baseAdapter.getItem(step).toString());
                step++;
                progressBar.setProgress(progressBar.getProgress() + 100/baseAdapter.getCount());
                progressText.setText((step+1) + "/" + baseAdapter.getCount());
                if (step == baseAdapter.getCount()) {
                    end = true;
                    progress = progressMax;
                }

                InputMethodManager inputManager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager.isAcceptingText()) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }


                flipper.showNext();

                //back.setVisibility(View.VISIBLE);
                if (end) {
                    /*
                    if(timeRunning) {
                        stopTimer();
                    }
                    */


                    flipper.setVisibility(View.INVISIBLE); //Linea Mágica


                    RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                    ratingBar.setRating((correct*5.0f) / questions.size());
                    ratingBar.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.INVISIBLE);



                    ringProgressBar.setVisibility(View.INVISIBLE);

                    ScoreAdapter scoreAdapter = new ScoreAdapter(questions, values, results);
                    recyclerView.setAdapter(scoreAdapter);

                    finishGame();
                }
            }
        });

        back = (FloatingActionButton) findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (results.get(results.size()-1).equals("true")) {
                    correct--;
                }
                results.remove(results.size()-1);
                values.remove(values.size()-1);
                step--;
                progressBar.setProgress(progressBar.getProgress() - 100/baseAdapter.getCount());
                progressText.setText((step+1) + "/" + baseAdapter.getCount());

                flipper.showPrevious();
                if (step==0) {
                    back.setVisibility(View.INVISIBLE);
                }
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Animation up = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.scale_up);
                Animation down = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.scale_down);
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
                    options.add(quizModel.getOptions());
                    types.add(quizModel.getType());
                }
                progressBar.setProgress(0);
                progressText.setText(1 + "/" + questions.size());
                int resourceId = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
                avatarView.setImageDrawable(getResources().getDrawable(resourceId));




                ringProgressBar.setMax(Integer.parseInt(GameSingleton.getInstance().getGameModel().getTime()));
                progressMax = Integer.parseInt(GameSingleton.getInstance().getGameModel().getTime());




                flipper = (AdapterViewFlipper) findViewById(R.id.adapter_view);

                //-------------Lineas nuevas---------------------

                baseAdapter = new GeneralAdapter(getApplication(), questions, options, answers, types);
                flipper.setAdapter(baseAdapter);

                //-------------Lineas nuevas---------------------

                /*
                switch (GameSingleton.getInstance().getGameModel().getSubject()) {
                    case "Spanish Language":
                        baseAdapter = new DragDropAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Mathematics":
                        baseAdapter = new PickerAdapter(getApplication(), questions, answers);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Natural Sciences":
                        baseAdapter = new DragNameAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Social Sciences":
                        baseAdapter = new DragImageAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Biology & Geology":
                        baseAdapter = new CheckboxAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Geography & History":
                        baseAdapter = new CompleteAdapter(getApplication(), questions, answers);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Music":
                        baseAdapter = new SoundAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "Sports":
                        baseAdapter = new ImageAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "English":
                        baseAdapter = new SpinnerAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                    case "General Knowledge":
                        baseAdapter = new SelectAdapter(getApplication(), questions, options);
                        flipper.setAdapter(baseAdapter);
                        break;
                }

                */
            }

            @Override
            public void onFailure(@NonNull Call<List<QuizModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void finishGame(){
        int student = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
        int game = Integer.parseInt(GameSingleton.getInstance().getGameModel().getId());
        float note = (correct*5.0f) / questions.size();
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<ActivityModel> call = restInterface.finishGame(student, game, GameSingleton.getInstance().getGameModel().getSubject(), note);
        call.enqueue(new Callback<ActivityModel>() {
            @Override
            public void onResponse(@NonNull Call<ActivityModel> call, @NonNull Response<ActivityModel> response) {
                //ActivityModel jsonResponse = response.body();
                Toast.makeText(PlayActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ActivityModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

        if (note >= 2.5f) {
            int score = Integer.parseInt(UserSingleton.getInstance().getUserModel().getScore());
            switch (GameSingleton.getInstance().getGameModel().getDifficulty()) {
                case "easy":
                    score += 10;
                    progressText.setText("+ 10");
                    break;
                case "medium":
                    score += 20;
                    progressText.setText("+ 20");
                    break;
                case "hard":
                    score += 30;
                    progressText.setText("+ 30");
                    break;
            }

            Call<UserModel> call2 = restInterface.updateUserScore(score, student);
            call2.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                    UserModel jsonResponse = response.body();
                    UserSingleton.getInstance().setUserModel(jsonResponse);
                }

                @Override
                public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });
        } else {
            progressText.setText("+ 0");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            //Toast.makeText(PlayActivity.this, results.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(PlayActivity.this, GameSingleton.getInstance().getGameModel().getDescription(), Toast.LENGTH_SHORT).show();
            return true;
        } else if (end) {
            infoDialog();
            return true;
        } else {
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (end) {
            infoDialog();
        } else {
            finish();
        }
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(R.string.finished);
        builder.setIcon(R.drawable.ic_done_teal_a700_24dp);
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(PlayActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                upvote();
                Intent i = new Intent(PlayActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                downvote();
                Intent i = new Intent(PlayActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });

        View dialogView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        TextView textView1 = (TextView) dialogView.findViewById(android.R.id.text1);
        textView1.setText(R.string.feedback);
        builder.setView(dialogView);
        builder.show();
    }

    private void upvote(){
        int position = Integer.parseInt(GameSingleton.getInstance().getGameModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<GameModel> call = restInterface.upvoteGame(position);
        call.enqueue(new Callback<GameModel>() {
            @Override
            public void onResponse(@NonNull Call<GameModel> call, @NonNull Response<GameModel> response) {
                GameModel jsonResponse = response.body();
                GameSingleton.getInstance().getGameModel().setVote(jsonResponse.getVote());
            }

            @Override
            public void onFailure(@NonNull Call<GameModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void downvote(){
        int position = Integer.parseInt(GameSingleton.getInstance().getGameModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<GameModel> call = restInterface.downvoteGame(position);
        call.enqueue(new Callback<GameModel>() {
            @Override
            public void onResponse(@NonNull Call<GameModel> call, @NonNull Response<GameModel> response) {
                GameModel jsonResponse = response.body();
                GameSingleton.getInstance().getGameModel().setVote(jsonResponse.getVote());
            }

            @Override
            public void onFailure(@NonNull Call<GameModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}
