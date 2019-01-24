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
import android.support.v7.widget.AppCompatCheckBox;
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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class DragImageEditorActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String EXTRA_POSITION = "position";
    private String IMAGES_URL = "images/";
    final int SEARCH_IMAGE = 10;
    final int UPLOAD_IMAGE = 12;
    private int id;
    private int selected = 0;

    private static final String TAG = DragNameEditorActivity.class.getSimpleName();

    private EditText question, edit_image1, edit_image2, edit_image3,
        edit_image4, edit_image5, edit_image6;

    private ImageButton image1, image2, image3, image4, image5, image6;

    private AppCompatCheckBox check_correct1, check_correct2, check_correct3,
            check_correct4, check_correct5, check_correct6;

    private boolean check1 = false, check2 = false, check3 = false,
            check4 = false, check5 = false, check6 = false;

    private Button save;

    private boolean loaded1 = false, loaded2 = false, loaded3 = false,
        loaded4 = false, loaded5 = false, loaded6 = false;

    private Uri path = null, image1_path = null, image2_path = null, image3_path = null,
        image4_path = null, image5_path = null, image6_path = null;

    private QuizModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (EditText) findViewById(R.id.question);
        edit_image1 = (EditText) findViewById(R.id.edit_image1);
        edit_image2 = (EditText) findViewById(R.id.edit_image2);
        edit_image3 = (EditText) findViewById(R.id.edit_image3);
        edit_image4 = (EditText) findViewById(R.id.edit_image4);
        edit_image5 = (EditText) findViewById(R.id.edit_image5);
        edit_image6 = (EditText) findViewById(R.id.edit_image6);
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
                } else if (edit_image4.getText().hashCode() == editable.hashCode() && edit_image4.getText().toString().equals("")) {
                    edit_image4.setError(getText(R.string.empty));
                } else if (edit_image5.getText().hashCode() == editable.hashCode() && edit_image5.getText().toString().equals("")) {
                    edit_image5.setError(getText(R.string.empty));
                } else if (edit_image6.getText().hashCode() == editable.hashCode() && edit_image6.getText().toString().equals("")) {
                    edit_image6.setError(getText(R.string.empty));
                }
            }
        };
        question.addTextChangedListener(watcher);
        edit_image1.addTextChangedListener(watcher);
        edit_image2.addTextChangedListener(watcher);
        edit_image3.addTextChangedListener(watcher);
        edit_image4.addTextChangedListener(watcher);
        edit_image5.addTextChangedListener(watcher);
        edit_image6.addTextChangedListener(watcher);

        image1 = (ImageButton) findViewById(R.id.image1);
        image2 = (ImageButton) findViewById(R.id.image2);
        image3 = (ImageButton) findViewById(R.id.image3);
        image4 = (ImageButton) findViewById(R.id.image4);
        image5 = (ImageButton) findViewById(R.id.image5);
        image6 = (ImageButton) findViewById(R.id.image6);

        IMAGES_URL = HelperClient.getBaseURL() + IMAGES_URL;

        loadJSON();
    }

    private void loadJSON() {
        id = getIntent().getIntExtra(EXTRA_POSITION, 0);
        RestInterface restInterface = RetrofitClient.getInstance();
        final Call<QuizModel> request = restInterface.getQuiz(id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                model = response.body();
                question.setText(model.getQuestion());
                if(!model.getOptions().equals("")) {
                    edit_image1.setText(model.getOptions().split(";")[0].split(",")[0]);
                    edit_image2.setText(model.getOptions().split(";")[1].split(",")[0]);
                    edit_image3.setText(model.getOptions().split(";")[2].split(",")[0]);
                    edit_image4.setText(model.getOptions().split(";")[3].split(",")[0]);
                    edit_image5.setText(model.getOptions().split(";")[4].split(",")[0]);
                    edit_image6.setText(model.getOptions().split(";")[5].split(",")[0]);

                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[0].split(",")[1]).into(image1);
                    loaded1 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[1].split(",")[1]).into(image2);
                    loaded2 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[2].split(",")[1]).into(image3);
                    loaded3 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[3].split(",")[1]).into(image4);
                    loaded4 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[4].split(",")[1]).into(image5);
                    loaded5 = true;
                    Picasso.with(getApplicationContext()).load(model.getOptions().split(";")[5].split(",")[1]).into(image6);
                    loaded6 = true;
                }
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragImageEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        if(edit_image4.getText().toString().equals("")) {
            edit_image4.setError(getText(R.string.empty));
            valid = false;
        }
        if(edit_image5.getText().toString().equals("")) {
            edit_image5.setError(getText(R.string.empty));
            valid = false;
        }
        if(edit_image6.getText().toString().equals("")) {
            edit_image6.setError(getText(R.string.empty));
            valid = false;
        }
        if(!loaded1 || !loaded2 || !loaded3 || !loaded4 || !loaded5 || !loaded6) {
            Toast.makeText(DragImageEditorActivity.this, R.string.need_6_images_error, Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    public void imageClick(View view) {
        switch(view.getId()) {
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
            case R.id.image4:
                selected = 4;
                addImage(image4, loaded4);
                break;
            case R.id.image5:
                selected = 5;
                addImage(image5, loaded5);
                break;
            case R.id.image6:
                selected = 6;
                addImage(image6, loaded6);
                break;
        }
    }

    public void checkClick(View view) {
        boolean checked = ((AppCompatCheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.check_correct1:
                if(checked) {
                    check1 = true;
                } else {
                    check1 = false;
                }
            case R.id.check_correct2:
                if(checked) {
                    check2 = true;
                } else {
                    check2 = false;
                }
            case R.id.check_correct3:
                if(checked) {
                    check3 = true;
                } else {
                    check3 = false;
                }
            case R.id.check_correct4:
                if(checked) {
                    check4 = true;
                } else {
                    check4 = false;
                }
            case R.id.check_correct5:
                if(checked) {
                    check5 = true;
                } else {
                    check5 = false;
                }
            case R.id.check_correct6:
                if(checked) {
                    check6 = true;
                } else {
                    check6 = false;
                }
        }
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
                    case 4:
                        image4.setImageURI(path);
                        image4_path = path;
                        loaded4 = true;
                        break;
                    case 5:
                        image5.setImageURI(path);
                        image5_path = path;
                        loaded5 = true;
                        break;
                    case 6:
                        image6.setImageURI(path);
                        image6_path = path;
                        loaded6 = true;
                        break;
                }
            } else {
                path = null;
                Toast.makeText(DragImageEditorActivity.this, R.string.image_error, Toast.LENGTH_LONG).show();
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
                    case 4:
                        image4.setImageResource(R.drawable.add_photo_icon);
                        image4_path = null;
                        loaded4 = false;
                        break;
                    case 5:
                        image5.setImageResource(R.drawable.add_photo_icon);
                        image5_path = null;
                        loaded5 = false;
                        break;
                    case 6:
                        image6.setImageResource(R.drawable.add_photo_icon);
                        image6_path = null;
                        loaded6 = false;
                        break;
                }
            }
        }
    }

    public void saveDragImage(View view) {
        if(checkFieldValidation()) {
            if(EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
                save();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.get_permises), UPLOAD_IMAGE, READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void save() {
        String url1 = uploadImage(image1_path, 1);
        String url2 = uploadImage(image2_path, 2);
        String url3 = uploadImage(image3_path, 3);
        String url4 = uploadImage(image4_path, 4);
        String url5 = uploadImage(image5_path, 5);
        String url6 = uploadImage(image6_path, 6);

        String answer = fillAnswer();

        String options  =   edit_image1.getText().toString() + "," + url1+ ";" + edit_image2.getText().toString() + "," + url2+ ";" +
                            edit_image3.getText().toString() + "," + url3+ ";" + edit_image4.getText().toString() + "," + url4+ ";" +
                            edit_image5.getText().toString() + "," + url5+ ";" + edit_image6.getText().toString() + "," + url6+ ";";

        RestInterface restInterface = RetrofitClient.getInstance();
        Call<QuizModel> request = restInterface.updateQuiz(question.getText().toString(), answer, options, id);
        request.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
                finish();
                startActivity(getIntent());
                Toast.makeText(DragImageEditorActivity.this, R.string.quiz_saved, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {
                Toast.makeText(DragImageEditorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public String fillAnswer() {
        String res = "";
        if(check1)
            res = res + edit_image1.getText().toString() + ", ";
        if(check2)
            res = res + edit_image2.getText().toString() + ", ";
        if(check3)
            res = res + edit_image3.getText().toString() + ", ";
        if(check4)
            res = res + edit_image4.getText().toString() + ", ";
        if(check5)
            res = res + edit_image5.getText().toString() + ", ";
        if(check6)
            res = res + edit_image6.getText().toString() + ", ";
        if(!res.equals(""))
            res = res.substring(0, 2);
        return res;
    }

    public String uploadImage(Uri path, int position) {
        String res = "";
        if(path != null) {
            String filePath = getRealPathFromURIPath(path, DragImageEditorActivity.this);
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
            res = model.getOptions().split(";")[position - 1].split(",")[1];
        }
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DragImageEditorActivity.this);
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
                Toast.makeText(DragImageEditorActivity.this, R.string.deletequiz_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
}
