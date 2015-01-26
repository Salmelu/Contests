package cz.salmelu.contests.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * A helper class for loading the data for the server.<br>
 * Allows loading server data from a file specified in the configuration file.<br>
 * Supports loading and saving a map of all contests. Also saves and loads an {@link IdFactory}
 * instance to continue using the same set of unique ids.
 * @author salmelu
 */
public class DataLoader {

	/**	Represents the save file */
	private File saveFile;
	@SuppressWarnings("unused")
	private String dbName;
	
	/**
	 * Constructs a new data loader for loading from file.
	 * @param saveFile file to be loaded from
	 */
	public DataLoader(File saveFile) {
		this.saveFile = saveFile;
	}
	
	/**
	 * Loads contests. Uses the settings given with class' constructor.
	 * @return A map of all contests found in the saving location
	 * @throws LoaderException Loading was unsuccessful. Either the save file doesn't exists, the Loader couldn't read the file or some of the required classes are missing.
	 */
	public Map<Integer, Contest> load() throws LoaderException {
		return loadFromFile();
	}
	
	/**
	 * Loads all contests from a file and resets an {@link IdFactory} instance.
	 * @return A map of all contests found in the save file
	 * @throws LoaderException Loading was unsuccessful. Either the save file doesn't exists, the Loader couldn't read the file or some of the required classes are missing.
	 */
	private Map<Integer, Contest> loadFromFile() throws LoaderException {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
			IdFactory idf = (IdFactory) ois.readObject();
			IdFactory.loadFactory(idf);
			int contestSize = ois.readInt();
			Map<Integer, Contest> list = new TreeMap<>();
			for(int i=0; i<contestSize; i++) {
				list.put(ois.readInt(), (Contest) ois.readObject()); 
			}
			return list;
		}
		catch (FileNotFoundException e) {
			throw new LoaderException(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new LoaderException(e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			throw new LoaderException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Saves contests. Uses the settings given with class' constructor.
	 * @param contests saved data
	 * @throws LoaderException Loader couldn't access or write to the file
	 */
	public void save(Map<Integer, Contest> contests) throws LoaderException {
		saveToFile(contests);
	}
	
	/**
	 * Saves contests to a file. Also saves an {@link IdFactory} instance.
	 * @param contests data which will be saved
	 * @throws LoaderException Loader couldn't access or write to the file
	 */
	private void saveToFile(Map<Integer, Contest> contests) throws LoaderException  {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
			oos.writeObject(IdFactory.getInstance());
			oos.writeInt(contests.size());
			for(Map.Entry<Integer, Contest> entry : contests.entrySet()) {
				oos.writeInt(entry.getKey());
				oos.writeObject(entry.getValue());
			}
		} catch (FileNotFoundException e) {
			throw new LoaderException(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new LoaderException(e.getLocalizedMessage());
		}
	}
}
