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
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.service.UploadObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class CompleteEditorActivity extends AppCompatActivity  implements EasyPermissions.PermissionCallbacks{
    private String EXTRA_POSITION = "position";
    private String IMAGES_URL = "http://192.168.1.42/edukka/images/";
    final int SEARCH_IMAGE = 10;
    final int UPLOAD_IMAGE = 12;
    private int id;

    private static final String TAG = DragNameEditorActivity.class.getSimpleName();

    private EditText question;
    private ImageButton image;
    private Button save;

    private boolean loaded = false;
    private Uri path = null;
    private QuizModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        image = (ImageButton) findViewById(R.id.image);
        save = (Button) findViewById(R.id.save);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (question.getText().hashCode() == editable.hashCode() && question.getText().toString().equals("")) {
                    question.setError(getText(R.string.empty));
                }
            }
        };
        question.addTextChangedListener(watcher);

        loadJSON();

    }

    public void loadJSON() {
        RestInterface restInterface = RetrofitClient.getInstance();
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                question.setText(model.getAnswer());
                if (!model.getQuestion().equals("")) {
                    Picasso.with(getApplicationContext()).load(model.getQuestion().split(",")[1]).into(image);
                    loaded = true;
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(CompleteEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        return valid;
    }

    public void clickImage(View view) {
        addImage(image, loaded);
    }

    private void addImage(ImageButton image, boolean loaded) {
        if (loaded) {
            image.setImageResource(R.drawable.add_photo_icon);
            this.loaded = false;
            path = null;
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, getString(R.string.select_application)), SEARCH_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_IMAGE) {
            if (resultCode == RESULT_OK) {
                path = data.getData();
                image.setImageURI(path);
                loaded = true;
            } else {
                path = null;
                Toast.makeText(CompleteEditorActivity.this, R.string.image_error, Toast.LENGTH_LONG).show();
                image.setImageResource(R.drawable.add_photo_icon);
                loaded = false;
            }
        }
    }

    public void saveComplete(View view) {
        if(checkFieldValidation()) {
            if(EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
                save();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.get_permises), UPLOAD_IMAGE, READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void save() {
        String url = uploadImage(path);
        String answer = question.getText().toString();
        String finalQuestion = answer + "," + url;

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(finalQuestion, answer, "", id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                Toast.makeText(CompleteEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(CompleteEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public String uploadImage(Uri path) {
        String res = "";
        if(path != null) {
            String filePath = getRealPathFromURIPath(path, CompleteEditorActivity.this);
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
            res = model.getQuestion().split(",")[1];
        }
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, CompleteEditorActivity.this);
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
                Toast.makeText(CompleteEditorActivity.this, R.string.deletequiz_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}