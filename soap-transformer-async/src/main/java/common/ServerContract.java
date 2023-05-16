package common;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface ServerContract {

	@WebMethod
	@Oneway
	void transform(String mapper, DataHandler handler);
	
}
