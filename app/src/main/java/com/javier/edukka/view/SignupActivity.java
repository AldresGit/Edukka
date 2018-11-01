package com.javier.edukka.view;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.javier.edukka.R;
import com.javier.edukka.adapter.AvatarAdapter;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.HelperClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private final String[] array = HelperClient.array_avatar();
    private EditText name, user, pass, pass1, classid;
    private AvatarAdapter avatarAdapter;
    private List<Drawable> avatars;
    private RecyclerView recyclerView;
    private String radio = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        StatusBarUtil.setTransparent(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.fullname);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        pass1 = (EditText) findViewById(R.id.password1);
        classid = (EditText) findViewById(R.id.classid);

        initAvatars();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        avatarAdapter = new AvatarAdapter(avatars);
        recyclerView.setAdapter(avatarAdapter);

        CardView cardview = (CardView) findViewById(R.id.cardView);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFieldValidation()) {
                    signup();
                }
            }
        });

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
                } else if (pass.getText().hashCode() == editable.hashCode() && pass.getText().toString().length()<4) {
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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_student:
                if (checked)
                    radio = "student";
                    initAvatars();
                    avatarAdapter = new AvatarAdapter(avatars);
                    recyclerView.setAdapter(avatarAdapter);
                break;
            case R.id.radio_teacher:
                if (checked)
                    radio = "teacher";
                    initAvatars();
                    avatarAdapter = new AvatarAdapter(avatars);
                    recyclerView.setAdapter(avatarAdapter);
                break;
        }
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
        if (pass.getText().toString().length()<4) {
            pass.setError(getText(R.string.minimum));
            valid = false;
        }
        if (!pass1.getText().toString().equals(pass.getText().toString())) {
            pass1.setError(getText(R.string.password_error));
            valid = false;
        }
        return valid;
    }

    private void initAvatars() {
        avatars = new ArrayList<>();
        TypedArray a = getResources().obtainTypedArray(R.array.avatar_pictures);
        if (radio.equals("teacher")) {
            avatars.add(a.getDrawable(array.length-1));
        } else {
            for (int i=0; i<array.length-1; i++) {
                avatars.add(a.getDrawable(i));
            }
        }
        a.recycle();
    }

    private void signup() {
        String str = classid.getText().toString();
        if (classid.getText().toString().equals("")) {
            str = "1";
        }
        int position = avatarAdapter.getSelectedPos();
        if (radio.equals("teacher")) {
            position = 15;
        }
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<UserModel> request = restInterface.signUp(name.getText().toString(), user.getText().toString(),
                pass.getText().toString(), radio, array[position], Integer.parseInt(str));
        request.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                UserModel model = response.body();
                if (model.getId()==null) {
                    if (model.getName().equals("username")) {
                        Toast.makeText(SignupActivity.this, R.string.username_fail, Toast.LENGTH_SHORT).show();
                        user.setError(getText(R.string.username_fail));
                    } else {
                        Toast.makeText(SignupActivity.this, R.string.class_fail, Toast.LENGTH_SHORT).show();
                        classid.setError(getText(R.string.class_fail));
                    }
                } else {
                    UserSingleton.getInstance().setUserModel(model);
                    Toast.makeText(SignupActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                finish();
                startActivity(getIntent());
                Toast.makeText(SignupActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
