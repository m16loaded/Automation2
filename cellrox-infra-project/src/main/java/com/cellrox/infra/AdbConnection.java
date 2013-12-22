package com.cellrox.infra;

import java.util.ArrayList;

import jsystem.extensions.analyzers.text.FindText;

import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.LinuxDefaultCliConnection;
import com.cellrox.infra.enums.Persona;

public class AdbConnection extends LinuxDefaultCliConnection{

	public AdbConnection(String host, String user, String pass) {
		super(host, user, pass);
	}
	
	
	public void switchToPersona(Persona persona) throws Exception{
		handleCliCommand("switching to "+persona, new CliCommand("cell console "+persona));
		handleCliCommand("validate persona change",new CliCommand("getprop | grep cellname"));
		analyze(new FindText("[ro.cellrox.cellname]: ["+persona+"]"));
	}
	public void switchToHost() throws Exception{
		close();
		connect();
		handleCliCommand("Getting Back to Host", new CliCommand("adb shell"));
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
		p.setPrompt("Console opened. Press enter if you don't see a prompt.\\s*");
		p.setRegularExpression(true);
		p.setStringToSend(String.valueOf((char) 13));
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
		p.setPrompt("login: ");
		p.setStringToSend(getUser());
		prompts.add(p);

		p = new Prompt();
		p.setPrompt("Password: ");
		p.setStringToSend(getPassword());
		prompts.add(p);
		return prompts.toArray(new Prompt[prompts.size()]);
	}
	
}
