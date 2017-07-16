package com.globant.maguila.vertx.poc.vrt;

import org.slf4j.Logger;
import com.globant.maguila.vertx.poc.App;
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
		if(logger.isDebugEnabled()) {
			logger.debug(
					"Request Process host: " + request.host() + 
					" path: " + request.path() + 
					" query: " + request.query()
					);
		}
		request
			.response()	
			.putHeader("content-type", "application/json; charset=utf-8")
			.end("{result: pong}");
	}
}
