package com.globant.maguila.vertx.poc.vrt;

import org.slf4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ServiceVerticle extends AbstractVerticle {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(ServiceVerticle.class);
	private JsonObject verticleConfig;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		verticleConfig = config().getJsonObject(
				ServerHttpVerticle.class.getSimpleName());
		if(logger.isInfoEnabled())
			logger.info("Start " + this.getClass().getName() 
					+ " with config: "+ verticleConfig);
		
		vertx.eventBus().consumer(Event.A.name(), this::replyA);	
		vertx.eventBus().consumer(Event.B.name(), this::replyB);	
		startFuture.complete();

	}
	
	private void replyA(Message<JsonObject> request)
	{
		logger.info("PID " + Thread.currentThread().getId() + " received event A");
		reply(request, Event.A);
		logger.info("PID " + Thread.currentThread().getId() + " replied PANG!");
	}
	
	private void replyB(Message<JsonObject> request)
	{
		logger.info("PID " + Thread.currentThread().getId() + " received event B");
		reply(request, Event.B);
		logger.info("PID " + Thread.currentThread().getId() + " received event BONG!");
	}
	
	private void reply(Message<JsonObject> request, Event event)
	{
		String message = "";
		switch (event) {
		case A: message = "PANG!"; break;
		case B: message = "BONG!"; break;
		}
		request.reply(new JsonObject().put("message", message));
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
