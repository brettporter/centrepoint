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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;

import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implementation of the Maven repository service for querying the central repository. The Maven libraries are used so
 * that the local repository is also considered and behaviour is consistent.
 */
@Singleton
public class CentralRepositoryService
    implements RepositoryService
{
    private final ArtifactMetadataSource metadataSource;

    private final ArtifactFactory artifactFactory;

    private ArtifactRepository repository;

    private final ArtifactRepository localRepository;

    private final MavenProjectBuilder projectBuilder;

    /**
     * Configuration parameters that can be modified by alternate bindings in Guice.
     */
    static class RepositoryParams
    {
        @Inject( optional = true )
        private @RemoteRepositoryUrl
        String repositoryUrl = "http://repo1.maven.org/maven2";

        @Inject( optional = true )
        private @LocalRepository
        String localRepositoryPath = System.getProperty( "user.home" ) + "/.m2/repository";
    }

    @Inject
    private CentralRepositoryService( RepositoryParams params )
    {
        try
        {
            // jetty:run friendly setting
            Map<? extends Object, ? extends Object> context =
                Collections.singletonMap( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION, Boolean.TRUE );

            // use Plexus to load the Maven components needed to retrieve repository information
            InputStreamReader configurationReader =
                new InputStreamReader( getClass().getResourceAsStream( "/custom-plexus.xml" ) );
            PlexusContainer container = new DefaultPlexusContainer( configurationReader, context );

            this.artifactFactory = (ArtifactFactory) container.lookup( ArtifactFactory.ROLE );

            this.metadataSource =
                (ArtifactMetadataSource) container.lookup( MavenMetadataSource.ROLE, MavenMetadataSource.ROLE_HINT );

            ArtifactRepositoryFactory repositoryFactory =
                (ArtifactRepositoryFactory) container.lookup( ArtifactRepositoryFactory.ROLE );

            String localRepositoryUrl = new File( params.localRepositoryPath ).toURL().toExternalForm();
            this.localRepository =
                repositoryFactory.createDeploymentArtifactRepository( "local", localRepositoryUrl,
                                                                      new DefaultRepositoryLayout(), false );

            repository =
                repositoryFactory.createArtifactRepository( "central", params.repositoryUrl,
                                                            new DefaultRepositoryLayout(),
                                                            new ArtifactRepositoryPolicy(),
                                                            new ArtifactRepositoryPolicy() );

            projectBuilder = (MavenProjectBuilder) container.lookup( MavenProjectBuilder.ROLE );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error starting Maven components for repository service", e );
        }
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getAvailableVersions( String groupId, String artifactId )
        throws RepositoryException
    {
        // the version supplied is arbitrary, but must not be null
        Artifact artifact = artifactFactory.createProjectArtifact( groupId, artifactId, "1.0" );

        List<ArtifactVersion> versions = null;
        try
        {
            versions =
                metadataSource.retrieveAvailableVersions( artifact, localRepository,
                                                          Collections.singletonList( repository ) );
        }
        catch ( ArtifactMetadataRetrievalException e )
        {
            throw new RepositoryException( e.getMessage(), e );
        }

        // sort the versions as required by the spec
        Collections.sort( versions );

        // convert the list into one of strings to return
        List<String> availableVersions = new ArrayList<String>( versions.size() );
        for ( ArtifactVersion version : versions )
        {
            availableVersions.add( version.toString() );
        }
        return availableVersions;
    }

    public Project retrieveProject( String groupId, String artifactId, String version )
        throws RepositoryException
    {
        // get the project from the repository
        Artifact artifact = artifactFactory.createProjectArtifact( groupId, artifactId, version );
        MavenProject mavenProject;
        try
        {
            mavenProject =
                projectBuilder.buildFromRepository( artifact, Collections.singletonList( repository ), localRepository );
        }
        catch ( ProjectBuildingException e )
        {
            throw new RepositoryException( e.getMessage(), e );
        }

        // populate the Centrepoint model from the Maven project
        Project project = new Project();
        project.setId( MavenCoordinates.constructProjectId( groupId, artifactId ) );
        project.setVersion( version );

        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setGroupId( groupId );
        coordinates.setArtifactId( artifactId );
        project.addExtensionModel( coordinates );

        project.setDescription( mavenProject.getDescription() );
        project.setName( mavenProject.getName() );
        if ( mavenProject.getCiManagement() != null )
        {
            project.setCiManagementUrl( mavenProject.getCiManagement().getUrl() );
        }
        if ( mavenProject.getIssueManagement() != null )
        {
            project.setIssueTrackerUrl( mavenProject.getIssueManagement().getUrl() );
        }
        if ( mavenProject.getScm() != null )
        {
            project.setScmUrl( mavenProject.getScm().getUrl() );
        }
        project.setUrl( mavenProject.getUrl() );

        DistributionManagement distMgmt = mavenProject.getDistributionManagement();
        if ( distMgmt != null )
        {
            project.setRepositoryUrl( distMgmt.getRepository().getUrl() );
            project.setSnapshotRepositoryUrl( distMgmt.getSnapshotRepository().getUrl() );
        }

        return project;
    }

    String getLocalRepositoryUrl()
    {
        return localRepository.getUrl();
    }

    String getRemoteRepositoryUrl()
    {
        return repository.getUrl();
    }
}
