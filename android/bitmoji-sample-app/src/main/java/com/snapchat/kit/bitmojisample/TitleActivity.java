package com.snapchat.kit.bitmojisample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

public class TitleActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.title_page);

        Button T=(Button)findViewById(R.id.datebutton);
        T.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i=new Intent(getApplicationContext(),TestAppActivity.class);
                startActivity(i);
            }
        });
    }

    public void goOnDate()
    {
        Intent i = new Intent(getApplicationContext(),TestAppActivity.class);
        startActivity(i);
    }
}
