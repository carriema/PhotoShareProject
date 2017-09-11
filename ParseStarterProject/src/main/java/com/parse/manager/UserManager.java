package com.parse.manager;

import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.constants.DatabaseSchema;
import com.parse.starter.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.parse.starter.R.id.usernameEditText;

/**
 * Created by myr on 9/10/17.
 */

public class UserManager {

    private static UserManager userManager = new UserManager();

    public static UserManager getUserManager() {
        return userManager;
    }

    public boolean alreadyFollowed(String username1, String username2) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseSchema.USER_RELATION);
        query.whereEqualTo(DatabaseSchema.FOLLOWER, username1);
        query.whereEqualTo(DatabaseSchema.FOLLOWING, username2);
        boolean hasFollowed = true;
        try {
            List<ParseObject> res = query.find();
            hasFollowed = (res != null && query.find().size() > 0);
        } catch (ParseException e) {
            e.printStackTrace();
            hasFollowed = true;
        }
        return hasFollowed;
    }

    public void addRelationShip(String username1, String username2) {
        if (alreadyFollowed(username1, username2)) {
            return;
        }

        ParseObject relation = new ParseObject(DatabaseSchema.USER_RELATION);
        relation.put(DatabaseSchema.FOLLOWER, username1);
        relation.put(DatabaseSchema.FOLLOWING, username2);
        relation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();;
                }
            }
        });
    }
    public void followedOnePerson(String username, String follower) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseSchema.FOLLOWER_COUNT);
        query.whereEqualTo("username", username);
        final String follow_method = follower;
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject p : objects) {
                        p.put(follow_method, (int)p.get(follow_method) + 1);
                        Log.i("getSuccess", follow_method + " " + p.get(follow_method));
                        p.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    Log.i("Follower", "Successfully");

                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
