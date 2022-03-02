package com.example.showmewhat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;


public class TextRecognition extends AppCompatActivity {

    private static final int PICK_REQUEST_CODE= 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    ImageButton btnPhoto;
    ImageView photo;
    EditText imageText;
    Bitmap imageBitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        btnPhoto = findViewById(R.id.btnPhoto);
        photo = findViewById(R.id.photo);
        imageText = findViewById(R.id.imageText);



        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_REQUEST_CODE);
            }
        });

    }

    public void scanText(Uri uri){
        try{
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(TextRecognition.this, uri);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            imageText.setText(firebaseVisionText.getText());
                            Toast.makeText(TextRecognition.this, "Text converted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TextRecognition.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == RESULT_OK && requestCode == PICK_REQUEST_CODE){

        Uri selectedImage = data.getData();
        photo.setImageURI(selectedImage);
        scanText(selectedImage);
    }
    else if(resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE){
       Bundle extras = data.getExtras();
       imageBitmap = (Bitmap) extras.get("data");
       photo.setImageBitmap(imageBitmap);


    }


    }
}
