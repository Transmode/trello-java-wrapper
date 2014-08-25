package com.julienvey.trello.impl.http;

import com.julienvey.trello.TrelloHttpClient;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.exception.TrelloHttpException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class RestTemplateHttpClient implements TrelloHttpClient {

	private RestTemplate restTemplate;

	public RestTemplateHttpClient() {
		restTemplate = new RestTemplate();
	}

	@Override
	public <T> T postForObject(String url, T object, Class<T> objectClass,
			String... params) {
		try {
			return restTemplate.postForObject(url, object, objectClass, params);
		} catch (RestClientException e) {
			throw new TrelloHttpException(e);
		}

	}

	@Override
	public URI postForLocation(String url, Object object, String... params) {
		try {
			return restTemplate.postForLocation(url, object, params);
		} catch (RestClientException e) {
			throw new TrelloHttpException(e);
		}
	}

	@Override
	public <T> T get(String url, Class<T> objectClass, String... params) {
		try {
			return restTemplate.getForObject(url, objectClass, params);
		} catch (RestClientException e) {
			throw new TrelloHttpException(e);
		}
	}

	@Override
	public <T> T post(String url, Class<T> objectClass, byte[] bytes,
			String... params) {
		final HttpHeaders httpHeaders = new HttpHeaders();

		// Sending multipart/form-data
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
		formData.add("file", new ByteArrayResource(bytes));

		// Populate the MultiValueMap being serialized and headers in an
		// HttpEntity object to use for the request
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
				formData, httpHeaders);
		// Make the network request, posting the message, expecting a String in
		// response from the server
		final ResponseEntity<T> responseEntity = restTemplate.exchange(url,
				HttpMethod.POST, requestEntity, objectClass, params);

		// Return the response body to display to the user
		return responseEntity.getBody();
	}
}
