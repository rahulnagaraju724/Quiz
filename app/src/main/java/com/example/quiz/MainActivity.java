package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    ArrayList<String> stringList = new ArrayList<String>();

    int correctAnswers = 0; // Track the number of correct answers
    long startTime; // To track the start time of the quiz
    static int questionNum = 0;

    private RadioGroup radioQuestions;
    private RadioButton radioButton;

    ImageView image;
    RatingBar ratingBar;
    private TextView timeTakenTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BackgroundTask bt = new BackgroundTask();
        bt.execute("http://www.papademas.net:81/sample.txt"); //grab url

        startTime = System.currentTimeMillis(); // Start the timer

    }//end onCreate

    //background process to download the file from internet
    private class BackgroundTask extends AsyncTask<String, Integer, Void> {

        protected void onPreExecute() {  }

        protected Void doInBackground(String... params) {
            URL url;
            String StringBuffer = null;
            try {
                //create url object to point to the file location on internet
                url = new URL(params[0]);
                //make a request to server
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //get InputStream instance
                InputStream is = con.getInputStream();
                //create BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                //read content of the file line by line & add it to Stringbuffer
                while ((StringBuffer = br.readLine()) != null) {
                    stringList.add(StringBuffer);  //add to Arraylist
                }

                br.close();

            } catch (Exception e) { e.printStackTrace(); }
            return null;
        }

        protected void onPostExecute(Void result) {
            txtView = findViewById(R.id.textView1);
            //display read text in TextVeiw
            txtView.setText(stringList.get(0));
            startQuiz();
        }
    }//end BackgroundTask class

    public void startQuiz() {
        buttonListener();
    }

    public void buttonListener() {

        Button btnDisplay;

        radioQuestions = findViewById(R.id.radioQuestions);
        btnDisplay = findViewById(R.id.btnDisplay);

        btnDisplay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioQuestions.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = findViewById(selectedId);

                // Increment correctAnswers if the selected answer is correct
                if ((questionNum == 0 || questionNum == 2 || questionNum == 3) && radioButton.getText().equals("True") ||
                        (questionNum == 1 || questionNum == 4) && radioButton.getText().equals("False")) {
                    correctAnswers++;
                }

                switch (questionNum) {

                    case 0:
                    case 2:
                    case 3:

                        //verify if result matches the right button selection
                        //i.e., (True or false!)
                        if (radioButton.getText().equals("True"))
                            Toast.makeText(MainActivity.this,
                                    " Right!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this,
                                    " Wrong!", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                    case 4:

                        //verify if result matches the right button selection
                        //i.e., (True or false!)
                        if (radioButton.getText().equals("False"))
                            Toast.makeText(MainActivity.this,
                                    " Right!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this,
                                    " Wrong!", Toast.LENGTH_SHORT).show();
                        break;

                    //finish switch cases 2-4
                }//end switch


                // If it's the last question, show RatingBar and calculate elapsed time
                if (questionNum == stringList.size() - 1) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    displayElapsedTime(elapsedTime);

                    ratingBar=findViewById(R.id.ratingBar);
                    displayRating();
                    ratingBar.setVisibility(View.VISIBLE);
                }
            }
        });
        imageListener();
    }//end buttonListener

    // Helper method to display elapsed time
    private void displayElapsedTime(long elapsedTime) {
        // Convert milliseconds to seconds
        long elapsedSeconds = elapsedTime / 1000;
        // Display elapsed time in some TextView or LogCat
        // Initialize the TextView for displaying time taken

        timeTakenTextView = findViewById(R.id.timeTakenTextView);
        timeTakenTextView.setVisibility(View.VISIBLE);
        timeTakenTextView.setText("Time taken: " + elapsedSeconds + " seconds");
        Log.d("Quiz", "Elapsed Time: " + elapsedSeconds + " seconds");
    }

    // Helper method to display the rating based on correct answers
    private void displayRating() {
        // Calculate the percentage of correct answers
        int totalQuestions = stringList.size();
        float percentageCorrect = (float) correctAnswers / totalQuestions * 100;

        // Set the rating based on the percentage of correct answers
        float rating = percentageCorrect / 20; // Since RatingBar ranges from 0 to 5
        ratingBar.setRating(rating);
    }
    public void imageListener() {

        image = findViewById(R.id.imageView1);
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // get new question for viewing
                if (questionNum == 4)
                    //reset count to -1 to start first question again
                    questionNum = -1;
                txtView.setText(stringList.get(++questionNum));
                //reset radio button (radioTrue) to default
                radioQuestions.check(R.id.radioTrue);
            }
        });
    }//end imageListener
}//end activity
