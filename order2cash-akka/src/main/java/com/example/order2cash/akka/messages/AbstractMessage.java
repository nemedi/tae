package com.example.order2cash.akka.messages;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    public final String workflowId;
    public final String xml;

    public AbstractMessage(String workflowId, String xml) {
        this.workflowId = workflowId;
        this.xml = xml;
    }
}
