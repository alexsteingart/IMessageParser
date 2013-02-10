package com.ams.imessageparser;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class LoadConversation
		extends AsyncTask<String, Integer, ArrayList<Message>> {

	
	private final MessageListAdapter mAdapter;
	 
    public LoadConversation(MessageListAdapter adapter) {           
       mAdapter = adapter;
    }

    
    @Override
    protected ArrayList<Message> doInBackground(String... params) {
    	
    	ArrayList<Message> messages = new ArrayList<Message>();
    	try{
    		
    		
    		SQLiteDatabase mmsdb = new MMSCacheOpenHelper(mAdapter.getContext()).getReadableDatabase();
    		MMSCacheDataHelper.updateMMSCache(mAdapter.getContext(), mmsdb);
    		
	    	String messageQuery = " select m.* " +
	    							" from " + MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP + " m " +
	    							" where m." + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + "=? " +
	    							" order by m." + MMSCacheOpenHelper.COLUMN_DATE ;
	    	
	    	
	    	
	    	
			Cursor mc = mmsdb.rawQuery(messageQuery, new String[]{params[0]});
			
			messages = MMSCacheDataHelper.createMessageArrayFromCursor(mc);
        		
	    	
	    	mmsdb.close();
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	
		return messages;
    }

    
    
    protected void onPostExecute(ArrayList<Message> entries) {
       mAdapter.upDateEntries(entries);                       
    }


	
	
	
	
}