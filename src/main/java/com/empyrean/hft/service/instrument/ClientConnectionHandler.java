package com.empyrean.hft.service.instrument;

import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.net.NetSocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionHandler implements Handler<NetSocket> {

  private final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);
  private final Map<String, NetSocket> clientConnections = new ConcurrentHashMap<>();

  @Override
  public void handle(NetSocket socket) {
    // Generate a unique client ID
    String clientId = UUID.randomUUID().toString();

    // Store the client ID along with the socket connection
    clientConnections.put(clientId, socket);

    socket.handler(buffer -> {
      logger.debug("Received bytes: " + buffer.length());

      socket.write("Server Response: " + buffer.toString().toUpperCase());

      if (socket.writeQueueFull()) {
        socket.pause();
        socket.drainHandler(done -> {
          socket.resume();
        });
      }

    });
    socket.write("You are now connected!\n");

    socket.closeHandler(close -> {
      // Handle client disconnection
      clientConnections.remove(clientId);
      logger.warn("Client disconnected: " + clientId);
    });

    logger.info("New client connected with ID: " + clientId);
  }
}
