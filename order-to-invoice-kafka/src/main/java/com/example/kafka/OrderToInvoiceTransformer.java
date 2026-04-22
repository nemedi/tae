package com.example.kafka;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class OrderToInvoiceTransformer {

    private final Transformer transformer;

    public OrderToInvoiceTransformer(String xsltPath) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        this.transformer = factory.newTransformer(new StreamSource(new File(xsltPath)));
    }

    public String transform(String orderXml) throws TransformerException {
        StringWriter outputWriter = new StringWriter();
        Source xmlInput = new StreamSource(new StringReader(orderXml));
        Result output = new StreamResult(outputWriter);

        transformer.transform(xmlInput, output);
        return outputWriter.toString();
    }
}
