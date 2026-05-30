package game.services

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ApiClient {
    final URI baseUri
    final TokenHandler tokenHandler
    private final HttpClient httpClient = HttpClient.newHttpClient()
    private final JsonSlurper jsonSlurper = new JsonSlurper()

    ApiClient(String baseUrl, TokenHandler tokenHandler) {
        this.baseUri = URI.create(baseUrl)
        this.tokenHandler = tokenHandler
    }

    Map get(String path) {
        send('GET', path, null)
    }

    Map post(String path, Map body) {
        send('POST', path, body)
    }

    Map put(String path, Map body) {
        send('PUT', path, body)
    }

    Map delete(String path) {
        send('DELETE', path, null)
    }

    private Map send(String method, String path, Map body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(path))
                .header('Accept', 'application/json')

        if (tokenHandler?.authenticated) {
            builder.header('Authorization', "Bearer ${tokenHandler.token}")
        }

        if (body != null) {
            builder.header('Content-Type', 'application/json')
            builder.method(method, HttpRequest.BodyPublishers.ofString(JsonOutput.toJson(body)))
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody())
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() >= 400) {
            throw new ApiException(response.statusCode(), extrairMensagemErro(response.body()), response.body())
        }

        if (!response.body()) {
            return [:]
        }

        def parsed = jsonSlurper.parseText(response.body())
        parsed instanceof Map ? parsed as Map : [data: parsed]
    }

    private String extrairMensagemErro(String body) {
        if (!body) {
            return 'Nao foi possivel concluir a requisicao.'
        }

        try {
            def parsed = jsonSlurper.parseText(body)
            if (parsed instanceof Map) {
                return parsed.mensagem ?: parsed.message ?: parsed.erro ?: body
            }
        } catch (Exception ignored) {
        }

        body
    }
}

class ApiException extends IOException {
    final int statusCode
    final String responseBody

    ApiException(int statusCode, String message, String responseBody) {
        super(message)
        this.statusCode = statusCode
        this.responseBody = responseBody
    }
}
