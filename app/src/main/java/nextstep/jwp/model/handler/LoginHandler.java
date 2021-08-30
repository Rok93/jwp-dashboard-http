package nextstep.jwp.model.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginHandler implements CustomHandler {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String LOGIN_PAGE_PATH = "login.html";
    private static final String LOGIN_SUCCESS_PATH = "index.html";
    private static final String LOGIN_FAILURE_PATH = "401.html";

    @Override
    public void handle(JwpHttpRequest jwpHttpRequest, OutputStream outputStream) throws IOException, URISyntaxException {
        if (jwpHttpRequest.isEmptyParams()) {
            String resourceFile = findResourceFile(RESOURCE_PREFIX + LOGIN_PAGE_PATH);
            final String response = JwpHttpResponse.ok(resourceFile);
            outputStream.write(response.getBytes());
            return;
        }

        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(user -> requestLogin(user, password, outputStream),
                        () -> loginFail(outputStream));
    }

    private String findResourceFile(String resourceUrl) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(resourceUrl);
        final Path path = Paths.get(resource.toURI());
        return new String(Files.readAllBytes(path));
    }

    private void requestLogin(User user, String password, OutputStream outputStream) {
        if (user.checkPassword(password)) {
            try {
                final String response = JwpHttpResponse.found(LOGIN_SUCCESS_PATH);
                outputStream.write(response.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        loginFail(outputStream);
    }

    private void loginFail(OutputStream outputStream) {
        try {
            String response = JwpHttpResponse.found(LOGIN_FAILURE_PATH);
            outputStream.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
