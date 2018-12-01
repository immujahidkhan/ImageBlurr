package com.justclack.imageblurr;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vansuita.gaussianblur.GaussianBlur;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final int SELECT_PHOTO = 909;
    ImageView imageView, crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        crop = findViewById(R.id.crop);
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choose = new Intent(Intent.ACTION_GET_CONTENT);
                choose.setType("image/*");
                startActivityForResult(choose, SELECT_PHOTO);
            }
        });
        //Synchronous blur
        Bitmap blurredBitmap = GaussianBlur.with(MainActivity.this).render(R.drawable.assests);
        imageView.setImageBitmap(blurredBitmap);

/*//Asynchronous blur
        GaussianBlur.with(MainActivity.this).put(R.drawable.assests, imageView);

//Asynchronous with scaleDown and changing radius
        GaussianBlur.with(MainActivity.this).size(300).radius(10).put(R.drawable.assests, imageView);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                //doing some uri parsing
                Uri selectedImage = data.getData();
                Dothis(selectedImage);
                CropImage.activity(selectedImage)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                crop.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void Dothis(Uri selectedImage) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        GaussianBlur.with(MainActivity.this).size(800).radius(25).put(bitmap, imageView);
    }
}