package org.example;

import org.example.model.Component;
import org.example.server.HttpServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @BeforeAll
    static void startServer() {
        new Thread(() -> HttpServer.main(new String[]{})).start();
        try {
            Thread.sleep(1000); // Esperar a que el servidor inicie
        } catch (InterruptedException ignored) {}
    }

    private String sendHttpRequest(String request) throws IOException {
        try (Socket socket = new Socket("localhost", 35000);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            out.write(request.getBytes());
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
                if (line.isEmpty()) break; // Fin de encabezados
            }
            return response.toString();
        }
    }

    @Test
    void testGetHello() throws IOException {
        String response = sendHttpRequest("GET /hello?name=John HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(response.contains("200 OK"));
    }

    @Test
    void testGetPi() throws IOException {
        String response = sendHttpRequest("GET /pi HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(response.contains("200 OK"));
    }

    @Test
    void testPostComponent() throws IOException {
        String requestBody = "{\"name\": \"CPU\", \"type\": \"Processor\", \"price\": \"299.99\"}";
        String request = "POST /api/components HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + requestBody.length() + "\r\n\r\n" +
                requestBody;

        String response = sendHttpRequest(request);
        assertTrue(response.contains("201 Created"));
    }

    @Test
    void testInvalidRoute() throws IOException {
        String response = sendHttpRequest("GET /invalid HTTP/1.1\r\nHost: localhost\r\n\r\n");
        assertTrue(response.contains("404 Not Found"));
    }

    @Test
    public void testParseJsonValid() {
        String json = "{\"name\":\"TestComponent\",\"type\":\"CPU\",\"price\":\"100\"}";
        // Se asume que parseJson se ha hecho public en HttpServer para facilitar la prueba.
        Map<String, String> map = HttpServer.parseJson(json);
        assertEquals("TestComponent", map.get("name"));
        assertEquals("CPU", map.get("type"));
        assertEquals("100", map.get("price"));
    }

    @Test
    public void testParseJsonInvalid() {
        String json = "Not a valid json";
        Map<String, String> map = HttpServer.parseJson(json);
        // Si el JSON no es válido, se espera un mapa vacío
        assertTrue(map.isEmpty());
    }

    @Test
    public void testToJson() {
        // Crea una lista de componentes
        List<Component> components = List.of(
                new Component("Test1", "CPU", 100.0),
                new Component("Test2", "GPU", 200.0)
        );
        String json = HttpServer.toJson(components);
        // Verifica que la cadena resultante contenga fragmentos esperados
        assertTrue(json.contains("\"name\": \"Test1\""));
        assertTrue(json.contains("\"type\": \"GPU\""));
        assertTrue(json.contains("\"price\": 200.0"));
    }
}
