package com.javier.edukka.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.adapter.QuizAdapter;
import com.javier.edukka.controller.GameSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.HelperClient;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameModifyActivity extends AppCompatActivity {
    private EditText title, description, timeLimit;
    private MaterialBetterSpinner difficulty, questionType;
    private Switch visibility;
    private ArrayList<QuizModel> mArrayList;
    private String SUBJECT_NAME = "subject";
    private String EXTRA_POSITION = "position";
    private int position;
    private QuizAdapter quizAdapter;
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_modify);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        String subject = getIntent().getStringExtra(SUBJECT_NAME);
        if(Locale.getDefault().getLanguage().equals("es")) {
            subject = HelperClient.subjectTranslateEs(subject);
        }
        getSupportActionBar().setTitle(getText(R.string.modifying_game) + subject);

        title = (EditText) findViewById(R.id.gameTitle);
        description = (EditText) findViewById(R.id.gameDescription);
        timeLimit = (EditText) findViewById(R.id.gameTime);

        difficulty = (MaterialBetterSpinner) findViewById(R.id.gameDifficulty);
        String[] difficulties = getResources().getStringArray(R.array.level);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficulties);
        difficulty.setAdapter(arrayAdapter);

        visibility = (Switch) findViewById(R.id.visibility_switch);

        questionType = (MaterialBetterSpinner) findViewById(R.id.question_type);
        String[] question_types = getResources().getStringArray(R.array.question_type);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, question_types);
        questionType.setAdapter(arrayAdapter2);
        questionType.setText(question_types[0]);

        loadJSON();
        refresh();

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

    private void loadJSON() {
        position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<GameModel> request = restInterface.getGame(position);
        request.enqueue(new Callback<GameModel>() {
            @Override
            public void onResponse(Call<GameModel> call, Response<GameModel> response) {
                title.setText(response.body().getTitle());
                description.setText(response.body().getDescription());
                timeLimit.setText(response.body().getTime());
                if(Locale.getDefault().getLanguage().equals("es")) {
                    difficulty.setText(HelperClient.levelTranslateEs(response.body().getDifficulty()));
                } else {
                    difficulty.setText(response.body().getDifficulty());
                }
                if(response.body().getVisibility().equals("yes")) {
                    visibility.setChecked(true);
                } else {
                    visibility.setChecked(false);
                }
            }
            @Override
            public void onFailure(Call<GameModel> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

        loadJSON2();

    }

    private void loadJSON2() {
        position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface2 = RetrofitClient.getInstance();
        Call<List<QuizModel>> request2 = restInterface2.getGameQuiz(position);
        request2.enqueue(new Callback<List<QuizModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<QuizModel>> call, @NonNull Response<List<QuizModel>> response) {
                List<QuizModel> jsonResponse2 = response.body();
                if(jsonResponse2.get(0).getId() == null) {
                    mArrayList = new ArrayList<>();
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                } else {
                    ArrayList<QuizModel> quizList = new ArrayList<>();
                    boolean visibility_change = true;
                    for(QuizModel quiz : jsonResponse2) {
                        quizList.add(quiz);
                        if(visibility_change) {
                            visibility_change = quiz.getEdited().equals("yes");
                        }
                    }
                    mArrayList = quizList;
                    if(!visibility_change) {
                        visibility.setEnabled(false);
                        visibility.setChecked(false);
                    } else {
                        visibility.setEnabled(true);
                    }

                    if (mArrayList.isEmpty()) {
                        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
                        findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                    }

                }

                quizAdapter = new QuizAdapter(mArrayList);
                mRecyclerView.setAdapter(quizAdapter);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    public void save(View v) {
        if(checkFieldValidation()) {
            String visibility_value = "no";
            if(visibility.isChecked()) {
                visibility_value = "yes";
            }
            RestInterface restInterface = RetrofitClient.getInstance();
            final Call<GameModel> request;
            String level = HelperClient.levelCode(difficulty.getText().toString());
            request = restInterface.updateGame(title.getText().toString(), description.getText().toString(), level,
                    timeLimit.getText().toString(), visibility_value, String.valueOf(position));                         //-------------Canmbiar visibilidad------------------//
            request.enqueue(new Callback<GameModel>() {
                @Override
                public void onResponse(Call<GameModel> call, Response<GameModel> response) {
                    Toast.makeText(GameModifyActivity.this, R.string.modified_game, Toast.LENGTH_SHORT).show();
                    loadJSON();
                }

                @Override
                public void onFailure(Call<GameModel> call, Throwable t) {
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(GameModifyActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void createQuiz(View v) {
        String question = "", answer = "", options = "";
        String type = questionType.getText().toString();
        switch(type){
            case "Drag Drop":
                type = "dragdrop";
                break;
            case "Picker":
                type = "picker";
                answer = "0";
                break;
            case "Drag Name":
                type = "dragname";
                break;
            case "Drag Image":
                type = "dragimage";
                break;
            case "Checkbox":
                type = "checkbox";
                break;
            case "Complete":
                type = "complete";
                break;
            case "Sound":
                type = "sound";
                break;
            case "Image":
                type = "image";
                break;
            case "Spinner":
                type = "spinner";
                break;
            case "Select":
                type = "select";
                answer = getString(R.string.true_value);
                options = getString(R.string.truefalse_value);
                break;
        }

        RestInterface restInterface = RetrofitClient.getInstance();
        final Call<QuizModel> request2;
        request2 = restInterface.createQuiz(question, answer, options, type, position);
        request2.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(GameModifyActivity.this, R.string.question_added, Toast.LENGTH_SHORT).show();
                loadJSON2();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(GameModifyActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteGame() {
        int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> deleteRequest = restInterface.deleteGame(id);
        deleteRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Intent i = new Intent(GameModifyActivity.this, MainActivity.class);
                finish();
                Toast.makeText(GameModifyActivity.this, R.string.deletegame_success, Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.deletegame);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                deleteGame();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        View dialogView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        TextView textView1 = (TextView) dialogView.findViewById(android.R.id.text1);
        textView1.setText(R.string.dialoggame);
        builder.setView(dialogView);
        builder.show();
    }

    private void refresh() {
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadJSON2();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                infoDialog();
                return true;
            default:
                finish();
                return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJSON2();
    }

}
