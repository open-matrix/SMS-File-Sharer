package com.Matrix.smsfilesharer.model;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.Matrix.smsfilesharer.R;
import com.Matrix.smsfilesharer.db.SMSFileSharerDataBase;

public class SMSFile implements SMSFileFromFileNameErrorHandler {
	String TAG = "SMSFile";
	private int mSessionId;
	private boolean isNoticeReceived;
	private String mAddress;
	private boolean isReceiver;
	private boolean mStatus;
	private int mNumberOfDataSMS;
	private int mFileContentSize;
	private String mFileName;
	private String mMimeType;
	private ArrayList<DataSMS> mDataSmsArrayList;

	private Context mContext;

	// Creatting SMSFile from given file(Sender side)
	public SMSFile(String fileName, Context context) {
		mContext = context;
		new ConstructSmsFileAsycTask(context, this).execute(fileName);
	}

	// Creatting SMSFile from DB using sessionId(Receiver side)
	public SMSFile(int sesssionId, Context context) {
		mContext = context;
		mSessionId = sesssionId;
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(mContext);
		db.getSMSFileContent(this);
		db.close();
		getDataSmssAsync();
	}

	private void getDataSmssAsync() {
		mDataSmsArrayList = new ArrayList<DataSMS>();
		new AsyncTask<String, String, String>() {
			private ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(mContext);
				progressDialog.setMessage(mContext
						.getString(R.string.please_wait));
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(false);
				progressDialog.show();
			}

			@Override
			protected String doInBackground(String... params) {
				for (int i = 0; i < mNumberOfDataSMS; i++)
					mDataSmsArrayList.add(new DataSMS(mSessionId, i, mContext));
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				progressDialog.dismiss();
			}
		}.execute();
	}

	public void setSessionId(int sessionId) {
		mSessionId = sessionId;
	}

	public void setIsNoticeReceived(boolean isNoticeReceived) {
		this.isNoticeReceived = isNoticeReceived;
	}

	public void setAddress(String address) {
		mAddress = address;
	}

	public void setFileName(String fileName) {
		mFileName = fileName;
	}

	public void setMimeType(String mimeType) {
		mMimeType = mimeType;
	}

	public void setFileContentSize(int fileContentSize) {
		mFileContentSize = fileContentSize;
	}

	public void setIsReciever(boolean isReciever) {
		this.isReceiver = isReciever;
	}

	public void setDataSmsArrayList(ArrayList<DataSMS> dataSmsArrayList) {
		mDataSmsArrayList = dataSmsArrayList;
	}

	public void setStatus(boolean status) {
		mStatus = status;
	}

	public void setNumberOfDataSms(int numberOfDataSms) {
		mNumberOfDataSMS = numberOfDataSms;
	}

	public int getNumberOfDataSms() {
		return mNumberOfDataSMS;
	}

	public int getSessionId() {
		return mSessionId;
	}

	@Override
	public void notifyErrorReport(Exception e, boolean isExaption) {
		if (isExaption)
			Log.e(TAG, e.getMessage());
	}

}
