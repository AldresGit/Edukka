package com.javier.edukka.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;


import com.javier.edukka.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<String> questions;
    private final List<String> answers;
    private final List<String> options;
    private final List<String> types;
    private DragDropAdapter dragDropAdapter;    // Spanish Language             V
    private PickerAdapter pickerAdapter;        // Mathematics                  V
    private DragNameAdapter dragNameAdapter;    // Natural Sciences             V
    private DragImageAdapter dragImageAdapter;  // Social Sciences              V
    private CheckboxAdapter checkboxAdapter;    // Biology and Geology          V
    private CompleteAdapter completeAdapter;    // Geography and History        V
    private SoundAdapter soundAdapter;          // Music                        V
    private ImageAdapter imageAdapter;          // Sports                       V
    private SpinnerAdapter spinnerAdapter;      // English                      V
    private SelectAdapter selectAdapter;        // General Knowledge            V
    private int cont = 0;


    public GeneralAdapter(Context applicationContext, List<String> questions, List<String> options, List<String> answers, List<String> types) {
        this.inflater = (LayoutInflater.from(applicationContext));
        this.questions = questions;
        this.options = options;
        this.answers = answers;
        this.types = types;

        dragDropAdapter = new DragDropAdapter(applicationContext, questions, options);      //dragdrop
        pickerAdapter = new PickerAdapter(applicationContext, questions, answers);          //picker
        dragNameAdapter = new DragNameAdapter(applicationContext, questions, options);      //dragname
        dragImageAdapter = new DragImageAdapter(applicationContext, questions, options);    //dragimage
        checkboxAdapter = new CheckboxAdapter(applicationContext, questions, options);      //checkbox
        completeAdapter = new CompleteAdapter(applicationContext, questions, answers);      //complete
        soundAdapter = new SoundAdapter(applicationContext, questions, options);            //sound
        imageAdapter = new ImageAdapter(applicationContext, questions, options);            //image
        spinnerAdapter = new SpinnerAdapter(applicationContext, questions, options);        //spinner
        selectAdapter = new SelectAdapter(applicationContext, questions, options);          //select
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int i) {
        String str = new String();
        switch(types.get(i)) {
            case "dragdrop":
                str = dragDropAdapter.getItem(i).toString();
                break;
            case "picker":
                str = pickerAdapter.getItem(i).toString();
                break;
            case "dragname":
                str = dragNameAdapter.getItem(i).toString();
                break;
            case "dragimage":
                str = dragImageAdapter.getItem(i).toString();
                break;
            case "checkbox":
                str = checkboxAdapter.getItem(i).toString();
                break;
            case "complete":
                str = completeAdapter.getItem(i).toString();
                break;
            case "sound":
                str = soundAdapter.getItem(i).toString();
                break;
            case "image":
                str = imageAdapter.getItem(i).toString();
                break;
            case "spinner":
                str = spinnerAdapter.getItem(i).toString();
                break;
            case "select":
                str = selectAdapter.getItem(i).toString();
                break;
        }
        return str;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        switch (types.get(i)) {
            case "dragdrop":
                view = dragDropAdapter.getView(i, view, viewGroup);
                break;
            case "picker":
                view = pickerAdapter.getView(i, view, viewGroup);
                break;
            case "dragname":
                view = dragNameAdapter.getView(i, view, viewGroup);
                break;
            case "dragimage":
                view = dragImageAdapter.getView(i, view, viewGroup);
                break;
            case "checkbox":
                view = checkboxAdapter.getView(i, view, viewGroup);
                break;
            case "complete":
                view = completeAdapter.getView(i, view, viewGroup);
                break;
            case "sound":
                view = soundAdapter.getView(i, view, viewGroup);
                break;
            case "image":
                view = imageAdapter.getView(i, view, viewGroup);
                break;
            case "spinner":
                view = spinnerAdapter.getView(i, view, viewGroup);
                break;
            case "select":
                view = selectAdapter.getView(i, view, viewGroup);
                break;
        }
        return view;
    }


}

