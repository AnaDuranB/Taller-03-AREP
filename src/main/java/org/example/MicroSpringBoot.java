package org.example;

import org.example.annotations.RequestParam;
import org.example.clase.preTaller3.GetMapping;
import org.example.clase.preTaller3.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MicroSpringBoot {
    public static void main(String[] args) {
        loadComponents(args);
    }

    public static void loadComponents(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java -cp target/classes org.example.MicroSpringBoot <fully.qualified.BeanClass>");
            System.exit(1);
        }

        String beanClassName = args[0];
        try {
            // cargamos la clase
            Class beanClass = Class.forName(beanClassName);

            if (!beanClass.isAnnotationPresent(RestController.class)){
                System.exit(0);
            }

            Object beanInstance = beanClass.getDeclaredConstructor().newInstance();
            Method[] methods = beanClass.getDeclaredMethods();
            for (Method m: methods){
                if (m.isAnnotationPresent(GetMapping.class)){
                    GetMapping mapping = m.getAnnotation(GetMapping.class);
                    String path = mapping.value();

                    HttpServer.get(path, (req, res) -> {
                        try {
                            Parameter[] parameters = m.getParameters();
                            Object[] argsForMethod = new Object[parameters.length];
                            for (int i = 0; i < parameters.length; i++) {
                                Parameter parameter = parameters[i];
                                if (parameter.isAnnotationPresent(RequestParam.class)) {
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
                            Object result = m.invoke(beanInstance, argsForMethod);
                            return result != null ? result.toString() : "";
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            return "{\"error\": \"Internal Server Error\"}";
                        }
                    });
                }
            }

            HttpServer.staticfiles("src/main/webapp");
            HttpServer.start(35000);

        } catch (ClassNotFoundException e) {
            System.err.println("Clase no encontrada: " + beanClassName);
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.err.println("Error al instanciar la clase: " + beanClassName);
            e.printStackTrace();
        }
    }

}


