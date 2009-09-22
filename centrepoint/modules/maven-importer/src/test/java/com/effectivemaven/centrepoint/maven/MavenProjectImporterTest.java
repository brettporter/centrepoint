package com.effectivemaven.centrepoint.maven;

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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.store.MemoryProjectStore;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MavenProjectImporterTest
{
    private MavenProjectImporter importer;

    private ProjectStore store;

    @BeforeMethod
    public void setUp()
    {
        Injector injector = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( ProjectStore.class ).to( MemoryProjectStore.class );
                bind( RepositoryService.class ).to( TestRepositoryService.class );
            }
        } );

        importer = injector.getInstance( MavenProjectImporter.class );

        store = injector.getInstance( ProjectStore.class );
    }

    @Test
    public void importArtifact()
    {
        String id = "groupId:artifactId";
        assert store.getProjectById( id ) == null;

        Project project = importer.importMavenProject( "groupId", "artifactId", "1.0-SNAPSHOT" );

        assert id.equals( project.getId() );
        assert "1.0-SNAPSHOT".equals( project.getVersion() );
        assert "description".equals( project.getDescription() );
        assert "name".equals( project.getName() );
        assert "issueTrackerUrl".equals( project.getIssueTrackerUrl() );
        assert "scmUrl".equals( project.getScmUrl() );
        assert "url".equals( project.getUrl() );
        assert "ciManagementUrl".equals( project.getCiManagementUrl() );
        assert "repositoryUrl".equals( project.getRepositoryUrl() );
        assert "snapshotRepositoryUrl".equals( project.getSnapshotRepositoryUrl() );

        MavenCoordinates coordinate = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert "groupId".equals( coordinate.getGroupId() );
        assert "artifactId".equals( coordinate.getArtifactId() );

        assert store.getProjectById( id ) == project;
    }

    @Test
    public void importModifiedArtifact()
    {
        String id = "groupId:artifactId";
        assert store.getProjectById( id ) == null;

        Project project = new Project();
        project.setId( id );
        store.store( project );
        assert store.getProjectById( id ) == project;
        
        Project newProject = importer.importMavenProject( "groupId", "artifactId", "1.0-SNAPSHOT" );

        assert id.equals( newProject.getId() );
        assert "1.0-SNAPSHOT".equals( newProject.getVersion() );
        assert "description".equals( newProject.getDescription() );
        assert "name".equals( newProject.getName() );
        assert "issueTrackerUrl".equals( newProject.getIssueTrackerUrl() );
        assert "scmUrl".equals( newProject.getScmUrl() );
        assert "url".equals( newProject.getUrl() );
        assert "ciManagementUrl".equals( newProject.getCiManagementUrl() );
        assert "repositoryUrl".equals( newProject.getRepositoryUrl() );
        assert "snapshotRepositoryUrl".equals( newProject.getSnapshotRepositoryUrl() );

        MavenCoordinates coordinate = (MavenCoordinates) newProject.getExtensionModel( "maven" );
        assert "groupId".equals( coordinate.getGroupId() );
        assert "artifactId".equals( coordinate.getArtifactId() );

        assert store.getProjectById( id ) != project;
        assert store.getProjectById( id ) == newProject;
    }
}
