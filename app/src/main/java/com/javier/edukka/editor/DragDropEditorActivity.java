package com.javier.edukka.editor;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.DoubleClickListener;
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


public class DragDropEditorActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private EditText question, options;
    private List<String> answer;
    private RecyclerView recyclerView;
    private ImageButton validate, reload;
    private Button save;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop_editor);
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
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        final Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                question.setText(response.body().getQuestion());
                options.setText(response.body().getAnswer());
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragDropEditorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
            List<String> questionlist = new ArrayList<>();
            List<String> optionslist = new ArrayList<>();
            questionlist.add(question.getText().toString());
            optionslist.add(options.getText().toString());

            recyclerView = (RecyclerView) findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getApplicationContext(), findViewById(R.id.answerLayout), Arrays.asList(options.getText().toString().split(" ")));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            View.OnDragListener dragListener = new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    switch (dragEvent.getAction()) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            view.getBackground().setColorFilter(0xff33b5e5, PorterDuff.Mode.SRC_IN);
                            view.invalidate();
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            view.getBackground().clearColorFilter();
                            view.invalidate();
                            break;
                        case DragEvent.ACTION_DROP:
                            view.getBackground().clearColorFilter();
                            view.invalidate();
                            View v = (View) dragEvent.getLocalState();
                            ViewGroup owner = (ViewGroup) v.getParent();
                            owner.removeView(v);
                            LinearLayout container = (LinearLayout) view;
                            if (owner.getId() == R.id.answerLayout && container.getId() == R.id.answerLayout) {
                                answer.remove(dragEvent.getClipData().getItemAt(0).getText().toString());
                                answer.add(dragEvent.getClipData().getItemAt(0).getText().toString());
                                container.addView(v);
                            } else if (container.getId() == R.id.answerLayout) {
                                answer.add(dragEvent.getClipData().getItemAt(0).getText().toString());
                                container.addView(v);
                            } else {
                                answer.remove(dragEvent.getClipData().getItemAt(0).getText().toString());
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            };
            findViewById(R.id.optionsLayout).setOnDragListener(dragListener);
            findViewById(R.id.answerLayout).setOnDragListener(dragListener);
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
        LinearLayout container = (LinearLayout) findViewById(R.id.answerLayout);
        container.removeAllViews();
    }


    public void save(View view) {
        String correctAnswer = new String();
        for(int i = 0; i < answer.size() - 1; i++) {
            correctAnswer = correctAnswer.concat(answer.get(i));
            correctAnswer = correctAnswer.concat("");
        }
        correctAnswer = correctAnswer.concat(answer.get(answer.size() - 1));
        correctAnswer = correctAnswer.substring(1);

        List<String> sortedOptions = Arrays.asList(options.getText().toString().split(" "));
        Random rnd = new Random();
        rnd.setSeed(1000);
        Collections.shuffle(sortedOptions, rnd);

        String finalOptions = new String();
        for(int i = 0; i < sortedOptions.size() - 1; i++) {
            finalOptions = finalOptions.concat(sortedOptions.get(i));
            finalOptions = finalOptions.concat(",");
        }
        finalOptions = finalOptions.concat(sortedOptions.get(sortedOptions.size() - 1));

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), correctAnswer, finalOptions, id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(DragDropEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragDropEditorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });
    }


    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private final LayoutInflater mInflater;
        private final View layoutView;
        private final List<String> mData;

        MyRecyclerViewAdapter(Context context, View layoutView, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.layoutView = layoutView;
            this.mData = data;
        }

        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_text, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.textView.setText(" "+mData.get(position));
            holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Vibrator v = (Vibrator) mInflater.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(100);
                    ClipData data = ClipData.newPlainText("", holder.textView.getText().toString());
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                }
            });
            holder.textView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View view) {
                    LinearLayout container = (LinearLayout) layoutView;
                    container.removeView(view);
                    container.invalidate();
                    ClipData data = ClipData.newPlainText("", holder.textView.getText().toString());
                    answer.remove(data.getItemAt(0).getText().toString());
                }
            });
        }

        @Override
        public int getItemCount() { return mData.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
            }
        }
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

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.deletequiz);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                deleteQuiz();
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
        textView1.setText(R.string.dialogquiz);
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteQuiz() {
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> deleteRequest = restInterface.deleteQuiz(id);
        deleteRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finish();
                Toast.makeText(DragDropEditorActivity.this, R.string.deletequiz_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}
