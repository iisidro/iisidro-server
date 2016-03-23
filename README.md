# IISIdro Server

La aplicación fue construida utilizando **JHipster**, la documentación de esta herramienta se encuentra en: [https://jhipster.github.io](https://jhipster.github.io).

Antes de levantar el proyecto debes installar y configurar las siguientes dependencias:

1. **Node.js**: para ejecutar el servidor web de desarrollo y constuir del proyecto .

Después de installar Node, deberías ejecutar los siguientes comandos para installar las herramientas de desarrollo (como
Bower y BrowserSync). Sólo tendrás que ejecutar este comando cuando se modifiquen las dependencias en package.json. 

    npm install

Utilizamos **Gulp** como nuestro sistema de construcción. Para installar Gulp:

    npm install -g gulp

Ejecuta los siguientes comando en dos consolas separadas para que el navegador se actualice automaticamente cuando se modifiquen los archivos en tu disco.

    mvn
    gulp

Utilizamos **Bower** para gestionar CSS y las dependencias de JavaScript del proyecto. Puedes actualizar las dependencias especificando la versión más reciente en `bower.json`. También puedes ejectuar `bower update` y `bower install` para administrar las dependencias. Para conocer como se utiliza un comando puedes agregar el paramentro `-h`. Por ejemplo, `bower update -h`.

# Building para producción

Para optimizar el cliente de iisidro para producción, ejecuta:

    mvn -Pprod clean package

Contatenará y minificará los archivos de CSS y JavaScript . También modificará ` index.html`.

Para asegurarte que todo funciona bien, ejecuta:

    java -jar target/*.war --spring.profiles.active=prod

A continuación ingresa en el navegador a [http://localhost:8080](http://localhost:8080).

# Testing

Los unit tests son ejecutadas mediante **JUnit** a través de:

    mvn test

# Integración Continua

Para configurar este proyecto en **Jenkins** , utiliza la siguiente configuración:

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

[JHipster]: https://jhipster.github.io/
[Maven]: https://maven.apache.org/

