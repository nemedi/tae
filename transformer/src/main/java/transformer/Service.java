package transformer;

import java.io.ByteArrayOutputStream;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.soap.MTOM;

@WebService(endpointInterface = "transformer.Contract")
@MTOM
public class Service implements Contract {

	@Override
	public @XmlMimeType("application/octet-stream") DataHandler transform(String transformation,
			@XmlMimeType("application/octet-stream") DataHandler handler) throws Exception {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			handler.writeTo(stream);
			byte[] input = stream.toByteArray();
			byte[] output = XsltTransformer.transform("./src/main/resources/" + transformation + ".xsl", input);
			return new DataHandler(new String(output), "text/xml"); 
		}
	}

}
