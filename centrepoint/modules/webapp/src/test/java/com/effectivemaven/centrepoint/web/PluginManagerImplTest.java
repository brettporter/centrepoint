package com.effectivemaven.centrepoint.web;

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

import java.net.URL;
import java.net.URLClassLoader;

import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

public class PluginManagerImplTest
{
    @Test
    public void noPlugins()
    {
        PluginManager pluginManager = Guice.createInjector( new PluginModule() ).getInstance( PluginManager.class );

        assert pluginManager.getPlugins().isEmpty();
        assert pluginManager.getConfigurablePlugins().isEmpty();
        assert pluginManager.getConfigurablePlugin( "foo" ) == null;
    }

    @Test
    public void missingPluginClassProperty()
    {
        PluginManager pluginManager =
            Guice.createInjector( new PluginModule( "bad-plugin-metadata" ) ).getInstance( PluginManager.class );

        assert pluginManager.getPlugins().isEmpty();
        assert pluginManager.getConfigurablePlugins().isEmpty();
        assert pluginManager.getConfigurablePlugin( "foo" ) == null;
    }

    @Test
    public void loadNonConfigurablePlugin()
    {
        PluginManager pluginManager =
            Guice.createInjector( new PluginModule( "non-config-plugin" ) ).getInstance( PluginManager.class );

        assert pluginManager.getPlugins().size() == 1;
        PanelPlugin panel = pluginManager.getPlugins().iterator().next();
        assert "title".equals( panel.getTitle( new Project() ) );

        assert pluginManager.getConfigurablePlugins().isEmpty();
        assert pluginManager.getConfigurablePlugin( "foo" ) == null;
        assert pluginManager.getConfigurablePlugin( "my-plugin" ) == null;
    }

    @Test
    public void loadConfigurablePlugin()
    {
        PluginManager pluginManager =
            Guice.createInjector( new PluginModule( "config-plugin" ) ).getInstance( PluginManager.class );

        assert pluginManager.getPlugins().size() == 1;
        PanelPlugin panel = pluginManager.getPlugins().iterator().next();
        assert "title-configurable".equals( panel.getTitle( new Project() ) );

        assert pluginManager.getConfigurablePlugins().size() == 1;
        assert pluginManager.getConfigurablePlugin( "foo" ) == null;

        ConfigurablePanel<? extends ExtensionModel> plugin = pluginManager.getConfigurablePlugin( "my-plugin" );
        assert plugin == panel;
        assert plugin.getId().equals( "my-plugin" );
    }

    private static class PluginModule
        extends AbstractModule
    {
        private final String plugin;

        public PluginModule()
        {
            this( null );
        }

        public PluginModule( String plugin )
        {
            this.plugin = plugin;
        }

        public void configure()
        {
            bind( PluginManager.class ).to( PluginManagerImpl.class );

            ClassLoader classLoader;
            if ( plugin != null )
            {
                URL resource = getClass().getResource( "/" + plugin + ".jar" );
                classLoader = new URLClassLoader( new URL[] { resource } );
            }
            else
            {
                classLoader = getClass().getClassLoader();
            }
            bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( classLoader );
        }
    }
}
