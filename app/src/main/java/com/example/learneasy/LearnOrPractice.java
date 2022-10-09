package com.example.learneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.learneasy.utils.IntentExtras;

public class LearnOrPractice extends AppCompatActivity {

    private String classString;
    private String subCodeString;
    private String chapterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_or_practice);

        classString = getIntent().getStringExtra(IntentExtras.CLASS);
        subCodeString = getIntent().getStringExtra(IntentExtras.SUBJECT);
        chapterString = getIntent().getStringExtra(IntentExtras.CHAPTER);
        Log.d("LEARNEASY_CLASS_STRING", classString);
        Log.d("LEARNEASY_SUBJECT_CODE", subCodeString);
        Log.d("LEARNEASY_CHAPTER", chapterString);


    }

    public void learnClicked(View view) {
        Intent intent = new Intent(LearnOrPractice.this, Learn.class);
        intent.putExtra(IntentExtras.CLASS, classString);
        intent.putExtra(IntentExtras.SUBJECT, subCodeString);
        intent.putExtra(IntentExtras.CHAPTER, chapterString);
        startActivity(intent);
    }

    public void practiceClicked(View view) {
        Intent intent = new Intent(LearnOrPractice.this, PlaygroundActivity.class);
        intent.putExtra(IntentExtras.CLASS, classString);
        intent.putExtra(IntentExtras.SUBJECT, subCodeString);
        intent.putExtra(IntentExtras.CHAPTER, chapterString);
        startActivity(intent);
    }
}