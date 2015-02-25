package cz.salmelu.contests.server;

import cz.salmelu.contests.model.DataLoader;
import cz.salmelu.contests.model.LoaderException;
import cz.salmelu.contests.util.Logger;
import cz.salmelu.contests.util.LoggerSeverity;

/**
 * A class designed for automatically saving all the data.<br>
 * Runs another thread, which is used once a while to save all the data required.<br>
 * It is only created when {@link Config#AUTO_SAVE} is set to true and {@link Config#SAVE_ON_CHANGE} to false.<br>
 * This thread should be manually stopped on shutdown to prevent data corruption.
 * @author salmelu
 */
class AutoSaver extends Thread {

	/** Reference to DataHolder, which stores all the data */
	private final DataHolder dh;
	/** Reference to DataLoader used to save the data */
	private final DataLoader dl;
	/** If set to false, will stop itself */
	private volatile boolean running = true;
	
	/**
	 * Initializes an autosaver with given {@link DataHolder} and {@link DataLoader}.
	 * @param dh DataHolder to be used to extract the data
	 * @param dl DataLoader used to save the data
	 */
	protected AutoSaver(DataHolder dh, DataLoader dl) {
		this.dh = dh;
		this.dl = dl;
	}
	
	/**
	 * This method should be called when the thread should end.<br>
	 * It is called by the {@link Server} when the shutdown is initiated.
	 */
	public void stopRunning() {
		this.running = false;
	}

	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(Config.SAVE_INTERVAL * 1000);
			} 
			catch (InterruptedException e) {
				Logger.getInstance().log("Saving thread interrupted", LoggerSeverity.WARNING);
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
