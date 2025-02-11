# AREP Taller 3 💻
## Arquitecturas de Servidores de Aplicaciones, Meta protocolos de objetos, Patrón IoC, Reflexión

Este proyecto es un framework web ligero desarrollado en Java que permite a los desarrolladores crear aplicaciones web con servicios REST y gestionar archivos estáticos (HTML, CSS, JavaScript, imágenes, etc.). El framework proporciona herramientas para definir rutas REST usando funciones lambda, extraer parámetros de consulta de las solicitudes y especificar la ubicación de archivos estáticos. Además, implementa un mecanismo de Inversión de Control (IoC) basado en reflexión para cargar automáticamente controladores anotados con @RestController.

En la aplicación web podrás añadir los componentes que quieres y te hacen falta para armar tu computador deseado. 😎

### Application Screenshots

![image](https://github.com/user-attachments/assets/3d24abb6-89fe-4114-9d93-66e60ea56a28)

![image](https://github.com/user-attachments/assets/5b251167-2564-4fd6-bc1a-779417b98774)

![image](https://github.com/user-attachments/assets/c05bd747-7914-49ea-873c-5e347029f298)


---

### Main features

1. **Definición de Rutas REST**:
    - Permite definir rutas REST usando el método `get()` y `post()`.
    - Soporta el uso de funciones lambda para manejar solicitudes y respuestas.
    - Ejemplo:
        
        ```java
        get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        ```
        
2. **Extracción de Parámetros de Consulta**:
    - Proporciona un mecanismo para extraer parámetros de consulta de las URLs.
    - Ejemplo:
        
        ```java
        get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        ```
        
3. **Gestión de Archivos Estáticos**:
    - Permite especificar la carpeta donde se encuentran los archivos estáticos usando el método `staticfiles()`.
    - Ejemplo:
        
        ```java
        staticfiles("src/main/webapp");
        ```

4. **Inversión de Control (IoC) y Reflexión**:

    - Carga automáticamente controladores anotados con @RestController.
    - Soporta anotaciones como @GetMapping, @PostMapping, y @RequestParam.
    - Ejemplo:
    
        ```java
        @RestController
        public class GreetingController {
            @GetMapping("/greeting")
            public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
                return "Hola " + name;
            }
        }
        ```
4. **Escalabilidad y Mantenibilidad**:
    - Diseñado para ser ligero y fácil de extender.
    - Ideal para aplicaciones pequeñas y medianas que requieren un servidor HTTP personalizado.

---

### Prerequisites

Para ejecutar este proyecto necesitas instalar lo siguiente:

```
- Java 17 o superior
- Maven 3.8.1 o superior (la versión en el entorno donde fue creado es la 3.9.9)
- Un navegador web
```
En caso de no tener maven instalado, aquí encuentras un tutorial [Maven](https://dev.to/vanessa_corredor/instalar-manualmente-maven-en-windows-10-50pb).


---

### Installing ⚙️

Sigue estos pasos para obtener un entorno de desarrollo funcional:

1. Clona este repositorio:

```
git clone https://github.com/AnaDuranB/Taller-03-AREP.git
```

2. Ingresa al directorio del proyecto:

```
cd Taller-03-AREP
```

En caso de no contar con un IDE de java que se haga responsable de la compilación y ejecución:

3. Compila el proyecto con Maven:

```
mvn clean compile
```

4. Ejecuta el servidor:

```
java -cp target/classes org.example.MicroSpringBoot
```
![image](https://github.com/user-attachments/assets/69a407fd-b368-4694-8aa0-15bfd23fb449)


5. Abre tu navegador y accede a:

```
http://localhost:35000/
```

---

## Project Structure

```
src/
  main/
    java/
      org/
        example/
            annotations/               # Anotaciones personalizadas
                GetMapping.java
                PostMappingjava
                RequestBody.java
                Requestparamjava
                RestControllerjava
            controller/
                ComponentController.java   # Controlador para manejar componentes
                GreetingController.java    # Controlador de ejemplo
            model/
                Component.java        # Modelo de datos para componentes
            server/
                HttpServer.java       # Clase principal del servidor
                Request.java          # Maneja las solicitudes HTTP
                Response.java         # Maneja las respuestas HTTP
          MicroSpringBoot.java        # Clase principal del framework IoC
    webapp/                    # Carpeta de archivos estáticos             
        index.html              # Archivo HTML
        styles.css              # Archivo CSS
        script.js               # Archivo JavaScript
  test/
    java/                       # Pruebas unitarias
pom.xml                         # Archivo de configuración de Maven
README.md                       # Documentación del proyecto
```

---

## Examples of Use

### 1. Definición de Rutas REST

```java

// En Component Controller
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


// En HttpServer (aún presente, pero no usado por la llegada de ComponentController)
public static void main(String[] args) {
    staticfiles("src/main/webapp");

    get("/hello", (req, res) -> "Hello " + req.getValues("name"));
    get("/pi", (req, res) -> String.valueOf(Math.PI));

    get("/api/components", (req, res) -> {
        return toJson(components);
    });

    post("/api/components", (req, res) -> {
        String body = req.getBody();
        Map<String, String> data = parseJson(body);
        if (data.containsKey("name") && data.containsKey("type") && data.containsKey("price")) {
            components.add(new Component(data.get("name"), data.get("type"), Double.parseDouble(data.get("price"))));
            return "{\"message\": \"Component added successfully\"}";
        }
        return "{\"error\": \"Missing fields\"}";
    });

    start(35000);
}

```
### 2. HTTP requests

- **GET `/hello?name=Pedro`**:
    - Respuesta: `Hello Pedro`
- **GET `/pi`**:
    - Respuesta: `3.141592653589793`
- **GET `/api/components`**:
    - Respuesta: `[{"name": "AMD RYZEN 5 5600X", "type": "CPU", "price": 769999}]`
- **POST `/api/components`** (con cuerpo JSON):
    
    
    ```json
    {
      "name": "AMD RYZEN 5 5600X",
      "type": "CPU",
      "price": 769999
    }
    ```
    
    - Respuesta: `{"message": "Component added successfully"}`

### 3. Static Files

- **GET `/index.html`**:
    - Sirve el archivo `index.html` ubicado en `src/main/webapp`.

---

## Running the tests

El servidor puede probarse usando herramientas como:

- **Navegador web**: Para solicitudes GET y archivos estáticos.
- **Postman**: Para enviar solicitudes POST y probar la API REST.
- **curl**: Para pruebas desde la terminal.

Para ejecutar las pruebas automatizadas:

```
mvn test
```
![image](https://github.com/user-attachments/assets/1e42f3e3-3167-4f3d-ac02-fe8bf0373433)

Estas pruebas verifican la correcta respuesta del servidor ante diferentes solicitudes.


Ejemplo con `curl`:

```bash
curl http://localhost:35000/hello?name=Ana
curl http://localhost:35000/pi
curl -X POST http://localhost:35000/api/components -d '{"name": "AMD RYZEN 5 5600X", "type": "CPU", "price": 769999}'
```

## Built With

- [Java SE](https://www.oracle.com/java/) - Lenguaje de programación
- [Maven](https://maven.apache.org/) - Herramienta de gestión de dependencias y construcción

## Authors

- Ana Maria Duran - *AREP* *Taller 3* - [AnaDuranB](https://github.com/AnaDuranB)
