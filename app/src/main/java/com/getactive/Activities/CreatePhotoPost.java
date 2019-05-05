package com.getactive.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.getactive.Migration.UserMigration;
import com.getactive.Model.PostModel;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class CreatePhotoPost extends AppCompatActivity {

    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private UserMigration userMigration;
    private Uri postImageUri = null;
    private ProgressDialog pd;
    private ImageView backbtn;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userMigration=new UserMigration();
        current_user_id = userMigration.getUserId();

        backbtn=findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        getSupportActionBar().setTitle("Add New Post");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(CreatePhotoPost.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String title = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(title) && postImageUri != null){

                    showPd();

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(CreatePhotoPost.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final StorageReference  ref=storageReference.child("post_images").child(randomName + ".jpg");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    UploadTask uploadTask = ref.putBytes(imageData);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url=task.getResult().toString();
                                    addDataToFirestore(title,url);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Config.toastShort(ApplicationContextProvider.getContext(), "Error "+e.getMessage());
                                }
                            });
                        }
                    }

        });
    }

    private void addDataToFirestore(String title,String downloadUri) {

        Date c = Calendar.getInstance().getTime();

//                                        Map<String, Object> postMap = new HashMap<>();
//                                        postMap.put("image_url", downloadUri);
//                                        postMap.put("image_thumb", downloadthumbUri);
//                                        postMap.put("title", title);
//                                        postMap.put("desc","");
//                                        postMap.put("user_id", current_user_id);
//                                        postMap.put("timestamp", FieldValue.serverTimestamp());

        PostModel model=new PostModel(current_user_id,title,c,downloadUri,"");

        firebaseFirestore.collection("Posts").add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){

                    Config.toastShort(ApplicationContextProvider.getContext(),Config.POST_MSG);
                    Intent mainIntent = new Intent(CreatePhotoPost.this, AllPostsActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {
                    Config.toastShort(ApplicationContextProvider.getContext(),Config.ERROR_GENERAL  );
                }

                pd.dismiss();

            }
        });


    }

    private void showPd() {

        pd=new ProgressDialog(CreatePhotoPost.this);
        pd.setMessage("Please Wait for a Sec");
        pd.setCancelable(false);
        pd.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(pd.isShowing())
                            Config.toastShort(ApplicationContextProvider.getContext(), Config.ERROR_TOAST);
                        pd.dismiss();
                    }
                }, 20000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,AllPostsActivity.class));
        finish();

    }

}