package com.example.order2cash.ws.util;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class XsltTransformer {
	
	private static final TransformerFactory factory = TransformerFactory.newInstance();
	
	private XsltTransformer() {}
	
    public static String transform(String inputXml, String xsltClasspathResource, Map<String, String> params) {
        try {
            InputStream xslt = XsltTransformer.class.getResourceAsStream(xsltClasspathResource);
            if (xslt == null) {
                throw new IllegalArgumentException("XSLT resource not found: " + xsltClasspathResource);
            }
            Transformer transformer = factory.newTransformer(new StreamSource(xslt));
            params.forEach(transformer::setParameter);
            StringWriter out = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(inputXml)), new StreamResult(out));
            return out.toString();
        } catch (TransformerException e) {
            throw new RuntimeException("XSLT transformation failed [" + xsltClasspathResource + "]", e);
        }
    }

}
