package com.globant.maguila.vertx.poc.vrt;

import java.time.LocalTime;

import org.slf4j.Logger;

import com.globant.maguila.vertx.poc.guice.Module;
import com.globant.maguila.vertx.poc.service.UserService;
import com.globant.maguila.vertx.poc.service.UserServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;

public class ServiceVerticle extends AbstractVerticle {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(ServiceVerticle.class);
	private JsonObject verticleConfig;
	private Record publishedRecord;
	private UserService userService;
	private ServiceDiscovery discovery;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		Injector injector = Guice.createInjector(new Module());
		verticleConfig = config().getJsonObject(
				ServerHttpVerticle.class.getSimpleName());
		if(logger.isInfoEnabled())
			logger.info("Start " + this.getClass().getName() 
					+ " with config: "+ verticleConfig);
		
		vertx.eventBus().consumer(Event.A.name(), this::replyA);	
		vertx.eventBus().consumer(Event.B.name(), this::replyB);	
		
		discovery = ServiceDiscovery.create(vertx);
		userService = injector.getInstance(UserService.class);
		ProxyHelper.registerService(UserService.class, vertx, userService, UserServiceImpl.SERVICE_NAME);
		Record serviceRecord = EventBusService.createRecord(UserServiceImpl.SERVICE_NAME, UserServiceImpl.SERVICE_NAME, UserService.class);
		
		discovery.publish(serviceRecord, publishHandler -> {
            if(publishHandler.succeeded()) {                           
                publishedRecord = publishHandler.result();                         
                if(logger.isDebugEnabled()) {
                    logger.debug("Published User Service id: " + publishedRecord.getRegistration());
                }                          
                startFuture.complete();
            } else {
                startFuture.fail(publishHandler.cause());
            }
		});		
		
		//startFuture.complete();

	}
	
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		discovery.unpublish(publishedRecord.getRegistration(), handler -> {
			if(handler.succeeded()) {
				if(logger.isDebugEnabled()) {
					logger.debug("Unpublished User Service id: " + publishedRecord.getRegistration());
				}
			} else {
				logger.error("Error Unpublished User Service id: " + publishedRecord.getRegistration(), handler.cause());
			}
			stopFuture.complete();
			discovery.close();
		});
	}
		
	private void replyA(Message<JsonObject> request)
	{
		
		logger.info("PID " + Thread.currentThread().getId() + " received event A");
		Future<UserService> futureService = Future.future();
		EventBusService.<UserService>getProxy(discovery, UserService.class, futureService.completer());
		
		futureService.compose(userService -> {
			Future<JsonObject> valueFuture = Future.future();
			userService.getUser(132l, valueFuture.completer());
			return valueFuture;
		}).setHandler(result -> {
			if (result.succeeded()){
				reply(request, Event.A, result.result().getString("value"));
			} else {
				reply(request, Event.A, "fail!");
			}
			logger.info("PID " + Thread.currentThread().getId() + " received event BANG!");
		});
	}
	
	private void replyB(Message<JsonObject> request)
	{
		logger.info("PID " + Thread.currentThread().getId() + " received event B");
		reply(request, Event.B, "");
		logger.info("PID " + Thread.currentThread().getId() + " received event BONG!");
	}
	
	private void reply(Message<JsonObject> request, Event event, String result)
	{
		String message = "";
		switch (event) {
		case A: message = "PANG!"; break;
		case B: message = "BONG!"; break;
		}
		request.reply(new JsonObject()
				.put("message", message)
				.put("source from", request.body().getString("from"))
				.put("source date", request.body().getString("date"))
				.put("reply result", result)
				.put("reply from", this.getClass().getName() + " " + Thread.currentThread().getId())
				.put("reply date", LocalTime.now().toString())
				);
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
