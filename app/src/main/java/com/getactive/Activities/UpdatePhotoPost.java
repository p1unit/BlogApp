package com.getactive.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getactive.Model.PostModel;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePhotoPost extends AppCompatActivity {

    private PostModel model;
    private String postId;
    private EditText title;
    private ImageView image;
    private Button update_btn;
    private ProgressDialog pd;
    private FirebaseFirestore firebaseFirestore;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo_post);

        model=(PostModel) getIntent().getSerializableExtra("model");
        postId=getIntent().getStringExtra("postid");

        title=findViewById(R.id.update_post_title);
        image=findViewById(R.id.update_post_image);
        update_btn=findViewById(R.id.update_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();

        backbtn=findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setData();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatePost();

            }
        });
    }

    private void updatePost() {

        if(!TextUtils.isEmpty(title.getText().toString())){
            showPd();
        }else{
            Config.toastShort(ApplicationContextProvider.getContext(), "Please Fill All the Fields");
            return;
        }

        model.setTitle(title.getText().toString());

        firebaseFirestore.collection("Posts").document(postId)
                .set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Config.toastShort(ApplicationContextProvider.getContext(), "Post successfully Updated!");
                        gotoPrevoius();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Config.toastShort(ApplicationContextProvider.getContext(), "Post Updated Failed!");
                        pd.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,AllPostsActivity.class));
        finish();

    }

    private void gotoPrevoius() {
        startActivity(new Intent(this,AllPostsActivity.class));
        finish();
    }

    private void showPd() {

        pd=new ProgressDialog(UpdatePhotoPost.this);
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

    private void setData() {

        title.setText(model.getTitle());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.temp);
        requestOptions.error(R.drawable.temp);

        Glide.with(ApplicationContextProvider.getContext()).load(model.getImage_url())
                .apply(requestOptions).thumbnail(0.5f).into(image);
    }
}
