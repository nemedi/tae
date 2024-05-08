package server;

import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

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

import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.addressing.WsaPropertyBag;

@WebService(
		targetNamespace = "http://axway.com",
		serviceName = "transformerServerService",
		endpointInterface = "common.ServerContract"
)
@MTOM
@Addressing(required = true)
public class ServerService implements ServerContract {
	
	@Resource
	private WebServiceContext context;
	
	@Override
	public @XmlMimeType("application/octet-stream") DataHandler transform(String transformation,
			@XmlMimeType("application/octet-stream") DataHandler handler)
			throws Exception {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			handler.writeTo(stream);
			byte[] data = XsltTransformer.transform(transformation, stream.toByteArray());
			return new DataHandler(new String(data), "text/xml");
		}
	}

	@Override
	public void transformAsync(String transformation,
			@XmlMimeType("application/octet-stream") DataHandler handler) {
		ClientContract proxy = getClientProxy();
		try {
			proxy.onTransformationCompleted(transformation,
					transform(transformation, handler));
		} catch (Exception e) {
			proxy.onTransformationFailed(e.getMessage());
		}
	}
	
	private ClientContract getClientProxy() {
		WSEndpointReference reference = (WSEndpointReference) context.getMessageContext()
				.get(WsaPropertyBag.WSA_REPLYTO_FROM_REQUEST);
		return new W3CEndpointReferenceBuilder()
				.serviceName(new QName("http://axway.com", "transformerClientService"))
				.address(reference.getAddress())
				.wsdlDocumentLocation(reference.getAddress() + "?wsdl")
				.build()
				.getPort(ClientContract.class);
		
	}

}
