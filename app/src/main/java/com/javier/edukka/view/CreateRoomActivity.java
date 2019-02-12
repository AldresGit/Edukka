package com.javier.edukka.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.javier.edukka.R;
import com.javier.edukka.app.Config;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.MyFirebaseMessagingService;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.utils.NotificationUtils;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomActivity extends AppCompatActivity {
    private String EXTRA_POSITION = "position";
    private String EXTRA_ROLE = "role";
    private String EXTRA_POINTS = "extra";
    public static final String QUIZZES = "quizzes";
    public static final String RIVAL_ID = "rival_id";

    private TextView player1Name, player2Name;
    private ImageView player1Image, player2Image, extraIcon;
    private String role;
    private boolean roomFull = false;
    private boolean extraPoints = false;
    private String firebaseId = "";
    private String rivalFirebaseId = "";
    private List<QuizModel> quizzes;
    private String quizString;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseId = getFirebaseID();
        role = getIntent().getStringExtra(EXTRA_ROLE);

        player1Name = (TextView) findViewById(R.id.player1_name);
        player2Name = (TextView) findViewById(R.id.player2_name);
        player1Image = (ImageView) findViewById(R.id.player1_image);
        player2Image = (ImageView) findViewById(R.id.player2_image);
        extraIcon = (ImageView) findViewById(R.id.extra_icon);

        if(role.equals("host")) {
            player1Name.setText(UserSingleton.getInstance().getUserModel().getName());
            int resourceId = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
            player1Image.setImageDrawable(getResources().getDrawable(resourceId));

            loadJSON();

        } else if(role.equals("guest")) {
            roomFull = true;

            String name1 = getIntent().getStringExtra("name1");
            String image1 = getIntent().getStringExtra("image1");
            rivalFirebaseId = getIntent().getStringExtra("firebase_id");
            String extra = getIntent().getStringExtra("extra");

            player1Name.setText(name1);
            int resourceId1 = getResources().getIdentifier(image1, "drawable", getPackageName());
            player1Image.setImageDrawable(getResources().getDrawable(resourceId1));

            player2Name.setText(UserSingleton.getInstance().getUserModel().getName());
            int resourceId2 = getResources().getIdentifier(UserSingleton.getInstance().getUserModel().getImage(), "drawable", getPackageName());
            player2Image.setImageDrawable(getResources().getDrawable(resourceId2));

            if(extra.equals("yes")) {
                extraPoints = true;
                extraIcon.setVisibility(View.VISIBLE);
            }
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {

                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if(intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    if(role.equals("host")) {
                        switch (message.split(";")[0]){
                            case "hello" :
                                if(roomFull) {
                                    RestInterface restInterface = RetrofitClient.getInstance();
                                    Call<Void> request = restInterface.sendMessage(message.split(";")[3], "full" + ";" + "0");
                                    request.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                } else {
                                    roomFull = true;
                                    int roomId = getIntent().getIntExtra(EXTRA_POSITION, 0);
                                    rivalFirebaseId = message.split(";")[3];

                                    player2Name.setText(message.split(";")[1]);
                                    int resourceId2 = getResources().getIdentifier(message.split(";")[2], "drawable", getPackageName());
                                    player2Image.setImageDrawable(getResources().getDrawable(resourceId2));

                                    String extra = "no";
                                    if(extraPoints) {
                                        extra = "yes";
                                    }

                                    RestInterface restInterface = RetrofitClient.getInstance();
                                    Call<Void> request = restInterface.sendMessage(rivalFirebaseId, "come" + ";" +  String.valueOf(roomId) + ";" + firebaseId + ";" + extra);
                                    request.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            findViewById(R.id.animation_view).setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                }

                                break;

                            case "adorn" :
                                if(!extraPoints) {
                                    extraPoints = true;
                                    extraIcon.setVisibility(View.VISIBLE);
                                }
                                break;

                            case "disconnect" :
                                player2Name.setText(getResources().getString(R.string.waiting_player));
                                player2Image.setImageResource(R.drawable.neutral_user);
                                roomFull = false;
                                rivalFirebaseId = "";
                                findViewById(R.id.animation_view).setVisibility(View.INVISIBLE);
                                break;
                        }
                    } else if(role.equals("guest")) {
                        switch (message.split(";")[0]) {
                            case "start" :
                                //--------------Aqui se inicia el juego--------------------
                                int roomId = getIntent().getIntExtra(EXTRA_POSITION, 0);
                                Intent i = new Intent(CreateRoomActivity.this, MultiPlayActivity.class);
                                i.putExtra(EXTRA_POSITION, roomId);
                                i.putExtra(RIVAL_ID, message.split(";")[1]);
                                i.putExtra(QUIZZES, message.split(";")[2]);
                                i.putExtra(EXTRA_POINTS, extraPoints);
                                finish();
                                startActivity(i);
                                //--------------Aqui se inicia el juego--------------------
                                break;
                            case "disconnect" :
                                Toast.makeText(context, R.string.player1_leave, Toast.LENGTH_SHORT).show();
                                finish();
                        }
                    }
                }
            }
        };

    }

    private void loadJSON() {
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<List<QuizModel>> quizRequest = restInterface.getRandomQuizzes(Integer.parseInt(UserSingleton.getInstance().getUserModel().getClassId()));
        quizRequest.enqueue(new Callback<List<QuizModel>>() {
            @Override
            public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                quizzes = response.body();
                updateQuizzes();
            }

            @Override
            public void onFailure(Call<List<QuizModel>> call, Throwable t) {

            }
        });
    }

    private void updateQuizzes() {
        quizString = "";
        for(int i = 0; i < quizzes.size() - 1; i++) {
            quizString = quizString + quizzes.get(i).getId() + ",";
        }
        quizString = quizString + quizzes.get(quizzes.size()-1).getId();

        int id = getIntent().getIntExtra(EXTRA_POSITION, 0);

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> updateQuizRequest = restInterface.updateQuizzes(quizString, id);
        updateQuizRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        infoDialog();
        return true;
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.leave_room);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                disconnect();
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
        if(role.equals("host")) {
            textView1.setText(R.string.leave_room_dialog);
        } else {
            textView1.setText(R.string.leave_room_dialog2);
        }
        builder.setView(dialogView);
        builder.show();
    }

    public void play(View v) {

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> messageRequest = restInterface.sendMessage(rivalFirebaseId, "start;" + firebaseId + ";" + quizString);
        messageRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int roomId = getIntent().getIntExtra(EXTRA_POSITION, 0);
                Intent i = new Intent(CreateRoomActivity.this, MultiPlayActivity.class);
                i.putExtra(EXTRA_POSITION, roomId);
                i.putExtra(RIVAL_ID, rivalFirebaseId);
                i.putExtra(QUIZZES, quizString);
                i.putExtra(EXTRA_POINTS, extraPoints);
                finish();
                startActivity(i);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void disconnect() {
        int roomId = getIntent().getIntExtra(EXTRA_POSITION, 0);
        switch (role) {
            case "host" :
                if(roomFull) {
                    RestInterface restInterface = RetrofitClient.getInstance();
                    Call<Void> deleteRequest = restInterface.deleteRoom(roomId);
                    Call<Void> messageRequest = restInterface.sendMessage(rivalFirebaseId, "disconnect;0");

                    messageRequest.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) { }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                    deleteRequest.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                } else {
                    RestInterface restInterface = RetrofitClient.getInstance();
                    Call<Void> request = restInterface.deleteRoom(roomId);
                    request.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("Error",t.getMessage());
                        }
                    });

                }

                break;

            case "guest" :
                RestInterface restInterface = RetrofitClient.getInstance();
                Call<Void> leaveRequest = restInterface.leaveRoom(roomId);
                Call<Void> messageRequest = restInterface.sendMessage(rivalFirebaseId, "disconnect;0");

                messageRequest.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

                leaveRequest.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    public String getFirebaseID() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        return regId;
    }
}
