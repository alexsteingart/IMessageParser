package com.ams.imessageparser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {
 
       private Context mContext;
 
       private LayoutInflater mLayoutInflater;                              
 
       private ArrayList<Message> mMessages = new ArrayList<Message>();          
 
       
 
       public MessageListAdapter(Context context) {                           
          mContext = context;
          mLayoutInflater = (LayoutInflater) mContext
                   .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          
       }
 
       @Override
       public int getCount() {
          return mMessages.size();
       }
 
       @Override
       public Object getItem(int position) {
          return mMessages.get(position);
       }
 
       @Override
       public long getItemId(int position) {
          return position;
       }
 
       @Override
       public View getView(int position, View convertView,
             ViewGroup parent) {                                           
          RelativeLayout itemView;
          if (convertView == null) {                                        
             itemView = (RelativeLayout) mLayoutInflater.inflate(
                      R.layout.messages_list_item, parent, false);
 
             		ViewHolder holder = new ViewHolder();
             		holder.fromText = (TextView) itemView.findViewById(R.id.messageListSender);                        
             		holder.messageText = (TextView) itemView.findViewById(R.id.messageListText);                  
             		holder.messageReceivedTime = (TextView)	itemView.findViewById(R.id.messageReceivedTime);
             		holder.messageId = (TextView) itemView.findViewById(R.id.messageId);
                 	holder.conversationId = (TextView) itemView.findViewById(R.id.conversationId);
                 	
                 	itemView.setTag(holder);
                 	
                 	
             
          } else {
             itemView = (RelativeLayout) convertView;
          }
                         
          ViewHolder holder = (ViewHolder)itemView.getTag();
          SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
          Message m = mMessages.get(position);
          
          
          
          String name="";
    	Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(m.getFromNumber()));
    	Cursor contactCursor = mContext.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
    	if(contactCursor!=null){
    		if(contactCursor.moveToFirst()){
    				name = contactCursor.getString(contactCursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
    				
    		}
    	}
    	contactCursor.close();
          
          holder.fromText.setText("".equals(name)?m.getFromNumber():name);
          holder.messageText.setText(m.getBody());
          if(m.getReceivedTime()!=null)holder.messageReceivedTime.setText(sdf.format(new Date(m.getReceivedTime()*1000)));
          holder.messageId.setText(m.getMessageId());
          holder.conversationId.setText(m.getConversationId());
          
          return itemView;
       }
 
       public void upDateEntries(ArrayList<Message> messages) {
          mMessages = messages;
          notifyDataSetChanged();
       }
       
       public Context getContext(){
    	   return mContext;
       }
       
       
       public static class ViewHolder {
    	   public TextView fromText;
    	   public TextView messageText;
    	   public TextView messageReceivedTime;
    	   public TextView messageId;
    	   public TextView conversationId;
    	   
    	   
    	   
       }
       
       
}