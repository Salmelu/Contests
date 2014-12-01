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

import cz.salmelu.contests.server.LoaderException;

public class DataLoader {

	private final boolean usingDatabase;
	private File saveFile;
	@SuppressWarnings("unused")
	private String dbName;
	
	public DataLoader(File saveFile) {
		this.usingDatabase = false;
		this.saveFile = saveFile;
	}
	
	public Map<Integer, Contest> load() throws LoaderException {
		if(usingDatabase) {
			throw new UnsupportedOperationException();
		}
		else {
			return loadFromFile();
		}
	}
	
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
	
	public void save(Map<Integer, Contest> contests) throws LoaderException {
		if(usingDatabase) {
			throw new UnsupportedOperationException();
		}
		else {
			saveToFile(contests);
		}
	}
	
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
