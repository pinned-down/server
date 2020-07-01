package de.pinneddown.server;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
public class ServerApplication implements ApplicationListener<ApplicationReadyEvent> {
	@Autowired
	private Environment environment;

	@Autowired
	private DiscoveryClient discoveryClient;

	Logger logger = LoggerFactory.getLogger(ServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		List<ServiceInstance> instances = this.discoveryClient.getInstances("open-game-backend-matchmaking");

		for (ServiceInstance instance : instances) {
			logger.info("Found " + instance.getServiceId() + " at " + instance.getUri());


			try {
				RestTemplate restTemplate = new RestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				JSONObject gameServer = new JSONObject();

				gameServer.put("version", "0.1");
				gameServer.put("gameMode", "PD");
				gameServer.put("region", "EU");
				gameServer.put("ipV4Address", "localhost");
				gameServer.put("port", environment.getProperty("local.server.port"));

				logger.info(gameServer.toString());

				HttpEntity<String> request =  new HttpEntity<String>(gameServer.toString(), headers);
				String result =	restTemplate.postForObject(instance.getUri() + "/register", request, String.class);

				logger.info(result);

			} catch (JSONException e) {
				logger.error(e.toString());
			}
		}
	}
}
