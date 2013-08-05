package com.Matrix.smsfilesharer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	String TAG = "DatabaseHelper";

	public DatabaseHelper(Context context) {
		super(context, SMSFileSharerDataBase.DATABASE_NAME, null,
				SMSFileSharerDataBase.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SMSFileSharerDataBase.CREATE_SME_FILE_DETAILS_TABLE);
		db.execSQL(SMSFileSharerDataBase.CREATE_DATA_SMS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SMSFileSharerDataBase.DROP_SME_FILE_DETAILS_TABLE);
		db.execSQL(SMSFileSharerDataBase.DROP_DATA_SMS_TABLE);
		onCreate(db);
	}
}
