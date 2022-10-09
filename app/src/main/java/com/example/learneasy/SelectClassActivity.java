package com.example.learneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.learneasy.utils.ClassSubConstants;
import com.learneasy.utils.IntentExtras;

public class SelectClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);
    }

    public void classSelected(View view) {
        Intent intent = new Intent(SelectClassActivity.this, SelectSubjectActivity.class);

        if(view.getId() == R.id.button_class_5){
            intent.putExtra(IntentExtras.CLASS, ClassSubConstants.CLASS_5);
            startActivity(intent);
        }

    }
}