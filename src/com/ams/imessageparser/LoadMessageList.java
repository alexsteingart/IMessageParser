package com.ams.imessageparser;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class LoadMessageList
		extends AsyncTask<Integer, Integer, ArrayList<Message>> {

	
	private final MessageListAdapter mAdapter;
	 
    public LoadMessageList(MessageListAdapter adapter) {           
       mAdapter = adapter;
    }

    
    @Override
    protected ArrayList<Message> doInBackground(Integer... page) {
    	
    	ArrayList<Message> messages = new ArrayList<Message>();
    	try{
    		
    		
    		SQLiteDatabase mmsdb = new MMSCacheOpenHelper(mAdapter.getContext()).getReadableDatabase();
    		MMSCacheDataHelper.updateMMSCache(mAdapter.getContext(), mmsdb);
    		
	    	String messageQuery = " select m.* " + //", c." + MMSCacheOpenHelper.COLUMN_RECIPIENTS + 
	    							" from " + MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP + " m " +
	    							" inner join " + MMSCacheOpenHelper.TABLE_CONVERSATION + " c on " +
	    							" m." + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + " = c." + MMSCacheOpenHelper.COLUMN_ID +
	    							" inner join (select max(" + MMSCacheOpenHelper.COLUMN_DATE + ") as " + MMSCacheOpenHelper.COLUMN_DATE + ", " +
	    											MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + 
	    											" from " + MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP + " m " + 
	    											" group by " + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + 
	    										") x on x." + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + "=m." + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID +
	    												" and x." + MMSCacheOpenHelper.COLUMN_DATE + "=m." + MMSCacheOpenHelper.COLUMN_DATE +
	    							" order by m." + MMSCacheOpenHelper.COLUMN_DATE + " desc";
	    	
	    	
	    	
	    	
			Cursor mc = mmsdb.rawQuery(messageQuery, null);
			
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