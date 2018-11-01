package com.javier.edukka.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.ActivityModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.HelperClient;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_EDITION = "edition";
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView total, approved, average, failure, best, worst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.statistics);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        total = (TextView) findViewById(R.id.total);
        approved = (TextView) findViewById(R.id.approved);
        average = (TextView) findViewById(R.id.average);
        failure = (TextView) findViewById(R.id.failure);
        best = (TextView) findViewById(R.id.best);
        worst = (TextView) findViewById(R.id.worst);

        refresh();
        if (getIntent().getStringExtra(EXTRA_EDITION).equals("student")) {
            loadJSON1();
        } else {
            loadJSON2();
        }
    }

    private void loadJSON1(){
        int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        //int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<ActivityModel>> call = restInterface.getUserActivity(id);
        call.enqueue(new Callback<List<ActivityModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityModel>> call, @NonNull Response<List<ActivityModel>> response) {
                List<ActivityModel> jsonResponse = response.body();
                setData(jsonResponse);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void loadJSON2(){
        //int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<ActivityModel>> call = restInterface.getClassActivity(id);
        call.enqueue(new Callback<List<ActivityModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityModel>> call, @NonNull Response<List<ActivityModel>> response) {
                List<ActivityModel> jsonResponse = response.body();
                setData(jsonResponse);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityModel>> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void setData(List<ActivityModel> list) {
        if (list.get(0).getStudentId()==null) {
            total.setText("0");
            approved.setText("0");
            average.setText("-");
            failure.setText("-");
            best.setText("-");
            worst.setText("-");
        } else {
            float res = 0;
            float sum = 0;
            for (ActivityModel a : list) {
                sum += Float.parseFloat(a.getResult());
                if (Float.parseFloat(a.getResult()) >= 2.5) {
                    res++;
                }
            }
            float media = Math.round((sum / list.size()) * 100f) / 100f;
            int error = Math.round(100 - (res / list.size() * 100));
            String mode1 = modeMax(list);
            String mode2 = modeMin(list);
            total.setText(String.valueOf(list.size()));
            approved.setText(String.valueOf(Math.round(res)));
            average.setText(String.valueOf(media));
            failure.setText(String.valueOf(error)+"%");
            best.setText(mode1);
            if (mode1.equals(mode2)) {
                worst.setText("-");
            } else {
                worst.setText(mode2);
            }
        }
    }

    private String modeMax(List<ActivityModel> list) {
        String maxValue="";
        int maxCount=0;
        for (int i=0; i<list.size(); i++) {
            int count=0;
            for (int j=0; j<list.size(); j++) {
                if (list.get(j).getSubject().equals(list.get(i).getSubject())) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = list.get(i).getSubject();
            }
        }
        if (Locale.getDefault().getLanguage().equals("es")) {
            maxValue = HelperClient.subjectTranslateEs(maxValue);
        }
        maxValue += " (" + maxCount + ")";
        return maxValue;
    }

    private String modeMin(List<ActivityModel> list) {
        String minValue="";
        int minCount=999;
        for (int i=0; i<list.size(); i++) {
            int count=0;
            for (int j=0; j<list.size(); j++) {
                if (list.get(j).getSubject().equals(list.get(i).getSubject())) {
                    count++;
                }
            }
            if (count < minCount) {
                minCount = count;
                minValue = list.get(i).getSubject();
            }
        }
        if (Locale.getDefault().getLanguage().equals("es")) {
            minValue = HelperClient.subjectTranslateEs(minValue);
        }
        minValue += " (" + minCount + ")";
        return minValue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void refresh() {
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getIntent().getStringExtra(EXTRA_EDITION).equals("student")) {
                            loadJSON1();
                        } else {
                            loadJSON2();
                        }
                    }
                }
        );
    }

}
