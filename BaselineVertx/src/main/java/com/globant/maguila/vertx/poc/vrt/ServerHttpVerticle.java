package com.globant.maguila.vertx.poc.vrt;

import java.time.LocalTime;

import org.slf4j.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ServerHttpVerticle extends AbstractVerticle {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(ServerHttpVerticle.class);
	private JsonObject verticleConfig;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		verticleConfig = config().getJsonObject(
				ServerHttpVerticle.class.getSimpleName(), new JsonObject());
		if(logger.isInfoEnabled())
			logger.info("Start " + this.getClass().getName() 
					+ " with config: "+ verticleConfig);

		vertx
			.createHttpServer()
			.requestHandler(getRouter(verticleConfig)::accept)
			.listen(verticleConfig.getInteger("http.port",80), result -> {
				if(result.succeeded()){
					startFuture.complete();
				} else {
					startFuture.fail(result.cause());
				}
			});
	}
	
	private Router getRouter(JsonObject config) {
		Router router = Router.router(vertx);
		HttpMethod httpMethod = HttpMethod.valueOf(
				config.getString("root.endpoint.httpmethod","GET"));
		router.route(httpMethod, 
				config.getString("root.endpoint.uri","/"))
					.handler(this::requestProcess);
		return router;
	}
	
	private void requestProcess(RoutingContext context) {
		HttpServerRequest request = context.request();
		String eventParam = request.getParam("event");
		Event event = Event.getEventFromString(eventParam);
		JsonObject requestMessage = new JsonObject()
				.put("from", this.getClass().getName())
				.put("date", LocalTime.now().toString())
				;
		
		vertx
		.eventBus()
		.send(event.name(), requestMessage, reply -> {
			
			if(reply.succeeded()){
				if(logger.isDebugEnabled()) logger.debug("Receive answer: " + reply.succeeded() + " body: " + reply.result().body());
				request
					.response()	
					.putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(reply.result().body()));
			} else {
				if(logger.isDebugEnabled()) logger.error("error " + reply.cause());
			} 
		});
	}
	
	
	private enum Event {
		A,
		B;
		private static Event getEventFromString(String str) {
			for (Event event : Event.values()){
				if (event.name().equalsIgnoreCase(str)){
					return event;
				}
			}
			throw new RuntimeException("Event not supported");
		}
	}
}
