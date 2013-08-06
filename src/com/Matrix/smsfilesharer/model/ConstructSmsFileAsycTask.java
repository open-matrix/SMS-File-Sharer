package com.Matrix.smsfilesharer.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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
			mSmsFile.setFileName(smsFileSenderEngine.getFileName());
			mSmsFile.setMimeType(smsFileSenderEngine.getMime());
			mSmsFile.setFileContentSize(smsFileSenderEngine
					.getFileContentLength());
			mSmsFile.setNumberOfDataSms(smsFileSenderEngine
					.getNumberOfDataSms());
			mSmsFile.saveSmsFile();
			mSmsFile.setDataSmsArrayList(smsFileSenderEngine.getFullDataSms(
					mContext, mSmsFile.getSessionId()));

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
