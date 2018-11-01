package com.javier.edukka.service;

import android.os.SystemClock;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private final long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

    protected DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick(v);
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    protected abstract void onDoubleClick(View view);

}