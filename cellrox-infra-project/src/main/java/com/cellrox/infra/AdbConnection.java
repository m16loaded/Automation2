package com.cellrox.infra;

import java.util.ArrayList;

import jsystem.extensions.analyzers.text.FindText;

import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.LinuxDefaultCliConnection;
import com.cellrox.infra.enums.Persona;

public class AdbConnection extends LinuxDefaultCliConnection{

	private String deviceSerial; 
	
	public AdbConnection(String host, String user, String pass, String deviceSerial) {
		super(host, user, pass);
		this.deviceSerial = deviceSerial;
	}
	
	
	public void switchToPersona(Persona persona) throws Exception{
		handleCliCommand("switching to "+persona, new CliCommand("cell console "+persona));
		handleCliCommand("validate persona change",new CliCommand("getprop | grep cellname"));
		analyze(new FindText("[ro.cellrox.cellname]: ["+persona+"]"));
	}
	public void switchToHost() throws Exception{
		close();
		connect();
		handleCliCommand("Getting Back to Host", new CliCommand("adb -s "+deviceSerial+" shell"));
		handleCliCommand("validate Host",new CliCommand("getprop | grep role"));
		analyze(new FindText("[ro.cellrox.role]: [host]"));
	}

	public Prompt[] getPrompts() {
		ArrayList<Prompt> prompts = new ArrayList<Prompt>();		
		Prompt p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("# ");
		prompts.add(p);
		
		
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("#\\s*");
		p.setStringToSend(String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("Console opened. Press enter if you don't see a prompt.");
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("Console opened. Press enter if you don't see a prompt\\.\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);

		 
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("and height \\d*\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("Console opened. Press enter if you don't see a prompt.\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("at java.lang.Thread.run(Thread.java:841)\\s*(\n)\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setCommandEnd(false);
		p.setPrompt("at java.lang.Thread.run(Thread.java:841)");
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("Console opened. Press enter if you don't see a prompt.\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);
		
		
		p.setAddEnter(true);
		p.setPrompt("nc: short write\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));
		prompts.add(p);

		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("nc: can't connect to remote host (127.0.0.1): Connection refused");
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));		
		prompts.add(p);
		
		
		p = new Prompt();
		p.setAddEnter(true);
		p.setPrompt("opening application");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13) + String.valueOf((char) 13));		
		prompts.add(p);

		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("$ ");
		prompts.add(p);
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("> ");
		prompts.add(p);
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("INSTRUMENTATION_CODE: 0");
		prompts.add(p);
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("INSTRUMENTATION_STATUS_CODE: 1");
		prompts.add(p);
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setAddEnter(true);
		p.setPrompt("Killed");
		prompts.add(p);
		
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt("nc: bind: Address already in use");
		prompts.add(p);
		
		p = new Prompt();
		p.setPrompt("login: ");
		p.setStringToSend(getUser());
		prompts.add(p);

		p = new Prompt();
		p.setPrompt("Password: ");
		p.setStringToSend("1q2w3e4r");
		p.setAddEnter(true);
		prompts.add(p);
		
		p = new Prompt();
		p.setCommandEnd(true);
		p.setPrompt(".zip\\s*");
		p.setRegularExpression(true);
		prompts.add(p);

		
		return prompts.toArray(new Prompt[prompts.size()]);

	}
	
}
