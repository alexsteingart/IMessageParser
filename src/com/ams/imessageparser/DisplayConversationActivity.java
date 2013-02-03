package com.ams.imessageparser;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DisplayConversationActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		 
		setContentView(R.layout.activity_display_conversation);
 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		
		
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		String cid = i.getStringExtra(MainActivity.CONVERSATION_ID);
		
		String conversationQuery = " select c.* " + 
		" from " + MMSCacheOpenHelper.TABLE_CONVERSATION + " c " + 
		" where " + MMSCacheOpenHelper.COLUMN_ID + "=? ";

		
		Context context = getBaseContext();
		SQLiteDatabase mmsdb = new MMSCacheOpenHelper(context).getReadableDatabase();
		Cursor c = mmsdb.rawQuery(conversationQuery, new String[]{cid});
		
		
		if(c!=null && c.moveToFirst()){
		
			do{
				
				String phoneNumberString = c.getString(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_RECIPIENTS));
				String[] pnArr = phoneNumberString.split(",");
				String selectionStr = "";
				String title = "";
				for(int k=0; k<pnArr.length; k++){
				
				  String name=pnArr[k];
			    	Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(pnArr[k]));
			    	Cursor contactCursor = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
			    	if(contactCursor!=null){
			    		if(contactCursor.moveToFirst()){
			    				name = contactCursor.getString(contactCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			    				
			    		}
			    	}
			    	contactCursor.close();
			    	title += (k==0?"":", ")+name;
				}
		    	setTitle(title);
			}while(c.moveToNext());
			
		}
		
		
		MessageListAdapter adapter = new MessageListAdapter(this);
        setListAdapter(adapter);                                  

        ListView lv = getListView();
        
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
 
              
        	  
          }
        });
        
        //LoadMessageList lml = new LoadMessageList(adapter);
        //lml.execute(); 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_conversation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}