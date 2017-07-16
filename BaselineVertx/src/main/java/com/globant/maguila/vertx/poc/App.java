package com.globant.maguila.vertx.poc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.globant.maguila.vertx.poc.vrt.ServerHttpVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Hello world!
 *
 */
public class App {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	private static final String VERTX_CONFIG_FILE_PATH_DEFAULT = "/config.json";
	private static final String VERTX_CLUSTER_CONFIG_FILE_PATH_DEFAULT = "/cluster.xml";
	private static final String VERTX_CONFIG_FILE_JVM_PARAM = "vertx.config-file";
	private static final String HAZELCAST_CLUSTER_CONFIG_FILE_JVM_PARAM = "hazelcast.cluster-config-file";
	private final static String VERTX_OPTIONS_CONFIG_FIELD = "vertxOptions";
	private final static String VERT_MS_OPTIONS_CONFIG_FIELD = "vertxMsOptions";
	
	public static void main(String[] args) {
		App app = new App();
		app.startVertxApp();
	}

	public void startVertxApp() {
		
		ClusterManager clusterManager = getClusterManager();
		JsonObject vertxConfig = loadConfig();
		DeploymentOptions deploymentOptions = new DeploymentOptions()
				.setConfig(vertxConfig);
		VertxOptions vertxOptions = new VertxOptions(
				vertxConfig
					.getJsonObject(VERTX_OPTIONS_CONFIG_FIELD))
					.setClusterManager(clusterManager);
		
		
		Vertx vertx = Vertx.vertx(vertxOptions);
		startVertxApp(vertx, deploymentOptions);

	}

	protected void startVertxApp(Vertx vertx, DeploymentOptions options) {
		
		if (logger.isInfoEnabled())
			logger.info("Deploy Verticle " + ServerHttpVerticle.class.getName());
		Future<String> futureHello = Future.future();
		vertx.deployVerticle(ServerHttpVerticle.class.getName(), 
				options, futureHello.completer());
	}
	
	private JsonObject loadConfig(){
		
		String configFilePath = System.getProperty(VERTX_CONFIG_FILE_JVM_PARAM);
		String json = null;
		if(configFilePath == null){
			if (logger.isInfoEnabled()){
				logger.info("loading vertx configuration from classpath.");
			}
			json = loadConfigFromClasspath(); 
		} else {
			if (logger.isInfoEnabled()){
				logger.info("loading vertx configuration from " + configFilePath);
			}
			json = loadConfigFromFile(configFilePath);
		}
		
		if (logger.isInfoEnabled()){
			logger.info(json.toString());
		}
		return json != null ? new JsonObject(json) : new JsonObject();
	}
	
	private String loadConfigFromClasspath() {
		try{
			InputStream in = App.class.getResourceAsStream(VERTX_CONFIG_FILE_PATH_DEFAULT);
			return IOUtils.toString(in, "UTF-8");
		} catch(Exception ex){
			String errorMsg = "Can't build a classpath uri: " + VERTX_CONFIG_FILE_PATH_DEFAULT + "return an empty config";
			logger.error(errorMsg, ex);
			throw new RuntimeException(ex);
		}
	}
	
	private String loadConfigFromFile(String configFilePath) {
		
		String json = null;
		try{
			Path path = Paths.get(configFilePath);
			json = new String(Files.readAllBytes(path),"UTF-8");
		}catch(Exception ex){
			logger.error("Can't load the config from the classpath: " + configFilePath + "return an empty config");			
		}
		
		return json;
	}
	
	private ClusterManager getClusterManager() {
 		
		String clusterConfigFilePath = System.getProperty(HAZELCAST_CLUSTER_CONFIG_FILE_JVM_PARAM);
 		Config hcConfiguration = null;
 		if(clusterConfigFilePath == null){
 			if(logger.isInfoEnabled())
 				logger.info("Loading Hazelcast Configuration File from the classpath");			
 			InputStream in = App.class.getResourceAsStream(VERTX_CLUSTER_CONFIG_FILE_PATH_DEFAULT);
 			if(in != null){
 				hcConfiguration = new XmlConfigBuilder(in).build();
 			} 
 		} else {
 			if (existsClusterConfig(clusterConfigFilePath)) {
 				try{
 					if(logger.isInfoEnabled())
 						logger.info("Loading Hazelcast Configuration "
 								+ "File from file: " + clusterConfigFilePath);					
 					hcConfiguration = new XmlConfigBuilder(clusterConfigFilePath).build();
 				} catch(FileNotFoundException ex){
 					logger.error("I'll be never happend ... "
 							+ "Error to load hazelcast config from file: " 
 							+ clusterConfigFilePath, ex);
 				}
 			}
 		}
 		
 		if(hcConfiguration != null){
 			return new HazelcastClusterManager(hcConfiguration);
 		} else {
			logger.warn("cluster configuration not found, "
					+ "returning empty hazelcast config");
 			return new HazelcastClusterManager();
 		}
 		
 	}

 	private boolean existsClusterConfig(String clusterConfigPath){
 		if(clusterConfigPath != null && ! clusterConfigPath.isEmpty()){
 			Path configPath = Paths.get(clusterConfigPath);
 			return Files.exists(configPath);
 		} else {
 			return false;
 		}
 	}

}
