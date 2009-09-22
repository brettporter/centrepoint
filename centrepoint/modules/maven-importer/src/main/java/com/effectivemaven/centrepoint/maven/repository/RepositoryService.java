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

import java.util.List;

import com.effectivemaven.centrepoint.model.Project;
import com.google.inject.ImplementedBy;

/**
 * Maven repository service for querying a repository for artifact information.
 */
@ImplementedBy( CentralRepositoryService.class )
public interface RepositoryService
{
    /**
     * Get the available versions for a given artifact by querying the repository metadata.
     * The versions should be returned in sorted order from oldest to newest.
     * 
     * @param groupId the group ID of the artifact to query
     * @param artifactId the artifact ID of the artifact to query
     * @return versions available in the repository
     * @throws RepositoryException
     */
    List<String> getAvailableVersions( String groupId, String artifactId )
        throws RepositoryException;

    /**
     * Retrieve a project from the repository and convert it to a basic Centrepoint project model.
     * 
     * @param groupId the Maven group ID to lookup
     * @param artifactId the Maven artifact ID to lookup
     * @param version the Maven artifact version to lookup
     * @return the project created
     * @throws RepositoryException
     */
    Project retrieveProject( String groupId, String artifactId, String version )
        throws RepositoryException;
}
