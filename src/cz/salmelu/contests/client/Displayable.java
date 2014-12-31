package cz.salmelu.contests.client;

/**
 * An interface defining all the showable GUI components. 
 * Defines one method, displayAll, which sets Client mainPanel to itself. 
 * @author salmelu
 */
interface Displayable {
	
	/**
	 * Loads all the components necessary of the class, prepares the gui and displays it in 
	 * client's BorderPane
	 */
	void displayAll();
}
