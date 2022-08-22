package com.esref.bankingsystem.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class ExchangeAPI implements Exchange {
	
	@Autowired
	private RestTemplate client;
	
	public double gold() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 4ALL1gH68A4XCNpY9CdBXn:7Hti197hM0O4dmloEo6Q17");
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = "https://api.collectapi.com/economy/goldPrice";
		ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String res = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode node = objectMapper.readTree(res);
			ArrayNode data = (ArrayNode) node.get("result");
			JsonNode singledata = data.get(0);
			return singledata.get("buying").asDouble();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public double tryToUsd(double amount) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 4ALL1gH68A4XCNpY9CdBXn:7Hti197hM0O4dmloEo6Q17");
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = "https://api.collectapi.com/economy/exchange?int=" + amount + "&to=USD&base=TRY";
		ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String res = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode node = objectMapper.readTree(res);
			JsonNode resultNode = node.get("result");
			ArrayNode data = (ArrayNode) resultNode.get("data");
			JsonNode singledata = data.get(0);
			return singledata.get("calculated").asDouble();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amount;
	}
	
	public double usdToTry(double amount) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-type", "application/json");
		headers.add("authorization", "apikey 4ALL1gH68A4XCNpY9CdBXn:7Hti197hM0O4dmloEo6Q17");
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		String url = "https://api.collectapi.com/economy/exchange?int=" + amount + "&to=TRY&base=USD";
		ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, requestEntity, String.class);
		String res = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode node = objectMapper.readTree(res);
			JsonNode resultNode = node.get("result");
			ArrayNode data = (ArrayNode) resultNode.get("data");
			JsonNode singledata = data.get(0);
			return singledata.get("calculated").asDouble();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amount;
	}

	@Override
	public double exchange(double amount, String accountTypeOne, String accountTypeTwo) {
		
		if (accountTypeOne.equals("TL") && accountTypeTwo.equals("Dolar")) {
			double usd = tryToUsd(amount);
			return usd;
		}
		
		if (accountTypeOne.equals("TL") && accountTypeTwo.equals("Altın")) {
			double gold = gold();
			return amount / gold;
		}
		
		if (accountTypeOne.equals("Dolar") && accountTypeTwo.equals("TL")) {
			double tl = usdToTry(amount);
			return tl;
		}
		
		if (accountTypeOne.equals("Dolar") && accountTypeTwo.equals("Altın")) {
			double gold = gold();
			double tl = usdToTry(amount);
			return tl / gold;
		}
		
		if (accountTypeOne.equals("Altın") && accountTypeTwo.equals("TL")) {
			double gold = gold();
			return amount * gold;
		}
		
		if (accountTypeOne.equals("Altın") && accountTypeTwo.equals("Dolar")) {
			double gold = gold();
			double tl = usdToTry(1);
			return amount * gold / tl;
		}
		
		return amount;
	}
	

}
