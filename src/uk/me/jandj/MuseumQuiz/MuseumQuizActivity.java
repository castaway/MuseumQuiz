package uk.me.jandj.MuseumQuiz;

import android.app.Activity;
import android.os.Bundle;
import android.database.sqlite.*;
import android.widget.*;
import android.database.*;
import java.util.*;
import android.view.View;
import android.widget.Button;
import android.graphics.*;
import android.view.View.*;
import android.view.*;
import android.util.*;
import java.io.*;
import org.json.*;
import android.content.*;
import android.graphics.drawable.*;

public class MuseumQuizActivity extends Activity implements View.OnClickListener
{
    private int currentQuiz = 1;
    private int currentQuestion;
	private int currentScore;
	private MuseumQuizDatabaseHelper quizDb;
	public static final int MENU_IMPORT = Menu.FIRST+1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		if(savedInstanceState != null) {
			currentQuestion = savedInstanceState.getInt("currentQuestion", 1);
			currentScore = savedInstanceState.getInt("currentScore", 0);
		} else {
			currentQuestion = 1;
			currentScore = 0;
		}
        setContentView(R.layout.main);
		Button nextButton = (Button)findViewById(R.id.next_button);

		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				currentQuestion++;
				displayNextQuestion(currentQuestion);
			}
		});
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_IMPORT, Menu.NONE, "Import");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch(menuItem.getItemId()) {
			case MENU_IMPORT:
			  importQuiz();
		}
		
		return super.onOptionsItemSelected(menuItem);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("currentQuestion", currentQuestion);
		outState.putInt("currentScore", currentScore);
	}
	@Override 
	protected void onStart() {
		super.onStart();
		quizDb = new MuseumQuizDatabaseHelper(this);
		if(!quizLoaded()) {
			importQuiz();
		}
		displayNextQuestion(currentQuestion);		
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void onClick(View view) {
		Button answerBtn = (Button)view;
		int btnId = (Integer)answerBtn.getTag();
		int correctId = getCorrectAnswer();
		
		if(btnId == correctId) {
			answerBtn.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
		} else {
			answerBtn.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);
			Button correctBtn = (Button)findViewById(R.id.answers).findViewWithTag(new Integer(correctId));
			correctBtn.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
		}
		findViewById(R.id.next_button).setEnabled(true);
	}
	
	private int getCorrectAnswer() {
		SQLiteDatabase readDb = quizDb.getReadableDatabase();
		String answerSQL = "SELECT id FROM answers WHERE question_id =  ? AND correct=1";
		String[] answerArgs = {String.valueOf(currentQuestion)};
		
		Cursor answerCursor = readDb.rawQuery(answerSQL, answerArgs);
		answerCursor.moveToFirst();
		int answerCorrect = answerCursor.getInt(answerCursor.getColumnIndex("id"));
		return answerCorrect;
	}
	
	private boolean quizLoaded() {
		SQLiteDatabase readDb = quizDb.getReadableDatabase();
		Cursor countQCursor = readDb.rawQuery("SELECT COUNT(*) FROM questions", null);
		countQCursor.moveToFirst();
		int total = countQCursor.getInt(0);
		if (total > 0) {
			return true;
		}
		return false;
	}
	
	private void displayNextQuestion(int question) {
		SQLiteDatabase readDb = quizDb.getReadableDatabase();
//		System.err.println("Path = " + readDb.getPath());
		String nextQSQL = "SELECT questions.desc, answers.id, answers.answer FROM quizzes " +
		"JOIN questions ON quizzes.id = questions.quiz_id " +
		"JOIN answers ON questions.id = answers.question_id " +
		"WHERE quizzes.id = ? AND questions.id = ? " +
		"ORDER BY answers.id";
		String countQSQL = "SELECT COUNT(*) as count FROM questions WHERE quiz_id = ?";
		
		String[] countArgs = {String.valueOf(currentQuiz)};
		Cursor countQCursor = readDb.rawQuery(countQSQL, countArgs);
		int totalQuestions;
		countQCursor.moveToFirst();
		totalQuestions = countQCursor.getInt(countQCursor.getColumnIndex("count"));
		countQCursor.close();
		
		TextView nOfM = (TextView)findViewById(R.id.n_of_m);
		nOfM.setText("Question " + question + " of " + totalQuestions);
		
//		System.err.println("currentQuiz " + currentQuiz + " question " + question);
		String[] nextQArgs = {String.valueOf(currentQuiz), String.valueOf(question)};
		Cursor nextQCursor = readDb.rawQuery(nextQSQL, nextQArgs);
		nextQCursor.moveToFirst();
		String[] cols = nextQCursor.getColumnNames();
		int rows = nextQCursor.getCount();
		System.err.println("Rows " + rows);
		
		int descIndex = nextQCursor.getColumnIndex("desc");
		System.err.println("index of desc col "+ descIndex);
		String questionDesc = nextQCursor.getString(nextQCursor.getColumnIndex("desc"));
		TextView questionView = (TextView)findViewById(R.id.question_text);
		questionView.setText(questionDesc);

		HashMap<Integer, Integer> buttonMap = new HashMap();
		buttonMap.put(1, R.id.answer_1);
		buttonMap.put(2, R.id.answer_2);
		buttonMap.put(3, R.id.answer_3);
		buttonMap.put(4, R.id.answer_4);
		int countButton = 0;
		do {
			int answerId = Integer.parseInt(nextQCursor.getString(nextQCursor.getColumnIndex("id")));
			String answerText = nextQCursor.getString(nextQCursor.getColumnIndex("answer"));
			countButton++;
			System.err.println("Answer row: id=" + answerId + " text=" + answerText + " btn="+countButton);
			Button answerButton = (Button)findViewById(buttonMap.get(countButton));
			answerButton.setText(answerText);
			answerButton.setTag(new Integer(answerId));
			answerButton.setOnClickListener(this);
			answerButton.getBackground().clearColorFilter();
		} while (nextQCursor.moveToNext());
		nextQCursor.close();

		findViewById(R.id.next_button).setEnabled(false);
	}
	
	private JSONObject readJSONFile(InputStream jsonStream) {
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(jsonStream, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch(IOException e) {
			System.err.println(e);
		} finally {
			try {
				jsonStream.close();
			} catch(IOException e) {
				System.err.println(e);
			}
		}

		String jsonString = writer.toString();
		JSONObject quiz;
		try {
			quiz = (JSONObject) new JSONTokener(jsonString).nextValue();
			return quiz;
		} catch(JSONException e) {
			System.err.println(e);
		}
		return null;
	}
	
	private void importQuiz() {
		JSONObject quiz = readJSONFile(getResources().openRawResource(R.raw.quiz));
		SQLiteDatabase writeDb = quizDb.getWritableDatabase();

		try {
			ContentValues row = new ContentValues();
			String name = quiz.getString("name");
			int quiz_id = quiz.getInt("id");
			row.put("id", quiz_id);
			row.put("name", name);
			writeDb.insert("quizzes", "id", row);
			JSONArray questions = quiz.getJSONArray("questions");
			for(int i=0; i< questions.length(); i++) {
				JSONObject question = questions.getJSONObject(i);
				int question_id = question.getInt("id");
				row = new ContentValues();
				row.put("id", question_id);
				row.put("quiz_id", quiz_id);
				row.put("desc", question.getString("desc"));
				writeDb.insert("questions", "id", row);
			
				JSONArray answers = question.getJSONArray("answers");
				for(int j=0; j< answers.length(); j++) {
					JSONObject answer = answers.getJSONObject(j);
					row = new ContentValues();
					row.put("id", answer.getInt("id"));
					row.put("quiz_id", quiz_id);
					row.put("question_id", question_id);
					row.put("answer", answer.getString("answer"));
					row.put("correct", answer.getInt("correct"));
					writeDb.insert("answers", "id", row);
				}
			}
		} catch(JSONException e) {
			System.err.println("Read JSON file failed: "+e);
		}
	}
}
