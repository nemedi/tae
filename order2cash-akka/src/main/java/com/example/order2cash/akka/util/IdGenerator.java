package com.example.order2cash.akka.util;

import java.util.UUID;

public final class IdGenerator {
	
	private IdGenerator() {}

    public static String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
