package com.example.learneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.learneasy.utils.ClassSubConstants;
import com.learneasy.utils.IntentExtras;

public class SelectChapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);

        String classString = getIntent().getStringExtra(IntentExtras.CLASS);
        String subCodeString = getIntent().getStringExtra(IntentExtras.SUBJECT);
        Log.d("CLASS_STRING", classString);
        Log.d("SUBJECT_CODE", subCodeString);

        LinearLayout linearLayout = findViewById(R.id.linear_layout);

        String label = getLabel(classString + subCodeString);
        int num = getNumberOfChapters(classString + subCodeString);

        for(int i=1; i<=num; i++){
            Button button = new Button(this);
            String str = label + " " + Integer.toString(i);
            button.setText(str);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 24, 0, 0);
            button.setLayoutParams(params);

            button.setVisibility(View.VISIBLE);

            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectChapterActivity.this, LearnOrPractice.class);
                    intent.putExtra(IntentExtras.CLASS, classString);
                    intent.putExtra(IntentExtras.SUBJECT, subCodeString);
                    intent.putExtra(IntentExtras.CHAPTER, Integer.toString(1000+finalI));
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }

    }

    private String getLabel(String classSubject){
        String label = null;
        switch (classSubject){
            case ClassSubConstants.CLASS_5 + ClassSubConstants.CODE_ENGLISH:
                label = "Unit";
                break;
        }
        return label;
    }

    private int getNumberOfChapters(String classSubject){
        int num = 0;
        switch (classSubject){
            case ClassSubConstants.CLASS_5 + ClassSubConstants.CODE_ENGLISH:
                num = 9;
                break;
        }
        return num;
    }
}