package com.example.learneasy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Learn extends AppCompatActivity {

    TextView questionTextView;
    TextView answerTextView;
    TextView labelTextView;
    Button button;

    ArrayList<String> answers;
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> doneQuestions = new ArrayList<>();

    int numberOfQuestions;
    String questionID;
    JSONObject jsonObject;

    String classString;
    String subCodeString;
    String chapterString;

    private String defaultUser = "default105";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        questionTextView = findViewById(R.id.question_tv);
        labelTextView = findViewById(R.id.label_tv);
        answerTextView = findViewById(R.id.answer_tv);
        button = findViewById(R.id.button);

        classString = getIntent().getStringExtra(IntentExtras.CLASS);
        subCodeString = getIntent().getStringExtra(IntentExtras.SUBJECT);
        chapterString = getIntent().getStringExtra(IntentExtras.CHAPTER);
        Log.d("LEARNEASY_CLASS_STRING", classString);
        Log.d("LEARNEASY_SUBJECT_CODE", subCodeString);
        Log.d("LEARNEASY_CHAPTER", chapterString);

        String filename = getBookName(classString, subCodeString);
        Log.d("LEARNEASY_filename", filename);

        // TODO populate doneQuestions from local DB

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

    private String getBookName(String classString, String subCodeString){
        return subCodeString + classString + ".json";
    }

    private void setQuestion(){
        String label = Integer.toString(doneQuestions.size()) + " out of " + Integer.toString(numberOfQuestions) +
                " questions completed";
        labelTextView.setText(label);
        try{
            //Random random = new Random();
            //int randomInt = random.nextInt(questions.size());
            //questionID = questions.get(randomInt);
            questionID = questions.get(0);

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

    public void buttonClicked(View view) {

        String buttonText = ((Button)view).getText().toString();

        if(buttonText.equals(getResources().getString(R.string.show_answer))){
            button.setText(getResources().getString(R.string.next));
            answerTextView.setText("Answer:\n\n" + answers.get(0) + ".");
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
        } else if(buttonText.equals(getResources().getString(R.string.next))){
            button.setText(getResources().getString(R.string.show_answer));
            questionTextView.setText("");
            answerTextView.setText("");
            setQuestion();
        }
    }

    private String getSFKey(){
        return defaultUser + "_" + subCodeString + classString + "_u" +
                chapterString + "learnt";
    }
}