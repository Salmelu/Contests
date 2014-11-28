package cz.salmelu.contests.server;

import java.io.File;

public class Server {

	private DataLoader dl;
	private DataHolder dh;
	private PacketProcesser processer;
	
	public Server() {
		dh = new DataHolder();
		processer = new PacketProcesser();
		if(Config.SAVE_METHOD_FILE) {
			File f = new File(Config.SAVE_FILE);
			try {
				dl = new DataLoader(f);
				dh.replaceContests(dl.load());
			}
			catch (LoaderException e) {
				System.out.println("Unable to load save file, starting new instance.");
				if(Config.VERBOSE) {
					System.err.println(e.getLocalizedMessage());
				}
			}
		}
		
		if(Config.AUTO_SAVE && !Config.SAVE_ON_CHANGE) {
			Thread autoSaver = new Thread(new AutoSaver(dh, dl));
			autoSaver.start();
		}
	}
	
	public void start() {
		// Get client
		while(true) {
			
		}
	}
	
	public static void main(String[] args) {
		Server s = new Server();
		s.start();
	}

}
