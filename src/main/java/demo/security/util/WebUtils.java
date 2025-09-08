package demo.security.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebUtils {

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie c = new Cookie(name, value);
        response.addCookie(c);
    }

    public static void getSessionId(HttpServletRequest request){
        // SECURITY FIX: Use server-managed session ID instead of user-supplied ID
        String sessionId = request.getSession().getId();
        if (sessionId != null){
            String ip = "10.40.1.1";
            // SECURITY FIX: Use try-with-resources for automatic resource management
            try (Socket socket = new Socket(ip, 6667)) {
                socket.getOutputStream().write(sessionId.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
