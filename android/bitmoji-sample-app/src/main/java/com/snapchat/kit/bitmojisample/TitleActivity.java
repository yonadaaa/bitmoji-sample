package com.snapchat.kit.bitmojisample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snapchat.kit.sdk.SnapKit;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

public class TitleActivity extends Activity implements LoginStateController.OnLoginStateChangedListener {

    private ImageView mBitmojiImageView;
    private TextView mNameTextView;
    private String mMyExternalId = "AKJFKDFJD";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SnapLogin.isUserLoggedIn(this)) {
            nextScreen();
        } else {
            loginScreen();
        }
    }

    private void loginScreen(){
        // creating LinearLayout
        LinearLayout linLayout = new LinearLayout(this);
        // specifying vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // creating LayoutParams
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // set LinearLayout as a root element of the screen
        setContentView(linLayout, linLayoutParam);

        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this);

        View mLoginButton = SnapLogin.getButton(getApplicationContext(), linLayout);
        mLoginButton.setY(800);
    }

    private void nextScreen(){

        String query = "{me{bitmoji{avatar},displayName}}";

        SnapLogin.fetchUserData(this, "{me{externalId}}", null, new FetchUserDataCallback() {
            @Override
            public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                if (userDataResponse == null || userDataResponse.hasError()) {
                    return;
                }
                mMyExternalId = userDataResponse.getData().getMe().getExternalId();
            }

            @Override
            public void onFailure(boolean isNetworkError, int statusCode) {
                // handle error
            }
        });


        setContentView(R.layout.title_page);

        mBitmojiImageView = findViewById(R.id.profileView);
        Button T = findViewById(R.id.adventureButton);

        mNameTextView.setText(mMyExternalId);

        T.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goOnDate();
            }
        });

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapKit.unlink(TitleActivity.this);
            }
        });
    }

    @Override
    public void onLoginSucceeded() {
        nextScreen();
    }

    @Override
    public void onLoginFailed() {
        loginScreen();
    }

    @Override
    public void onLogout() {
        loginScreen();
    }

    private void goOnDate(){
        Intent i=new Intent(getApplicationContext(),TestAppActivity.class);
        startActivity(i);
    }
}
