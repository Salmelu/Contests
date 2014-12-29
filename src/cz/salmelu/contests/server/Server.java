package cz.salmelu.contests.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.salmelu.contests.model.Category;
import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.DataLoader;
import cz.salmelu.contests.model.Discipline;
import cz.salmelu.contests.model.LoaderException;
import cz.salmelu.contests.model.ScoreMode;
import cz.salmelu.contests.model.Team;
import cz.salmelu.contests.model.TeamCategory;
import cz.salmelu.contests.model.TeamContestant;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerError;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

/**
 * A main class used by server. Creates a socket, listens on it and hands all the packets to processer.
 * @author salmelu
 */
public class Server {

	/** Instance of DataLoader to be used for loading/saving data */
	private DataLoader dl;
	/** Instance of DataHolder to store the data */
	private DataHolder dh;
	/** Instance of PacketProcesser to process the packets */
	private PacketProcesser processer;
	
	/**
	 * Creates a new server instance
	 */
	public Server() {
		dh = new DataHolder();
		processer = new PacketProcesser(dh);
		if(Config.SAVE_METHOD_FILE) {
			File f = new File(Config.SAVE_FILE);
			try {
				Logger.getInstance().log("Trying to load data from file " + f.getName(), LoggerSeverity.VERBOSE);
				dl = new DataLoader(f);
				dh.replaceContests(dl.load());
				Logger.getInstance().log("Data successfully loaded from " + f.getName(), LoggerSeverity.INFO);
			}
			catch (LoaderException e) {
				Logger.getInstance().log("Unable to load save file, starting new instance.", LoggerSeverity.WARNING);
				Logger.getInstance().log(e.getLocalizedMessage(), LoggerSeverity.WARNING);
				// TODO - remove, testing stuff
				Contest c = new Contest("Hello");
				Discipline d1 = new Discipline("Pozdrav");
				Discipline d2 = new Discipline("Utok");
				Discipline d3 = new Discipline("Obrana");
				c.addDiscipline(d1);
				c.addDiscipline(d2);
				c.addDiscipline(d3);
				Category ct1 = new Category("Bla");
				ct1.addDiscipline(d1);
				ct1.addDiscipline(d2);
				Category ct2 = new Category("Dla");
				ct2.addDiscipline(d2);
				ct2.addDiscipline(d3);
				c.addCategory(ct1);
				c.addCategory(ct2);
				TeamContestant p1 = new TeamContestant("Lama", "Lamut", ct1);
				TeamContestant p2 = new TeamContestant("Lama2", "Lamut2", ct1);
				TeamContestant p3 = new TeamContestant("Lama3", "Lamut3", ct1);
				TeamContestant p4 = new TeamContestant("Lama4", "Lamut4", ct2);
				Team t1 = new Team("Lamas");
				Team t2 = new Team("Noobs");
				t1.addContestant(p1);
				t2.addContestant(p2);
				t1.addContestant(p3);
				t2.addContestant(p4);
				c.addContestant(p1);
				c.addContestant(p2);
				c.addContestant(p3);
				c.addContestant(p4);
				TeamCategory tc = new TeamCategory("Main cat", ScoreMode.Additive);
				c.addTeamCategory(tc);
				c.addTeam(tc, t1);
				c.addTeam(tc, t2);
				dh.addContest(c);
			}
		}
		
		if(Config.AUTO_SAVE && !Config.SAVE_ON_CHANGE) {
			Logger.getInstance().log("AutoSaver enabled, starting AutoSaver thread", LoggerSeverity.INFO);
			Thread autoSaver = new Thread(new AutoSaver(dh, dl));
			autoSaver.start();
		}
	}
	
	/**
	 * Starts the server by opening a server socket and starting to listen
	 */
	public void start() {
		// Get client
		ServerSocket sckt = null;
		try {
			sckt = new ServerSocket();
			SocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
			sckt.bind(addr);
			Logger.getInstance().log("Server socket bound at port " + Config.INET_PORT, LoggerSeverity.INFO);
			while(true) {
				try {
					Socket client = sckt.accept();
					Logger.getInstance().log("Accepted client: " + client.getInetAddress(), LoggerSeverity.VERBOSE);
					ObjectInputStream input = new ObjectInputStream(client.getInputStream());
					ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
					int packetCode = input.read();
					Logger.getInstance().log("Received packet with id " + (byte) packetCode, LoggerSeverity.VERBOSE);
					if(packetCode != -1) {
						Packet p = Packet.getPacket((byte) packetCode);
						processer.processPacket(p, input, output);
					}
					else {
						try {
							output.writeBoolean(false);
							output.writeObject(ServerError.InvalidPacket);
						}
						catch(IOException e1) {
							e1.printStackTrace();
						}
					}
					try {
						output.flush();
						input.close();
						output.close();
						client.close();
					}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
				catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
				}
			}
		}
		catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
		finally {
			if(sckt != null) {
				try {
					sckt.close();
					Logger.getInstance().log("Server socket closed.", LoggerSeverity.VERBOSE);
				}
				catch (IOException e) {}
			}
		}
	}
	
	/**
	 * Entry point of the program
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		// Start logging
		String logs[] = Config.LOGGING.split("-");
		switch(logs[1]) {
		case "verbose":
			Logger.getInstance().openOutput(System.out, LoggerSeverity.VERBOSE);
			break;
		case "info":
			Logger.getInstance().openOutput(System.out, LoggerSeverity.INFO);
			break;
		case "warning":
			Logger.getInstance().openOutput(System.out, LoggerSeverity.WARNING);
			break;	
		}
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		try {
			switch(logs[0]) {
			case "verbose":
				Logger.getInstance().openOutput(new File(Config.LOGGING_PATH
						+ date + "-server-verbose.log"), LoggerSeverity.VERBOSE);
			case "info":
				Logger.getInstance().openOutput(new File(Config.LOGGING_PATH
						+ date + "-server-info.log"), LoggerSeverity.INFO);
			case "warning":
				Logger.getInstance().openOutput(new File(Config.LOGGING_PATH
						+ date + "-server-warning.log"), LoggerSeverity.WARNING);
			}	
		}
		catch (FileNotFoundException e) {
			Logger.getInstance().log("Couldn't open log files", LoggerSeverity.ERROR);
			Logger.getInstance().log(e.getLocalizedMessage(), LoggerSeverity.ERROR);
		}
		
		Logger.getInstance().logAlways("Initializing server");
		Server s = new Server();
		Logger.getInstance().logAlways("Server initialized, starting server");
		s.start();
	}

}
