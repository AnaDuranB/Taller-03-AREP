package org.example.clase.ecispring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InvokePrintMembers {
    public static void main(String... args) {
        try {
            Class<?> c = Class.forName(args[0]); // obtengo la referencia de la clase

            Class[] argTypes = new Class[]{Member[].class, "Pedro".getClass()}; // obtengo los argumentos

            Method main = c.getDeclaredMethod("printMembers", argTypes); //definimos el tipo de metodo que se está buscando

            Class otraclase = Integer.class;

            Member[] members = otraclase.getMethods(); //obtenemos los métodos

            System.out.format("invoking %s.main()%n", c.getName());
            main.invoke(null, members, "Methods");
            // production code should handle these exceptions more gracefully
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException x) {
            x.printStackTrace();
        }
    }
}
