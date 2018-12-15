package com.javier.edukka.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.editor.CheckboxEditorActivity;
import com.javier.edukka.editor.CompleteEditorActivity;
import com.javier.edukka.editor.DragImageEditorActivity;
import com.javier.edukka.editor.DragNameEditorActivity;
import com.javier.edukka.editor.ImageEditorActivity;
import com.javier.edukka.editor.PickerEditorActivity;
import com.javier.edukka.editor.SelectEditorActivity;
import com.javier.edukka.editor.SoundEditorActivity;
import com.javier.edukka.editor.SpinnerEditorActivity;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.editor.DragDropEditorActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private String EXTRA_POSITION = "position";
    private final ArrayList<QuizModel> mArrayList;

    public QuizAdapter(ArrayList<QuizModel> arrayList) {
        this.mArrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quiz_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        switch(mArrayList.get(i).getType()) {
            case "dragdrop":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Drag Drop");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#607d8b"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.dragdrop_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#607d8b"));
                break;
            case "picker":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Picker");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#f44336"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.picker_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#f44336"));
                break;
            case "dragname":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Drag Name");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#9c27b0"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.dragname_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#9c27b0"));
                break;
            case "dragimage":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Drag Image");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#673ab7"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.dragimage_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#673ab7"));
                break;
            case "checkbox":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Checkbox");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#2196f3"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.checkbox_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#2196f3"));
                break;
            case "complete":
                holder.quiz_question.setText(mArrayList.get(i).getAnswer());
                holder.quiz_type.setText("Complete");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#009688"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.complete_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#009688"));
                break;
            case "sound":
                holder.quiz_question.setText(mArrayList.get(i).getOptions());
                holder.quiz_type.setText("Sound");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#4caf50"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer().replace(",", " "));
                holder.icon_quiz.setImageResource(R.drawable.sound_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#4caf50"));
                break;
            case "image":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Image");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#ffb300"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.image_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#ffb300"));
                break;
            case "spinner":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Spinner");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#ff5722"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.spinner_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#ff5722"));
                break;
            case "select":
                holder.quiz_question.setText(mArrayList.get(i).getQuestion());
                holder.quiz_type.setText("Select");
                holder.quiz_type.setBackgroundColor(Color.parseColor("#795548"));
                holder.quiz_options.setText(mArrayList.get(i).getAnswer());
                holder.icon_quiz.setImageResource(R.drawable.select_icon);
                holder.icon_quiz.setColorFilter(Color.parseColor("#795548"));
                break;

        }
        if(mArrayList.get(i).getEdited().equals("yes")) {
            holder.quiz_edited.setText(R.string.quiz_ready);
            holder.quiz_edited.setTextColor(Color.parseColor("#4caf50"));

        } else {
            holder.quiz_edited.setText(R.string.quiz_not_ready);
            holder.quiz_edited.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView quiz_type;
        private final TextView quiz_question;
        private final TextView quiz_options;
        private final TextView quiz_edited;
        private final ImageView icon_quiz;

        private ViewHolder(View view) {
            super(view);
            quiz_type = (TextView) view.findViewById(R.id.quiz_type);
            quiz_question = (TextView) view.findViewById(R.id.quiz_question);
            quiz_options = (TextView) view.findViewById(R.id.quiz_options);
            quiz_edited = (TextView) view.findViewById(R.id.quiz_edited);
            icon_quiz =  (ImageView) view.findViewById(R.id.icon_quiz);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = Integer.parseInt(mArrayList.get(getAdapterPosition()).getId());
                    String s = mArrayList.get(getAdapterPosition()).getType();
                    Context context = v.getContext();
                    Intent intent;

                    switch(s){
                        case "dragdrop":
                            intent = new Intent(context, DragDropEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "picker":
                            intent = new Intent(context, PickerEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "dragname":
                            intent = new Intent(context, DragNameEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "dragimage":
                            intent = new Intent(context, DragImageEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "checkbox":
                            intent = new Intent(context, CheckboxEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "complete":
                            intent = new Intent(context, CompleteEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "sound":
                            intent = new Intent(context, SoundEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "image":
                            intent = new Intent(context, ImageEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "spinner":
                            intent = new Intent(context, SpinnerEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                        case "select":
                            intent = new Intent(context, SelectEditorActivity.class);
                            intent.putExtra(EXTRA_POSITION, id);
                            context.startActivity(intent);
                            break;

                    }
                }
            });
        }
    }
}
