package com.example.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CustomView frame_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame_animation = findViewById(R.id.frame_animation);
//        frame_animation.startAnim();
    }

    public void onClick(View v) {
//        frame_animation.reset();
    }

}
