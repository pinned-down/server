package de.pinneddown.server;

import de.opengamebackend.matchmaking.model.requests.ServerRegisterRequest;
import de.opengamebackend.matchmaking.model.responses.ServerRegisterResponse;
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
import java.util.ArrayList;
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

		if (instances.isEmpty()) {
			logger.error("Unable to connect to matchmaking service.");
		} else {
			ServiceInstance instance = instances.get(0);

			logger.info("Found " + instance.getServiceId() + " at " + instance.getUri());

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			List<MediaType> acceptedTypes = new ArrayList<>();
			acceptedTypes.add(MediaType.APPLICATION_JSON);
			headers.setAccept(acceptedTypes);

			String version = "0.1";
			String gameMode = "PD";
			String region = "EU";
			String ipV4Address = "localhost";

			int port = 0;
			try {
				port = Integer.parseInt(environment.getProperty("local.server.port"));
			} catch (NumberFormatException e) {
				logger.error(e.toString());
			}

			int maxPlayers = 2;

			ServerRegisterRequest serverRegisterRequest = new ServerRegisterRequest
					(version, gameMode, region, ipV4Address, port, maxPlayers);

			HttpEntity<ServerRegisterRequest> request =
					new HttpEntity<ServerRegisterRequest>(serverRegisterRequest, headers);
			String response =
					restTemplate.postForObject(instance.getUri() + "/server/register", request, String.class);
		}
	}
}
