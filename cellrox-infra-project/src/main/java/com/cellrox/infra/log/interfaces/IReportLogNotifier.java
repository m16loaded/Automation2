package com.cellrox.infra.log.interfaces;


public interface IReportLogNotifier {
	
	public void setListener(ILogListener listener);
	
	public void notifyListener();
	public void notifyListener(String report);


}
