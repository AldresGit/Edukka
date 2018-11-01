package com.javier.edukka.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.javier.edukka.adapter.AvatarAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.HelperClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private final String[] array = HelperClient.array_avatar();
    private EditText name, user, pass, pass1;
    private AvatarAdapter avatarAdapter;
    private List<Drawable> avatars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.editprofile);

        name = (EditText) findViewById(R.id.user_fullname);
        user = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.user_pass);
        pass1 = (EditText) findViewById(R.id.user_pass1);

        initAvatars();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        avatarAdapter = new AvatarAdapter(avatars);
        recyclerView.setAdapter(avatarAdapter);
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
                } else if (user.getText().hashCode() == editable.hashCode() && user.getText().toString().equals("")) {
                    user.setError(getText(R.string.empty));
                } else if (pass.getText().hashCode() == editable.hashCode() && pass.getText().toString().length()>0 && pass.getText().toString().length()<4) {
                    pass.setError(getText(R.string.minimum));
                } else if (pass1.getText().hashCode() == editable.hashCode() && !pass1.getText().toString().equals(pass.getText().toString())) {
                    pass1.setError(getText(R.string.password_error));
                }
            }
        };
        name.addTextChangedListener(watcher);
        user.addTextChangedListener(watcher);
        pass.addTextChangedListener(watcher);
        pass1.addTextChangedListener(watcher);
    }

    private void initAvatars() {
        avatars = new ArrayList<>();
        TypedArray a = getResources().obtainTypedArray(R.array.avatar_pictures);
        if (UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
            avatars.add(a.getDrawable(array.length-1));
        } else {
            for (int i=0; i<array.length-1; i++) {
                avatars.add(a.getDrawable(i));
            }
        }
        a.recycle();
    }

    private void loadJSON(){
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<UserModel> call = restInterface.getUser(position);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                UserModel jsonResponse = response.body();
                name.setText(jsonResponse.getName());
                user.setText(jsonResponse.getUsername());
                if (UserSingleton.getInstance().getUserModel().getRole().equals("student")) {
                    int pos = Arrays.asList(array).indexOf(jsonResponse.getImage());
                    avatarAdapter.setSelectedPos(pos);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
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
        if (user.getText().toString().equals("")) {
            user.setError(getText(R.string.empty));
            valid = false;
        }
        if (pass.getText().toString().length()>0 && pass.getText().toString().length()<4) {
            pass.setError(getText(R.string.minimum));
            valid = false;
        }
        if (!pass1.getText().toString().equals(pass.getText().toString())) {
            pass1.setError(getText(R.string.password_error));
            valid = false;
        }
        return valid;
    }

    public void save(View v) {
        if (checkFieldValidation()) {
            int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
            int position = avatarAdapter.getSelectedPos();
            if (UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
                position = 15;
            }
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<UserModel> call = restInterface.updateUser(name.getText().toString(), user.getText().toString(),
                    pass.getText().toString(), array[position], id);
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                    UserModel jsonResponse = response.body();
                    if (jsonResponse.getId()==null) {
                        Toast.makeText(ProfileEditActivity.this, R.string.username_fail, Toast.LENGTH_SHORT).show();
                        user.setError(getText(R.string.username_fail));
                    } else {
                        UserSingleton.getInstance().setUserModel(jsonResponse);
                        Toast.makeText(ProfileEditActivity.this, R.string.data_update, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
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
        builder.setTitle(R.string.deleteuser);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                deleteUser();
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
        textView1.setText(R.string.dialoguser);
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteUser(){
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        String role = UserSingleton.getInstance().getUserModel().getRole();
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> call = restInterface.deleteUser(position, role);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(ProfileEditActivity.this, R.string.deleteuser_success, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ProfileEditActivity.this, LoginActivity.class);
                //finish();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

}
