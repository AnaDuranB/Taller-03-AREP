package org.example;
// ejecutar con: java -cp target/classes org.example.MicroSpringBoot
import org.example.annotations.GetMapping;
import org.example.annotations.PostMapping;
import org.example.annotations.RequestBody;
import org.example.annotations.RequestParam;
import org.example.annotations.RestController;
import org.example.controller.ComponentController;
import org.example.controller.GreetingController;
import org.example.server.HttpServer;
import org.example.server.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class MicroSpringBoot {

    public static void main(String[] args) {
        System.out.println("Iniciando MicroSpringBoot...");
        loadControllers();
        HttpServer.staticfiles("src/main/webapp");
        HttpServer.start(35000);
        System.out.println("El servidor está corriendo...");
    }

    /**
     * Registra manualmente todos los controladores de la aplicación.
     * En una versión real se podría hacer un escaneo del classpath.
     */
    private static void loadControllers() {
        registerController(new GreetingController()); // http://localhost:35000/greeting?name=Juan
        registerController(new ComponentController());
    }

    private static void registerController(Object controllerInstance) {
        Class<?> beanClass = controllerInstance.getClass();
        if (!beanClass.isAnnotationPresent(RestController.class)) {
            System.err.println("La clase " + beanClass.getName() + " no está anotada con @RestController.");
            return;
        }
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = m.getAnnotation(GetMapping.class);
                String path = mapping.value();
                HttpServer.get(path, (req, res) -> invokeMethod(m, controllerInstance, req));
            }
            if (m.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = m.getAnnotation(PostMapping.class);
                String path = mapping.value();
                HttpServer.post(path, (req, res) -> invokeMethod(m, controllerInstance, req));
            }
        }
    }

    /**
     * Invoca el metodo del controlador, inyectando los parámetros según las anotaciones
     */
    private static String invokeMethod(Method method, Object instance, Request req) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] argsForMethod = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(RequestBody.class)) {
                    if (Map.class.isAssignableFrom(parameter.getType())) {
                        argsForMethod[i] = HttpServer.parseJson(req.getBody());
                    } else {
                        argsForMethod[i] = req.getBody();
                    }
                } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam reqParam = parameter.getAnnotation(RequestParam.class);
                    String paramName = reqParam.value();
                    String value = req.getQueryParams().get(paramName);
                    if (value == null || value.isEmpty()) {
                        value = reqParam.defaultValue();
                    }
                    argsForMethod[i] = value;
                } else {
                    argsForMethod[i] = null;
                }
            }
            Object result = method.invoke(instance, argsForMethod);
            return result != null ? result.toString() : "";
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return "{\"error\": \"Internal Server Error\"}";
        }
    }
}
