package common;

import java.util.ResourceBundle;

public class Settings {

	public static final String SERVER_ENDPOINT;
	public static final String CLIENT_ENDPOINT;
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("settings");
		SERVER_ENDPOINT = bundle.getString("serverEndpoint");
		CLIENT_ENDPOINT = bundle.getString("clientEndpoint");
	}
}
