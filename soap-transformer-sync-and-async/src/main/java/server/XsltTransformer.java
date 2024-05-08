package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltTransformer {
	
	private static final String FOLDER = "runtime/server/transformations";

	public static byte[] transform(String transformation, byte[] data)
			throws IOException, TransformerException {
		transformation = String.format("%s/%s.xsl", FOLDER, transformation);
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()){
			Transformer transformer = TransformerFactory
					.newInstance()
					.newTransformer(
							new StreamSource(
									new File(transformation)));
			transformer.transform(new StreamSource(new ByteArrayInputStream(data)),
					new StreamResult(stream));
			return stream.toByteArray();
		}
	}

}
