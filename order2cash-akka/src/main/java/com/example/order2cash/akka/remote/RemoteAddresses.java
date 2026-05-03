package com.example.order2cash.akka.remote;

import akka.actor.ActorContext;
import akka.actor.ActorSelection;

public final class RemoteAddresses {

    private static final int PORT = 2551;
    private static final String SYSTEM = "order2cash";

    public static ActorSelection buyer(ActorContext context) {
        return select(context, "buyer", "BUYER_HOST", "buyer");
    }

    public static ActorSelection supplier(ActorContext context) {
        return select(context, "supplier", "SUPPLIER_HOST", "supplier");
    }

    public static ActorSelection inventory(ActorContext context) {
        return select(context, "inventory", "INVENTORY_HOST", "inventory");
    }

    public static ActorSelection logistics(ActorContext context) {
        return select(context, "logistics", "LOGISTICS_HOST", "logistics");
    }

    public static ActorSelection bank(ActorContext context) {
        return select(context, "bank", "BANK_HOST", "bank");
    }

    private static ActorSelection select(ActorContext context, String type, String envVar, String defaultHost) {
        String host = System.getenv().getOrDefault(envVar, defaultHost);
        return context.actorSelection("akka://" + SYSTEM + "-" + type + "@" + host + ":" + PORT + "/user/" + defaultHost);
    }

    private RemoteAddresses() {}
}
