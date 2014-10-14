package com.cellrox.infra.log.interfaces;

/**
 * This interface will be in use by log classes<br>
 * The interface will allow the observer:<br>
 * - Notify all logs that a reboot is invoked by automation<br>
 * - Notify all logs that reboot is completed 
 * @author entsec
 *
 */
public interface ILogRebootListner {
	
	public void rebootInProcess() throws Exception;
	public void rebootCompleted() throws Exception;

}
