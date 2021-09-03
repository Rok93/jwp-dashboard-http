package nextstep.jwp.controller;

import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

import java.io.IOException;

public class ResourceController extends AbstractController {

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws IOException {
        String uri = request.getUri();
        String resourceFile = findResourceFile(RESOURCE_PREFIX + uri);
        return JwpHttpResponse.ok(uri, resourceFile);
    }
}
