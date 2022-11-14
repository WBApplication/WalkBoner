package com.fusoft.walkboner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChangeLogActivity extends AppCompatActivity {
    private MaterialTextView changeLogText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_log);
        initView();
        setup();
    }

    private void initView() {
        changeLogText = (MaterialTextView) findViewById(R.id.change_log_text);
    }

    private void setup() {

        InputStream changeLogInputStream = getResources().openRawResource(R.raw.changelog);

        changeLogText.setText(readTextFile(changeLogInputStream));
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
