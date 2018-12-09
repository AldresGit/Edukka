package com.javier.edukka.editor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectEditorActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private int id;
    private QuizModel model;
    private EditText question;
    private TextView optionValue;
    private Button save;
    private boolean value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        optionValue = (TextView) findViewById(R.id.option_value);
        save = (Button) findViewById(R.id.save);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (question.getText().hashCode() == editable.hashCode() && question.getText().toString().equals("")) {
                    question.setError(getText(R.string.empty));
                }
            }
        };

        question.addTextChangedListener(watcher);

        loadJSON();
    }

    private void loadJSON() {
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                if(!model.getQuestion().equals("")) {
                    question.setText(model.getQuestion());
                }
                if(model.getAnswer().equals(getString(R.string.true_value))) {
                    value = true;
                    optionValue.setBackgroundColor(getResources().getColor(R.color.colorMusic));
                    optionValue.setText(getString(R.string.true_value));
                } else {
                    value = false;
                    optionValue.setBackgroundColor(getResources().getColor(R.color.colorMaths));
                    optionValue.setText(getString(R.string.false_value));
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(SelectEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (question.getText().toString().equals("")) {
            question.setError(getText(R.string.empty));
            valid = false;
        }
        return valid;
    }

    public void change(View view) {
        if(value) {
            value = false;
            optionValue.setBackgroundColor(getResources().getColor(R.color.colorMaths));
            optionValue.setText(getString(R.string.false_value));
        } else {
            value = true;
            optionValue.setBackgroundColor(getResources().getColor(R.color.colorMusic));
            optionValue.setText(getString(R.string.true_value));
        }
    }

    public void save(View view) {
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), optionValue.getText().toString(), getString(R.string.truefalse_value), id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(SelectEditorActivity.this, getString(R.string.quiz_saved), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(SelectEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
