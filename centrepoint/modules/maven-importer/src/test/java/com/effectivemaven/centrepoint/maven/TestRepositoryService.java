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

import java.util.List;

import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;

public class TestRepositoryService
    implements RepositoryService
{
    public List<String> getAvailableVersions( String groupId, String artifactId )
    {
        throw new UnsupportedOperationException( "not needed for our testing" );
    }

    public Project retrieveProject( String groupId, String artifactId, String version )
    {
        Project project = new Project();
        project.setId( MavenCoordinates.constructProjectId( groupId, artifactId ) );
        project.setVersion( version );

        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setGroupId( groupId );
        coordinates.setArtifactId( artifactId );
        project.addExtensionModel( coordinates );

        project.setDescription( "description" );
        project.setName( "name" );
        project.setIssueTrackerUrl( "issueTrackerUrl" );
        project.setScmUrl( "scmUrl" );
        project.setUrl( "url" );
        project.setCiManagementUrl( "ciManagementUrl" );
        project.setRepositoryUrl( "repositoryUrl" );
        project.setSnapshotRepositoryUrl( "snapshotRepositoryUrl" );

        return project;
    }
}
