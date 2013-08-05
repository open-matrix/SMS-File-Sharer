package com.Matrix.smsfilesharer.controler;

import android.content.Context;

import com.Matrix.smsfilesharer.model.SMSFile;

public class SMSFileSharerHelper {

	public void sendFile(String fileName,Context context) {
		SMSFile smsFile=new SMSFile(fileName, context);
	}

	public String receiveFile() {
		return "";
	}

	public void getLogs() {

	}

	public void currentlySendingOrReceivingLogs() {

	}

	public void pauseSending(String sessionId) {

	}

	public void resumeSending(String sessionId) {

	}

	public void pauseAllSending() {

	}

	public void resumeAllSending() {

	}

	public void cancelSending(String sessionId) {

	}

	public void cancelReceiving(String sessionId) {

	}

	public void cancelAllSending() {

	}

	public void cancelAllReceiving() {

	}
}
