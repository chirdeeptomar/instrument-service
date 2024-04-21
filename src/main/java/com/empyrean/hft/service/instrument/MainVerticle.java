package com.empyrean.hft.service.instrument;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    NetServer server = vertx.createNetServer();

    server.connectHandler(socket -> {
      socket.handler(buffer -> {
        System.out.println("Received bytes: " + buffer.length());

        socket.write("Server Response: " + buffer.toString().toUpperCase());
      });
      socket.write("You are now connected!\n");

      socket.closeHandler(v -> {
        System.out.println("The socket has been closed");
      });
    });

    server.listen(9090, "localhost").onComplete(res -> {
      if (res.succeeded()) {
        System.out.println("Server is now listening on actual port: " + server.actualPort());
      } else {
        System.out.println("Failed to bind!");
      }
    });
  }
}
