package common;

import java.io.File;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

public class Message {

	private static final String TEMPLATES = "templates/";
	
	public static DataHandler dataHandler(File file) {
		return new DataHandler(new FileDataSource(file));
	}
}
