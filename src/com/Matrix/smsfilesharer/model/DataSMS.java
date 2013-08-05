package com.Matrix.smsfilesharer.model;

import android.content.Context;
import android.database.SQLException;

import com.Matrix.smsfilesharer.db.SMSFileSharerDataBase;

public class DataSMS {
	private String mSessioId;
	private int mSeqNumber;
	private String mFullData;
	private boolean mStatus;
	private Context mContext;

	public DataSMS(String sessionId, int seqNumber, Context context) {
		mSessioId = sessionId;
		mSeqNumber = seqNumber;
		mContext = context;
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(mContext);
		String[] dataSmsContent = db.getDataSmsContent(mSessioId, mSeqNumber);
		db.close();
		mFullData = dataSmsContent[0];
		mStatus = dataSmsContent[1].equals("1") ? true : false;
	}

	public String getSessionId() {
		return mSessioId;
	}

	public int getSeqNumber() {
		return mSeqNumber;
	}

	public String getFullData() {
		return mFullData;
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

	public String getSmsBody() {
		String[] sms = mFullData.split(CommenConstance.SMS_NEW_LINE_SPLITTER);
		return sms[1];
	}

	public String toString() {
		return getSmsBody();
	}
}
