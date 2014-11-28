package cz.salmelu.contests.server;

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
				System.err.println("Saving thread interrupted.");
			}
			
			try {
				if(dh.lock()) {
					if(Config.VERBOSE) System.out.println("Saving data");
					dl.save(dh.getAllContests());
					if(Config.VERBOSE) System.out.println("Data successfully saved");
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
