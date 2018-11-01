package com.javier.edukka.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.fragment.ListContentFragment;
import com.javier.edukka.model.ClassModel;
import com.javier.edukka.service.HelperClient;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private TextView id, info, size;
    private Menu mMenu;
    private boolean create = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        id = (TextView) findViewById(R.id.class_id);
        info = (TextView) findViewById(R.id.class_info);
        size = (TextView) findViewById(R.id.class_size);
        loadJSON();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (create) {
                    Intent intent = new Intent(ClassActivity.this, ClassNewActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ClassActivity.this, ClassEditActivity.class);
                    int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
                    intent.putExtra(ClassEditActivity.EXTRA_POSITION, id);
                    startActivity(intent);
                }
            }
        });
    }

    protected void onRestart() {
        super.onRestart();
        loadJSON();
    }

    private void loadJSON(){
        int position = Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<ClassModel> call = restInterface.getClass(position);
        call.enqueue(new Callback<ClassModel>() {
            @Override
            public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                ClassModel jsonResponse = response.body();
                collapsingToolbar.setTitle(jsonResponse.getName());
                id.setText(jsonResponse.getId());
                info.setText(jsonResponse.getInformation());
                size.setText(getString(R.string.class_size,ListContentFragment.getSize()));
                if (UserSingleton.getInstance().getUserModel().getId().equals(jsonResponse.getTeacherId())) {
                    fab.setVisibility(View.VISIBLE);
                } else if (jsonResponse.getId().equals("1") && UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
                    fab.setImageResource(android.R.drawable.ic_input_add);
                    fab.setVisibility(View.VISIBLE);
                    create = true;
                }

                if (!UserSingleton.getInstance().getUserModel().getId().equals(jsonResponse.getTeacherId())) {
                    if (jsonResponse.getId().equals("1")) {
                        mMenu.findItem(R.id.add_class).setVisible(true);
                    } else {
                        mMenu.findItem(R.id.rem_class).setVisible(true);
                    }
                }

                if (jsonResponse.getId().equals("1") && Locale.getDefault().getLanguage().equals("es")) {
                    collapsingToolbar.setTitle(HelperClient.defaultClassNameEs());
                    info.setText(HelperClient.defaultClassInfoEs());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_class:
                infoDialog1();
                return true;
            case R.id.rem_class:
                infoDialog2();
                return true;
            default:
                finish();
                return true;
        }
    }

    private void infoDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.enter_class);
        builder.setMessage(R.string.dialogenter);
        builder.setIconAttribute(android.R.attr.alertDialogIcon);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                addClass(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void infoDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.leave_class);
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                removeClass();
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
        textView1.setText(R.string.dialogleave);
        builder.setView(dialogView);
        builder.show();
    }

    private void addClass(String idclass){
        int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<ClassModel> call = restInterface.addUserClass(id, Integer.parseInt(idclass));
        call.enqueue(new Callback<ClassModel>() {
            @Override
            public void onResponse(@NonNull Call<ClassModel> call, @NonNull Response<ClassModel> response) {
                ClassModel jsonResponse = response.body();
                if (jsonResponse.getId()==null) {
                    Toast.makeText(ClassActivity.this, R.string.class_fail, Toast.LENGTH_SHORT).show();
                    infoDialog1();
                } else {
                    UserSingleton.getInstance().getUserModel().setClassId(jsonResponse.getId());
                    Toast.makeText(ClassActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ClassActivity.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClassModel> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void removeClass(){
        int id = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> call = restInterface.removeUserClass(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                UserSingleton.getInstance().getUserModel().setClassId("1");
                Toast.makeText(ClassActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ClassActivity.this, MainActivity.class);
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
