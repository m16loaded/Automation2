package com.cellrox.infra.log.interfaces;

public interface ILogListener {
	public void onNotify();
	public void onNotify(String report);
}
