/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.shrinkwrap.springboot.app;

import org.awaitility.Duration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipStoredExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.shrinkwrap.springboot.api.SpringBoot13Archive;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import static io.restassured.RestAssured.get;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 *
 * Functional test that checks that the Spring Boot file created is bootable.
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class SpringBoot13Test {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_create_valid_spring_boot_archive() throws IOException {


        SpringBoot13Archive springBoot13Archive = ShrinkWrap.create(SpringBoot13Archive.class)
                .addClass(HelloController.class)

                .addLibs(Maven.resolver()
                        .resolve("org.springframework.boot:spring-boot-starter-web:1.3.5.RELEASE")
                        .withTransitivity()
                        .as(JavaArchive.class)
                )

                .addLauncher(Maven.resolver()
                        .resolve("org.springframework.boot:spring-boot-loader:1.3.5.RELEASE")
                        .withTransitivity()
                        .as(JavaArchive.class))

                .addSpringBootApplication(Application.class);

        springBoot13Archive.as(ZipStoredExporter.class).exportTo(new File(temporaryFolder.getRoot(), "app.jar"));
        Process process = null;

        try {
            process = new ProcessBuilder("java", "-jar", temporaryFolder.getRoot().getAbsolutePath() + "/app.jar").start();

            final Callable<Integer> statusCode = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    try {
                        URL url = new URL("http://localhost:8080");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();

                        return connection.getResponseCode();
                    } catch(ConnectException e) {
                        return 404;
                    }
                }
            };

            await()
                    .atMost(Duration.TEN_SECONDS)
                    .until(statusCode, equalTo(200));

            get().then().body(equalTo("Greetings From Spring Boot"));
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

}
