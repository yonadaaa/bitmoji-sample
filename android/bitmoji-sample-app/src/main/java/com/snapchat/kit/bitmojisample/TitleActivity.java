package com.snapchat.kit.bitmojisample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.snapchat.kit.sdk.SnapKit;
import com.snapchat.kit.sdk.SnapLogin;

public class TitleActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup layout = findViewById(R.id.title_template);
        if (layout.isMotionEventSplittingEnabled()) goOnDate();
        //View mLoginButton = SnapLogin.getButton(getApplicationContext(), layout);
        //layout.addView(mLoginButton);
        setContentView(R.layout.title_page);


        Button T = findViewById(R.id.datebutton);
        T.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goOnDate();
            }
        });

        findViewById(R.id.unlink_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapKit.unlink(TitleActivity.this);
            }
        });
    }

    private void goOnDate(){
        Intent i=new Intent(getApplicationContext(),TestAppActivity.class);
        startActivity(i);
    }
}
