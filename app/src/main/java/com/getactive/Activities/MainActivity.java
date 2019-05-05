package com.getactive.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.getactive.Migration.UserMigration;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;

public class MainActivity extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 100;
    private EditText username;
    private Button add_user;
    private UserMigration userMigration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermissionForReadExtertalStorage()){
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
                Config.toastShort(this,Config.ERROR_GENERAL);
                finish();
            }
        }

        userMigration=new UserMigration();

        if(!userMigration.getUserId().equals("")) {
            startActivity(new Intent(this, AllPostsActivity.class));
            finish();
        }

        username = findViewById(R.id.username);
        add_user = findViewById(R.id.add_username);

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkPermissionForReadExtertalStorage()){
                    Config.toastShort(ApplicationContextProvider.getContext(),"Permissions Missing");
                }
                addUser();
            }
        });


    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Config.toastShort(ApplicationContextProvider.getContext(),e.toString());
            throw e;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void addUser() {

        if(TextUtils.isEmpty(username.getText())){
            Config.toastLong(this,"Enter the userName");
            return;
        }

        userMigration.setUserId(username.getText().toString());
        if(!userMigration.getUserId().equals("")) {
            startActivity(new Intent(this, AllPostsActivity.class));
            finish();
        }

    }
}
