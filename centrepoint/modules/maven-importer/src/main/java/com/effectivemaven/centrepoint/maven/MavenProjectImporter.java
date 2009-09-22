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

import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Class to handle importing a Maven project from a repository and storing it the Centrepoint project storage.
 */
@Singleton
public class MavenProjectImporter
{
    @Inject
    private ProjectStore projectStore;
    
    @Inject
    private RepositoryService repositoryService;

    /**
     * Import the Maven project. The project will be stored and returned. Maven information that does not map directly
     * will be stored in project extension information. The project identifier is constructed from the Maven co-ordinate
     * using <code>groupId:artifactId</code>
     * 
     * @param groupId the group ID of the artifact to import
     * @param artifactId the artifact ID of the artifact to import
     * @param version the version of the artifact to import
     * @return the project that was created and is already stored
     */
    public Project importMavenProject( String groupId, String artifactId, String version )
    {
        Project project = repositoryService.retrieveProject( groupId, artifactId, version );

        projectStore.store( project );

        return project;
    }
}
