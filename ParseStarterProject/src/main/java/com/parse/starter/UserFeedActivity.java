package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.constants.DatabaseSchema;
import com.parse.manager.UserManager;

import java.util.List;

public class UserFeedActivity extends BaseActivity {

    String selectedUser;
    String feedMode;
    UserManager userManager;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (selectedUser != null && !userManager.alreadyFollowed(ParseUser.getCurrentUser().getUsername() ,selectedUser)) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.follow_user_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.follow) {
            String currentUser = ParseUser.getCurrentUser().getUsername();
            userManager.addRelationShip(currentUser, selectedUser);
            userManager.followedOnePerson(currentUser, DatabaseSchema.FOLLOWER);
            userManager.followedOnePerson(selectedUser, DatabaseSchema.FOLLOWING);
            Toast.makeText(this, "Already followed " + selectedUser, Toast.LENGTH_SHORT);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        userManager = UserManager.getUserManager();
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        Intent intent = getIntent();

        feedMode = intent.getStringExtra("mode");
        ParseQuery<ParseObject> query = null;

        if (feedMode.equals("all")) {

            setTitle("Friends' Feed");


            query = new ParseQuery<ParseObject>("Image");



        } else if (feedMode.equals("any")){

            selectedUser = intent.getStringExtra("username");
            setTitle(selectedUser + "' Feed");

            query = new ParseQuery<ParseObject>("Image");

            query.whereEqualTo("username", selectedUser);
            query.orderByDescending("createdAt");

        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {

                            ParseFile file = (ParseFile) object.get("image");

                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if (e == null && data != null) {

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));

                                        imageView.setImageBitmap(bitmap);

                                        linearLayout.addView(imageView);

                                    }


                                }
                            });

                        }

                    }

                }

            }
        });




    }
}
