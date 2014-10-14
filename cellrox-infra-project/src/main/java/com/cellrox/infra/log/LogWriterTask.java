package com.cellrox.infra.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogWriterTask implements Runnable {

	private BufferedReader br;
	private Process mProccess;
	private StringBuffer log;

	public LogWriterTask() {
		log = new StringBuffer();
	}

	@Override
	public void run() {
		try {
			ProcessBuilder ps = new ProcessBuilder("adb logcat");

			ps.redirectErrorStream(true);
			mProccess = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(mProccess.getInputStream()));
			String line = null;

			line = br.readLine();

			while ((line != null) && !(Thread.currentThread().isInterrupted())) {
				log.append(line + "\n");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
	
	public void stop() {
		mProccess.destroy();
	}
	
	public String getLog(){
		return new String(log.toString());
	}

}
