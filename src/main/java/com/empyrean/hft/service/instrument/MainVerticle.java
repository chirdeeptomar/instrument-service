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
        System.out.println("I received some bytes: " + buffer.length());
      });
    });

    server
      .listen(0, "localhost")
      .onComplete(res -> {
        if (res.succeeded()) {
          System.out.println("Server is now listening on actual port: " + server.actualPort());
        } else {
          System.out.println("Failed to bind!");
        }
      });
  }
}
