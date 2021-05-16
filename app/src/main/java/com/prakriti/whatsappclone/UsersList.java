package com.prakriti.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.developers.smartytoast.SmartyToast;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview_userList;
    private ArrayList<String> whatsappUserList;
    private ArrayAdapter arrayAdapter;
    private SwipeRefreshLayout swipeToRefreshUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        listview_userList = findViewById(R.id.listview_userList);
        whatsappUserList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, whatsappUserList);

        listview_userList.setOnItemClickListener(this);

        // get users from server
        getUserListFromServer();

        // add dependency for swipe to refresh layout
        swipeToRefreshUsers = findViewById(R.id.swipeToRefreshUsers);
        // set listener for swipe action
        swipeToRefreshUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    // get any new users from server // dont repeat users already present in arraylist
                    getRefreshedUserList();
                }
                catch (Exception e) {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_editProfile:
                break;

            case R.id.item_logout:
                logOutCurrentUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ChatsPage.class);
        // pass user info to next activity
        intent.putExtra("clickedUser", whatsappUserList.get(position));
        startActivity(intent);
    }

    private void logOutCurrentUser() {
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    SmartyToast.makeText(UsersList.this, "You have logged out", SmartyToast.LENGTH_SHORT, SmartyToast.DONE).show();
                    startActivity(new Intent(UsersList.this, LoginActivity.class));
                    finish();
                }
                else {
                    SmartyToast.makeText(UsersList.this, "Unable to log out. Please try again", SmartyToast.LENGTH_SHORT,
                            SmartyToast.ERROR).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserListFromServer() {
        // in case no other users are signed up, app will crash
        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            // don't show current user on list
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseUser user : objects) {
                                whatsappUserList.add(user.getUsername());
                            }
                            listview_userList.setAdapter(arrayAdapter); // change codes to add user status as well
                        }
                    } else {
                        SmartyToast.makeText(UsersList.this, "Unable to retrieve users\n" + e.getMessage(), Toast.LENGTH_SHORT,
                                SmartyToast.ERROR).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            SmartyToast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
            e.printStackTrace();
        }
    }

    private void getRefreshedUserList() {
        // in case no other users are signed up, app will crash
        try {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            // don't show current user on list & dont repeat users already present
            userQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            userQuery.whereNotContainedIn("username", whatsappUserList);

            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e)
                {
                    if (e == null && objects.size() > 0) {
                            for (ParseUser user : objects) {
                                whatsappUserList.add(user.getUsername());
                            }
                            arrayAdapter.notifyDataSetChanged(); // update listview
                            // stop refresh process
                            if(swipeToRefreshUsers.isRefreshing()) {
                                swipeToRefreshUsers.setRefreshing(false);
                            }

                    } else { // if no data is available in callback then e will be null but else{} will be called, not if{}
                        // so no calls to Exception object e here, on possibility of null return from Parse server
                        if(swipeToRefreshUsers.isRefreshing()) {
                            swipeToRefreshUsers.setRefreshing(false);
                        }
                        SmartyToast.makeText(UsersList.this, "Updated", SmartyToast.LENGTH_SHORT, SmartyToast.DONE).show();
                    }
                }
            });
        }
        catch (Exception f) {
            SmartyToast.makeText(this, f.getMessage(), Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
            f.printStackTrace();
        }
    }
}