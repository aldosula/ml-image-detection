package com.example.showmewhat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NativeActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.showmewhat.Helper.CheckConnection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.List;
import java.util.function.Consumer;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK =0;

    private ImageButton btnPhoto;
    private ImageView photo;
    private ProgressBar progressBar;
    private TextView Labels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPhoto = findViewById(R.id.btnPhoto);
        photo = findViewById(R.id.photo);
        progressBar = findViewById(R.id.progressBar);
        Labels = findViewById(R.id.Labels);


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK);
            }
        });



    }

    private  void labelImage(final Uri uri){

        try{
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, uri);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
            progressBar.setVisibility(View.VISIBLE);
            labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                    if(firebaseVisionImageLabels.isEmpty()){
                        Labels.setText("No tags detected");

                    }

                    else{

                        StringBuilder sb = new StringBuilder("Recognized tags:" + " \n");
                        for (int i =1; i<= firebaseVisionImageLabels.size(); i++){

                            sb.append(i +". " + firebaseVisionImageLabels.get(i-1).getText() + "\n");

                        }

                        Labels.setText(sb.toString());
                        progressBar.setVisibility(View.GONE);
                        photo.setImageURI(uri);

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "Failed to recognize image", Toast.LENGTH_SHORT).show();
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
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK){
            Uri uri = data.getData();
            labelImage(uri);

        }


    }



}
