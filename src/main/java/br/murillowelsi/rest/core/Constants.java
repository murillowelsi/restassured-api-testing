package br.murillowelsi.rest.core;

import io.restassured.http.ContentType;

public interface Constants {

    String APP_BASE_URI = "http://barrigarest.wcaquino.me";
    Integer APP_PORT = 80;
    String APP_BASE_PATH = "";

    ContentType APP_CONTENT_TYPE = ContentType.JSON;
    Long MAX_TIMEOUT = 5000L;
}
