package com.empyrean.hft.service.instrument;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.net.NetServer;

public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {

    NetServer server = vertx.createNetServer();

    server.connectHandler(new ClientConnectionHandler());

    server.listen(9090, "localhost").onComplete(this::handlerServerStartup);
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    logger.error("!!!!! Shutting Down !!!!!");
    stopPromise.complete();
  }

  private void handlerServerStartup(AsyncResult<NetServer> server) {
    if (server.succeeded()) {
      logger.info("Server is listening on port: " + server.result().actualPort());
    } else {
      logger.error("Failed to bind!");
    }
  }

}
