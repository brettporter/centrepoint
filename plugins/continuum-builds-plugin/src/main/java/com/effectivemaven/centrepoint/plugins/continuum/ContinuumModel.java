package com.effectivemaven.centrepoint.plugins.continuum;

import com.effectivemaven.centrepoint.model.ExtensionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContinuumModel
    implements ExtensionModel
{
    private Integer projectId;

    public String getId()
    {
        return "continuum-builds";
    }

    public int getProjectId()
    {
        return projectId != null ? projectId : 0;
    }

    public void setProjectId( int projectId )
    {
        this.projectId = projectId;
    }

    public Map<String, String> getValuesAsMap()
    {
        Map<String, String> values = new HashMap<String, String>();
        values.put( "projectId", projectId != null ? Integer.toString( projectId ) : null );
        return values;
    }

    public void setValuesFromMap( Map<String, String> values )
    {
        String value = values.get( "projectId" );
        this.projectId = value != null ? Integer.valueOf( value ) : null;
    }

    public List<String> getKeys()
    {
        return Arrays.asList( "projectId" );
    }

}
