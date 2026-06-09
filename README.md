# Taller de Pruebas de Integración y Sistema

toda la documentacion se encuentra en la Wiki del repositorio

INTEGRANTES: Sadane Geronimo Miguel Santiago Acevedo Virgues

## estructura del proyecto

``` cli
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── edu
    │           └── unisabana
    │               └── tyvs
    │                   └── registry
    │                       ├── RegistryApplication.java
    │                       ├── application
    │                       │   ├── port
    │                       │   │   └── out
    │                       │   │       └── RegistryRepositoryPort.java
    │                       │   └── usecase
    │                       │       └── Registry.java
    │                       ├── config
    │                       │   └── RegistryConfig.java
    │                       ├── delivery
    │                       │   └── rest
    │                       │       └── RegistryController.java
    │                       ├── domain
    │                       │   └── model
    │                       │       ├── Gender.java
    │                       │       ├── Person.java
    │                       │       ├── RegisterResult.java
    │                       │       └── rq
    │                       │           └── PersonDTO.java
    │                       └── infrastructure
    │                           └── persistence
    │                               ├── RegistryRecord.java
    │                               └── RegistryRepository.java
    └── test
        └── java
            └── edu
                └── unisabana
                    └── tyvs
                        └── registry
                            ├── AppTest.java
                            ├── application
                            │   └── usecase
                            │       ├── RegistryTest.java
                            │       └── RegistryWithMockTest.java
                            └── delivery
                                └── rest
                                    └── RegistryControllerIT.java
```

## pasos de ejecucion

- pruebas unitarias

``` cli
mvn test
```

- se usa el siguiente comando para poder ejecutar tanto las pruebas unitarias, del sistema y el reporte de jacoco

``` cli
mvn -Djacoco.skip=false clean verify
```
