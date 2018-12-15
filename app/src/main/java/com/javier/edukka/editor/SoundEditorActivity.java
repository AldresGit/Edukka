package com.javier.edukka.editor;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.UploadObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;

public class SoundEditorActivity extends AppCompatActivity  implements MediaPlayer.OnCompletionListener, EasyPermissions.PermissionCallbacks {
    private String EXTRA_POSITION = "position";
    private String SOUNDS_URL = "http://docs.google.com/uc?export=download&id=";
    private final int RECORD_SOUND = 10;
    private final int UPLOAD_SOUND = 12;
    private int id;
    private TextView sound_label;
    private EditText question, edit_option1, edit_option2, edit_option3, edit_option_answer;
    private ImageButton play, record, stop;
    private Button save;

    private static final String TAG = DragNameEditorActivity.class.getSimpleName();

    private boolean loaded = false;
    private String url = "";
    private QuizModel model;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sound_label = (TextView) findViewById(R.id.sound_label);
        question = (EditText) findViewById(R.id.question);
        edit_option1 = (EditText) findViewById(R.id.edit_option1);
        edit_option2 = (EditText) findViewById(R.id.edit_option2);
        edit_option3 = (EditText) findViewById(R.id.edit_option3);
        edit_option_answer = (EditText) findViewById(R.id.edit_answer);
        save = (Button) findViewById(R.id.save);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (question.getText().hashCode() == editable.hashCode() && question.getText().toString().equals("")) {
                    question.setError(getText(R.string.empty));
                } else if (edit_option1.getText().hashCode() == editable.hashCode() && edit_option1.getText().toString().equals("")) {
                    edit_option1.setError(getText(R.string.empty));
                } else if (edit_option2.getText().hashCode() == editable.hashCode() && edit_option2.getText().toString().equals("")) {
                    edit_option2.setError(getText(R.string.empty));
                } else if (edit_option3.getText().hashCode() == editable.hashCode() && edit_option3.getText().toString().equals("")) {
                    edit_option3.setError(getText(R.string.empty));
                } else if(edit_option_answer.getText().hashCode() == editable.hashCode() && edit_option_answer.getText().toString().equals("")) {
                    edit_option_answer.setError(getText(R.string.empty));
                }
            }
        };

        question.addTextChangedListener(watcher);
        edit_option1.addTextChangedListener(watcher);
        edit_option2.addTextChangedListener(watcher);
        edit_option3.addTextChangedListener(watcher);
        edit_option_answer.addTextChangedListener(watcher);

        play = (ImageButton) findViewById(R.id.play);
        record = (ImageButton) findViewById(R.id.record);
        stop = (ImageButton) findViewById(R.id.stop);

        play.setEnabled(false);
        play.setAlpha((float) 0.5);

        loadJSON();
    }

    private void loadJSON() {
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(!model.getQuestion().equals("")) {
                    url = SOUNDS_URL + model.getQuestion().split(",")[1];
                    try {
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepare();
                        play.setEnabled(true);
                        play.setAlpha((float) 1);
                        loaded = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SoundEditorActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    question.setText(model.getQuestion().split(",")[0]);
                }
                if(!model.getOptions().equals("")) {
                    List<String> options = Arrays.asList(model.getOptions().split(","));
                    for(int i = 0; i < options.size(); i++) {
                        if(model.getAnswer().equals(options.get(i))) {
                            options.remove(i);
                            i = options.size();
                        }
                    }
                    edit_option_answer.setText(model.getAnswer());
                    edit_option1.setText(options.get(0));
                    edit_option2.setText(options.get(1));
                    edit_option3.setText(options.get(2));
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {

            }
        });
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (question.getText().toString().equals("")) {
            question.setError(getText(R.string.empty));
            valid = false;
        }
        if (question.getText().toString().contains(",")) {
            question.setError(getText(R.string.invalid_coma_character));
            valid = false;
        }
        if (edit_option_answer.getText().toString().equals("")) {
            edit_option_answer.setError(getText(R.string.empty));
            valid = false;
        }
        if (edit_option1.getText().toString().equals("")) {
            edit_option1.setError(getText(R.string.empty));
            valid = false;
        }
        if (edit_option2.getText().toString().equals("")) {
            edit_option2.setError(getText(R.string.empty));
            valid = false;
        }
        if (edit_option3.getText().toString().equals("")) {
            edit_option3.setError(getText(R.string.empty));
            valid = false;
        }
        return valid;
    }

    public void play(View view) {
        play.setEnabled(false);
        play.setAlpha((float) 0.5);
        record.setEnabled(false);
        record.setAlpha((float) 0.5);
        mediaPlayer.start();
    }

    public void record(View view) {
        if(EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            File path = new File(Environment.getExternalStorageDirectory()
                    .getPath());
            try {
                String prefix = "Game_" + model.getGameId() + "_Quiz_" + model.getId();
                file = File.createTempFile(prefix, ".3gp", path);
            } catch (IOException e) { e.printStackTrace(); }

            try {
                mediaRecorder.prepare();
            } catch (IOException e) { e.printStackTrace(); }

            record.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            play.setEnabled(false);
            play.setAlpha((float) 0.5);
            sound_label.setText(getString(R.string.sound_recording));
            sound_label.setTextColor(getResources().getColor(R.color.colorMaths));
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.get_permises), RECORD_SOUND, RECORD_AUDIO);
        }

    }

    public void stop(View view) {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
        } catch (IOException e) { }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) { }
        stop.setVisibility(View.GONE);
        record.setVisibility(View.VISIBLE);
        play.setEnabled(true);
        play.setAlpha((float) 1);
        sound_label.setText(getString(R.string.sound_edit));
        sound_label.setTextColor(getResources().getColor(R.color.colorPrimary));
        loaded = true;
    }

    public void save(View view) {
        if(checkFieldValidation()) {
            String url = uploadSound();
            String answer = edit_option_answer.getText().toString();
            List<String> options = new ArrayList<>();
            options.add(answer);
            options.add(edit_option1.getText().toString());
            options.add(edit_option2.getText().toString());
            options.add(edit_option3.getText().toString());
            Random rnd = new Random(1000);
            Collections.shuffle(options, rnd);
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<QuizModel> request = restInterface.updateQuiz(answer+url, answer, options.toString().substring(2,2), id);
            request.enqueue(new Callback<QuizModel>() {
                @Override
                public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                    Toast.makeText(SoundEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<QuizModel> call, Throwable t) {
                    Toast.makeText(SoundEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }

    private String uploadSound() {
        String res = "";
        if(file != null) {
            Log.d(TAG, "Filename " + file.getName());
            res = SOUNDS_URL + file.getName();
            RequestBody mFile = RequestBody.create(MediaType.parse("sound/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<UploadObject> fileUpload = restInterface.postSound(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {

                }

                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "Error " + t.getMessage());
                }
            });
        } else {
            res = SOUNDS_URL + model.getQuestion().split(",")[1];
        }
        return res;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play.setEnabled(true);
        play.setAlpha((float) 1);
        record.setEnabled(true);
        record.setAlpha((float) 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, SoundEditorActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, getString(R.string.get_permises_failed));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                infoDialog();
                return true;
            default:
                finish();
                return true;
        }
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.deletequiz);
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                setContentView(R.layout.progressbar_layout);
                deleteQuiz();
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
        textView1.setText(R.string.dialogquiz);
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteQuiz() {
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<Void> deleteRequest = restInterface.deleteQuiz(id);
        deleteRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finish();
                Toast.makeText(SoundEditorActivity.this, R.string.deletequiz_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}
