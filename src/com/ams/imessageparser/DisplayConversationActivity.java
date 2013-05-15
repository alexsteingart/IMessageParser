package com.ams.imessageparser;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayConversationActivity extends ListActivity {

	
	String recipients;
	
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
		
			TelephonyManager phn_mngr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			String PhnNo= phn_mngr.getLine1Number();
			System.out.println(PhnNo);
			
			do{
				
				String phoneNumberString = c.getString(c.getColumnIndex(MMSCacheOpenHelper.COLUMN_RECIPIENTS));
				String[] pnArr = phoneNumberString.split(",");
				
				String title = "";
				/**
				 * TODO: create single query with IN clause instead of looping!
				 */
				for(int k=0; k<pnArr.length; k++){
				
				  String name=pnArr[k];
				  if(name.equals(PhnNo)){
					  name = "Me";
				  }else{
			    	Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(pnArr[k]));
			    	Cursor contactCursor = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
			    	if(contactCursor!=null){
			    		if(contactCursor.moveToFirst()){
			    				name = contactCursor.getString(contactCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			    				
			    		}
			    	}
			    	contactCursor.close();
				  }
			    title += (k==0?"":", ")+name;
				}
				
				recipients = title;
		    	((TextView)findViewById(R.id.displayConversationTitle)).setText(title);
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
        
        LoadConversation lml = new LoadConversation(adapter);
        lml.execute(cid); 
		
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
	
	public void viewAllRecipients(View v){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			// set title
		alertDialogBuilder.setTitle("Recipients");
 
			// set dialog message
		
		TextView t= new TextView(this);
		t.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		t.setText(recipients);
		t.setMaxLines(20);
		
		alertDialogBuilder
			.setCancelable(true)
			.setView(t)
						
			
			;
		
		
		
				// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
		alertDialog.show();
		
		
			
	}

}
