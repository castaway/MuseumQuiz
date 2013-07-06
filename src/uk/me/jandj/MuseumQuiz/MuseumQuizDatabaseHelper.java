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

	private static final String ANSWER_TABLE_CREATE =
	"CREATE TABLE answers (" +
	"quiz_id INTEGER, " +
	"answer TEXT, " + 
	"correct BOOLEAN);";
	
	MuseumQuizDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QUIZ_TABLE_CREATE);
		db.execSQL(ANSWER_TABLE_CREATE);
	}
    
    @Override 
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
