package edu.unisabana.tyvs.registry.application.usecase;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.SQLException;

/**
 * Clase de prueba unitaria para {@link Registry} utilizando un mock de {@link RegistryRepositoryPort}.
 *
 * <p>Estas pruebas ilustran cómo aislar el caso de uso del repositorio real,
 * aplicando dobles de prueba (Mockito) para simular los escenarios.</p>
 *
 * <p><b>Formato AAA:</b></p>
 * <ul>
 *   <li><b>Arrange</b>: se preparan datos y comportamiento del mock.</li>
 *   <li><b>Act</b>: se ejecuta el método bajo prueba.</li>
 *   <li><b>Assert</b>: se verifican resultados y que no haya interacciones no deseadas.</li>
 * </ul>
 *
 * <p><b>Beneficio:</b> este tipo de prueba es una <i>unitaria pura</i>,
 * sin necesidad de levantar bases de datos ni infraestructura adicional.</p>
 */
public class RegistryWithMockTest {

    /** Mock del puerto de persistencia. */
    private RegistryRepositoryPort repo;

    /** Caso de uso bajo prueba, instanciado con el mock. */
    private Registry registry;

    /**
     * Configura el mock y el caso de uso antes de cada prueba.
     *
     * <p>Se crea un mock de {@link RegistryRepositoryPort} usando Mockito
     * y se inyecta en la instancia de {@link Registry}.</p>
     */
    @Before
    public void setUp() {
        repo = mock(RegistryRepositoryPort.class);
        registry = new Registry(repo);
    }

    /**
     * Caso de prueba: detectar registros duplicados.
     *
     * <p><b>Escenario (BDD):</b></p>
     * <ul>
     *   <li><b>Given</b>: una persona con ID=7 y el repositorio ya indica que ese ID existe.</li>
     *   <li><b>When</b>: se intenta registrar la persona.</li>
     *   <li><b>Then</b>: el resultado debe ser {@link RegisterResult#DUPLICATED}
     *       y no se debe invocar el método {@code save(...)} en el repositorio.</li>
     * </ul>
     *
     * @throws Exception propagada en caso de error durante la ejecución.
     */
    @Test
    public void shouldReturnDuplicatedWhenRepoSaysExists() throws Exception {
        // Arrange: configurar mock y datos
        when(repo.existsById(7)).thenReturn(true);
        Person p = new Person("Ana", 7, 25, Gender.FEMALE, true);

        // Act: ejecutar método bajo prueba
        RegisterResult result = registry.registerVoter(p);

        // Assert: verificar resultado y comportamiento esperado del mock
        assertEquals(RegisterResult.DUPLICATED, result);
        verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
    }


    
    // Prueba vivo: el repositorio indica que el ID NO existe, se debe invocar `save(...)`
    @Test
    public void shouldSaveWhenRepoSaysNotExists() throws Exception {
        // Arrange
        when(repo.existsById(10)).thenReturn(false);
        Person p = new Person("Carlos", 10, 30, Gender.MALE, true);

        // Act
        RegisterResult result = registry.registerVoter(p);

        // Assert: result is VALID and save was invoked with the exact values
        assertEquals(RegisterResult.VALID, result);
        verify(repo).save(10, "Carlos", 30, true);
    }

    // Prueba de error: simula una SQLException en el repositorio y verifica
    // que el caso de uso la envuelva en IllegalStateException y NO invoque `save(...)`.
    @Test
    public void shouldWrapSQLExceptionFromRepo() throws Exception {
        // Arrange: simulate SQL exception when checking existence
        when(repo.existsById(5)).thenThrow(new SQLException("DB down"));
        Person p = new Person("Luis", 5, 40, Gender.MALE, true);

        // Act / Assert: registry should wrap the exception into IllegalStateException
        try {
            registry.registerVoter(p);
            fail("Expected IllegalStateException due to repository failure");
        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Persistencia"));
            assertTrue(msg.contains("SQLException"));
        }

        // And ensure save was never called
        verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
    }
}
