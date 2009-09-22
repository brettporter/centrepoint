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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class PluginManagerImpl
    implements PluginManager
{
    private final Collection<PanelPlugin> plugins;

    private final Map<String, ConfigurablePanel<? extends ExtensionModel>> configurablePlugins;

    private static Logger logger = Logger.getLogger( PluginManagerImpl.class.getName() );

    @SuppressWarnings( "unchecked" )
    @Inject
    private PluginManagerImpl( @Named( "Plugin ClassLoader") ClassLoader classLoader )
    {
        Collection<PanelPlugin> plugins = null;
        Map<String, ConfigurablePanel<? extends ExtensionModel>> configurablePlugins = null;
        Enumeration<URL> resources = null;
        try
        {
            resources = classLoader.getResources( "META-INF/centrepoint/plugin.properties" );
        }
        catch ( IOException e )
        {
            // ignore - none found
            plugins = Collections.emptyList();
            configurablePlugins = Collections.emptyMap();
        }

        if ( plugins == null )
        {
            ArrayList<URL> list = Collections.list( resources );
            logger.info( "Found " + list.size() + " plugin descriptors" );

            plugins = new ArrayList<PanelPlugin>();
            configurablePlugins = new HashMap<String, ConfigurablePanel<? extends ExtensionModel>>();
            for ( URL resource : list )
            {
                Properties properties = new Properties();
                InputStream is = null;
                try
                {
                    is = resource.openStream();
                    properties.load( is );

                    String implementation = properties.getProperty( "panel.class" );
                    if ( implementation != null )
                    {
                        PanelPlugin plugin = (PanelPlugin) Class.forName( implementation, true, classLoader ).newInstance();
                        plugins.add( plugin );
                        if ( plugin instanceof ConfigurablePanel )
                        {
                            ConfigurablePanel<ExtensionModel> c = (ConfigurablePanel<ExtensionModel>) plugin;
                            configurablePlugins.put( c.getId(), c );
                        }
                        logger.info( "Loaded plugin: " + implementation );
                    }
                }
                catch ( IOException e )
                {
                    // skip this plugin
                    logger.warning( "Failed to load resource: " + e.getMessage() );
                }
                catch ( InstantiationException e )
                {
                    // skip this plugin
                    logger.warning( "Failed to create plugin: " + e.getMessage() );
                }
                catch ( IllegalAccessException e )
                {
                    // skip this plugin
                    logger.warning( "Failed to create plugin: " + e.getMessage() );
                }
                catch ( ClassNotFoundException e )
                {
                    // skip this plugin
                    logger.warning( "Incorrect plugin name: " + e.getMessage() );
                }
                finally
                {
                    if ( is != null )
                    {
                        try
                        {
                            is.close();
                        }
                        catch ( IOException e )
                        {
                            // ignore
                        }
                    }
                }
            }
        }

        this.plugins = plugins;
        this.configurablePlugins = configurablePlugins;
    }

    /*
     * (non-Javadoc)
     * @see com.effectivemaven.centrepoint.plugin.manager.PluginManager#getPlugins()
     */
    public Collection<PanelPlugin> getPlugins()
    {
        return plugins;
    }

    public ConfigurablePanel<? extends ExtensionModel> getConfigurablePlugin( String panel )
    {
        return configurablePlugins.get( panel );
    }

    public Collection<ConfigurablePanel<? extends ExtensionModel>> getConfigurablePlugins()
    {
        return configurablePlugins.values();
    }
}
