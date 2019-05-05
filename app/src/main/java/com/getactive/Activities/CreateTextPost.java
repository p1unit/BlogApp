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
import android.widget.Toast;

import com.getactive.Migration.UserMigration;
import com.getactive.Model.PostModel;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateTextPost extends AppCompatActivity {

    private EditText newPostDesc;
    private EditText newPostTitle;
    private Button newPostBtn;
    private UserMigration userMigration;
    private ProgressDialog pd;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private String title,desc;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text_post);

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

        newPostTitle = findViewById(R.id.post_title);
        newPostDesc = findViewById(R.id.post_desc);
        newPostBtn = findViewById(R.id.post__btn);

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=newPostTitle.getText().toString();
                desc=newPostDesc.getText().toString();
                createPost();
            }
        });


    }

    private void createPost() {

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)){
            showPd();
        }else{
            Config.toastShort(ApplicationContextProvider.getContext(), "Please Fill All the Fields");
            return;
        }

        Date c = Calendar.getInstance().getTime();


//        Map<String, Object> postMap = new HashMap<>();
//        postMap.put("image_url", "");
//        postMap.put("image_thumb", "");
//        postMap.put("title", title);
//        postMap.put("desc", desc);
//        postMap.put("user_id", current_user_id);
//        postMap.put("timestamp", FieldValue.serverTimestamp());

        PostModel model=new PostModel(current_user_id,title,c,"",desc);

        firebaseFirestore.collection("Posts").add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {

                    Config.toastShort(ApplicationContextProvider.getContext(), Config.POST_MSG);
                    Intent mainIntent = new Intent(CreateTextPost.this, AllPostsActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {
                    Config.toastShort(ApplicationContextProvider.getContext(), Config.ERROR_GENERAL);
                }

                pd.dismiss();
            }
        });
    }

    private void showPd() {

        pd=new ProgressDialog(CreateTextPost.this);
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,AllPostsActivity.class));
        finish();

    }
}
