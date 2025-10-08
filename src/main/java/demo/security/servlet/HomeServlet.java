package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public HomeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        
        // 🔒 SECURITY: Validate and sanitize input to prevent XSS
        if (name == null || name.trim().isEmpty()) {
            name = "Guest";
        } else {
            name = sanitizeInput(name.trim());
        }
        
        response.setContentType("text/html");
        writeResponse(response, name);
    }
    
    protected void writeResponse(HttpServletResponse response, String name) throws IOException {
        PrintWriter out = response.getWriter();
        // 🔒 SECURITY: Use proper HTML escaping to prevent XSS
        out.print("<h2>Hello " + escapeHtml(name) + "</h2>");
        out.close();
    }
    
    /**
     * Sanitize user input by removing potentially dangerous characters
     */
    private String sanitizeInput(String input) {
        if (input == null) return "";
        
        // Remove script tags and other potentially dangerous content
        return input.replaceAll("(?i)<script[^>]*>.*?</script>", "")
                   .replaceAll("(?i)<[^>]*>", "")
                   .replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Escape HTML special characters to prevent XSS
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
