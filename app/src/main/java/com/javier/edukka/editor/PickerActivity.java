package com.javier.edukka.editor;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickerActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private EditText question, options;
    private TextView answer;
    private ImageButton validate, reload;
    private Button save;
    private int id, min;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        options = (EditText) findViewById(R.id.options);
        answer = (TextView) findViewById(R.id.answer);

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

        reload.setEnabled(false);
        save.setEnabled(false);
        seekBar.setEnabled(false);
        answer.setVisibility(View.INVISIBLE);
    }

    public void loadJSON() {
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                question.setText(response.body().getQuestion());
                options.setText(response.body().getAnswer());
                min = (Integer.parseInt(response.body().getAnswer()) / 10) * 10;

            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(PickerActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(10);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorMaths), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                answer.setText(String.valueOf(i + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar.setProgress(5);
        answer.setText(options.getText().toString());
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
        if (checkFieldValidation()) {
            min = (Integer.parseInt(options.getText().toString()) / 10) * 10;
            answer.setText(String.valueOf(seekBar.getProgress() + min));
            answer.setVisibility(View.VISIBLE);
            options.setEnabled(false);
            validate.setEnabled(false);
            reload.setEnabled(true);
            save.setEnabled(true);
            seekBar.setEnabled(true);
        }
    }

    public void reset(View view) {
        options.setEnabled(true);
        validate.setEnabled(true);
        reload.setEnabled(false);
        save.setEnabled(false);
        seekBar.setEnabled(false);
        answer.setVisibility(View.INVISIBLE);
    }

    public void save(View view) {
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), options.getText().toString(), "", id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(PickerActivity.this, R.string.quiz_saved, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                finish();
                startActivity(getIntent());
                Toast.makeText(PickerActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
