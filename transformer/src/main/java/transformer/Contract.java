package transformer;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface Contract {

	DataHandler transform(String transformation, DataHandler handler) throws Exception;
}
