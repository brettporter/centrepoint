package com.effectivemaven.centrepoint.plugins.continuum;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ContinuumModelTest
{
    private ContinuumModel model;

    @BeforeMethod
    public void createDefaultModel()
    {
        model = new ContinuumModel();
    }

    @Test
    public void testAccessors()
    {
        assert model.getProjectId() == 0;

        model.setProjectId( 1 );

        assert model.getProjectId() == 1;
    }

    @Test
    public void testGetId()
    {
        assert "continuum-builds".equals( model.getId() );
    }

    @Test
    public void testGetKeys()
    {
        assert model.getKeys().equals( Arrays.asList( "projectId" ) );
    }

    @Test
    public void testGetMapNotSet()
    {
        Map<String, String> values = model.getValuesAsMap();
        assert values.size() == 1;
        assert values.get( "projectId" ) == null;
    }

    @Test
    public void testGetMap()
    {
        model.setProjectId( 2 );
        Map<String, String> values = model.getValuesAsMap();
        assert values.size() == 1;
        assert "2".equals( values.get( "projectId" ) );
    }

    @Test
    public void testSetMap()
    {
        Map<String, String> values = Collections.singletonMap( "projectId", "3" );
        model.setValuesFromMap( values );

        assert model.getProjectId() == 3;

        values = model.getValuesAsMap();
        assert values.size() == 1;
        assert "3".equals( values.get( "projectId" ) );
    }
    
    @Test(expectedExceptions=NumberFormatException.class)
    public void testSetMapNonIntegerValue()
    {
        Map<String, String> values = Collections.singletonMap( "projectId", "foo" );
        model.setValuesFromMap( values );
    }
}
