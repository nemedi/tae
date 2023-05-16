package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

public class DocumentTransformer {

	public static byte[] transform(String xsl, byte[] data)
			throws IOException, TransformerException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Transformer transformer = TransformerFactory
					.newInstance()
					.newTransformer(new StreamSource(new File(xsl)));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
			transformer.transform(new StreamSource(
						new ByteArrayInputStream(data)),
					new StreamResult(stream));
			return stream.toByteArray();
		}
	}
}
