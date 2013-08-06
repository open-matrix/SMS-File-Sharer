package com.Matrix.smsfilesharer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.Matrix.smsfilesharer.R;
import com.Matrix.smsfilesharer.model.SMSFile;

public class SMSFileSharerDataBase {

	String TAG = "SMSFileSharerDataBase";
	private final Context mContext;
	private DatabaseHelper mDatabaseHelper;
	public static final String DATABASE_NAME = "sms_file_sharer";
	public static final int DATABASE_VERSION = 1;
	// TABLES
	public static final String TABLE_SMS_FILE_DETAILS = "sms_file_details";
	public static final String TABLE_DATA_SMS = "data_sms";
	// COLUMNS
	public static final String COL_SESSION_ID = "session_id";
	public static final String COL_IS_NOTICE_RECIVED = "is_notice_received";
	public static final String COL_IS_RECEIVER = "is_receiver";
	public static final String COL_ADDRESS_NUMBER = "address_number";
	public static final String COL_NUMBER_OF_DATA_SMS = "number_of_data_sms";
	public static final String COL_FILE_CONTENT_SIZE = "file_content_size";
	public static final String COL_FILE_NAME = "file_name";
	public static final String COL_MIME_TYPE = "mime_type";
	public static final String COL_SEQ_NUMBER = "seq_number";
	public static final String COL_DATA = "data";
	public static final String COL_STATUS = "status";
	public static final String COL_SENDER_STATUS = "sender_status";
	public static final String COL_COUNT = "count(*)";

	public static final String CREATE_SME_FILE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_SMS_FILE_DETAILS
			+ "("
			+ COL_SESSION_ID
			+ " VARCHAR(256),"
			+ COL_IS_NOTICE_RECIVED
			+ " BOOLEAN NOT NULL DEFAULT 0, "
			+ COL_ADDRESS_NUMBER
			+ " VARCHAR(50), "
			+ COL_IS_RECEIVER
			+ " BOOLEAN, "
			+ COL_STATUS
			+ " BOOLEAN NOT NULL DEFAULT 0, "
			+ COL_NUMBER_OF_DATA_SMS
			+ " INTEGER, "
			+ COL_FILE_CONTENT_SIZE
			+ " INTEGER, "
			+ COL_FILE_NAME
			+ " VARCHAR(256), "
			+ COL_MIME_TYPE
			+ " VARCHAR(50), " + "PRIMARY KEY (" + COL_SESSION_ID + "));";

	public static final String DROP_SME_FILE_DETAILS_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_SMS_FILE_DETAILS + ";";

	public static final String CREATE_DATA_SMS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_DATA_SMS
			+ "("
			+ COL_SESSION_ID
			+ "  VARCHAR(256), "
			+ COL_SEQ_NUMBER
			+ " INTEGER, "
			+ COL_DATA
			+ " VARCHAR(160), "
			+ COL_SENDER_STATUS
			+ " BOOLEAN NOT NULL DEFAULT 0, "
			+ "PRIMARY KEY ("
			+ COL_SESSION_ID
			+ ","
			+ COL_SEQ_NUMBER
			+ "), FOREIGN KEY("
			+ COL_SESSION_ID
			+ ") REFERENCES "
			+ TABLE_SMS_FILE_DETAILS + " (" + COL_SESSION_ID + "));";
	public static final String DROP_DATA_SMS_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_DATA_SMS + ";";

	public SMSFileSharerDataBase(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(mContext);
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public String[] getDataSmsContent(String sessionId, int seqNumber) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DATA_SMS, new String[] { COL_DATA,
				COL_SENDER_STATUS }, COL_SESSION_ID + "=? AND "
				+ COL_SEQ_NUMBER + "=? ", new String[] { sessionId,
				seqNumber + "" }, null, null, null);
		String result[] = new String[2];
		if (cursor.moveToNext()) {
			result[0] = cursor.getString(cursor.getColumnIndex(COL_DATA));
			result[1] = cursor.getString(cursor
					.getColumnIndex(COL_SENDER_STATUS));
		}
		cursor.close();
		return result;
	}

	public void setDataSmsStatus(String sessionId, int seqNumber, boolean status) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_SENDER_STATUS, status);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long result = db.update(TABLE_DATA_SMS, contentValues, COL_SESSION_ID
				+ "=? AND " + COL_SEQ_NUMBER + "=? ", new String[] { sessionId,
				seqNumber + "" });
		if (result == 0)
			throw new android.database.SQLException(
					mContext.getString(R.string.error_while_setting_sms_status));
	}

	public void getSMSFileContent(SMSFile smsFile) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DATA_SMS, new String[] {
				COL_IS_NOTICE_RECIVED, COL_ADDRESS_NUMBER, COL_IS_RECEIVER,
				COL_STATUS, COL_NUMBER_OF_DATA_SMS, COL_FILE_CONTENT_SIZE,
				COL_FILE_NAME, COL_MIME_TYPE }, COL_SESSION_ID + "=? ",
				new String[] { smsFile.getSessionId() + "" }, null, null, null);
		if (cursor.moveToNext()) {
			smsFile.setIsNoticeReceived(cursor.getString(
					cursor.getColumnIndex(COL_IS_NOTICE_RECIVED)).equals("1") ? true
					: false);
			smsFile.setAddress(cursor.getString(cursor
					.getColumnIndex(COL_ADDRESS_NUMBER)));
			smsFile.setIsReciever(cursor.getString(
					cursor.getColumnIndex(COL_IS_RECEIVER)).equals("1") ? true
					: false);
			smsFile.setStatus(cursor.getString(
					cursor.getColumnIndex(COL_STATUS)).equals("1") ? true
					: false);
			smsFile.setNumberOfDataSms(Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(COL_NUMBER_OF_DATA_SMS))));
			smsFile.setFileContentSize(Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(COL_FILE_CONTENT_SIZE))));
			smsFile.setFileName(cursor.getString(cursor
					.getColumnIndex(COL_FILE_NAME)));
			smsFile.setMimeType(cursor.getString(cursor
					.getColumnIndex(COL_MIME_TYPE)));
		}
		cursor.close();
	}

	public boolean insertDataSms(String sessionId, int seqNum, String data) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_SESSION_ID, sessionId);
		contentValues.put(COL_SEQ_NUMBER, seqNum);
		contentValues.put(COL_DATA, data);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long result = db.insert(TABLE_DATA_SMS, COL_SESSION_ID, contentValues);
		return result != -1;
	}

	public boolean insertSmsFile(String sessionId, String address,
			boolean isReceiver, int numberOfDataSMS, int fileContentSize,
			String fileName, String mimeType) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_SESSION_ID, sessionId);
		contentValues.put(COL_ADDRESS_NUMBER, address);
		contentValues.put(COL_IS_RECEIVER, isReceiver);
		contentValues.put(COL_NUMBER_OF_DATA_SMS, numberOfDataSMS);
		contentValues.put(COL_FILE_CONTENT_SIZE, fileContentSize);
		contentValues.put(COL_FILE_NAME, fileName);
		contentValues.put(COL_MIME_TYPE, mimeType);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long result = db.insert(TABLE_SMS_FILE_DETAILS, COL_SESSION_ID,
				contentValues);
		return result != -1;
	}
}
