package com.fikraai.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Fikra AI");
        title.setTextSize(32);
        title.setTextColor(Color.rgb(50, 70, 180));
        title.setGravity(Gravity.CENTER);

        TextView message = new TextView(this);
        message.setText("أهلاً بك في فكرتي AI ❤️");
        message.setTextSize(22);
        message.setTextColor(Color.DKGRAY);
        message.setGravity(Gravity.CENTER);

        layout.addView(title);
        layout.addView(message);
        setContentView(layout);
    }
}
