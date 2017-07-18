package com.globant.maguila.vertx.poc.vrt;

import java.util.Calendar;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import com.globant.maguila.vertx.poc.App;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
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
	private CircuitBreaker circuitBreaker;
	private final static String CB_NAME="CB";
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		verticleConfig = config().getJsonObject(
				ServerHttpVerticle.class.getSimpleName());
		if(logger.isInfoEnabled())
			logger.info("Start " + this.getClass().getName() 
					+ " with config: "+ verticleConfig);
		
//		ConfigStoreOptions configStoreOptions = 
//				new ConfigStoreOptions(config().getJsonObject("configStoreOptions"));
//		ConfigRetrieverOptions configRetrieveOptions = new ConfigRetrieverOptions()
//				.setScanPeriod(2000)
//				.addStore(configStoreOptions);
//		ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieveOptions);
//		configRetriever.getConfig(json -> {
//			Calendar calendar = Calendar.getInstance();
//			verticleConfig.mergeIn(json.result());
//			verticleConfig.put("LastUpdate", calendar.get(Calendar.MINUTE) + ":" +  calendar.get(Calendar.SECOND));
//			logger.info("PID " + Thread.currentThread().getId() + " original: " + json.result().encode());
//			
//		});
		
//		configRetriever.listen(change -> {
//			  // Previous configuration
//			  JsonObject previous = change.getPreviousConfiguration();
//			  logger.info("PID " + Thread.currentThread().getId() + " previous: " + previous.encode());
//			  // New configuration
//			  Calendar calendar = Calendar.getInstance();
//			  verticleConfig.put("LastUpdate", calendar.get(Calendar.MINUTE) + ":" +  calendar.get(Calendar.SECOND));
//			  verticleConfig.mergeIn(change.getNewConfiguration());
//			  JsonObject conf = change.getNewConfiguration();
//			  logger.info("PID " + Thread.currentThread().getId() + " new: " + conf.encode());
//			  //((CircuitBreakerImpl)circuitBreaker).options().se
//
//		});
		
		
//		circuitBreaker = CircuitBreaker.create(CB_NAME, vertx,
//			    new CircuitBreakerOptions()
//		        .setMaxFailures(5) 
//		        .setFallbackOnFailure(true) 
//		        .setTimeout(1000)
//		        .setResetTimeout(5000) 
//			);
//		
//		circuitBreaker
//			.closeHandler(handler -> logger.info("Circuit Breaker " + CB_NAME + " Close"))
//			.openHandler(handler -> logger.info("Circuit Breaker "  + CB_NAME + " Open failures: " + circuitBreaker.failureCount()))
//			.halfOpenHandler(handler -> logger.info("Circuit Breaker " + CB_NAME + " Half Open"));
//		

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
		JsonObject requestMessage = new JsonObject().put("message", "ping");
		
		vertx
		.eventBus()
		.send(event.name(), requestMessage, reply -> {
			if(logger.isDebugEnabled()) logger.debug("Receive answer succeeded: " + reply.succeeded() + " body: " + reply.result().body());
			if(reply.succeeded()){
				request
					.response()	
					.putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(reply.result().body()));
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
