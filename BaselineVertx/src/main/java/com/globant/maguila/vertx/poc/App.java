package com.globant.maguila.vertx.poc;

import org.slf4j.Logger;

import com.globant.maguila.vertx.poc.vrt.ServerHttpVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
public class App {

	Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);

	public void startVertxApp() {

		Vertx vertx = Vertx.vertx();
		DeploymentOptions options = new DeploymentOptions()
				.setConfig(new JsonObject());
		startVertxApp(vertx, options);

	}

	public void startVertxApp(Vertx vertx, DeploymentOptions options) {
		
		if (logger.isDebugEnabled())
			logger.debug("Deploy Verticle " + ServerHttpVerticle.class.getName());
		
		Future<String> futureHello = Future.future();
		vertx.deployVerticle(ServerHttpVerticle.class.getName(), 
				options, futureHello.completer());
	}

	public static void main(String[] args) {
		App app = new App();
		app.startVertxApp();

	}
}
