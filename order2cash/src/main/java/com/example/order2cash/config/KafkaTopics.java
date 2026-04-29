package com.example.order2cash.config;

public final class KafkaTopics {

    public static final String PURCHASE_ORDERS       = "purchase-orders";
    public static final String INVENTORY_REQUESTS    = "inventory-requests";
    public static final String INVENTORY_RESPONSES   = "inventory-responses";
    public static final String SHIPMENT_REQUESTS     = "shipment-requests";
    public static final String SHIPMENT_NOTIFICATIONS = "shipment-notifications";
    public static final String INVOICES              = "invoices";
    public static final String PAYMENT_INSTRUCTIONS  = "payment-instructions";
    public static final String PAYMENT_CONFIRMATIONS = "payment-confirmations";

    private KafkaTopics() {}
}
