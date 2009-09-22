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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model to store the Maven coordinates for a project.
 */
public class MavenCoordinates
    implements ExtensionModel
{
    /** The Maven group ID of the project. */
    private String groupId;
    
    /** The Maven artifact ID of the project. */
    private String artifactId;

    public String getId()
    {
        return "maven";
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public static String constructProjectId( String groupId, String artifactId )
    {
        return groupId + ":" + artifactId;
    }

    public Map<String, String> getValuesAsMap()
    {
        Map<String, String> values = new HashMap<String, String>();
        values.put( "groupId", groupId );
        values.put( "artifactId", artifactId );
        return values;
    }

    public void setValuesFromMap( Map<String, String> values )
    {
        groupId = values.get( "groupId" );
        artifactId = values.get( "artifactId" );
    }

    public List<String> getKeys()
    {
        return Arrays.asList( "groupId", "artifactId" );
    }
}
