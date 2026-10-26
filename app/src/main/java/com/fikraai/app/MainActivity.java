package com.fikraai.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.os.AsyncTask;

import org.json.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;

public class MainActivity extends Activity {

    private LinearLayout messages;
    private EditText input;
    private ScrollView scroll;
    private Button send;

    // ضع هنا عنوان Cloudflare Worker الحقيقي
    private static final String API_URL =
            "https://YOUR-WORKER-NAME.workers.dev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);
        root.setBackgroundColor(Color.rgb(245, 247, 255));

        TextView title = new TextView(this);
        title.setText("Fikra AI ✨");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(55, 75, 190));
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

        root.addView(scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 1));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        input = new EditText(this);
        input.setHint("اكتب سؤالك هنا...");
        input.setSingleLine(false);

        bar.addView(input,
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        send = new Button(this);
        send.setText("إرسال");
        bar.addView(send);
        root.addView(bar);

        setContentView(root);

        send.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String question = input.getText().toString().trim();

        if (question.isEmpty()) {
            return;
        }

        if (API_URL.contains("YOUR-WORKER-NAME")) {
            addMessage("يرجى إضافة عنوان خادم Gemini أولاً.");
            return;
        }

        addMessage("أنت: " + question);
        input.setText("");
        send.setEnabled(false);

        TextView loading = new TextView(this);
        loading.setText("Fikra AI: جارٍ التفكير...");
        loading.setTextSize(16);
        messages.addView(loading);

        new GeminiTask(loading).execute(question);
    }

    private void addMessage(String text) {
        TextView message = new TextView(this);
        message.setText(text);
        message.setTextSize(17);
        message.setTextColor(Color.rgb(35, 35, 45));
        message.setPadding(12, 14, 12, 14);

        messages.addView(message);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private class GeminiTask extends AsyncTask<String, Void, String> {

        private final TextView loading;

        GeminiTask(TextView loading) {
            this.loading = loading;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(60000);
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("message", params[0]);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(body.toString().getBytes("UTF-8"));
                }

                int status = connection.getResponseCode();

                InputStream stream = (status >= 200 && status < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                if (stream == null) {
                    return "خطأ في الاتصال بالخادم: " + status;
                }

                StringBuilder result = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                JSONObject response = new JSONObject(result.toString());

                if (status < 200 || status >= 300) {
                    return "خطأ: " + response.optString(
                            "error", "تعذر الاتصال بـ Gemini");
                }

                return response.optString(
                        "reply", "لم يصل رد من Gemini.");

            } catch (Exception e) {
                return "تعذر الاتصال: تحقق من الإنترنت وعنوان الخادم.";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            loading.setText("Fikra AI: " + result);
            send.setEnabled(true);
            scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
        }
    }
}
