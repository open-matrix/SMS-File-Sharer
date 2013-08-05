package com.Matrix.smsfilesharer.model;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.Matrix.smsfilesharer.R;

public class ConstructSmsFileAsycTask extends AsyncTask<String, String, String> {
	String TAG = "ConstructSmsFileAsycTask";
	private SMSFile mSmsFile;
	private ProgressDialog progressDialog;
	private Context mContext;
	private SMSFileFromFileNameErrorHandler mErrorHandler;
	private boolean isExaption = false;
	private Exception mException;

	public ConstructSmsFileAsycTask(Context context, SMSFile smsFile) {
		mSmsFile = smsFile;
		mContext = context;
		mErrorHandler = (SMSFileFromFileNameErrorHandler) smsFile;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage(mContext.getString(R.string.please_wait));
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		if (params.length == 0) {
			mException = new IllegalArgumentException(
					mContext.getString(R.string.expectting_file_name));
			isExaption = true;
			return null;
		}
		try {
			SMSFileSenderEngine smsFileSenderEngine = new SMSFileSenderEngine(
					params[0]);
			ArrayList<String> fullSmsData = smsFileSenderEngine
					.getFullDataSms();
			Log.e(TAG, "Number of sms : " + fullSmsData.size());
			for (int i = 0; i < fullSmsData.size(); i++) {
				Log.e(TAG, "SqNo = " + i);
				Log.e(TAG, "Size = " + fullSmsData.get(i).length());
				Log.e(TAG, fullSmsData.get(i));
			}

		} catch (Exception e) {
			mException = e;
			isExaption = true;
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		mErrorHandler.notifyErrorReport(mException, isExaption);
	}

}
