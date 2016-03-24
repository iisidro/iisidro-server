[![Stories in Ready](https://badge.waffle.io/iisidro/iisidro-server.png?label=ready&title=Ready)](https://waffle.io/iisidro/iisidro-server)

# IISIdro Server

La aplicación fue construida utilizando **JHipster**, la documentación de esta herramienta se encuentra disponible en: [https://jhipster.github.io](https://jhipster.github.io).

Antes de levantar el proyecto debe instalar y configurar las siguientes dependencias:

1. **Node.js**: para ejecutar el servidor web de desarrollo y constuir el proyecto .

	Después de installar Node, deberá ejecutar los siguientes comandos para instalar las herramientas de desarrollo (como
	Bower y BrowserSync). 
	
	Sólo tendrá que ejecutar este comando cuando se modifiquen las dependencias en `package.json`. 
	
	    npm install
	
	Utilizamos **Gulp** como nuestro sistema de construcción, para instalarlo:
	
	    npm install -g gulp
	
	Ejecute los siguientes comando en dos consolas separadas para que el navegador se actualice automáticamente cuando los archivos en su disco cambien.
	
	    mvn
	    gulp

2. **Bower**: para gestionar CSS y las dependencias de JavaScript del proyecto. Puede actualizar las dependencias especificando la versión más reciente en `bower.json`. También puede ejectuar `bower update` y `bower install` para administrar las dependencias. Para conocer como se utiliza un comando puede agregar el parámentro `-h`. Por ejemplo, `bower update -h`.

# Building para producción

Para optimizar el cliente de iisidro en producción, ejecute:

    mvn -Pprod clean package

Concatenará y minificará los archivos de CSS y JavaScript . También modificará el ` index.html`.

Para asegurarse que todo funciona bien, ejecute:

    java -jar target/*.war --spring.profiles.active=prod

A continuación ingrese en el navegador a [http://localhost:8080](http://localhost:8080).

# Testing

Los unit tests son ejecutadas mediante **JUnit** a través de:

    mvn test

# Integración Continua

Para configurar este proyecto en **Jenkins** , utilize la siguiente configuración:

* Project name: `iisidro`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/iisidro.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

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
