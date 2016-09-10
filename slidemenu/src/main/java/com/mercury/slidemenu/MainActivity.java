package com.mercury.slidemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mercury.slidemenu.view.SlideMenu;

public class MainActivity extends AppCompatActivity {

    private SlideMenu mSlideMenu;
    private ImageView mIv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlideMenu = (SlideMenu) findViewById(R.id.slidemenu);
        mIv_back = (ImageView) findViewById(R.id.iv_back);
        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlideMenu.isMenuShow()) {
                    mSlideMenu.hideMenu();
                } else {

                    mSlideMenu.showMenu();
                }
            }
        });
    }
}
