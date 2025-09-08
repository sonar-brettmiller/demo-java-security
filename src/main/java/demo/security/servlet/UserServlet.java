package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                        out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth != null) {
            try {
                // SECURITY FIX: Replace insecure deserialization with JSON parsing
                byte[] decoded = Base64.decodeBase64(sessionAuth);
                String jsonString = new String(decoded, "UTF-8");
                return parseSessionHeaderFromJson(jsonString);
            } catch (Exception e) {
                // Log security event but don't expose details
                System.err.println("Invalid session header format");
                return null;
            }
        }
        return null;
    }
    
    private SessionHeader parseSessionHeaderFromJson(String json) {
        // Simple JSON parsing for SessionHeader - replace with proper JSON library in production
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        // Basic validation - only allow expected JSON structure
        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
        
        // For demo purposes, create a simple parser
        // In production, use Jackson or similar secure JSON library
        SessionHeader header = new SessionHeader();
        
        // Extract username field safely (simplified for demo)
        if (json.contains("\"username\"")) {
            int start = json.indexOf("\"username\"") + 12;
            if (start < json.length()) {
                int valueStart = json.indexOf("\"", start) + 1;
                int valueEnd = json.indexOf("\"", valueStart);
                if (valueStart > 0 && valueEnd > valueStart) {
                    String username = json.substring(valueStart, valueEnd);
                    // Validate username contains only safe characters
                    if (username.matches("^[a-zA-Z0-9._-]+$")) {
                        header.setUsername(username);
                    }
                }
            }
        }
        
        return header;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) return;
        String user = sessionHeader.getUsername();
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
