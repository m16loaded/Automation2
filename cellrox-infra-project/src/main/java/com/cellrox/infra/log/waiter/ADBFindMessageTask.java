package com.cellrox.infra.log.waiter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.cellrox.infra.log.interfaces.ILogListener;


public class ADBFindMessageTask extends AbsWaitForReportTask {

	private String mFilter;
	private String mSearchParams;
	private ILogListener mLogListener;
	private Process mProccess;
	private boolean mReturnMessage = false;
	private String logCommand;

	public ADBFindMessageTask(String logCommand, boolean returnMessage, String filter, String searchParams) {
		super(logCommand, returnMessage, filter, searchParams);
	}

	@Override
	public void run() {
		try {
			ProcessBuilder ps = new ProcessBuilder("adb", "shell", logCommand + " | grep -F '" + mFilter + "'");

			ps.redirectErrorStream(true);
			mProccess = ps.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(mProccess.getInputStream()));
			String line;

			while (!(Thread.currentThread().isInterrupted()) & (line = in.readLine()) != null) {

				System.out.println(line);
				if (stringContainsAllItems(line)) {
					System.out.println("Found String in Log : " + line);
					mProccess.destroy();
					notifyListener(line);
				}
			}
		} catch (Exception e) {

		}
	}

	

	@Override
	public void setListener(ILogListener listener) {
		this.mLogListener = listener;
	}

	@Override
	public void notifyListener() {
		mLogListener.onNotify();
	}

	public void stop() {
		mProccess.destroy();
	}

	@Override
	public void notifyListener(String report) {
		mLogListener.onNotify(report);
	}

}
