package com.javier.edukka.editor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.UploadObject;
import com.squareup.picasso.Picasso;

import java.io.File;
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

public class DragNameEditorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String EXTRA_POSITION = "position";
    private String IMAGES_URL = "http://localhost/edukka/images/";
    final int SEARCH_IMAGE = 10;
    final int UPLOAD_IMAGE = 12;
    private int id;
    private int selected = 0;

    private static final String TAG = DragNameEditorActivity.class.getSimpleName();

    private EditText question, edit_image1, edit_image2, edit_image3;
    private ImageButton image1, image2, image3;
    private Button save;

    private boolean loaded1 = false, loaded2 = false, loaded3 = false;
    private Uri  path = null, image1_path = null, image2_path = null, image3_path = null;
    private QuizModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_name_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        edit_image1 = (EditText) findViewById(R.id.edit_image1);
        edit_image2 = (EditText) findViewById(R.id.edit_image2);
        edit_image3 = (EditText) findViewById(R.id.edit_image3);
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
                } else if (edit_image1.getText().hashCode() == editable.hashCode() && edit_image1.getText().toString().equals("")) {
                    edit_image1.setError(getText(R.string.empty));
                } else if (edit_image2.getText().hashCode() == editable.hashCode() && edit_image2.getText().toString().equals("")) {
                    edit_image2.setError(getText(R.string.empty));
                } else if (edit_image3.getText().hashCode() == editable.hashCode() && edit_image3.getText().toString().equals("")) {
                    edit_image3.setError(getText(R.string.empty));
                }
            }
        };
        question.addTextChangedListener(watcher);
        edit_image1.addTextChangedListener(watcher);
        edit_image2.addTextChangedListener(watcher);
        edit_image3.addTextChangedListener(watcher);

        image1 = (ImageButton) findViewById(R.id.image1);
        image2 = (ImageButton) findViewById(R.id.image2);
        image3 = (ImageButton) findViewById(R.id.image3);

        loadJSON();
    }

    public void loadJSON() {
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                question.setText(model.getQuestion());
                if(!model.getOptions().equals("")) {
                    edit_image1.setText(model.getAnswer().split(", ")[0]);
                    edit_image2.setText(model.getAnswer().split(", ")[1]);
                    edit_image3.setText(model.getAnswer().split(", ")[2]);

                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[1].split(",")[0]).into(image1);
                    loaded1 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[1].split(",")[1]).into(image2);
                    loaded2 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[1].split(",")[2]).into(image3);
                    loaded3 = true;
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragNameEditorActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkFieldValidation() {
        boolean valid = true;
        if (question.getText().toString().equals("")) {
            question.setError(getText(R.string.empty));
            valid = false;
        }
        if(edit_image1.getText().toString().equals("")) {
            edit_image1.setError(getText(R.string.empty));
            valid = false;
        }
        if(edit_image2.getText().toString().equals("")) {
            edit_image2.setError(getText(R.string.empty));
            valid = false;
        }
        if(edit_image3.getText().toString().equals("")) {
            edit_image3.setError(getText(R.string.empty));
            valid = false;
        }
        if(!loaded1 || !loaded2 || !loaded3 ) {
            Toast.makeText(DragNameEditorActivity.this, R.string.need_3_images_error, Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }


    public void image1Click(View view) {
        selected = 1;
        addImage(image1, loaded1);
    }

    public void image2Click(View view) {
        selected = 2;
        addImage(image2, loaded2);
    }

    public void image3Click(View view) {
        selected = 3;
        addImage(image3, loaded3);
    }


    private void addImage(ImageButton image, boolean loaded) {
        if(loaded) {
            image.setImageResource(R.drawable.add_photo_icon);
            switch(selected){
                case 1:
                    loaded1 = false;
                    image1_path = null;
                    break;
                case 2:
                    loaded2 = false;
                    image2_path = null;
                    break;
                case 3:
                    loaded3 = false;
                    image3_path = null;
                    break;
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, getString(R.string.select_application)), SEARCH_IMAGE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_IMAGE) {
            if (resultCode == RESULT_OK) {
                path = data.getData();
                switch (selected) {
                    case 1:
                        image1.setImageURI(path);
                        image1_path = path;
                        loaded1 = true;
                        break;
                    case 2:
                        image2.setImageURI(path);
                        image2_path = path;
                        loaded2 = true;
                        break;
                    case 3:
                        image3.setImageURI(path);
                        image3_path = path;
                        loaded3 = true;
                        break;
                }
            } else {
                path = null;
                Toast.makeText(DragNameEditorActivity.this, R.string.image_error, Toast.LENGTH_LONG).show();
                switch (selected) {
                    case 1:
                        image1.setImageResource(R.drawable.add_photo_icon);
                        image1_path = null;
                        loaded1 = false;
                        break;
                    case 2:
                        image2.setImageResource(R.drawable.add_photo_icon);
                        image2_path = null;
                        loaded2 = false;
                        break;
                    case 3:
                        image3.setImageResource(R.drawable.add_photo_icon);
                        image2_path = null;
                        loaded3 = false;
                        break;
                }
            }
        }
    }

    public void saveDragName(View view) {
        if(checkFieldValidation()) {
            if(EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
               save();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.get_permises), UPLOAD_IMAGE, READ_EXTERNAL_STORAGE);
            }
        }
    }

    public void save() {
        String url1 = uploadImage(image1_path, 1);
        String url2 = uploadImage(image2_path, 2);
        String url3 = uploadImage(image3_path, 3);
        String answer = edit_image1.getText().toString() + ", " + edit_image2.getText().toString() + ", " + edit_image3.getText().toString();
        List<String> sortedOptions = Arrays.asList(answer.split(", "));
        Random rnd = new Random();
        rnd.setSeed(1000);
        Collections.shuffle(sortedOptions, rnd);
        String options = sortedOptions.get(0)+"," + sortedOptions.get(1) + "," + sortedOptions.get(2) + ";"
                + url1 + "," + url2 + "," + url3;

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), answer, options, id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(DragNameEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragNameEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public String uploadImage(Uri path, int position) {
        String res = "";
        if(path != null) {
            String filePath = getRealPathFromURIPath(path, DragNameEditorActivity.this);
            File file = new File(filePath);
            Log.d(TAG, "Filename " + file.getName());
            res = IMAGES_URL + file.getName();                                          //--------Cambiarlo a la direccion final
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RestInterface restInterface = RetrofitClient.getInstance();
            Call<UploadObject> fileUpload = restInterface.postImage(fileToUpload, filename);
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
            res = model.getOptions().split(";")[1].split(",")[position - 1];
        }
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DragNameEditorActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        save();
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, getString(R.string.get_permises_failed));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
