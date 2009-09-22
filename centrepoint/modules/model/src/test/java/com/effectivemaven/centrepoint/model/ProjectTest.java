package com.effectivemaven.centrepoint.model;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

public class ProjectTest
{
    @Test
    public void testAccessors()
    {
        Project project = new Project();
        project.setId( "id" );
        project.setName( "name" );
        project.setDescription( "description" );
        project.setUrl( "url" );
        project.setIssueTrackerUrl( "issueTrackerUrl" );
        project.setScmUrl( "scmUrl" );
        project.setVersion( "version" );
        project.setCiManagementUrl( "ciManagementUrl" );
        project.setRepositoryUrl( "repositoryUrl" );
        project.setSnapshotRepositoryUrl( "snapshotRepositoryUrl" );

        assert "id".equals( project.getId() );
        assert "name".equals( project.getName() );
        assert "description".equals( project.getDescription() );
        assert "url".equals( project.getUrl() );
        assert "issueTrackerUrl".equals( project.getIssueTrackerUrl() );
        assert "scmUrl".equals( project.getScmUrl() );
        assert "version".equals( project.getVersion() );
        assert "ciManagementUrl".equals( project.getCiManagementUrl() );
        assert "repositoryUrl".equals( project.getRepositoryUrl() );
        assert "snapshotRepositoryUrl".equals( project.getSnapshotRepositoryUrl() );
    }

    @Test
    public void testMavenCoordinates()
    {
        Project project = new Project();
        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setGroupId( "groupId" );
        coordinates.setArtifactId( "artifactId" );
        project.addExtensionModel( coordinates );

        MavenCoordinates maven = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert "groupId".equals( maven.getGroupId() );
        assert "artifactId".equals( maven.getArtifactId() );
        
        Map<String, String> values = maven.getValuesAsMap();
        assert "groupId".equals( values.get( "groupId" ) );
        assert "artifactId".equals( values.get( "artifactId" ) );
        
        values = new HashMap<String, String>();
        values.put( "groupId", "otherGroupId" );
        values.put( "artifactId", "otherArtifactId" );
        maven.setValuesFromMap( values );
        assert "otherGroupId".equals( maven.getGroupId() );
        assert "otherArtifactId".equals( maven.getArtifactId() );
        
        List<String> keys = maven.getKeys();
        assert keys.size() == 2;
        assert keys.contains( "groupId" );
        assert keys.contains( "artifactId" );
    }

    @Test
    public void testGetExtensionModels()
    {
        Project project = new Project();
        MavenCoordinates coordinates = new MavenCoordinates();
        project.addExtensionModel( coordinates );

        ExtensionModel otherModel = new ExtensionModel()
        {
            public void setValuesFromMap( Map<String, String> values )
            {
            }

            public Map<String, String> getValuesAsMap()
            {
                return Collections.emptyMap();
            }

            public List<String> getKeys()
            {
                return Collections.emptyList();
            }

            public String getId()
            {
                return "other-model";
            }
        };
        project.addExtensionModel( otherModel );

        Collection<ExtensionModel> extensionModels = project.getExtensionModels();
        assert extensionModels.size() == 2;
        assert extensionModels.contains( otherModel );
        assert extensionModels.contains( coordinates );
    }

    @Test
    public void testMissingExtensionModel()
    {
        Project project = new Project();
        assert project.getExtensionModel( "bar" ) == null;
    }

    @Test
    public void testConstructProjectId()
    {
        assert "groupId:artifactId".equals( MavenCoordinates.constructProjectId( "groupId", "artifactId" ) );
    }
}
