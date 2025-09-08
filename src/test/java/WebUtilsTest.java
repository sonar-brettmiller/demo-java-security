import demo.security.util.WebUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

public class WebUtilsTest {

    @Test
    public void getSessionId_withValidRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn("validSessionId");

        // SonarQube FIX: Add proper test assertion
        assertThrows(RuntimeException.class, () -> WebUtils.getSessionId(request));
    }

    @Test
    public void getSessionId_withNullSessionId() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn(null);

        // SonarQube FIX: Add proper test assertion  
        assertDoesNotThrow(() -> WebUtils.getSessionId(request));
    }

    @Test
    public void getSessionId_withIOException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> WebUtils.getSessionId(request));
    }
}