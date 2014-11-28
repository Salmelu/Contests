package cz.salmelu.contests.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import cz.salmelu.contests.model.Contest;
import cz.salmelu.contests.model.IdFactory;
import cz.salmelu.contests.net.Packet;
import cz.salmelu.contests.net.ServerInputException;

public class Server {

	private DataLoader dl;
	private DataHolder dh;
	private PacketProcesser processer;
	
	public Server() {
		dh = new DataHolder();
		processer = new PacketProcesser(dh);
		if(Config.SAVE_METHOD_FILE) {
			File f = new File(Config.SAVE_FILE);
			try {
				if(Config.VERBOSE) System.out.println("Trying to load data from file " + f.getName());
				dl = new DataLoader(f);
				dh.replaceContests(dl.load());
				if(Config.VERBOSE) System.out.println("Data successfully loaded");
			}
			catch (LoaderException e) {
				if(Config.VERBOSE) {
					System.out.println("Unable to load save file, starting new instance.");
					System.err.println(e.getLocalizedMessage());
				}
			}
		}
		
		if(Config.AUTO_SAVE && !Config.SAVE_ON_CHANGE) {
			if(Config.VERBOSE) System.out.println("AutoSaver enabled, starting AutoSaver thread");
			Thread autoSaver = new Thread(new AutoSaver(dh, dl));
			autoSaver.start();
		}
		
		dh.addContest(new Contest(IdFactory.getInstance().getNewContestId()));
		
		Thread t = new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				try {
					InetSocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
			        Socket socket = new Socket();
			        socket.connect(addr);
			        ObjectOutputStream send = new ObjectOutputStream(socket.getOutputStream());
			        ObjectInputStream get = new ObjectInputStream(socket.getInputStream());
			        send.writeByte(0x10);
			        send.writeInt(1);
			        send.flush();
			        boolean ret = get.readBoolean();
			        Object o = get.readObject();
			        socket.close();
			        System.out.println(ret);
			        if(!ret) throw (Exception) o;
			        else {
			        	System.out.println(((Contest) o).getId());
			        }
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void start() {
		// Get client
		ServerSocket sckt = null;
		try {
			sckt = new ServerSocket();
			SocketAddress addr = new InetSocketAddress(Inet4Address.getLocalHost(), Config.INET_PORT);
			sckt.bind(addr);
			if(Config.VERBOSE) System.out.println("Server socket bound at port " + Config.INET_PORT);
			while(true) {
				try {
					Socket client = sckt.accept();
					if(Config.VERBOSE) System.out.println("Accepted client: " + client.getInetAddress());
					ObjectInputStream input = new ObjectInputStream(client.getInputStream());
					ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
					int packetCode = input.read();
					if(Config.VERBOSE) System.out.println("Received packet with id " + (byte) packetCode);
					if(packetCode != -1) {
						Packet p = Packet.getPacket((byte) packetCode);
						processer.processPacket(p, input, output);
					}
					else {
						try {
							output.writeBoolean(false);
							output.writeObject(new ServerInputException("No packet received."));
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
					if(Config.VERBOSE) System.out.println("Server socket closed.");
				}
				catch (IOException e) {}
			}
		}
	}
	
	public static void main(String[] args) {
		if(Config.VERBOSE) System.out.println("Initializing server");
		Server s = new Server();
		if(Config.VERBOSE) System.out.println("Server initialized, starting server");
		s.start();
	}

}
