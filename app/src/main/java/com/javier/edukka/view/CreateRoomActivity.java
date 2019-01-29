package com.javier.edukka.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private TextView message1, message2, message3;
    private String role;
    private MultiplayerGameModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        message1 = (TextView) findViewById(R.id.message);
        message2 = (TextView) findViewById(R.id.message2);
        message3 = (TextView) findViewById(R.id.message3);

        loadJSON();
    }

    private void loadJSON() {
        RestInterface restInterface = RetrofitClient.getInstance();
        int id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Call<MultiplayerGameModel> request = restInterface.getRoom(id);
        request.enqueue(new Callback<MultiplayerGameModel>() {
            @Override
            public void onResponse(Call<MultiplayerGameModel> call, Response<MultiplayerGameModel> response) {
                model = response.body();
                message1.setText(message1.getText() + model.getId());
                message2.setText(message2.getText() + "CLAVE DE USUARIOOOOO");
            }

            @Override
            public void onFailure(Call<MultiplayerGameModel> call, Throwable t) {
                Toast.makeText(CreateRoomActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
