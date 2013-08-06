package com.Matrix.smsfilesharer.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.Matrix.smsfilesharer.R;
import com.Matrix.smsfilesharer.db.SMSFileSharerDataBase;

public class SMSFile implements SMSFileFromFileNameErrorHandler,
		SMSEngineConstants {
	String TAG = "SMSFile";
	private String mSessionId;
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
	public SMSFile(Context context, String fileName, String phoneNumber) {
		mContext = context;
		mAddress = phoneNumber;
		isReceiver = true;
		isNoticeReceived = false;
		mStatus = false;
		mSessionId = generateSessionId();
		new ConstructSmsFileAsycTask(context, this).execute(fileName);
	}

	private String generateSessionId() {
		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		return Character.toString(GSM_CHARS_V1.charAt(random
				.nextInt(GSM_CHARS_V1.length())))
				+ Character.toString(GSM_CHARS_V1.charAt(random
						.nextInt(GSM_CHARS_V1.length()))) + mAddress;
	}

	// Creatting SMSFile from DB using sessionId(Receiver side)
	public SMSFile(String sesssionId, Context context) {
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

	public void setSessionId(String sessionId) {
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

	public String getSessionId() {
		return mSessionId;
	}

	@Override
	public void notifyErrorReport(Exception e, boolean isExaption) {
		if (isExaption)
			Log.e(TAG, e.getMessage());
		else
			printLog();
	}

	public void saveSmsFile() {
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(mContext);
		db.insertSmsFile(mSessionId, mAddress, isReceiver, mNumberOfDataSMS,
				mFileContentSize, mFileName, mMimeType);
		db.close();
	}

	private void printLog() {
		Log.e(TAG, mSessionId + " " + isNoticeReceived + " " + isReceiver + " "
				+ mAddress + " " + mStatus + " " + mFileContentSize + " "
				+ mFileName + " " + mMimeType + " " + mNumberOfDataSMS);
		for (int i = 0; i < mNumberOfDataSMS; i++)
			Log.e(TAG, mDataSmsArrayList.get(i).getFullData());
	}
}
