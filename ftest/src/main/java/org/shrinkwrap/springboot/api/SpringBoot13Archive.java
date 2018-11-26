/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.shrinkwrap.springboot.api;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;


/**
 * Implements Launching executable JARS for Spring Boot versions prior to 1.4.X apps,
 * following http://docs.spring.io/spring-boot/docs/1.3.5.RELEASE/reference/htmlsingle/#executable-jar strategy
 *
 * Basically the most important thing to remain is that libs are placed in a root directory called lib.
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 */
public interface SpringBoot13Archive extends Archive<SpringBoot13Archive>, ServiceProviderContainer<SpringBoot13Archive>  {

    /**
     * This method adds the Spring Boot application class (one with main method) inside current archive.
     * @param startClass application class
     * @return Spring Boot Archive with application class added
     */
    SpringBoot13Archive addSpringBootApplication(Class<?> startClass);

    /**
     * This method adds a library inside current Spring Boot archive. In terms of Spring Boot format it adds this library into lib directory using STORED method.
     * @param lib to add
     * @return Spring Boot Archive with given library added
     */
    SpringBoot13Archive addLib(JavaArchive lib);

    /**
     * This method adds all libraries as Spring Boot library
     * @param libs to add
     * @return Spring Boot Archive with given libraries added
     */
    SpringBoot13Archive addLibs(JavaArchive...libs);

    /**
     * This method adds the Java Archive that contains the Spring Boot loader
     * @param launcher java archive containing Spring Boot loader
     * @return Spring Boot Archive with Spring Boot loader added
     */
    SpringBoot13Archive addLauncher(JavaArchive... launcher);

}
