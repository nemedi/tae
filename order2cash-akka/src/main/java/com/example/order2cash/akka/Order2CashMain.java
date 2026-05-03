package com.example.order2cash.akka;

import akka.actor.ActorSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.order2cash.akka.actors.*;

import java.util.concurrent.CountDownLatch;

public class Order2CashMain {

    private static final Logger log = LoggerFactory.getLogger(Order2CashMain.class);

    public static void main(String[] args) throws InterruptedException {
        String agentType = System.getenv().getOrDefault("AGENT_TYPE", "buyer").toLowerCase();
        log.info("Starting Order2Cash agent: {}", agentType);

        ActorSystem system = ActorSystem.create("order2cash-" + agentType);

        switch (agentType) {
            case "buyer":
                system.actorOf(BuyerActor.props(), "buyer");
                break;
            case "supplier":
                system.actorOf(SupplierActor.props(), "supplier");
                break;
            case "inventory":
                system.actorOf(InventoryActor.props(), "inventory");
                break;
            case "logistics":
                system.actorOf(LogisticsActor.props(), "logistics");
                break;
            case "bank":
                system.actorOf(BankActor.props(), "bank");
                break;
            default:
                log.error("Unknown AGENT_TYPE '{}'. Valid values: buyer, supplier, inventory, logistics, bank", agentType);
                system.terminate();
                System.exit(1);
        }

        log.info("Agent '{}' running. Ctrl+C to stop.", agentType);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down agent: {}", agentType);
            system.terminate();
        }));

        new CountDownLatch(1).await();
    }
}
