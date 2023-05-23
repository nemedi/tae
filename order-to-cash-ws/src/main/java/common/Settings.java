package common;

import java.util.ResourceBundle;

public class Settings {

	public static final int SUPPLIER_PORT;
	public static final int BUYER_PORT;
	public static final int LOGISTICS_PORT;
	public static final int BANK_PORT;
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("settings");
		SUPPLIER_PORT = Integer.parseInt(bundle.getString("supplierPort"));
		BUYER_PORT = Integer.parseInt(bundle.getString("buyerPort"));
		LOGISTICS_PORT = Integer.parseInt(bundle.getString("logisticsPort"));
		BANK_PORT = Integer.parseInt(bundle.getString("bankPort"));
	}
}
