package com.javier.edukka.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.controller.GameSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.service.HelperClient;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameCreateActivity extends AppCompatActivity {
    private EditText title, description, timeLimit;
    private MaterialBetterSpinner difficulty;
    private String SUBJECT_NAME = "subject";
    private String EXTRA_POSITION = "position";
    private String subject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subject = getIntent().getStringExtra(SUBJECT_NAME);
        getSupportActionBar().setTitle(getText(R.string.creating_game) + subject);

        title = (EditText) findViewById(R.id.gameTitle);
        description = (EditText) findViewById(R.id.gameDescription);
        timeLimit = (EditText) findViewById(R.id.gameTime);

        difficulty = (MaterialBetterSpinner) findViewById(R.id.gameDifficulty);
        String[] difficulties = getResources().getStringArray(R.array.level);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficulties);
        difficulty.setAdapter(arrayAdapter);
        difficulty.setText(difficulties[0]);


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (title.getText().hashCode() == editable.hashCode() && title.getText().toString().equals("")) {
                    title.setError(getText(R.string.empty));
                } else if (description.getText().hashCode() == editable.hashCode() && description.getText().toString().equals("")) {
                    description.setError(getText(R.string.empty));
                } else if (timeLimit.getText().hashCode() == editable.hashCode() && timeLimit.getText().toString().equals("")) {
                    timeLimit.setError(getText(R.string.empty));
                }
            }
        };
        title.addTextChangedListener(watcher);
        description.addTextChangedListener(watcher);
        timeLimit.addTextChangedListener(watcher);
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (title.getText().toString().equals("")) {
            title.setError(getText(R.string.empty));
            valid = false;
        }
        if (description.getText().toString().equals("")) {
            description.setError(getText(R.string.empty));
            valid = false;
        }
        if (timeLimit.getText().toString().equals("")){
            timeLimit.setError(getText(R.string.empty));
            valid = false;
        }
        if (!(timeLimit.getText().toString().equals("")) && (Integer.parseInt(timeLimit.getText().toString()) < 30)) {
            timeLimit.setError(getText(R.string.time_limit_error));
            valid = false;
        }
        return valid;
    }

    public void save(View v) {
        if (checkFieldValidation()) {
            RestInterface restInterface = RetrofitClient.getInstance();
            final Call<GameModel> request;
            subject = HelperClient.subjectTranslateEn(subject);
            String level = HelperClient.levelCode(difficulty.getText().toString());

            request = restInterface.createGame(subject, title.getText().toString(), description.getText().toString(),
                     Locale.getDefault().getLanguage(), level, timeLimit.getText().toString(),"1");            //CAMBIAR EL ID DE LA CLASE

            request.enqueue(new Callback<GameModel>(){
                 @Override
                 public void onResponse(@NonNull Call<GameModel> call, @NonNull Response<GameModel> response) {
                     Toast.makeText(GameCreateActivity.this, R.string.game_created, Toast.LENGTH_SHORT).show();
                     //GameSingleton.getInstance().setGameModel(response.body());     -> Forma Fácil para pussies
                     Intent i = new Intent(GameCreateActivity.this, GameModifyActivity.class);
                     i.putExtra(EXTRA_POSITION, Integer.parseInt(response.body().getId()));
                     i.putExtra(SUBJECT_NAME, response.body().getSubject());
                     finish();
                     startActivity(i);
                 }
                 @Override
                 public void onFailure(Call<GameModel> call, Throwable t) {
                     finish();
                     startActivity(getIntent());
                     Toast.makeText(GameCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                 }
             });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
