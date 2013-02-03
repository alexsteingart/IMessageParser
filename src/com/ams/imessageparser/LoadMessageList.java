package com.ams.imessageparser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class LoadMessageList
		extends AsyncTask<Integer, Integer, ArrayList<Message>> {

	public final static String COLUMN_DATE = "date";
	public final static String COLUMN_ADDRESS = "address";
	public final static String COLUMN_TYPE = "type";
	public final static Integer TYPE_FROM = 137;
	public final static Integer TYPE_TO = 151;
	
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
	    	
	    	String conversationQuery = " select max(" + MMSCacheOpenHelper.COLUMN_DATE + ") as " + MMSCacheOpenHelper.COLUMN_DATE + ", " +
	    						MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + 
	    						" from " + MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP + " m " + 
	    						" group by " + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID +
	    						" order by m." + MMSCacheOpenHelper.COLUMN_DATE  + " desc";
	    	
	    	String messageQuery = " select m.* " + //", c." + MMSCacheOpenHelper.COLUMN_RECIPIENTS + 
	    							" from " + MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP + " m " +
	    							" inner join " + MMSCacheOpenHelper.TABLE_CONVERSATION + " c on " +
	    							" m." + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + " = c." + MMSCacheOpenHelper.COLUMN_ID + 
	    							" where " + MMSCacheOpenHelper.COLUMN_CONVERSATION_ID + "=? " +
	    							" and " + MMSCacheOpenHelper.COLUMN_DATE + "=?";
	    	
	    	
	    	Cursor c = mmsdb.rawQuery(conversationQuery, null);
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    	
	    	if(c!=null && c.moveToFirst()){
	    		
	    		do{
	    			Cursor mc = mmsdb.rawQuery(messageQuery, new String[]{c.getString(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_CONVERSATION_ID)),c.getString(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_DATE))});
	    			
	    			if(mc!=null && mc.moveToFirst()){
	    				
	    				do{
			    			Message m = new Message();
			    			m.setBody(mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_MESSAGE_TEXT)));
			    			m.setFromNumber(mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_MESSAGE_FROM)));
			    			m.setReceivedTime(sdf.parse(mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_DATE))).getTime());
			    			m.setMessageId(mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_ID)));
			    			m.setConversationId(mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_CONVERSATION_ID)));
			    			
			    			//String tos = mc.getString(mc.getColumnIndex(MMSCacheOpenHelper.COLUMN_RECIPIENTS));
			    			//String[] toarr = tos.split(",");
			    			//for(int k=0; k<toarr.length; k++){
			    			//	m.addTo(toarr[k], "");
			    			//}
			    			
			    			messages.add(m);
	    				}while(mc.moveToNext());
	    				mc.close();
	    			}
	    		}while(c.moveToNext());
	    		
	    		c.close();
	    		
	    	}
	    	
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