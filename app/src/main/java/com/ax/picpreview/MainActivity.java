package com.ax.picpreview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	public final static int ALBUM_PIC_CODE = 1;
	Button btnOpenAlbum;
	ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv = (ImageView) findViewById(R.id.iv);
		btnOpenAlbum = (Button) findViewById(R.id.btn_open_album);
		btnOpenAlbum.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_open_album:
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, ALBUM_PIC_CODE);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ALBUM_PIC_CODE && resultCode == RESULT_OK){
			Log.i("onActivityResult", data.getData().toString());
			try {
				Uri imageUri = data.getData();
				Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				iv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
