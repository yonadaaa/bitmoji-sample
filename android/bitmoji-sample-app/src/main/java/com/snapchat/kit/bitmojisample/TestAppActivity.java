package com.snapchat.kit.bitmojisample;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.bitmoji.OnBitmojiSelectedListener;
import com.snapchat.kit.sdk.bitmoji.ui.BitmojiFragment;
import com.snapchat.kit.bitmojisample.chat.ChatAdapter;
import com.snapchat.kit.bitmojisample.chat.model.ChatImageMessage;
import com.snapchat.kit.bitmojisample.chat.model.ChatImageUrlMessage;
import com.snapchat.kit.bitmojisample.chat.model.ChatMessage;
import com.snapchat.kit.bitmojisample.chat.model.ChatTextMessage;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

import java.util.Random;


public class TestAppActivity extends AppCompatActivity implements
        OnBitmojiSelectedListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        LoginStateController.OnLoginStateChangedListener {

    private static final float BITMOJI_CONTAINER_FOCUS_WEIGHT = 9.0f;
    private static final String EXTERNAL_ID_QUERY = "{me{externalId}}";

    private final ChatAdapter mAdapter = new ChatAdapter();

    private View mContentView;
    private View mBitmojiContainer;
    private RecyclerView mChatView;

    private int mBitmojiContainerHeight;
    private int mBaseRootViewHeightDiff = 0;
    private String mMyExternalId;
    private String theme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_app);

        mContentView = findViewById(R.id.content_view);
        mBitmojiContainer = findViewById(R.id.sdk_container);
        mChatView = findViewById(R.id.chat);
        mBitmojiContainerHeight = getResources().getDimensionPixelSize(R.dimen.bitmoji_container_height);

        mAdapter.setHasStableIds(true);
        mChatView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, true /* reverseLayout*/));
        mChatView.setAdapter(mAdapter);

        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(this);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sdk_container, BitmojiFragment.builder()
                        .withShowSearchBar(false)
                        .withShowSearchPills(false)
                        .withTheme(R.style.MyBitmojiTheme)
                        .build())
                .commit();
        getSupportFragmentManager().beginTransaction()
                .commit();

        if (SnapLogin.isUserLoggedIn(this)) {
            loadExternalId();
        }

        String[] themes = new String[]{"dance","food","party","sleep","music","football","soccer","school","gym","meme"};
        int index = new Random().nextInt(themes.length);
        theme = themes[index];

        String dancingURL = "https://sdk.bitmoji.com/render/panel/10212299-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1";
        if (Build.VERSION.SDK_INT >= 21) sendMessage(new ChatImageUrlMessage(false /*isFromMe*/, dancingURL, getDrawable(R.drawable.looking_good)));
        sendMessage(new ChatTextMessage(false /*isFromMe*/, "On your Adventure, you and Daniel are going to " + theme +"!"));

        delayFilter(2000);
        delayKeyboard(0);
    }

    @Override
    public void onGlobalLayout() {
        int heightDiff = getRootViewHeightDiff(mContentView);

        if (heightDiff > getResources().getDimensionPixelSize(R.dimen.min_keyboard_height)) {
            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams) mBitmojiContainer.getLayoutParams();

            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            params.height = heightDiff - mBaseRootViewHeightDiff;
            mBitmojiContainer.setLayoutParams(params);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } else {
            mBaseRootViewHeightDiff = heightDiff;
        }
    }

    @Override
    public void onBitmojiSelected(String imageUrl, Drawable previewDrawable) {
        setBitmojiVisible(false);
        handleBitmojiSend(imageUrl, previewDrawable);
    }

    @Override
    public void onLoginSucceeded() {
        loadExternalId();
    }

    @Override
    public void onLoginFailed() {
        // no-op
    }

    @Override
    public void onLogout() {
        // no-op
    }

    private void loadExternalId() {
        SnapLogin.fetchUserData(this, EXTERNAL_ID_QUERY, null, new FetchUserDataCallback() {
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
    }

    private void setBitmojiVisible(boolean isBitmojiVisible) {
        mBitmojiContainer.setVisibility(isBitmojiVisible ? View.VISIBLE : View.GONE);
    }

    private void handleBitmojiSend(String imageUrl, Drawable previewDrawable) {
        setBitmojiVisible(false);
        sendMessage(new ChatImageUrlMessage(true /*isFromMe*/, imageUrl, previewDrawable));

        sendDelayedMessage(new ChatImageMessage(false /*isFromMe*/, R.drawable.looking_good),3000);
        delayKeyboard(3500);
    }

    private void sendMessage(ChatMessage message) {
        mAdapter.add(message);
        mChatView.scrollToPosition(0);
    }

    private void sendDelayedMessage(final ChatMessage message, long delayMs) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage(message);
            }
        }, delayMs);
    }

    private void delayFilter(long delayMs) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String danielsID = "CAESIEtR2/E5Hs7eJGsIr58KBn1Bkdm2qxUoDtS/O2bER8CB";

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sdk_container);
                if (fragment instanceof BitmojiFragment) {
                    ((BitmojiFragment) fragment).setFriend(danielsID);
                    ((BitmojiFragment) fragment).setSearchText(theme);
                    //((BitmojiFragment) fragment).setSearchText(themes[index]);
                }
            }
        }, delayMs);
    }

    private void delayKeyboard(long delayMs) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBitmojiVisible(true);
            }
        }, delayMs);
    }

    private static int getRootViewHeightDiff(View view) {
        return view.getRootView().getHeight() - view.getHeight();
    }
}
