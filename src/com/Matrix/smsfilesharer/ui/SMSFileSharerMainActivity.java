package com.Matrix.smsfilesharer.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Matrix.smsfilesharer.R;
import com.Matrix.smsfilesharer.controler.SMSFileSharerHelper;
import com.Matrix.smsfilesharer.filechooser.util.FileUtils;

public class SMSFileSharerMainActivity extends Activity {
	String TAG = "SMSFileSharerMainActivity";
	String fileName = "";
	TextView fileNameTextView;
	SMSFileSharerHelper sfsHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsfile_sharer_main_activity_layout);
		fileNameTextView = (TextView) findViewById(R.id.fileNameTextView);
		sfsHelper=new SMSFileSharerHelper();
	}

	public void getFile(View v) {
		Intent target = FileUtils.createGetContentIntent();
		target.setPackage(getPackageName());
		Intent intent = Intent.createChooser(target, "Select a file");
		try {
			startActivityForResult(intent, 1000);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void share(View v) {
		if (TextUtils.isEmpty(fileName)) {
			Toast.makeText(getApplicationContext(), "Please select a file",
					Toast.LENGTH_SHORT).show();
			return;
		}
		sfsHelper.sendFile(fileName,this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			switch (requestCode) {
			case 1000:
				fileName = data.getData().getPath();
				fileNameTextView.setText(fileName);
				break;
			}
	}
}
