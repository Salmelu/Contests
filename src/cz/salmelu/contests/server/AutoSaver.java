package cz.salmelu.contests.server;

import cz.salmelu.contests.model.DataLoader;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

public class AutoSaver extends Thread {

	private DataHolder dh;
	private DataLoader dl;
	
	public AutoSaver(DataHolder dh, DataLoader dl) {
		this.dh = dh;
		this.dl = dl;
	}

	@Override
	public void run() {
		this.setDaemon(true);
		while(true) {
			try {
				Thread.sleep(Config.SAVE_INTERVAL * 1000);
			} 
			catch (InterruptedException e) {
				Logger.getInstance().log("Saving thread interrupted", LoggerSeverity.ERROR);
				Logger.getInstance().log(e.getLocalizedMessage(), LoggerSeverity.ERROR);
			}
			
			try {
				if(dh.lock()) {
					Logger.getInstance().log("Saving data", LoggerSeverity.INFO);
					dl.save(dh.getAllContests());
					Logger.getInstance().log("Data successfully saved", LoggerSeverity.INFO);
					dh.unlock();
				}
				else {
					System.err.println("Couldn't lock data.");
				}
			}
			catch(LoaderException e) {
				System.err.println(e.getLocalizedMessage());
			}
		}
	}
	
}
