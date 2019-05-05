package com.getactive.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.getactive.Migration.UserMigration;
import com.getactive.R;
import com.getactive.Utils.Config;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private Button add_user;
    private UserMigration userMigration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                addUser();
            }
        });
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
