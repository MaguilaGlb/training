package com.globant.maguila.vertx.poc.vrt;

import java.util.Calendar;

import org.slf4j.Logger;
import com.globant.maguila.vertx.poc.App;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ServerHttpVerticle extends AbstractVerticle {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	private JsonObject verticleConfig;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		verticleConfig = config().getJsonObject(ServerHttpVerticle.class.getSimpleName());
		if(logger.isInfoEnabled())
			logger.info("Start " + this.getClass().getName() 
					+ " with config: "+ verticleConfig);
		
		ConfigStoreOptions configStoreOptions = 
				new ConfigStoreOptions(config().getJsonObject("configStoreOptions"));
		ConfigRetrieverOptions configRetrieveOptions = new ConfigRetrieverOptions()
				.setScanPeriod(2000)
				.addStore(configStoreOptions);
		ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieveOptions);
		configRetriever.getConfig(json -> {
			Calendar calendar = Calendar.getInstance();
			verticleConfig.mergeIn(json.result());
			verticleConfig.put("LastUpdate", calendar.get(Calendar.MINUTE) + ":" +  calendar.get(Calendar.SECOND));
			logger.info("PID " + Thread.currentThread().getId() + " original: " + json.result().encode());
			
		});
		
		configRetriever.listen(change -> {
			  // Previous configuration
			  JsonObject previous = change.getPreviousConfiguration();
			  logger.info("PID " + Thread.currentThread().getId() + " previous: " + previous.encode());
			  // New configuration
			  Calendar calendar = Calendar.getInstance();
			  verticleConfig.put("LastUpdate", calendar.get(Calendar.MINUTE) + ":" +  calendar.get(Calendar.SECOND));
			  verticleConfig.mergeIn(change.getNewConfiguration());
			  JsonObject conf = change.getNewConfiguration();
			  logger.info("PID " + Thread.currentThread().getId() + " new: " + conf.encode());

		});

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
//		if(logger.isDebugEnabled()) {
//			logger.debug(
//					"Request Process host: " + request.host() + 
//					" path: " + request.path() + 
//					" query: " + request.query()
//					);
//		}
		JsonObject jsonResponse = new JsonObject()
				.put("result", "pong")
				.put("verticleConfig", verticleConfig)
				;
		
		request
			.response()	
			.putHeader("content-type", "application/json; charset=utf-8")
			.end(jsonResponse.toString());
	}
}
