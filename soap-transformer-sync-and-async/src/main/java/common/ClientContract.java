package common;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface ClientContract {

	@WebMethod
	@Oneway
	void onTransformationCompleted(String transformation, DataHandler handler);

	@WebMethod
	@Oneway
	void onTransformationFailed(String message);
}
