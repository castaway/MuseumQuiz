package uk.me.jandj.MuseumQuiz;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.*;

public class MuseumQuizActivity extends Activity
{
    private int currentQuiz = 1;
    private int currentQuestion;
	private int currentScore;
	private MuseumQuizDatabaseHelper quizDb;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		currentQuestion = savedInstanceState.getInt("currentQuestion", 1);
		currentScore = savedInstanceState.getInt("currentScore", 0);
        setContentView(R.layout.main);
    }
	@Override 
	protected void onStart() {
		super.onStart();
		quizDb = new MuseumQuizDatabaseHelper(this);
		displayNextQuestion(currentQuestion);		
	}
	
	private void displayNextQuestion(int question) {
	}
}
