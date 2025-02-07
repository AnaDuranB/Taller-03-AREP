package org.example.clase.ecispring;

/*
* java -cp "target/classes/" org.example.clase.ecispring.InvokePrintMembers org.example.clase.ecispring.Ecispring
* */
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.System.out;

public class Ecispring {

    public static void main(String[] args) {
        out.println("Reflexion");

        // instancia que representa una clase (String)
        // obtengo un objeto que representa la clase
        Class c = "Mundo".getClass();

        // deme los metodos de esa clase
        // printMembers(c.getDeclaredMethods(), "Members");
        printMembers(c.getMethods(), "Members");
    }

    public static void printMembers(Member[] mbrs, String s) {
        out.format("%s:%n", s);
        for (Member mbr : mbrs) {
            if (mbr instanceof Field)
                out.format(" %s%n", ((Field) mbr).toGenericString());
            else if (mbr instanceof Constructor)
                out.format(" %s%n", ((Constructor) mbr).toGenericString());
            else if (mbr instanceof Method)
                out.format(" %s%n", ((Method) mbr).toGenericString());
        }
        if (mbrs.length == 0)
            out.format(" -- No %s --%n", s);
        out.format("%n");
    }
}
