package com.Matrix.smsfilesharer.model;

import com.Matrix.smsfilesharer.db.SMSFileSharerDataBase;

import android.content.Context;
import android.database.SQLException;

public class DataSMS {
	private int mSessioId;
	private int mSeqNumber;
	private String mData;
	private boolean mStatus;
	private Context mContext;

	public DataSMS(int sessionId, int seqNumber, Context context) {
		mSessioId = sessionId;
		mSeqNumber = seqNumber;
		mContext = context;
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(mContext);
		String[] dataSmsContent = db.getDataSmsContent(mSessioId, mSeqNumber);
		db.close();
		mData = dataSmsContent[0];
		mStatus = dataSmsContent[1].equals("1") ? true : false;
	}

	public int getSessionId() {
		return mSessioId;
	}

	public int getSeqNumber() {
		return mSeqNumber;
	}

	public String getData() {
		return mData;
	}

	public boolean isSend() {
		return mStatus;
	}

	public void setStatus(boolean status) {
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(mContext);
		try {
			db.setDataSmsStatus(mSessioId, mSeqNumber, status);
			mStatus = status;
		} catch (SQLException e) {
			throw e;
		} finally {
			db.close();
		}
	}
}
