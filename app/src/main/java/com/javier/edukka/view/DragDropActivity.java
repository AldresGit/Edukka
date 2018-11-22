package com.javier.edukka.view;

import android.content.ClipData;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.service.DoubleClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DragDropActivity extends AppCompatActivity {

    private EditText question, options;
    private List<String> answer;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            Toast.makeText(DragDropActivity.this, "Patata", Toast.LENGTH_LONG).show();

            recyclerView = (RecyclerView) findViewById(R.id.rvOptions);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getApplicationContext(), findViewById(R.id.answerLayout), Arrays.asList(options.getText().toString().split(",")));
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
        }
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
}
