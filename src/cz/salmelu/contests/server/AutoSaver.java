package cz.salmelu.contests.server;

public class AutoSaver implements Runnable {

	private DataHolder dh;
	private DataLoader dl;
	
	public AutoSaver(DataHolder dh, DataLoader dl) {
		this.dh = dh;
		this.dl = dl;
	}

	@Override
	public void run() {
		Thread.currentThread().setDaemon(true);
		while(true) {
			try {
				Thread.sleep(Config.SAVE_INTERVAL);
			} 
			catch (InterruptedException e) {
				System.err.println("Saving thread interrupted.");
			}
			
			try {
				if(dh.lock()) {
					dl.save(dh.getAllContests());
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
