package com.parse.starter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.constants.DatabaseSchema;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends BaseActivity implements View.OnClickListener {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        final ListView userListView = (ListView) findViewById(R.id.userListView);
        final ArrayList<String> usernames = new ArrayList<String>();

        setTitle("Friends");

        Button search = (Button)findViewById(R.id.searchButton);
        search.setOnClickListener(this);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                intent.putExtra("username", usernames.get(i));
                intent.putExtra("mode", "any");
                startActivity(intent);

            }
        });


        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseSchema.USER_RELATION);

        query.whereEqualTo(DatabaseSchema.FOLLOWER, ParseUser.getCurrentUser().getUsername());

        query.addAscendingOrder(DatabaseSchema.FOLLOWING);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseObject user : objects) {

                            usernames.add((String)user.get(DatabaseSchema.FOLLOWING));

                        }

                        userListView.setAdapter(arrayAdapter);
                    }

                } else {

                    e.printStackTrace();

                }

            }
        });
    }

    public void queryNewUser(String usernameText) {

        final ListView userListView = (ListView) findViewById(R.id.userListView);
        final ArrayList<String> usernames = new ArrayList<String>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", usernameText);
        query.whereContains("username", usernameText);

        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseUser user : objects) {

                            usernames.add(user.getUsername());

                        }

                        userListView.setAdapter(arrayAdapter);
                    }

                } else {

                    e.printStackTrace();

                }

            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                intent.putExtra("username", usernames.get(i));
                intent.putExtra("mode", "any");
                startActivity(intent);


            }
        });
    }

    @Override
    public void onClick(View view) {
        EditText usernameText = (EditText) findViewById(R.id.searchText);

        if (view.getId() == R.id.searchButton) {
            queryNewUser(usernameText.getText().toString());
        }
    }
}
