package com.ams.imessageparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;



public class MMSCacheDataHelper{

	public final static String COLUMN_DATE = "date";
	public final static String COLUMN_ADDRESS = "address";
	public final static String COLUMN_TYPE = "type";
	public final static Integer TYPE_FROM = 137;
	public final static Integer TYPE_TO = 151;
	
	
	public static void updateMMSCache(Context context, SQLiteDatabase mmsdb){
		
		ContentResolver contentResolver = context.getContentResolver();
		final String[] projection = new String[]{"*"};
		Uri uri = Uri.parse("content://mms");
		
		/*
		Cursor c = mmsdb.query(MMSCacheOpenHelper.TABLE_LAST_UPDATED, 
				new String[]{MMSCacheOpenHelper.COLUMN_LAST_UPDATED}, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
		
		System.out.println("lastupdatecursor: " + c);
		String date = null;
		if(c.moveToFirst()){
			date = c.getString(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_LAST_UPDATED));
		}
		c.close();
		*/
		
		String dateStr = getMaxValue(mmsdb, MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP, MMSCacheOpenHelper.COLUMN_DATE);
		if(dateStr==null || "".equals(dateStr)){
			dateStr = "1970-01-01 00:00:00.000";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		try{
			String date = String.valueOf(sdf.parse(dateStr).getTime());
			
			
			Cursor query = contentResolver.query(uri, projection, COLUMN_DATE + ">?", new String[]{date}, COLUMN_DATE + " desc");
			
			
			
			ContentValues cv = new ContentValues();
			cv.put(MMSCacheOpenHelper.COLUMN_LAST_UPDATED, sdf.format(new Date()));
			mmsdb.update(MMSCacheOpenHelper.TABLE_LAST_UPDATED, cv, null, null);
			
			ArrayList<Message> messages = new ArrayList<Message>();
			
			int count = 0;
			if (query.moveToFirst()) {
			    do {
			        String string = query.getString(query.getColumnIndex("ct_t"));
			        String mmsId = query.getString(query.getColumnIndex("_id"));
			        if ("application/vnd.wap.multipart.related".equals(string)) {
			            // it's MMS
			        	String selectionPart = "mid=" + mmsId;
			        	uri = Uri.parse("content://mms/part");
			        	Cursor cursor = contentResolver.query(uri, null,
			        	    selectionPart, null, null);
			        	
			        	Message m = new Message();
			        	if (cursor.moveToFirst()) {
			        	    do {
			        	        String partId = cursor.getString(cursor.getColumnIndex("_id"));
			        	        String type = cursor.getString(cursor.getColumnIndex("ct"));
			        	        if ("text/plain".equals(type)) {
			        	            String data = cursor.getString(cursor.getColumnIndex("_data"));
			        	            String body;
			        	            
			        	            m.setReceivedTime(query.getLong(query.getColumnIndex(COLUMN_DATE)));
			        	            m.setMessageId(mmsId);
			        	            if (data != null) {
			        	                // implementation of this method below
			        	                body = getMmsText(contentResolver, partId);
			        	            } else {
			        	                body = cursor.getString(cursor.getColumnIndex("text"));
			        	            }

			        	            
			        	            m.setBody(body);
			        	            
			        	            String from = "";
			        	            List<String> to = new ArrayList<String>();
			        	            
			        	            uri = Uri.parse("content://mms/"+mmsId+"/addr");
						        	Cursor addrCursor = contentResolver.query(uri, null,
						        	    null, null, null);
						        	if (addrCursor.moveToFirst()) {
						        	    do {
						        	    	
						        	    	int addrType = addrCursor.getInt(addrCursor.getColumnIndex(COLUMN_TYPE));
						        	    	String address = addrCursor.getString(addrCursor.getColumnIndex(COLUMN_ADDRESS));
						        	    	
						        	    	if(TYPE_FROM.equals(addrType)){
						        	    		m.setFromNumber(address);
						        	    		//m.setFromName(name);
						        	    	}else if(TYPE_TO.equals(addrType)){
						        	    		m.addTo(address, "");
						        	    	}						        	 
						        	    	
						        	    	
						        	    	
						        	    } while (addrCursor.moveToNext());
						        	}
						        	addrCursor.close();
						        	count++;
						        	
						        	messages.add(m);
			        	        }
			        	    } while (cursor.moveToNext());
			        	    cursor.close();
			        	    
			        	    
			        	}
			        	
			        } else {
			            // it's SMS
			        }
			        
			    } while (query.moveToNext());
			    query.close();
			}
		
			for(int k=0; k<messages.size(); k++){
				
				Message m = messages.get(k);
				
				cv = new ContentValues();
				Set<String> convoList = new TreeSet<String>(m.getTo().keySet());
				convoList.add(m.getFromNumber());
				cv.put(MMSCacheOpenHelper.COLUMN_RECIPIENTS, TextUtils.join(",",convoList));
				Long convId = insertOrGetId(mmsdb, MMSCacheOpenHelper.TABLE_CONVERSATION,cv,MMSCacheOpenHelper.COLUMN_RECIPIENTS);
				
				
				cv = new ContentValues();
				cv.put(MMSCacheOpenHelper.COLUMN_CONVERSATION_ID, convId);
				cv.put(MMSCacheOpenHelper.COLUMN_MESSAGE_ID, m.getMessageId());
				cv.put(MMSCacheOpenHelper.COLUMN_MESSAGE_FROM, m.getFromNumber());
				cv.put(MMSCacheOpenHelper.COLUMN_MESSAGE_TEXT, m.getBody());
				cv.put(MMSCacheOpenHelper.COLUMN_DATE, sdf.format(new Date(m.getReceivedTime())));
				insertOrGetId(mmsdb, MMSCacheOpenHelper.TABLE_CONVERSATION_MESSAGE_MAP,cv,MMSCacheOpenHelper.COLUMN_MESSAGE_ID);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//mmsdb.close();
		
	}
	
	public static long insertOrGetId(SQLiteDatabase db, String table, ContentValues cv, String uniqueColumn){
		
		long ret = 0;
		
		Cursor c = db.query(table, null, uniqueColumn+"=?", new String[]{(String)cv.get(uniqueColumn)}, null, null, null);
		if(c!=null && c.moveToFirst()){
			ret = c.getLong(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_ID));
			
			
		}
		c.close();
		if(Long.valueOf(0).equals(ret)){
			
			ret = db.insert(table, null, cv);
		}
		return ret;
		
	}
	
	public static String getMaxValue(SQLiteDatabase db, String table, String column){
		
		String ret = "";
		
		Cursor c = db.rawQuery("SELECT MAX(" + column + ") FROM " + table, null);
		if(c!=null && c.moveToFirst()){
			ret = c.getString(0);
			
			
		}
		c.close();
		
		return ret;
	}
	
	 private static String getMmsText(ContentResolver cr, String id) {
		    Uri partURI = Uri.parse("content://mms/part/" + id);
		    InputStream is = null;
		    StringBuilder sb = new StringBuilder();
		    try {
		        is = cr.openInputStream(partURI);
		        if (is != null) {
		            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		            BufferedReader reader = new BufferedReader(isr);
		            String temp = reader.readLine();
		            while (temp != null) {
		                sb.append(temp);
		                temp = reader.readLine();
		            }
		        }
		    } catch (IOException e) {}
		    finally {
		        if (is != null) {
		            try {
		                is.close();
		            } catch (IOException e) {}
		        }
		    }
		    return sb.toString();
		}

}