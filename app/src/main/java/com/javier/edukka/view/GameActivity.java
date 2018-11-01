package com.javier.edukka.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.GameSingleton;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.HelperClient;

import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_SUBJECT = "subject";
    private final Map<String, String> map = HelperClient.map_subject();
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private TextView detail, difficulty, vote;
    private ImageView subjectImage;
    private GameModel jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        detail = (TextView) findViewById(R.id.game_detail);
        difficulty = (TextView) findViewById(R.id.game_difficulty);
        vote = (TextView) findViewById(R.id.game_vote);
        subjectImage = (ImageView) findViewById(R.id.image);
        loadJSON();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
                Intent intent = new Intent(GameActivity.this, GameEditActivity.class);
                intent.putExtra(GameEditActivity.EXTRA_POSITION, id);
                startActivity(intent);
            }
        });
    }

    protected void onRestart() {
        super.onRestart();
        loadJSON();
    }

    private void loadJSON(){
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<GameModel> call = restInterface.getGame(position);
        call.enqueue(new Callback<GameModel>() {
            @Override
            public void onResponse(@NonNull Call<GameModel> call, @NonNull Response<GameModel> response) {
                jsonResponse = response.body();
                collapsingToolbar.setTitle(jsonResponse.getTitle());
                setBackground(jsonResponse.getSubject());
                findViewById(R.id.animation_view).setVisibility(View.VISIBLE);
                detail.setText(jsonResponse.getDescription());
                vote.setText(jsonResponse.getVote());
                if (Locale.getDefault().getLanguage().equals("es")) {
                    String level = HelperClient.levelTranslateEs(jsonResponse.getDifficulty());
                    difficulty.setText(level);
                } else {
                    String upperString = jsonResponse.getDifficulty().substring(0,1).toUpperCase() + jsonResponse.getDifficulty().substring(1);
                    difficulty.setText(upperString);
                }

                String s = getIntent().getStringExtra(EXTRA_SUBJECT);
                int resourceId = getResources().getIdentifier(map.get(s), "drawable", getPackageName());
                subjectImage.setImageDrawable(getResources().getDrawable(resourceId));
                if (UserSingleton.getInstance().getUserModel().getId().equals("1")) {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GameModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void setBackground(String subject) {
        switch (subject) {
            case "Spanish Language":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorSpanish));
                break;
            case "Mathematics":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorMaths));
                break;
            case "Natural Sciences":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorNatural));
                break;
            case "Social Sciences":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorSocial));
                break;
            case "Biology & Geology":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorBioGeo));
                break;
            case "Geography & History":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorGeoHis));
                break;
            case "Music":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorMusic));
                break;
            case "Sports":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorSports));
                break;
            case "English":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorEnglish));
                break;
            case "General Knowledge":
                collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.colorGeneral));
                break;
        }
    }

    public void play(View v) {
        GameSingleton.getInstance().setGameModel(jsonResponse);
        if (UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
            Intent i = new Intent(GameActivity.this, GameLookActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(GameActivity.this, PlayActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
