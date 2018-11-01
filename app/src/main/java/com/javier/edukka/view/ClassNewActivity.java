package com.javier.edukka.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.ClassModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassNewActivity extends AppCompatActivity {

    private EditText name, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_new);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.newclass);

        name = (EditText) findViewById(R.id.class_name);
        info = (EditText) findViewById(R.id.class_info);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (name.getText().hashCode() == editable.hashCode() && name.getText().toString().equals("")) {
                    name.setError(getText(R.string.empty));
                } else if (info.getText().hashCode() == editable.hashCode() && info.getText().toString().equals("")) {
                    info.setError(getText(R.string.empty));
                }
            }
        };
        name.addTextChangedListener(watcher);
        info.addTextChangedListener(watcher);
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (name.getText().toString().equals("")) {
            name.setError(getText(R.string.empty));
            valid = false;
        }
        if (info.getText().toString().equals("")) {
            info.setError(getText(R.string.empty));
            valid = false;
        }
        return valid;
    }

    public void create(View v) {
        if (checkFieldValidation()) {
            int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<ClassModel> call = restInterface.createClass(name.getText().toString(), info.getText().toString(), id);
            call.enqueue(new Callback<ClassModel>() {
                @Override
                public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                    ClassModel jsonResponse = response.body();
                    UserSingleton.getInstance().getUserModel().setClassId(jsonResponse.getId());
                    Toast.makeText(ClassNewActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
                    //ListContentFragment.setSize(1);
                    Intent i = new Intent(ClassNewActivity.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }

                @Override
                public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
