package com.javier.edukka.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.ClassModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassEditActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private EditText name, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.editclass);

        name = (EditText) findViewById(R.id.class_name);
        info = (EditText) findViewById(R.id.class_info);
        loadJSON();

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

    private void loadJSON(){
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<ClassModel> call = restInterface.getClass(position);
        call.enqueue(new Callback<ClassModel>() {
            @Override
            public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                ClassModel jsonResponse = response.body();
                name.setText(jsonResponse.getName());
                info.setText(jsonResponse.getInformation());
            }

            @Override
            public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
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

    public void save(View v) {
        if (checkFieldValidation()) {
            int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<ClassModel> call = restInterface.updateClass(name.getText().toString(), info.getText().toString(), position);
            call.enqueue(new Callback<ClassModel>() {
                @Override
                public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                    Toast.makeText(ClassEditActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!UserSingleton.getInstance().getUserModel().getId().equals("1")) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            infoDialog();
            return true;
        } else {
            finish();
            return true;
        }
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.deleteclass);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                deleteClass();
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
        textView1.setText(R.string.dialogclass);
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteClass(){
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> call = restInterface.deleteClass(position);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                UserSingleton.getInstance().getUserModel().setClassId("1");
                Toast.makeText(ClassEditActivity.this, R.string.deleteclass_success, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ClassEditActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

}
