package com.snapchat.kit.bitmojisample;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String friendID = "CAESIEtR2/E5Hs7eJGsIr58KBn1Bkdm2qxUoDtS/O2bER8CB";
    HashMap<String, String[]> themeDict;
    private int index;

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

        String musicURL[] = {
                "https://sdk.bitmoji.com/render/panel/10221330-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1" ,
                "https://sdk.bitmoji.com/render/panel/10220687-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1" ,
                "https://sdk.bitmoji.com/render/panel/10220846-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1" ,
                "https://sdk.bitmoji.com/render/panel/10220684-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1" ,
                "https://sdk.bitmoji.com/render/panel/10221329-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String gymURL[] = {
                "https://sdk.bitmoji.com/render/panel/10237376-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237367-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237369-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20001394-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237372-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20001636-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String soccerURL[] = {
                "https://sdk.bitmoji.com/render/panel/10237542-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237540-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237541-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String footballURL[] = {
                "https://sdk.bitmoji.com/render/panel/10212477-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237542-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237542-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237541-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10224761-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String danceURL[] = {
                "https://sdk.bitmoji.com/render/panel/10218956-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10218945-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10211628-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212299-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212291-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };
        
        String sleepURL[] = {
                "https://sdk.bitmoji.com/render/panel/10227493-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10225328-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212773-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10221217-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String memeURL[] = {
                "https://sdk.bitmoji.com/render/panel/9991841-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10222623-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10188050-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10216404-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String schoolURL[] = {
                "https://sdk.bitmoji.com/render/panel/10224202-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10224204-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10224203-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212423-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20002105-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20002114-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String foodURL[] = {
                "https://sdk.bitmoji.com/render/panel/20012647-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237069-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237599-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237071-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237067-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10225316-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10236850-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20003411-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237595-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237068-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20005766-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237598-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237600-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10225328-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237070-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237596-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10237597-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/20005765-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1"
        };

        String partyURL[] = {
                "https://sdk.bitmoji.com/render/panel/10212419-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10221665-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10211835-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212299-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
                "https://sdk.bitmoji.com/render/panel/10212291-AWZlaHV3jILgfDP~yNdASBPh1gM19g-AWZlaHV3GUkpI8Rr~8UB38X2weMOyQ-v1.png?transparent=1&palette=1",
        };

        themeDict = new HashMap<>();

        themeDict.put("music",musicURL);
        themeDict.put("gym",gymURL);
        themeDict.put("soccer",soccerURL);
        themeDict.put("football",footballURL);
        themeDict.put("dance",danceURL);
        themeDict.put("sleep",sleepURL);
        themeDict.put("meme",memeURL);
        themeDict.put("school",schoolURL);
        themeDict.put("food",foodURL);
        themeDict.put("party",partyURL);

        // Theme for Adventure picked at random from array
        //String[] themes = new String[]{"music","gym"};
        String[] themes = new String[]{"dance","food","party","sleep","music","football","soccer","school","gym","meme"};
        int lel = new Random().nextInt(themes.length);

        theme = themes[lel];

        // Begin adventure with photo of activity and text explanation
        String openingURL = themeDict.get(theme)[0];
        sendMessage(new ChatImageUrlMessage(false /*isFromMe*/, openingURL, Drawable.createFromPath("")));
        sendMessage(new ChatTextMessage(false /*isFromMe*/, "On your Adventure, you and Daniel are going to " + theme +"!"));

        // Activate sticker keyboard and filter
        delayFilter(2000);
        delayKeyboard(0);

        index = themeDict.get(theme).length-1;
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
        handleBitmojiSend(imageUrl, previewDrawable);

        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Source Text", imageUrl);
        clipboardManager.setPrimaryClip(clipData);
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
        // If they have picked a sticker hide the keyboard
        setBitmojiVisible(false);
        // Send sticker by URL selected
        sendMessage(new ChatImageUrlMessage(true /*isFromMe*/, imageUrl, previewDrawable));

        respond();
        //Reactivate keyboard
        delayKeyboard(2500);
    }

    private void respond(){
        index--;
        if (index < 0) index = themeDict.get(theme).length-1;
        String imageUrl = themeDict.get(theme)[index];

        //Send reply with 3 second delay
        sendDelayedMessage(new ChatImageUrlMessage(false /*isFromMe*/, imageUrl , Drawable.createFromPath("")), 2000);
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

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sdk_container);
                if (fragment instanceof BitmojiFragment) {
                    ((BitmojiFragment) fragment).setFriend(friendID);
                    ((BitmojiFragment) fragment).setSearchText(theme);
                    //fragment.getString(0);
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
