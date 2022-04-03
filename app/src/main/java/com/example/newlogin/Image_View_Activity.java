package com.example.newlogin;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class Image_View_Activity extends AppCompatActivity {
    ImageView imageView , downloadImageButton;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.iv_imageView2);
        downloadImageButton = findViewById(R.id.iv_imageView3);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String url = getIntent().getStringExtra("url");
        Picasso.get().load(url).into(imageView);

        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImage(url);
            }
        });
    }

    private void DownloadImage(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, ".jpg");
        downloadManager.enqueue(request);

//      추후 확인하기
      progressDialog.setTitle("being downloaded");
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(Image_View_Activity.this, "Download completed !", Toast.LENGTH_SHORT).show();
            }
        },1000);




    }
}