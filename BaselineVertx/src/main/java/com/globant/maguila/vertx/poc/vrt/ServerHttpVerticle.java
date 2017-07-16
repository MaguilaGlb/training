package com.globant.maguila.vertx.poc.vrt;

import org.slf4j.Logger;
import com.globant.maguila.vertx.poc.App;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;

public class ServerHttpVerticle extends AbstractVerticle {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()) logger.debug("Start " + this.getClass().getName());
		
		vertx
			.createHttpServer()
			.requestHandler(this::requestProcess)
			.listen(config().getInteger("http.port",8080), result -> {
				if(result.succeeded()){
					startFuture.complete();
				} else {
					startFuture.fail(result.cause());
				}
			});
	}
	
	private void requestProcess(HttpServerRequest request) {

		if(logger.isDebugEnabled()) {
			logger.debug(
					"Request Process host: " + request.host() + 
					" path: " + request.path() + 
					" query: " + request.query());
		}

		request
			.response()	
			.putHeader("content-type", "application/json; charset=utf-8")
			.end("{result: ok}");
		
	}

	
	
}
