package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.constants.DatabaseSchema;
import com.parse.manager.FeedManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.parse.constants.DatabaseSchema.IMAGE_OB_ID;

public class UploadPhoto extends BaseActivity implements View.OnClickListener {


    ImageView uploadPlace;
    Bitmap bitmap;
    FeedManager feedManager;

    public void getPhoto() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getPhoto();

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                uploadPlace.setImageBitmap(bitmap);

                Log.i("Photo", "Received");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        setTitle("Upload Photo");
        uploadPlace = (ImageView) findViewById(R.id.uploadImageView);
        uploadPlace.setOnClickListener(this);

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);

        feedManager = FeedManager.getInstance();

    }

    public void upload(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        ParseFile file = new ParseFile("image.png", byteArray);

        final ParseObject object = new ParseObject("Image");

        object.put("image", file);

        object.put("username", ParseUser.getCurrentUser().getUsername());

        try {
            object.save();
            Toast.makeText(UploadPhoto.this, "Image Shared!", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(UploadPhoto.this, "Image could not be shared - please try again later.", Toast.LENGTH_SHORT).show();
        }


        feedManager.pushFeed(ParseUser.getCurrentUser().getUsername(), object.getObjectId().toString(), object.getCreatedAt());

//        ParseQuery<ParseObject> pushFeed = new ParseQuery<ParseObject>(DatabaseSchema.IMAGE_DATABASE);
//        pushFeed.whereEqualTo("id", IMAGE_OB_ID);
//        pushFeed.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null) {
//                    for (ParseObject o : objects) {
//                        Log.i("PushFEED", o.get("username").toString());
//                        feedManager.pushFeed(ParseUser.getCurrentUser().getUsername(), o.getObjectId().toString(), o.getCreatedAt());
//                    }
//
//                } else {
//                    e.printStackTrace();
//                }
//
//            }
//        });








    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.uploadImageView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {

                    getPhoto();

                }

            } else {

                getPhoto();

            }
        } else if (view.getId() == R.id.uploadButton) {
            upload(view);
            Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
            intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("mode", "any");
            startActivity(intent);
        }
    }
}
