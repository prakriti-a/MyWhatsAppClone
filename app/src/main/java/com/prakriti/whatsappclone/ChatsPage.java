package com.prakriti.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.developers.smartytoast.SmartyToast;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatsPage extends AppCompatActivity implements View.OnClickListener {

    // create variables locally to save memory
    private EditText edtMessage;
    private ImageButton imgSendChat;

    private ListView listview_chatPage;
    private ArrayList<String> chatsList;
    private ArrayAdapter arrayAdapter;
    private String clickedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_page);

        // get passed data by the key
        clickedUser = getIntent().getStringExtra("clickedUser");
        SmartyToast.makeText(this, "Chat with " + clickedUser, SmartyToast.LENGTH_SHORT, SmartyToast.DONE).show();

        setTitle(clickedUser);

        edtMessage = findViewById(R.id.edtMessage);
        imgSendChat = findViewById(R.id.imgSendChat);
        imgSendChat.setOnClickListener(this);

        edtMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // checking if user has pressed key
                    onClick(imgSendChat); // button is a view
                }
                return false;
            }
        });

        listview_chatPage = findViewById(R.id.listview_chatPage);
        chatsList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);
        listview_chatPage.setAdapter(arrayAdapter);

        // code for parse query for back and forth conversation
        // display conversation from server on chats page
        converseBetweenTwoUsers();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgSendChat:
                // sending the message to server on button click
                 sendMessagesToServer();
                break;
        }
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            view.clearFocus();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessagesToServer() {
        if(edtMessage.getText().toString().equalsIgnoreCase("")) {
            return;
        }
        else {
            String messageContent = edtMessage.getText().toString().trim();
            // new class
            ParseObject chats = new ParseObject("Chats");
            chats.put("sender", ParseUser.getCurrentUser().getUsername());
            chats.put("receiver", clickedUser);
            chats.put("message", messageContent);

            // save to server
            chats.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        chatsList.add(ParseUser.getCurrentUser().getUsername() + ":\n" + messageContent);
                        arrayAdapter.notifyDataSetChanged(); // update
                        edtMessage.setText("");
                    }
                    else {
                        SmartyToast.makeText(ChatsPage.this, "Unable to send message. Please try again",
                                Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void converseBetweenTwoUsers() {
        try {
            // create queries for 2 users
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chats");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chats");

            // condition for first user
            firstUserChatQuery.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("receiver", clickedUser);

            // condition for second user
            secondUserChatQuery.whereEqualTo("sender", clickedUser);
            secondUserChatQuery.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            // create new query
            // or() accepts array of queries & returns a query which is OR of passed queries
            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries); // myQuery just holds array of all the queries
            myQuery.orderByAscending("createdAt"); // older at top

            // get from server
            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        for (ParseObject obj : objects) {
                            String messageFromServer = obj.getString("message");

                            // if sender of message is current user
                            if (obj.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                messageFromServer = ParseUser.getCurrentUser().getUsername() + ":\n" + messageFromServer;
                            }
                            // else clicked user
                            if (obj.getString("sender").equals(clickedUser)) {
                                messageFromServer = clickedUser + ":\n" + messageFromServer;
                            }
                            chatsList.add(messageFromServer);
                        }
                        arrayAdapter.notifyDataSetChanged(); // dont put this inside loop
                    }
                    else { // will execute if there are no chats to retrieve from users
//                        SmartyToast.makeText(ChatsPage.this, "Unable to get data from server\nPlease try again",
//                                SmartyToast.LENGTH_SHORT, SmartyToast.ERROR).show();
                    }
                }
            });
        }
        catch (Exception e) {
            SmartyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
            e.printStackTrace();
        }
    }
}