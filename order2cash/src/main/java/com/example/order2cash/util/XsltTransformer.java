package com.example.order2cash.util;

import org.springframework.stereotype.Component;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Component
public class XsltTransformer {

    private final TransformerFactory factory = TransformerFactory.newInstance();

    public String transform(String inputXml, String xsltClasspathResource, Map<String, String> params) {
        try {
            InputStream xslt = getClass().getResourceAsStream(xsltClasspathResource);
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
