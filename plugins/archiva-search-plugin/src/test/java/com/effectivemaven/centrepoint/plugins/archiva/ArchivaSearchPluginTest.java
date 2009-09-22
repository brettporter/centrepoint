package com.effectivemaven.centrepoint.plugins.archiva;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;

public class ArchivaSearchPluginTest
{
    private ArchivaSearchPlugin plugin;

    private Project project;

    @BeforeMethod
    public void createDefaultPlugin()
    {
        plugin = new ArchivaSearchPlugin();
        project = new Project();
    }

    @Test
    public void testTitle()
    {
        assert "Archiva Search Results".equals( plugin.getTitle( null ) );
        assert "Archiva Search Results".equals( plugin.getTitle( project ) );
    }
}
