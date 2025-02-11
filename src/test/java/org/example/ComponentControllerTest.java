package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.example.controller.ComponentController;
import org.example.model.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentControllerTest {

    private ComponentController controller;

    @BeforeEach
    public void setup() {
        // Antes de cada prueba, crea una nueva instancia del controlador.
        controller = new ComponentController();
    }

    @Test
    public void testGetComponentsInitiallyEmpty() {
        List<Component> components = controller.getComponents();
        assertNotNull(components);
        assertTrue(components.isEmpty(), "La lista de componentes debe estar vacía inicialmente.");
    }

    @Test
    public void testAddComponent() {
        Map<String, String> data = Map.of(
                "name", "Ryzen 5",
                "type", "CPU",
                "price", "342"
        );

        Map<String, String> response = controller.addComponent(data);
        assertEquals("Component added successfully", response.get("message"),
                "El mensaje de respuesta debe indicar que el componente se agregó correctamente.");

        List<Component> components = controller.getComponents();
        assertEquals(1, components.size(), "Se debe tener un componente en la lista.");

        Component comp = components.get(0);
        assertEquals("Ryzen 5", comp.getName());
        assertEquals("CPU", comp.getType());
        assertEquals(342.0, comp.getPrice());
    }

    @Test
    public void testAddComponentInvalidPrice() {
        // Prueba con precio inválido
        Map<String, String> data = Map.of(
                "name", "Test",
                "type", "CPU",
                "price", "no-numeric"
        );

        Map<String, String> response = controller.addComponent(data);
        assertEquals("Invalid price format", response.get("error"));

        // La lista de componentes debe seguir vacía
        assertTrue(controller.getComponents().isEmpty());
    }

    @Test
    public void testAddComponentMissingField() {
        // Prueba con datos incompletos
        Map<String, String> data = Map.of(
                "name", "Test",
                "type", "CPU"
                // Falta el campo price
        );

        Map<String, String> response = controller.addComponent(data);
        assertEquals("Missing fields", response.get("error"));

        assertTrue(controller.getComponents().isEmpty());
    }
}
