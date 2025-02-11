package org.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.model.Component;
import org.example.annotations.*;

@RestController
public class ComponentController {
    private final List<Component> components = new ArrayList<>();

    @GetMapping("/api/components")
    public List<Component> getComponents() {
        return components;
    }

    @PostMapping("/api/components")
    public Map<String, String> addComponent(@RequestBody Map<String, String> data) {
        if (data.containsKey("name") && data.containsKey("type") && data.containsKey("price")) {
            try {
                double price = Double.parseDouble(data.get("price"));
                components.add(new Component(data.get("name"), data.get("type"), price));
                return Map.of("message", "Component added successfully");
            } catch (NumberFormatException e) {
                return Map.of("error", "Invalid price format");
            }
        }
        return Map.of("error", "Missing fields");
    }
}
