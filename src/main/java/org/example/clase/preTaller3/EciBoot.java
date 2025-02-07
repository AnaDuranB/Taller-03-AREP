package org.example.clase.preTaller3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EciBoot {
    public static Map<String, Method> services = new HashMap<>();

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        loadComponents(args);
        System.out.println(simulateRequest("/greeting"));
        System.out.println(simulateRequest("/pi"));

    }
// debo invocar el metodo de request

    private static String simulateRequest(String route) throws InvocationTargetException, IllegalAccessException {
        Method s = services.get(route);
        String response = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + "{\"resp\":\""
                        + (String) s.invoke(null, "Pedro")
                        + "\"}";
        return response;
    }

    public static void loadComponents(String[] args) {
        try {
            Class c = Class.forName(args[0]);
            if (!c.isAnnotationPresent(RestController.class)){
                System.exit(0);
            }

            for (Method m: c.getDeclaredMethods()){
                if (m.isAnnotationPresent(GetMapping.class)){
                    GetMapping a = m.getAnnotation(GetMapping.class);
                    services.put(a.value(), m);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EciBoot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
