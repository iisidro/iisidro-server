[![Build Status](https://travis-ci.org/iisidro/iisidro-server.svg?branch=master)](https://travis-ci.org/iisidro/iisidro-server)

# IISIdro Server

La aplicación fue construida utilizando **JHipster**, la documentación de esta herramienta se encuentra disponible en: [https://jhipster.github.io](https://jhipster.github.io).

# Una vez clonado el repositorio

Testear que la aplicación tiene todos los tests locales corriendo correctamente:

```bash
mvn test
```

Para trabajar con una base de datos relacional local, instalar: [PostgreSQL](https://www.postgresql.org)

Crear la definicion de los roles y la base de datos con:

```bash
CREATE ROLE iisidro;   

CREATE DATABASE iisidro_db WITH OWNER=iisidro;  

ALTER ROLE "iisidro" WITH LOGIN;

```

Luego verificar que la conexión esta disponible al PSQL:

```bash
psql -h localhost -U iisidro iisidro_db
```

Finalmente testear la aplicación contra el PSQL:

```bash
./mvnw -Dprod
```

# Configuracion IntelliJ IDEA (recomendada)

- Import project > Seleccionar el archivo pom.xml de nodo principal de la carpeta clonada.
- Darle Next en la pantalla de configuración de proyecto.
- Seleccionar profile: **dev**
- Seleccionar el unico proyecto a importar: ar.edu.utn.frro:iisidro:0.0.1-SNAPSHOT
- Configurar el JDK 1.8
- Finalmente configurar el nombre que le dara IntelliJ IDEA al proyecto: iisidro-server: Finish.
- Si aparece un cartel que dice que tiene el repositorio git configurado darle: "Add root".

# Configuración en Eclipse IDE

1. Importar el proyecto:

    * Abrir Eclipse
    * Seleccionar File -> Import
    * Elegir “Existing Maven Projects”
    * Seleccionar el directorio del proyecto
    * Click en “Finish”

    **Nota:** si obtiene el siguiente error: “Plugin execution not covered by lifecycle configuration: org.bsc.maven:maven-processor-plugin:2.2.4:process (execution: process, phase: generate-sources)”. Debe seleccionar la opción “Discover new m2e connectors” en la pestaña de marker. 

2. Excluir la carpeta node_modules:

    * Click derecho sobre el proyecto -> Properties -> Resource -> Resource Filters
    * Seleccionar: Exclude all, Applies to folders, Name matches node_modules
    * Click en “Ok”

3. Actualizar las dependencias del proyecto:

    * Click derecho sobre el proyecto -> Maven -> Update Project
