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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centrepoint project model.
 */
public class Project
{
    /** The identifier of the project, expected to be unique in the Centrepoint storage. */
    private String id;

    /** A human readable name for the project. */
    private String name;

    /** The current version of the project. */
    private String version;

    /** A short description of the project. */
    private String description;

    /** The URL to the source control viewer for the project (not the connection string). */
    private String scmUrl;

    /** The URL to the project issue tracker. */
    private String issueTrackerUrl;

    /** A URL to the project home page. */
    private String url;
    
    /** A URL for the CI system in use. */
    private String ciManagementUrl;
    
    /** A URL for the repository for releases. */
    private String repositoryUrl;  

    /** A URL for the repository for snapshots. */
    private String snapshotRepositoryUrl;  

    /** A set of models added to the project. */
    private Map<String, ExtensionModel> extensionModels = new LinkedHashMap<String, ExtensionModel>();

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getScmUrl()
    {
        return scmUrl;
    }

    public void setScmUrl( String scmUrl )
    {
        this.scmUrl = scmUrl;
    }

    public String getIssueTrackerUrl()
    {
        return issueTrackerUrl;
    }

    public void setIssueTrackerUrl( String issueTrackerUrl )
    {
        this.issueTrackerUrl = issueTrackerUrl;
    }

    /**
     * Retrieve a model by the given extension identifier for non-core project information.
     * @param extension the extension identifier
     * @return the model
     */
    public ExtensionModel getExtensionModel( String extension )
    {
        return extensionModels.get( extension );
    }

    /**
     * Retrieve all available models for non-core project information that have already been added to the project.
     * @return the models
     */
    public Collection<ExtensionModel> getExtensionModels()
    {
        return extensionModels.values();
    }
    
    /**
     * Add non-core project information from the given model.
     * @param model the extended model information
     */
    public void addExtensionModel( ExtensionModel model )
    {
        extensionModels.put( model.getId(), model );
    }

    public void setSnapshotRepositoryUrl( String snapshotRepositoryUrl )
    {
        this.snapshotRepositoryUrl = snapshotRepositoryUrl;
    }

    public String getSnapshotRepositoryUrl()
    {
        return snapshotRepositoryUrl;
    }

    public void setRepositoryUrl( String repositoryUrl )
    {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUrl()
    {
        return repositoryUrl;
    }

    public void setCiManagementUrl( String ciManagementUrl )
    {
        this.ciManagementUrl = ciManagementUrl;
    }

    public String getCiManagementUrl()
    {
        return ciManagementUrl;
    }
}
