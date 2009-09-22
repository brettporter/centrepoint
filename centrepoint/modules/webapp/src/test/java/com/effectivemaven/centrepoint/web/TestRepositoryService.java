package com.effectivemaven.centrepoint.web;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.google.inject.Singleton;

@Singleton
public class TestRepositoryService
    implements RepositoryService
{
    private Map<String, List<String>> versions = new HashMap<String, List<String>>();

    public List<String> getAvailableVersions( String groupId, String artifactId )
    {
        List<String> list = versions.get( MavenCoordinates.constructProjectId( groupId, artifactId ) );
        if ( list == null )
        {
            list = Collections.emptyList();
        }
        return list;
    }

    public void setAvailableVersions( String groupId, String artifactId, List<String> versions )
    {
        this.versions.put( MavenCoordinates.constructProjectId( groupId, artifactId ), versions );
    }

    public Project retrieveProject( String groupId, String artifactId, String version )
    {
        Project project = new Project();
        project.setId( MavenCoordinates.constructProjectId( groupId, artifactId ) );
        project.setVersion( version );

        return project;
    }
}
