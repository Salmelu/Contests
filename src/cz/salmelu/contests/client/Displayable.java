package cz.salmelu.contests.client;

/**
 * An interface defining all the showable GUI components.<br>
 * Defines one method, displayAll, which sets {@link Client}'s mainPanel to itself. 
 * @author salmelu
 */
interface Displayable {
	
	/**
	 * Loads all the components necessary of the class, prepares the GUI 
	 * and displays it in the client's BorderPane.
	 */
	void displayAll();
}
