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

import java.util.ArrayList;

public class SelectSubjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_subject);

        String classString = getIntent().getStringExtra(IntentExtras.CLASS);
        Log.d("CLASS_STRING", classString);

        LinearLayout linearLayout = findViewById(R.id.linear_layout);

        for(String string : getSubjectsForClass(classString)){
            Button button = new Button(this);
            button.setText(string);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 24, 0, 0);
            button.setLayoutParams(params);

            button.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectSubjectActivity.this, SelectChapterActivity.class);
                    intent.putExtra(IntentExtras.CLASS, classString);
                    intent.putExtra(IntentExtras.SUBJECT, getSubjectCode(((Button)v).getText().toString()));
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }
    }

    private String[] getSubjectsForClass(String classString){
        if(classString.equals(ClassSubConstants.CLASS_5)){
            return new String[]{ClassSubConstants.ENGLISH};
        }
        return new String[0];
    }

    private String getSubjectCode(String subject){
        String codeString = null;
        switch (subject){
            case ClassSubConstants.ENGLISH:
                codeString = ClassSubConstants.CODE_ENGLISH;
                break;
        }
        return codeString;
    }
}