package com.fikraai.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ScrollView;

public class MainActivity extends Activity {

    LinearLayout messages;
    EditText input;
    ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(245, 247, 255));

        TextView title = new TextView(this);
        title.setText("Fikra AI ✨");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(50, 70, 180));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView welcome = new TextView(this);
        welcome.setText("أهلاً بيك! أنا Fikra AI ❤️");
        welcome.setTextSize(18);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(8, 20, 8, 20);
        root.addView(welcome);

        scroll = new ScrollView(this);
        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(messages);

        root.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        input = new EditText(this);
        input.setHint("اكتب رسالتك هنا...");
        bar.addView(input, new LinearLayout.LayoutParams(
            0, -2, 1
        ));

        Button send = new Button(this);
        send.setText("إرسال");
        bar.addView(send);

        root.addView(bar);
        setContentView(root);

        send.setOnClickListener(v -> {
            String text = input.getText().toString().trim();

            if (text.isEmpty()) return;

            addMessage("أنت: " + text, true);
            input.setText("");

            addMessage(
                "Fikra AI: الواجهة تعمل! ربط محرك الذكاء الاصطناعي هو الخطوة التالية.",
                false
            );
        });
    }

    private void addMessage(String text, boolean user) {
        TextView message = new TextView(this);
        message.setText(text);
        message.setTextSize(17);
        message.setTextColor(Color.rgb(35, 35, 45));
        message.setPadding(16, 12, 16, 12);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 8, 0, 8);

        messages.addView(message, params);
        scroll.post(() -> scroll.fullScroll(
            ScrollView.FOCUS_DOWN
        ));
    }
}
