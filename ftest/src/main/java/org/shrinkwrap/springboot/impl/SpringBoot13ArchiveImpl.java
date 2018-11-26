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


package org.shrinkwrap.springboot.impl;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipStoredExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.shrinkwrap.springboot.api.SpringBoot13Archive;

/**
 * Implementation of the {@link SpringBoot13Archive} interface
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 */
public class SpringBoot13ArchiveImpl extends ContainerBase<SpringBoot13Archive> implements SpringBoot13Archive {

    public SpringBoot13ArchiveImpl(Archive<?> archive) {
        super(SpringBoot13Archive.class, archive);
    }

    /**
     * Path to the manifests inside of the Archive.
     */
    private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

    /**
     * Path to the resources inside of the Archive.
     */
    private static final ArchivePath PATH_RESOURCE = new BasicPath("/");

    /**
     * Path to the classes inside of the Archive.
     */
    private static final ArchivePath PATH_CLASSES = new BasicPath("/");

    /**
     * Path to the libraries inside of the Archive.
     */
    private static final ArchivePath PATH_LIBRARY = ArchivePaths.create(PATH_CLASSES, "lib");

    private static final String MANIFEST_FILE = "" +
            "Main-Class: org.springframework.boot.loader.JarLauncher" + System.lineSeparator() +
            "Start-Class: %s" + System.lineSeparator();

    @Override
    public SpringBoot13Archive addSpringBootApplication(Class<?> startClass) {
        this.addClass(startClass);
        final StringAsset manifestContent = new StringAsset(String.format(MANIFEST_FILE, startClass.getName()));

        this.addAsManifestResource(manifestContent, "MANIFEST.MF");
        return this;
    }

    @Override
    public SpringBoot13Archive addLib(JavaArchive lib) {
        this.addAsDirectory(PATH_LIBRARY);
        this.add(lib, PATH_LIBRARY, ZipStoredExporter.class);
        return this;
    }

    @Override
    public SpringBoot13Archive addLibs(JavaArchive... libs) {
        for (JavaArchive lib : libs) {
            this.addLib(lib);
        }
        return this;
    }

    @Override
    public SpringBoot13Archive addLauncher(JavaArchive... launcher) {
        for (JavaArchive l : launcher) {
            this.merge(l);
        }
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        return PATH_MANIFEST;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getClassesPath() {
        return PATH_CLASSES;
    }

    @Override
    protected ArchivePath getLibraryPath() {
        return PATH_LIBRARY;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        return PATH_RESOURCE;
    }
}
