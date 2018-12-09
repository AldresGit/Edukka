package com.javier.edukka.editor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpinnerEditorActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private int id;
    private EditText question, options, editLeftText, editRightText;
    private TextView leftTextView, rightTextView;
    private MaterialBetterSpinner spinner;
    private Button save;
    private ImageButton validate, reload;
    private QuizModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        options = (EditText) findViewById(R.id.options);
        editLeftText = (EditText) findViewById(R.id.left_text);
        editRightText = (EditText) findViewById(R.id.right_text);
        leftTextView = (TextView) findViewById(R.id.text_left_view);
        rightTextView = (TextView) findViewById(R.id.text_right_view);
        spinner = (MaterialBetterSpinner) findViewById(R.id.options_spinner);

        validate = (ImageButton) findViewById(R.id.validate);
        reload = (ImageButton) findViewById(R.id.reload);
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
                } else if (options.getText().hashCode() == editable.hashCode() && options.getText().toString().equals("")) {
                    options.setError(getText(R.string.empty));
                }
            }
        };

        question.addTextChangedListener(watcher);
        options.addTextChangedListener(watcher);

        loadJSON();

        spinner.setEnabled(false);
        reload.setEnabled(false);
        save.setEnabled(false);
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
                    question.setText(model.getQuestion().split(",")[1]);
                    editLeftText.setText(model.getQuestion().split(",")[0].split(" _ ")[0]);
                    editRightText.setText(model.getQuestion().split(",")[0].split(" _ ")[1]);
                }
                if(!model.getOptions().equals("")) {
                    options.setText(model.getOptions().replace(",", " "));
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(SpinnerEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (question.getText().toString().equals("")) {
            question.setError(getText(R.string.empty));
            valid = false;
        }
        if(options.getText().toString().equals("")) {
            options.setError(getText(R.string.empty));
            valid = false;
        }
        return valid;
    }

    public void validate(View view) {
        if(checkFieldValidation()) {
            rightTextView.setText(editRightText.getText().toString());
            leftTextView.setText(editLeftText.getText().toString());

            String[] options_list = options.getText().toString().split(" ");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options_list);
            spinner.setAdapter(arrayAdapter);

            spinner.setEnabled(true);
            validate.setEnabled(false);
            reload.setEnabled(true);
            save.setEnabled(true);
            editLeftText.setEnabled(false);
            editRightText.setEnabled(false);
            options.setEnabled(false);

            spinner.setText(options_list[0]);
        }
    }

    public void reset(View view) {
        rightTextView.setText(getString(R.string.right_text));
        leftTextView.setText(getString(R.string.left_text));

        String[] options_list = new String[0];
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options_list);
        spinner.setAdapter(arrayAdapter);

        validate.setEnabled(true);
        reload.setEnabled(false);
        save.setEnabled(false);
        editLeftText.setEnabled(true);
        editRightText.setEnabled(true);
        options.setEnabled(true);
    }

    public void save(View view) {
        String finalQuestion = editLeftText.getText().toString() + " _ " + editRightText.getText().toString() + "," + question.getText().toString();
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(finalQuestion, spinner.getText().toString(), options.getText().toString().replace(" ", ","), id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(SpinnerEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(SpinnerEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });
    }
}
