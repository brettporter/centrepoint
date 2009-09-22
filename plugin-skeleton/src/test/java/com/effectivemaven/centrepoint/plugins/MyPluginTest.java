package com.effectivemaven.centrepoint.plugins;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.PanelItem;

public class MyPluginTest
{
    private MyPlugin plugin;

    private Project project;

    @BeforeMethod
    public void createDefaultPlugin()
    {
        plugin = new MyPlugin();
        project = new Project();
    }

    @Test
    public void testTitle()
    {
        assert "Links".equals( plugin.getTitle( null ) );
        assert "Links".equals( plugin.getTitle( project ) );
    }

    @Test
    public void testGetId()
    {
        assert "my-plugin".equals( plugin.getId() );
    }

    @Test
    public void testGetModel()
    {
        assert project.getExtensionModel( "my-plugin" ) == null;

        MyModel model = plugin.getModel( project );
        assert model == project.getExtensionModel( "my-plugin" );
        assert model == plugin.getModel( project );
    }

    @Test
    public void testGetItemsEmpty()
    {
        List<PanelItem> items = plugin.getItems( project );
        assert items.size() == 1;

        PanelItem item = items.get( 0 );
        assert "My Link".equals( item.getName() );
        assert item.getUrl() == null;
    }

    @Test
    public void testGetItems()
    {
        MyModel model = plugin.getModel( project );
        model.setLink( "testGetItems-link" );

        List<PanelItem> items = plugin.getItems( project );
        assert items.size() == 1;

        PanelItem item = items.get( 0 );
        assert "My Link".equals( item.getName() );
        assert "testGetItems-link".equals( item.getUrl() );
    }
}
