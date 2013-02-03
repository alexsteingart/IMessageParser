package com.ams.imessageparser;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MMSCacheOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MMSCache";
    
    public static final String TABLE_CONVERSATION = "conversations";
    public static final String TABLE_CONVERSATION_MESSAGE_MAP = "conversationMessageMap";
    public static final String TABLE_LAST_UPDATED = "lastUpdated";
    
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONVERSATION_ID = "conversationId";
    public static final String COLUMN_RECIPIENTS = "recipients";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_MESSAGE_FROM = "messageFrom";
    public static final String COLUMN_MESSAGE_TEXT = "messageText";
    public static final String COLUMN_DATE = "receivedDate";
    public static final String COLUMN_LAST_UPDATED = "lastUpdated";
    
    private final Context mContext;
    
    private static final String CREATE_TABLE_CONVERSATION =
                "CREATE TABLE " + TABLE_CONVERSATION + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPIENTS + " TEXT, " +
                "UNIQUE(" + COLUMN_RECIPIENTS + ") ON CONFLICT IGNORE);";
    private static final String CREATE_TABLE_CONVERSATION_MESSAGE_MAP = 
    			"CREATE Table " + TABLE_CONVERSATION_MESSAGE_MAP + " (" + 
    			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    			COLUMN_CONVERSATION_ID + " INTEGER, " +
    			COLUMN_MESSAGE_ID + " INTEGER, " + 
    			COLUMN_MESSAGE_FROM + " TEXT, " + 
    			COLUMN_MESSAGE_TEXT + " TEXT, " + 
    			COLUMN_DATE + " TEXT, " +
    			"UNIQUE(" + COLUMN_MESSAGE_ID + ") ON CONFLICT IGNORE);";
    
    private static final String CREATE_TABLE_LAST_UPDATED = 
    			"CREATE TABLE " + TABLE_LAST_UPDATED + " (" +
    			COLUMN_LAST_UPDATED + " TEXT);";

    MMSCacheOpenHelper(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONVERSATION);
        db.execSQL(CREATE_TABLE_CONVERSATION_MESSAGE_MAP);
        db.execSQL(CREATE_TABLE_LAST_UPDATED);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAST_UPDATED, "1970-01-01 00:00:00.000");
        db.insert(TABLE_LAST_UPDATED, null, cv);
        
        //MMSCacheDataHelper.updateMMSCache(mContext, db);
        
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int fromVersion, int toVersion) {
		// TODO Auto-generated method stub
		
	}
}