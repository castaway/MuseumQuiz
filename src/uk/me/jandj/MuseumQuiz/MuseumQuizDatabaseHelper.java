package uk.me.jandj.MuseumQuiz;
import android.database.sqlite.*;
import android.content.*;

public class MuseumQuizDatabaseHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "museum_quiz";
	private static final String QUIZ_TABLE_CREATE =
	"CREATE TABLE quizzes (" +
	"id INTEGER PRIMARY KEY, " +
	"name TEXT);";

	private static final String QUESTION_TABLE_CREATE =
	"CREATE TABLE questions (" +
	"id INTEGER PRIMARY KEY, " +
	"quiz_id INTEGER, " +
	"desc TEXT);";

	private static final String ANSWER_TABLE_CREATE =
	"CREATE TABLE answers (" +
	"id INTEGER PRIMARY KEY, " +
	"question_id INTEGER, " +
	"answer TEXT, " + 
	"correct BOOLEAN);";
	
	private static final String INITIAL_DATA =
	"INSERT INTO quizzes VALUES (1, 'Robot Quiz');" +
	"INSERT INTO questions VALUES (1, 1, 'What model was Arnold Swarznegger's Terminator?');" +
	"INSERT INTO answers VALUES(1, 1, 'T-100', 0);" +
	"INSERT INTO answers VALUES(2, 1, 'T-20', 0);" +
	"INSERT INTO answers VALUES(3, 1, 'T-800', 1);" +
	"INSERT INTO answers VALUES(4, 1, 'T-1000', 0);";
	
	MuseumQuizDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QUIZ_TABLE_CREATE);
		db.execSQL(QUESTION_TABLE_CREATE);
		db.execSQL(ANSWER_TABLE_CREATE);
//		db.execSQL(INITIAL_DATA);
		ContentValues row = new ContentValues();
		row.put("id", 1);
		row.put("name", "Robots Quiz");
		db.insert("quizzes", "id", row);
		row = new ContentValues();
		row.put("id", 1);
		row.put("quiz_id", 1);
		row.put("desc", "What model was Arnold Swarznegger's Terminator?");
		db.insert("questions", "id", row);
		row = new ContentValues();
		row.put("id", 1);
		row.put("question_id", 1);
		row.put("answer", "T-100");
		row.put("correct", 0);
		db.insert("answers", "id", row);
		row = new ContentValues();
		row.put("id", 2);
		row.put("question_id", 1);
		row.put("answer", "T-20");
		row.put("correct", 0);
		db.insert("answers", "id", row);
		row = new ContentValues();
		row.put("id", 3);
		row.put("question_id", 1);
		row.put("answer", "T-800");
		row.put("correct", 1);
		db.insert("answers", "id", row);
		row = new ContentValues();
		row.put("id", 4);
		row.put("question_id", 1);
		row.put("answer", "T-1000");
		row.put("correct", 0);
		db.insert("answers", "id", row);
	}
    
    @Override 
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
