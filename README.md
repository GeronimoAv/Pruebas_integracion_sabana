# Taller de Pruebas de Integración y Sistema

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
