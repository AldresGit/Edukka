package com.javier.edukka.editor;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckboxEditorActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private EditText question, options;
    private List<String> answer;
    private QuizModel model;

    private ImageButton validate, reload;
    private Button save;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        validate = (ImageButton) findViewById(R.id.validate);
        reload = (ImageButton) findViewById(R.id.reload);
        save = (Button) findViewById(R.id.save);
        question = (EditText) findViewById(R.id.question);
        options = (EditText) findViewById(R.id.options);

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
        answer = new ArrayList<>();

        loadJSON();

        reload.setEnabled(false);
        save.setEnabled(false);
    }

    public void loadJSON() {
        RestInterface restInterface = RetrofitClient.getInstance();
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                question.setText(model.getQuestion());
                options.setText(model.getOptions().replace(",", " "));

                if(!model.getAnswer().equals("")) {
                    //Rellenar el recycler view y tal
                }

            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(CheckboxEditorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, Arrays.asList(options.getText().toString().split(" ")));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);

            options.setEnabled(false);
            validate.setEnabled(false);
            reload.setEnabled(true);
            save.setEnabled(true);
        }
    }

    public void reset(View view) {
        answer.clear();
        options.setEnabled(true);
        validate.setEnabled(true);

        reload.setEnabled(false);
        save.setEnabled(false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvOptions);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void save(View view) {
        Collections.sort(answer);
        String correctAnswer = answer.toString();
        Random rnd = new Random();
        rnd.setSeed(1000);
        List<String> optionsToSort = Arrays.asList(options.getText().toString().split(" "));
        Collections.shuffle(optionsToSort, rnd);
        String correctOptions = optionsToSort.toString().replace(" ", "");

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), correctAnswer.substring(1,correctAnswer.length()-1), correctOptions.substring(1,correctOptions.length()-1), id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(CheckboxEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(CheckboxEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
        private final LayoutInflater mInflater;
        private final List<String> mData;

        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_multi, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.myCheckBox.setText(mData.get(position));
            holder.myCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answer.contains(holder.myCheckBox.getText().toString())) {
                        answer.remove(holder.myCheckBox.getText().toString());
                    } else {
                        answer.add(holder.myCheckBox.getText().toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final CheckBox myCheckBox;

            ViewHolder(View itemView) {
                super(itemView);
                myCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            }
        }
    }
}
