package com.effectivemaven.centrepoint.maven.repository;

/**
 * Copyright 2009
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CentralRepositoryServiceTest
{
    private CentralRepositoryService repositoryService;

    @BeforeTest
    public void setUp()
        throws MalformedURLException
    {
        Injector injector = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                String testRepositoryUrl;
                try
                {
                    testRepositoryUrl = new File( "src/test/repository" ).toURL().toExternalForm();
                }
                catch ( MalformedURLException e )
                {
                    throw new RuntimeException( e );
                }
                bindConstant().annotatedWith( RemoteRepositoryUrl.class ).to( testRepositoryUrl );

                String testLocalRepository = new File( "target/test-local-repository" ).getAbsolutePath();
                bindConstant().annotatedWith( LocalRepository.class ).to( testLocalRepository );
            }
        } );

        repositoryService = injector.getInstance( CentralRepositoryService.class );
    }

    @Test
    public void getAvailableVersions()
        throws RepositoryException
    {
        List<String> versions = repositoryService.getAvailableVersions( "com.effectivemaven.test", "artifact" );

        assert Arrays.asList( "1.0", "1.2" ).equals( versions );
    }

    @Test
    public void getAvailableVersionsNoMetadata()
        throws RepositoryException
    {
        List<String> versions = repositoryService.getAvailableVersions( "com.effectivemaven.test", "parent" );

        assert Collections.emptyList().equals( versions );
    }

    @Test
    public void getProjectFromRepository()
        throws RepositoryException
    {
        Project project = repositoryService.retrieveProject( "com.effectivemaven.test", "artifact", "1.2" );

        assert "com.effectivemaven.test:artifact".equals( project.getId() );
        assert "Unnamed - com.effectivemaven.test:artifact:jar:1.2".equals( project.getName() );
        assert "Description from parent".equals( project.getDescription() );
        assert "http://www.effectivemaven.com/".equals( project.getUrl() );
        assert "1.2".equals( project.getVersion() );
        assert "http://localhost:8082/continuum/".equals( project.getCiManagementUrl() );
        assert "http://issues.effectivemaven.com/".equals( project.getIssueTrackerUrl() );
        assert "http://localhost:8081/archiva/repository/releases/".equals( project.getRepositoryUrl() );
        assert "http://localhost:8081/archiva/repository/snapshots/".equals( project.getSnapshotRepositoryUrl() );
        assert "http://svn.effectivemaven.com/repos/test/artifact/trunk/".equals( project.getScmUrl() );

        MavenCoordinates coordinates = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert "com.effectivemaven.test".equals( coordinates.getGroupId() );
        assert "artifact".equals( coordinates.getArtifactId() );
    }

    @Test
    public void getMinimalProjectFromRepository()
        throws RepositoryException
    {
        Project project = repositoryService.retrieveProject( "com.effectivemaven.test", "parent", "1.2" );

        assert "com.effectivemaven.test:parent".equals( project.getId() );
        assert "Unnamed - com.effectivemaven.test:parent:pom:1.2".equals( project.getName() );
        assert "Description from parent".equals( project.getDescription() );
        assert "http://url.from.parent/".equals( project.getUrl() );
        assert "1.2".equals( project.getVersion() );
        assert project.getCiManagementUrl() == null;
        assert project.getIssueTrackerUrl() == null;
        assert project.getRepositoryUrl() == null;
        assert project.getScmUrl() == null;
        assert project.getSnapshotRepositoryUrl() == null;

        MavenCoordinates coordinates = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert "com.effectivemaven.test".equals( coordinates.getGroupId() );
        assert "parent".equals( coordinates.getArtifactId() );
    }

    @Test
    public void testDefaultRepositoryBindings()
        throws MalformedURLException
    {
        Injector injector = Guice.createInjector();
        CentralRepositoryService repository = injector.getInstance( CentralRepositoryService.class );
        String expectedLocalRepositoryUrl =
            new File( System.getProperty( "user.home" ) + "/.m2/repository" ).toURL().toExternalForm();
        assert expectedLocalRepositoryUrl.equals( repository.getLocalRepositoryUrl() );
        assert "http://repo1.maven.org/maven2".equals( repository.getRemoteRepositoryUrl() );
    }
}
