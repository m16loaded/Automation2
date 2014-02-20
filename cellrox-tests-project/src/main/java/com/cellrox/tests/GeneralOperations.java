package com.cellrox.tests;

import java.rmi.RemoteException;

import jsystem.framework.TestProperties;

import org.junit.Test;

import com.cellrox.infra.TestCase;


public class GeneralOperations extends TestCase {
	
	/**
	 * 
	 * */
	@Test	
	@TestProperties(name ="sendEmail" ,paramsInclude = "currentDevicem,persona,onOff" )
	public void sendEmail() throws RemoteException {
	}

}
