package com.ams.imessageparser;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	
	public final static String MESSAGE_ID = "com.ams.imessageparser.MESSAGE_ID";
	public final static String CONVERSATION_ID = "com.ams.imessageparser.CONVERSATION_ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		MessageListAdapter adapter = new MessageListAdapter(this);
        setListAdapter(adapter);                                  

        ListView lv = getListView();
        
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
 
              
        	  loadConversation(view);
          }
        });
        
        LoadMessageList lml = new LoadMessageList(adapter);
        lml.execute(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void loadConversation(View view){
		
		
		
		Intent i = new Intent(this, DisplayConversationActivity.class);
		TextView et = (TextView) view.findViewById(R.id.messageId);
		String message = et.getText().toString();
		i.putExtra(MESSAGE_ID, message);
		
		et = (TextView) view.findViewById(R.id.conversationId);
		i.putExtra(CONVERSATION_ID, et.getText().toString());
		
		
		startActivity(i);
	}
}
