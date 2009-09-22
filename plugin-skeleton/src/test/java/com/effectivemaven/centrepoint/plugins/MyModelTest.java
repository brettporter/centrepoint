package com.effectivemaven.centrepoint.plugins;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyModelTest
{
    private MyModel model;

    @BeforeMethod
    public void createDefaultModel()
    {
        model = new MyModel();
    }

    @Test
    public void testAccessors()
    {
        assert model.getLink() == null;

        model.setLink( "my-link" );

        assert "my-link".equals( model.getLink() );
    }

    @Test
    public void testGetId()
    {
        assert "my-plugin".equals( model.getId() );
    }

    @Test
    public void testGetKeys()
    {
        assert model.getKeys().equals( Arrays.asList( "link" ) );
    }

    @Test
    public void testGetMapNotSet()
    {
        Map<String, String> values = model.getValuesAsMap();
        assert values.size() == 1;
        assert values.get( "link" ) == null;
    }

    @Test
    public void testGetMap()
    {
        model.setLink( "my-link" );
        Map<String, String> values = model.getValuesAsMap();
        assert values.size() == 1;
        assert "my-link".equals( values.get( "link" ) );
    }

    @Test
    public void testSetMap()
    {
        Map<String, String> values = Collections.singletonMap( "link", "other-link" );
        model.setValuesFromMap( values );

        assert "other-link".equals( model.getLink() );

        values = model.getValuesAsMap();
        assert values.size() == 1;
        assert "other-link".equals( values.get( "link" ) );
    }
}
