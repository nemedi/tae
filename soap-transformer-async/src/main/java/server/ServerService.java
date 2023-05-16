package server;

import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import com.sun.xml.ws.addressing.WsaPropertyBag;
import com.sun.xml.ws.api.addressing.WSEndpointReference;

import common.ClientContract;
import common.ServerContract;
import jakarta.activation.DataHandler;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

@WebService(
		serviceName = "serverService",
		targetNamespace = "http://axway.com",
		endpointInterface = "common.ServerContract"
)
@MTOM
@Addressing(required = true)
public class ServerService implements ServerContract {
	
	private final String FOLDER = "data/server/";
	
	@Resource
	private WebServiceContext context;

	public void transform(String mapper, @XmlMimeType("application/octet-stream") DataHandler handler) {
		ClientContract client = getClient();
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			handler.writeTo(stream);
			byte[] data = DocumentTransformer.transform(FOLDER + mapper + ".xsl",
					stream.toByteArray());
			client.onTransform(new DataHandler(new String(data), "text/xml"));
		} catch (Exception e) {
			client.onFault(e.getLocalizedMessage());
		}
	}
	
	private ClientContract getClient() {
		WSEndpointReference reference = (WSEndpointReference) context.getMessageContext()
			.get(WsaPropertyBag.WSA_REPLYTO_FROM_REQUEST);
		return new W3CEndpointReferenceBuilder()
				.serviceName(new QName("http://axway.com", "clientService"))
				.address(reference.getAddress())
				.wsdlDocumentLocation(reference.getAddress() + "?wsdl")
				.build()
				.getPort(ClientContract.class);
	}

}
