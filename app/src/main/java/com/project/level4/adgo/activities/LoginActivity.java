package com.project.level4.adgo.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.project.level4.adgo.R;

public class LoginActivity extends Activity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private ImageView background;
    private TextView about;

    private ImageView facebookIcon;
    private ImageView appIcon;
    private TextView appTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.app_icon, null));
        toolbar.setTitle("AdverCash");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        appIcon = (ImageView) findViewById(R.id.advercash_icon);
        appIcon.setImageDrawable(getResources().getDrawable(R.drawable.app_icon, null));

        appTitle = (TextView) findViewById(R.id.advercash_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "PixelFJVerdana12pt.ttf");
        appTitle.setTypeface(font);
        appTitle.setText("AdverCash");

        about = (TextView) findViewById(R.id.about);
        about.setTypeface(font);
        about.setText("Please login to AdverCash using your Facebook account information. " +
                "If you do not already have a Facebook account, please visit http://www.facebook.com" +
                "and create an account before proceeding.");

        background = (ImageView) findViewById(R.id.background_colour_image);
//        background.setImageDrawable(getResources().getDrawable(R.drawable.curtains, null));
        background.setBackgroundColor(getResources().getColor(R.color.background));

        facebookIcon = (ImageView) findViewById(R.id.facebook_icon);
        facebookIcon.setImageDrawable(getResources().getDrawable(R.drawable.facebook_icon, null));

        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setBackground(getResources().getDrawable(R.drawable.login_facebook, null));
        loginButton.setReadPermissions("email");

        // Initialize callback manager
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    protected void onResume() {
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        super.onResume();
    }
}
