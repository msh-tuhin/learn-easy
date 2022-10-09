package com.example.learneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learneasy.utils.IntentExtras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PlaygroundActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    TextView questionTextView;
    TextView solutionTextView;
    TextView correctAnswerTextView;
    TextView labelTextView;
    EditText answerEditText;
    Button submitButton;
    Button nextButton;

    ArrayList<String> answers;
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> doneQuestions = new ArrayList<>();
    // won't be used for now
    ArrayList<String> problemQuestions = new ArrayList<>();

    int numberOfQuestions;
    String questionID;
    JSONObject jsonObject;

    String subCodeString;
    String classString;
    String chapterString;

    private String defaultUser = "default105";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        linearLayout = findViewById(R.id.linear_layout);
        questionTextView = findViewById(R.id.question_tv);
        answerEditText = findViewById(R.id.answer_edittext);
        labelTextView = findViewById(R.id.label_tv);
        correctAnswerTextView = findViewById(R.id.correct_answer_tv);
        solutionTextView = findViewById(R.id.solution_tv);
        submitButton = findViewById(R.id.submit_button);
        nextButton = findViewById(R.id.next_button);

        classString = getIntent().getStringExtra(IntentExtras.CLASS);
        subCodeString = getIntent().getStringExtra(IntentExtras.SUBJECT);
        chapterString = getIntent().getStringExtra(IntentExtras.CHAPTER);
        Log.d("LEARNEASY_CLASS_STRING", classString);
        Log.d("LEARNEASY_SUBJECT_CODE", subCodeString);
        Log.d("LEARNEASY_CHAPTER", chapterString);

        String filename = getBookName(classString, subCodeString);
        Log.d("LEARNEASY_filename", filename);

        // TODO populate doneQuestions and problemQuestions from local DB

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        doneQuestions.addAll(sharedPreferences.getStringSet(getSFKey(), new HashSet<>()));

        try{
            jsonObject = new JSONObject(loadJSONFromAsset(this, filename));
            numberOfQuestions = jsonObject.getInt("unit" + chapterString);
            questions = getQuestionsList(classString, subCodeString, chapterString);

            if(questions.size() != doneQuestions.size()) {
                questions.removeAll(doneQuestions);
            } else {
                doneQuestions.clear();
            }

            setQuestion();

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> learntQuestions = new HashSet<>(doneQuestions);
        editor.putStringSet(getSFKey(), learntQuestions);
        editor.apply();
    }

    private String loadJSONFromAsset(Activity activity, String fileName) {
        String json = null;
        String bookName = "books/" + fileName;
        try {
            InputStream is = activity.getAssets().open(bookName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String getBookName(String classString, String subCodeString){
        return subCodeString + classString + ".json";
    }

    private ArrayList<String> getQuestionsList(String classString,
                                               String subCodeString,
                                               String chapterString){
        //int chapter = Integer.parseInt(chapterString);
        ArrayList<String> set = new ArrayList<>();
        for(int i=1; i<=numberOfQuestions; i++){
            set.add(subCodeString + classString + "_u" + chapterString + "_" +Integer.toString(i));
        }
        return set;
    }

    public void submitClicked(View view){
        nextButton.setEnabled(true);
        submitButton.setEnabled(false);
        hideKeyboard();
        String answerString = answerEditText.getText().toString().trim();

        if(!answerString.isEmpty()){
            if(answerString.charAt(answerString.length()-1) == '.') {
                StringBuilder sb = new StringBuilder();
                sb.append(answerString);
                sb.deleteCharAt(sb.length()-1);
                answerString = sb.toString();
            }
        }

        if(answers.contains(answerString)){
            correctAnswerTextView.setTextColor(Color.parseColor("#00ff00"));
            correctAnswerTextView.setText("Correct Answer");
            questions.remove(questionID);
            doneQuestions.add(questionID);
            String label = Integer.toString(doneQuestions.size()) + " out of " + Integer.toString(numberOfQuestions) +
                    " questions completed";
            labelTextView.setText(label);
            if(questions.size() == 0){
                Toast.makeText(this, "Congratulations! You have completed all questions", Toast.LENGTH_SHORT).show();
                questions.addAll(doneQuestions);
                doneQuestions.clear();
            }
        } else{
            correctAnswerTextView.setTextColor(Color.parseColor("#ff0000"));
            correctAnswerTextView.setText("Wrong Answer");
            StringBuilder solutionString = new StringBuilder();
            solutionString.append("Solution:\n");
            // show single answer (the most appropriate one)
            solutionString.append(answers.get(0));
            //int i=1;
            // show all poossible answers
            //for(String ans : answers){
            //    solutionString.append(Integer.toString(i++) + ". " + ans + ".\n");
            //}
            solutionTextView.setText(solutionString.toString());
        }
    }

    public void nextClicked(View view){
        nextButton.setEnabled(false);
        submitButton.setEnabled(true);
        hideKeyboard();
        answerEditText.setText("");
        questionTextView.setText("");
        correctAnswerTextView.setText("");
        solutionTextView.setText("");
        setQuestion();
    }

    private void setQuestion(){
        String label = Integer.toString(doneQuestions.size()) + " out of " + Integer.toString(numberOfQuestions) +
                " questions completed";
        labelTextView.setText(label);
        try{
            Random random = new Random();
            int randomInt = random.nextInt(questions.size());
            questionID = questions.get(randomInt);

            JSONObject questionObject = jsonObject.getJSONObject(questionID);
            String question = questionObject.getString("question");

            JSONArray answersJSONArray = questionObject.getJSONArray("answers");
            answers = new ArrayList<>();
            for(int i=0; i<answersJSONArray.length(); i++){
                answers.add(answersJSONArray.getString(i));
            }

            questionTextView.setText(question);
        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private String getSFKey(){
        return defaultUser + "_" + subCodeString + classString + "_u" +
                chapterString + "practiced";
    }
}