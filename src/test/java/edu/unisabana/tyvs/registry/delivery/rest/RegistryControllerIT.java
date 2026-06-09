// src/test/java/edu/unisabana/tyvs/registry/delivery/rest/RegistryControllerIT.java
package edu.unisabana.tyvs.registry.delivery.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import org.junit.Before;

// src/test/java/.../RegistryControllerIT.java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistryControllerIT {
    // Tests de integración para el endpoint /register.
    // Cada prueba envía un JSON al controlador y verifica el resultado
    // textual devuelto por la aplicación (ej. VALID, DUPLICATED, UNDERAGE, DEAD).

    @TestConfiguration
    static class TestBeans {
        @Bean
        public RegistryRepositoryPort registryRepositoryPort() throws Exception {
            String jdbc = "jdbc:h2:mem:regdb;DB_CLOSE_DELAY=-1";
            var repo = new edu.unisabana.tyvs.registry.infrastructure.persistence.RegistryRepository(jdbc);
            repo.initSchema();
            return repo;
        }

        @Bean
        public edu.unisabana.tyvs.registry.application.usecase.Registry registry(RegistryRepositoryPort port) {
            return new edu.unisabana.tyvs.registry.application.usecase.Registry(port);
        }
    }

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private RegistryRepositoryPort repo;

    @Before
    public void cleanDb() throws Exception {
        repo.deleteAll();
    }

    @Test
    // Caso feliz: persona válida debe ser aceptada y devolver "VALID".
    public void shouldRegisterValidPerson() throws Exception {
        // JSON con campos correctos: nombre, id positivo, mayor de edad, género y alive=true
        String json = "{\"name\":\"Ana\",\"id\":100,\"age\":30,\"gender\":\"FEMALE\",\"alive\":true}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> resp = rest.postForEntity("/register", new HttpEntity<>(json, headers), String.class);

        assert resp.getStatusCode() == HttpStatus.OK;
        assert "VALID".equals(resp.getBody());
    }

    @Test
    // Si se registra la misma cédula dos veces, la segunda llamada debe devolver "DUPLICATED".
    public void shouldReturnDuplicatedWhenIdExists() throws Exception {
        // La primera petición inserta el registro; la segunda detecta duplicado.
        String json = "{\"name\":\"Carlos\",\"id\":200,\"age\":45,\"gender\":\"MALE\",\"alive\":true}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> first = rest.postForEntity("/register", new HttpEntity<>(json, headers), String.class);
        assert first.getStatusCode() == HttpStatus.OK;
        assert "VALID".equals(first.getBody());

        ResponseEntity<String> second = rest.postForEntity("/register", new HttpEntity<>(json, headers), String.class);
        assert second.getStatusCode() == HttpStatus.OK;
        assert "DUPLICATED".equals(second.getBody());
    }

    @Test
    // Persona menor de 18 años debe clasificarse como "UNDERAGE".
    public void shouldReturnUnderageForYoungPerson() throws Exception {
        // age = 17 activa la regla de menor de edad en el caso de uso
        String json = "{\"name\":\"Luis\",\"id\":300,\"age\":17,\"gender\":\"MALE\",\"alive\":true}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> resp = rest.postForEntity("/register", new HttpEntity<>(json, headers), String.class);

        assert resp.getStatusCode() == HttpStatus.OK;
        assert "UNDERAGE".equals(resp.getBody());
    }

    @Test
    // Si la persona no está viva (alive=false) el caso de uso devuelve "DEAD".
    public void shouldReturnDeadWhenNotAlive() throws Exception {
        // alive=false debe devolver DEAD
        String json = "{\"name\":\"AnaMaria\",\"id\":400,\"age\":50,\"gender\":\"FEMALE\",\"alive\":false}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> resp = rest.postForEntity("/register", new HttpEntity<>(json, headers), String.class);

        assert resp.getStatusCode() == HttpStatus.OK;
        assert "DEAD".equals(resp.getBody());
    }

    @Test
    public void shouldReturnBadRequestForInvalidJsonTypes() throws Exception {
        // Prueba negativa: JSON con tipo incorrecto en 'id' -> deserialización falla y se espera 400
        String badJson = "{\"name\":\"X\",\"id\":\"not-a-number\",\"age\":30,\"gender\":\"MALE\",\"alive\":true}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> resp = rest.postForEntity("/register", new HttpEntity<>(badJson, headers), String.class);
        assert resp.getStatusCode() == HttpStatus.BAD_REQUEST;
    }
}
