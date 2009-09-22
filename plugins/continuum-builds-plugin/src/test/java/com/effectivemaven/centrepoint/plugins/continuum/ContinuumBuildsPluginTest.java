package com.effectivemaven.centrepoint.plugins.continuum;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;

public class ContinuumBuildsPluginTest
{
    private ContinuumBuildsPlugin plugin;

    private Project project;

    @BeforeMethod
    public void createDefaultPlugin()
    {
        plugin = new ContinuumBuildsPlugin();
        project = new Project();
    }

    @Test
    public void testTitle()
    {
        assert "Continuum Build Results".equals( plugin.getTitle( null ) );
        assert "Continuum Build Results".equals( plugin.getTitle( project ) );
    }

    @Test
    public void testGetId()
    {
        assert "continuum-builds".equals( plugin.getId() );
    }

    @Test
    public void testGetModel()
    {
        assert project.getExtensionModel( "continuum-builds" ) == null;

        ContinuumModel model = plugin.getModel( project );
        assert model == project.getExtensionModel( "continuum-builds" );
        assert model == plugin.getModel( project );
    }
}
