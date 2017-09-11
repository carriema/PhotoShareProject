package com.parse.manager;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.constants.DatabaseSchema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;

/**
 * Created by myr on 9/10/17.
 */

public class FeedManager {
    private static FeedManager feedManager = new FeedManager();

    public static FeedManager getInstance() {
        return feedManager;
    }


    public void pushFeed(final String username, String feedId, Date create) {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseSchema.FOLLOWER_COUNT);

        query.whereEqualTo(DatabaseSchema.USERNAME, username);

        query.setLimit(1);
        List<ParseObject> count = null;
        boolean needPush = false;
        try {
            count = query.find();
            needPush = (int)count.get(0).get(DatabaseSchema.FOLLOWER) <= 10000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String imageId = feedId;
        final Date createTime = create;
        if (needPush) {
            ParseQuery<ParseObject> friendsQuery = new ParseQuery<ParseObject>(DatabaseSchema.USER_RELATION);
            friendsQuery.whereEqualTo(DatabaseSchema.FOLLOWING, username);
            friendsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {

                        Log.i("FeedManager", objects.size() + "");
                        for (ParseObject object : objects) {
                            ParseObject userFeed = new ParseObject(DatabaseSchema.USER_FEED);
                            Log.i("FeedManager", object.get(DatabaseSchema.FOLLOWER).toString());
                            userFeed.put(DatabaseSchema.USERNAME, object.get(DatabaseSchema.FOLLOWER));
                            userFeed.put(DatabaseSchema.PUBLISH_USER, username);
                            userFeed.put(DatabaseSchema.IMAGE_ID, imageId);
                            userFeed.put(DatabaseSchema.CREATE_TIME, createTime);
                            userFeed.saveInBackground();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

}
