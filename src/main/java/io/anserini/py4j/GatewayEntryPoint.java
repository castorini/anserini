package io.anserini.py4j;

import py4j.GatewayServer;

import java.io.IOException;

/**
 * @author s43moham on 06/03/17.
 * @project anserini
 */
public class GatewayEntryPoint {

    public static void main(String[] args) throws IOException {
        GatewayServer gatewayServer = new GatewayServer(new GatewayEntryPoint());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
}
