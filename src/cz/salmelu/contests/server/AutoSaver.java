package cz.salmelu.contests.server;

import cz.salmelu.contests.model.DataLoader;
import cz.salmelu.contests.model.LoaderException;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

/**
 * A class designed for automatically saving all the data. 
 * Runs another thread, which is used once a while to save
 * all the data required.
 * @author salmelu
 */
class AutoSaver extends Thread {

	/** Reference to DataHolder, which stores all the data */
	private DataHolder dh;
	/** Reference to DataLoader used to save the data */
	private DataLoader dl;
	
	/**
	 * Initializes an autosaver
	 * @param dh DataHolder to be used to extract the data
	 * @param dl DataLoader used to save the data
	 */
	protected AutoSaver(DataHolder dh, DataLoader dl) {
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
					Logger.getInstance().log("Saving thread couldn't lock data.", LoggerSeverity.WARNING);
				}
			}
			catch(LoaderException e) {
				Logger.getInstance().log("Saving thread: " + e.getLocalizedMessage(), LoggerSeverity.ERROR);
			}
		}
	}
	
}
