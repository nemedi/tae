package common;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface ServerContract {

	@WebMethod
	DataHandler transform(String transformation, DataHandler handler) throws Exception;
	
	@WebMethod
	@Oneway
	void transformAsync(String transformation, DataHandler handler);
}
