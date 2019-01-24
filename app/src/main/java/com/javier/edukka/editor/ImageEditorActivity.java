package com.javier.edukka.editor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.javier.edukka.service.HelperClient;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.UploadObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
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

public class ImageEditorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String EXTRA_POSITION = "position";
    private String IMAGES_URL = "images/";
    final int SEARCH_IMAGE = 10;
    final int UPLOAD_IMAGE = 12;
    private int id;
    private int selected = 0;
    private List<String> sortedOptions;
    private String answer_path = "";

    private static final String TAG = DragNameEditorActivity.class.getSimpleName();

    private EditText question, edit_image1, edit_image2, edit_image3, edit_image_answer;
    private ImageButton image1, image2, image3, image_answer;
    private Button save;

    private boolean loaded1 = false, loaded2 = false, loaded3 = false, loaded_answer = false;
    private Uri path = null, image1_path = null, image2_path = null, image3_path = null, image_answer_path = null;
    private QuizModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        edit_image1 = (EditText) findViewById(R.id.edit_image1);
        edit_image2 = (EditText) findViewById(R.id.edit_image2);
        edit_image3 = (EditText) findViewById(R.id.edit_image3);
        edit_image_answer = (EditText) findViewById(R.id.edit_image_answer);
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
                } else if(edit_image_answer.getText().hashCode() == editable.hashCode() && edit_image_answer.getText().toString().equals("")) {
                    edit_image_answer.setError(getText(R.string.empty));
                }
            }
        };

        question.addTextChangedListener(watcher);
        edit_image1.addTextChangedListener(watcher);
        edit_image2.addTextChangedListener(watcher);
        edit_image3.addTextChangedListener(watcher);
        edit_image_answer.addTextChangedListener(watcher);

        image1 = (ImageButton) findViewById(R.id.image1);
        image2 = (ImageButton) findViewById(R.id.image2);
        image3 = (ImageButton) findViewById(R.id.image3);
        image_answer = (ImageButton) findViewById(R.id.image_answer);

        IMAGES_URL = HelperClient.getBaseURL() + IMAGES_URL;

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
                question.setText(model.getQuestion());
                edit_image_answer.setText(model.getAnswer());
                if(!model.getOptions().equals("")) {
                    List<String> orderedOptions = sortOptions(model.getOptions(), model.getAnswer());

                    edit_image1.setText(orderedOptions.get(0).split(",")[0]);
                    edit_image2.setText(orderedOptions.get(1).split(",")[0]);
                    edit_image3.setText(orderedOptions.get(2).split(",")[0]);

                    Picasso.with(getApplicationContext()).load(orderedOptions.get(0).split(",")[1]).into(image1);
                    loaded1 = true;
                    Picasso.with(getApplicationContext()).load(orderedOptions.get(1).split(",")[1]).into(image2);
                    loaded2 = true;
                    Picasso.with(getApplicationContext()).load(orderedOptions.get(2).split(",")[1]).into(image3);
                    loaded3 = true;
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(ImageEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> sortOptions(String options, String answer) {
        sortedOptions = new ArrayList<>();
        edit_image_answer.setText(answer);

        if(options.split(";")[0].split(",")[0].equals(answer)) {
            answer_path = options.split(";")[0].split(",")[1];
            Picasso.with(getApplicationContext()).load(answer_path).into(image_answer);
            loaded_answer = true;

            sortedOptions.add(options.split(";")[1]);
            sortedOptions.add(options.split(";")[2]);
            sortedOptions.add(options.split(";")[3]);

        } else if (options.split(";")[1].split(",")[0].equals(answer)) {
            answer_path = options.split(";")[1].split(",")[1];
            Picasso.with(getApplicationContext()).load(answer_path).into(image_answer);
            loaded_answer = true;

            sortedOptions.add(options.split(";")[0]);
            sortedOptions.add(options.split(";")[2]);
            sortedOptions.add(options.split(";")[3]);

        } else if (options.split(";")[2].split(",")[0].equals(answer)) {
            answer_path = options.split(";")[2].split(",")[1];
            Picasso.with(getApplicationContext()).load(answer_path).into(image_answer);
            loaded_answer = true;

            sortedOptions.add(options.split(";")[0]);
            sortedOptions.add(options.split(";")[1]);
            sortedOptions.add(options.split(";")[3]);

        } else {
            answer_path = options.split(";")[3].split(",")[1];
            Picasso.with(getApplicationContext()).load(answer_path).into(image_answer);
            loaded_answer = true;

            sortedOptions.add(options.split(";")[0]);
            sortedOptions.add(options.split(";")[1]);
            sortedOptions.add(options.split(";")[2]);
        }

        return sortedOptions;
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
        if(edit_image_answer.getText().toString().equals("")) {
            edit_image_answer.setError(getText(R.string.empty));
            valid = false;
        }
        if(!loaded1 || !loaded2 || !loaded3 || !loaded_answer ) {
            Toast.makeText(ImageEditorActivity.this, R.string.need_3_images_error, Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    public void imageClick(View view) {
        switch (view.getId()) {
            case R.id.image_answer:
                selected = 0;
                addImage(image_answer, loaded_answer);
                break;
            case R.id.image1:
                selected = 1;
                addImage(image1, loaded1);
                break;
            case R.id.image2:
                selected = 2;
                addImage(image2, loaded2);
                break;
            case R.id.image3:
                selected = 3;
                addImage(image3, loaded3);
                break;
        }
    }

    private void addImage(ImageButton image, boolean loaded) {
        if(loaded) {
            image.setImageResource(R.drawable.add_photo_icon);
            switch(selected){
                case 0:
                    loaded_answer = false;
                    image_answer_path = null;
                    break;
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
                    case 0:
                        image_answer.setImageURI(path);
                        image_answer_path = path;
                        loaded_answer = true;
                        break;
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
                Toast.makeText(ImageEditorActivity.this, R.string.image_error, Toast.LENGTH_LONG).show();
                switch (selected) {
                    case 0:
                        image_answer.setImageResource(R.drawable.add_photo_icon);
                        image_answer_path = null;
                        loaded_answer = false;
                        break;
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

    public void saveImage(View view) {
        if(checkFieldValidation()) {
            if(EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
                save();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.get_permises), UPLOAD_IMAGE, READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void save() {
        String url_answer = uploadImage(image_answer_path, 0);
        String url1 = uploadImage(image1_path, 1);
        String url2 = uploadImage(image2_path, 2);
        String url3 = uploadImage(image3_path, 3);

        List<String> listOptions = new ArrayList<>();
        listOptions.add(edit_image_answer.getText().toString() + "," + url_answer);
        listOptions.add(edit_image1.getText().toString() + "," + url1);
        listOptions.add(edit_image2.getText().toString() + "," + url2);
        listOptions.add(edit_image3.getText().toString() + "," + url3);
        Random rnd = new Random(1000);
        Collections.shuffle(listOptions, rnd);
        String finalOptions = listOptions.get(0) + ";" + listOptions.get(1) + ";" + listOptions.get(2) + ";" + listOptions.get(3);
        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), edit_image_answer.getText().toString(), finalOptions, id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(ImageEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(ImageEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
            String filePath = getRealPathFromURIPath(path, ImageEditorActivity.this);
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
            if(position == 0) {
                res = answer_path;
            } else {
                res = sortedOptions.get(position - 1);
            }
        }
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ImageEditorActivity.this);
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
                Toast.makeText(ImageEditorActivity.this, R.string.deletequiz_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}
