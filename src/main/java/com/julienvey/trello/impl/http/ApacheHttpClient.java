package com.julienvey.trello.impl.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.exception.TrelloHttpException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ApacheHttpClient extends AbstractHttpClient {

    private DefaultHttpClient httpClient;
    private ObjectMapper mapper;

    public ApacheHttpClient() {
        this(new DefaultHttpClient());
    }

    public ApacheHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
        this.mapper = new ObjectMapper();
    }

    @Override
    public <T> T get(String url, Class<T> objectClass, String... params) {
        HttpGet httpGet = new HttpGet(expandUrl(url, params));
        return getEntityAndReleaseConnection(objectClass, httpGet);
    }

    @Override
    public <T> T postForObject(String url, T object, Class<T> objectClass, String... params) {
        HttpPost httpPost = new HttpPost(expandUrl(url, params));

        try {
            HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(object), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            return getEntityAndReleaseConnection(objectClass, httpPost);
        } catch (JsonProcessingException e) {
            // TODO : custom exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public URI postForLocation(String url, Object object, String... params) {
        HttpPost httpPost = new HttpPost(expandUrl(url, params));

        try {
            HttpEntity entity = new ByteArrayEntity(this.mapper.writeValueAsBytes(object), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = this.httpClient.execute(httpPost);

            Header location = httpResponse.getFirstHeader("Location");
            if (location != null) {
                return URI.create(location.getValue());
            } else {
                // TODO : error
                throw new NullPointerException();
            }
        } catch (JsonProcessingException e) {
            // TODO : custom exception
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new TrelloHttpException(e);
        } finally {
            httpPost.releaseConnection();
        }
    }

    private <T> T getEntityAndReleaseConnection(Class<T> objectClass, HttpRequestBase httpRequest) {
        try {
            HttpResponse httpResponse = this.httpClient.execute(httpRequest);

            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                return this.mapper.readValue(httpEntity.getContent(), objectClass);
            } else {
                // TODO : error
                throw new NullPointerException();
            }
        } catch (IOException e) {
            throw new TrelloHttpException(e);
        } finally {
            httpRequest.releaseConnection();
        }
    }

    @Override
    public <T> T postMultiPart(String url, Class<T> objectClass, byte[] bytes,
                               String... params) {
        final HttpPost httpPost = new HttpPost(expandUrl(url, params));

        // todo actual request contains the token within the form but is also
        // works to add it to the url?
        // .addTextBody("token", token)

        final HttpEntity httpEntity = MultipartEntityBuilder.create().
                setMode(HttpMultipartMode.BROWSER_COMPATIBLE).
                addBinaryBody("file",  bytes, ContentType.create("image/jpeg"), "image.jpg").build();

        httpPost.setEntity(httpEntity);
        return getEntityAndReleaseConnection(objectClass, httpPost);
    }
}
