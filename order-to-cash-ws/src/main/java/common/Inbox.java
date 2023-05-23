package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import jakarta.activation.DataHandler;

public class Inbox {

	private String type;
	private String name;
	
	private Inbox(String name) {
		this.type = Thread.currentThread().getStackTrace()[2]
				.getMethodName()
				.substring("of".length())
				.toLowerCase();		
		this.name = name;
	}
	
	private File from(String name) {
		File file = Paths.get("inbox", type,
				this.name,
				Thread.currentThread().getStackTrace()[2]
						.getMethodName()
						.substring("from".length())
						.toLowerCase(),
				name)
				.toFile();
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	public static final Inbox ofBuyer(String name) {
		return new Inbox(name);
	}
	
	public static final Inbox ofSupplier(String name) {
		return new Inbox(name);
	}
	
	public static final Inbox ofLogistics(String name) {
		return new Inbox(name);
	}
	
	public static final Inbox ofBank(String name) {
		return new Inbox(name);
	}
	
	public File fromBuyer(String name) {
		return from(name);
	}
	
	public File fromSupplier(String name) {
		return from(name);
	}
	
	public File fromLogistics(String name) {
		return from(name);
	}
	
	public File fromBank(String name) {
		return from(name);
	}
	
	public static final void save(DataHandler dataHandler, String path)
			throws FileNotFoundException, IOException {
		dataHandler.writeTo(new FileOutputStream(Paths.get(path).toFile()));
	}
}
