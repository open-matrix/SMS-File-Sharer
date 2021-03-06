package com.Matrix.smsfilesharer.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import android.content.Context;

import com.Matrix.smsfilesharer.R;
import com.Matrix.smsfilesharer.db.SMSFileSharerDataBase;

public class SMSFileSenderEngine implements SMSEngineConstants {
	String TAG = "SMSFileSenderEngine";
	private byte[] mEncoded7bits;
	private int mCurrentEncodedArrayIndex;
	private int mFileContentLength;
	private String mFileName;
	private String mMime;
	private ArrayList<String> mFullDataSmsStringArrayList;

	public SMSFileSenderEngine(String fileName) throws Exception {
		mFileName = new File(fileName).getName();
		mMime = getExtention();
		fileName = compressInputFile(fileName);
		byte[] fileContents = read(fileName);
		mFileContentLength = fileContents.length;
		mEncoded7bits = new byte[fileContents.length * 8 / 7];
		mCurrentEncodedArrayIndex = 0;
		encode(fileContents);
		new File(fileName).delete();
		mFullDataSmsStringArrayList = costructFullDataSmsFromString(gsmEncode(
				mEncoded7bits, mCurrentEncodedArrayIndex));
	}

	private String getExtention() {
		String extension = "";
		int i = mFileName.lastIndexOf('.');
		int p = Math.max(mFileName.lastIndexOf('/'),
				mFileName.lastIndexOf('\\'));
		if (i > p)
			extension = mFileName.substring(i + 1);
		if (extension.length() > 4)
			extension = extension.substring(0, 5);
		return extension.toLowerCase(Locale.getDefault());
	}

	private ArrayList<String> costructFullDataSmsFromString(
			String fullDataSmsString) {
		ArrayList<String> tempFullDataSmsArrayList = new ArrayList<String>();
		String tempSms = "";
		int j = 0;
		for (int i = 0; i < fullDataSmsString.length(); i++) {
			if (i % (DEFAULT_SMS_LENGTH - Integer.toString(j).length()) == 0
					&& i != 0) {
				tempFullDataSmsArrayList.add(tempSms);
				tempSms = "" + fullDataSmsString.charAt(i);
				j++;
			} else
				tempSms += fullDataSmsString.charAt(i);
		}
		if (!tempSms.equals(""))
			tempFullDataSmsArrayList.add(tempSms);
		return tempFullDataSmsArrayList;
	}

	public ArrayList<DataSMS> getFullDataSms(Context context, String sessionId) {
		String sessionSignatureChars = sessionId.substring(0, 2);
		ArrayList<DataSMS> tempDataSmsArrayList = new ArrayList<DataSMS>();
		SMSFileSharerDataBase db = new SMSFileSharerDataBase(context);
		for (int i = 0; i < mFullDataSmsStringArrayList.size(); i++) {
			if (!db.insertDataSms(sessionId, i, sessionSignatureChars + (i + 1)
					+ CommenConstance.SMS_NEW_LINE_SPLITTER
					+ mFullDataSmsStringArrayList.get(i)))
				throw new RuntimeException(
						context.getString(R.string.save_sms_file_to_db_first));
			tempDataSmsArrayList.add(new DataSMS(sessionId, i, context));
		}
		db.close();
		return tempDataSmsArrayList;
	}

	public int getFileContentLength() {
		return mFileContentLength;
	}

	public String getFileName() {
		return mFileName;
	}

	public String getMime() {
		return mMime;
	}

	public int getNumberOfDataSms() {
		return mFullDataSmsStringArrayList.size();
	}

	private String compressInputFile(String fileName) throws Exception {
		String zipFileName = fileName + ".zip";
		InputStream is = null;
		try {
			File inputFile = new File(fileName);
			ZipFile zipFile = new ZipFile(zipFileName);
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters
					.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
			parameters.setFileNameInZip(inputFile.getName());
			parameters.setSourceExternalStream(true);
			is = new FileInputStream(fileName);
			zipFile.addStream(is, parameters);
		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		return zipFileName;
	}

	private void encode(byte[] fileContents) {
		byte[] setOf7bytes;
		for (int i = 0; i < fileContents.length / 7; i++) {
			setOf7bytes = new byte[7];
			for (int j = 0; j < 7; j++)
				setOf7bytes[j] = fileContents[i * 7 + j];
			encodeProcess(setOf7bytes, 6);
		}
		int remainingBytes = fileContents.length % 7;
		if (remainingBytes > 0) {
			setOf7bytes = new byte[remainingBytes];
			for (int i = 0; i < remainingBytes; i++)
				setOf7bytes[i] = fileContents[fileContents.length
						- remainingBytes + i];
			encodeProcess(setOf7bytes, remainingBytes - 1);
		}
	}

	private String gsmEncode(byte[] body, int len) {
		StringBuilder eGSM = new StringBuilder();
		for (int i = 0; i < len; i++)
			eGSM.append(GSM_CHARS_V1.charAt(body[i]));
		return eGSM.toString();
	}

	private void encodeProcess(byte[] setOf7bytes, int lim) {
		byte firstByte = (byte) (setOf7bytes[0] >>> 1);
		firstByte = (byte) (firstByte & 0x7F);
		mEncoded7bits[mCurrentEncodedArrayIndex++] = firstByte;
		if (lim > 0) {
			int mask[] = { 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F };
			for (int i = 1; i <= lim; i++)
				mEncoded7bits[mCurrentEncodedArrayIndex++] = (byte) ((byte) ((setOf7bytes[i - 1] & mask[i - 1]) << (7 - i)) | (unsignedRightShift(
						setOf7bytes[i], (i + 1))));
			if (lim == 6)
				mEncoded7bits[mCurrentEncodedArrayIndex++] = (byte) (setOf7bytes[6] & 0x7F);
		}
	}

	private byte unsignedRightShift(byte b, int lim) {
		for (int i = 0; i < lim; i++)
			b = (byte) (((byte) b >>> 1) & 0x7F);
		return b;
	}

	private byte[] read(String aInputFileName) throws Exception {
		File file = new File(aInputFileName);
		byte[] result = new byte[(int) file.length()];
		try {
			InputStream input = null;
			try {
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));
				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;
					int bytesRead = input.read(result, totalBytesRead,
							bytesRemaining);
					if (bytesRead > 0)
						totalBytesRead = totalBytesRead + bytesRead;
				}
			} finally {
				input.close();
			}
		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		}
		return result;
	}

	void write(byte[] aInput, String aOutputFileName) throws Exception {
		try {
			OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(
						aOutputFileName));
				output.write(aInput);
			} finally {
				output.close();
			}
		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		}
	}
}
