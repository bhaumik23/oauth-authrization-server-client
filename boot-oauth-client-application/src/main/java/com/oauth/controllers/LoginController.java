package com.oauth.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.model.Employee;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("login");
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView showEmployees(@RequestParam("code") String code) throws JsonProcessingException, IOException {
		ResponseEntity<String> response = null;
		System.out.println("Authorization Ccode------" + code);

		RestTemplate restTemplate = new RestTemplate();
		String credentials = "articles-client:secret";
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));


		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = "http://127.0.0.1:9000/oauth2/token";
		access_token_url += "?code=" + code;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=http://127.0.0.1:8090/home";

		response = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		System.out.println("Access Token Response ---------" + response.getBody());

		// Get the Access Token From the recieved JSON response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());
		String token = node.path("access_token").asText();

		String url = "http://127.0.0.1:9000/articles";

		// Use the access token for authentication
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers1);

		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		ModelAndView model = new ModelAndView("home");
		model.addObject("articles", response.getBody());
		return model;
	}

}