package com.effectivemaven.centrepoint.store.properties;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.Inject;

public class PropertiesProjectStore
    implements ProjectStore
{
    /** The projects, cached in memory. */
    private Map<String, Project> projects = new LinkedHashMap<String, Project>();

    /** Where to save the projects on disk. */
    private String dataLocation;

    /** Logger. */
    private static Logger logger = Logger.getLogger( PropertiesProjectStore.class.getName() );

    /**
     * Configuration parameters that can be modified by alternate bindings in Guice.
     */
    static class RepositoryParams
    {
        @Inject( optional = true )
        private @DataLocation
        String dataLocation = System.getProperty( "user.home" ) + "/.centrepoint/";
    }

    @Inject
    private PropertiesProjectStore( RepositoryParams params, PluginManager pluginManager )
    {
        this.dataLocation = params.dataLocation;
        Properties properties = loadProjectProperties();

        String projects = properties.getProperty( "projectList" );
        if ( projects != null )
        {
            String[] projectIds = projects.split( "," );
            for ( String id : projectIds )
            {
                Project project = new Project();
                project.setId( id );
                project.setCiManagementUrl( properties.getProperty( id + ":ciManagementUrl" ) );
                project.setDescription( properties.getProperty( id + ":description" ) );
                project.setIssueTrackerUrl( properties.getProperty( id + ":issueTrackerUrl" ) );
                project.setName( properties.getProperty( id + ":name" ) );
                project.setRepositoryUrl( properties.getProperty( id + ":repositoryUrl" ) );
                project.setSnapshotRepositoryUrl( properties.getProperty( id + ":snapshotRepositoryUrl" ) );
                project.setScmUrl( properties.getProperty( id + ":scmUrl" ) );
                project.setUrl( properties.getProperty( id + ":url" ) );
                project.setVersion( properties.getProperty( id + ":version" ) );

                MavenCoordinates coordinates = new MavenCoordinates();
                coordinates.setValuesFromMap( createValuesMapFromProperties( properties, id, "maven" ) );
                project.addExtensionModel( coordinates );

                // load the configuration for all available plugins and add the models to the project
                Collection<ConfigurablePanel<? extends ExtensionModel>> plugins = pluginManager.getConfigurablePlugins();
                for ( ConfigurablePanel<? extends ExtensionModel> plugin : plugins )
                {
                    ExtensionModel newModel = plugin.getModel( project );

                    if ( newModel != null )
                    {
                        Map<String, String> values = createValuesMapFromProperties( properties, id, newModel.getId() );
                        logger.fine( "Plugin + " + newModel.getId() + " configured with: " + values );
                        newModel.setValuesFromMap( values );
                    }
                }

                this.projects.put( id, project );
            }
        }
    }

    private Map<String, String> createValuesMapFromProperties( Properties properties, String id, String modelId )
    {
        Map<String, String> values = new HashMap<String, String>();
        for ( Object key : properties.keySet() )
        {
            String k = (String) key;
            String prefix = id + ":ext:" + modelId + ":";
            // find keys of the form project_id:ext:plugin_id:key and store the value under the given key
            if ( k.startsWith( prefix ) )
            {
                values.put( k.substring( prefix.length() ), properties.getProperty( k ) );
            }
        }
        return values;
    }

    private Properties loadProjectProperties()
    {
        Properties properties = new Properties();
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( new File( dataLocation, "projects.properties" ) );

            properties.load( fis );
        }
        catch ( FileNotFoundException e )
        {
            // no error, just doesn't exist yet
        }
        catch ( IOException e )
        {
            // ignore the error, it'll be recreated if possible
        }
        finally
        {
            if ( fis != null )
            {
                try
                {
                    fis.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }

        return properties;
    }

    public Collection<Project> getAllProjects()
    {
        return projects.values();
    }

    public Project getProjectById( String id )
    {
        return projects.get( id );
    }

    public void store( Project project )
    {
        projects.put( project.getId(), project );

        List<Project> projects = new ArrayList<Project>( this.projects.values() );
        Properties properties = new Properties();
        String list = "";
        for ( Project p : projects )
        {
            String id = p.getId();
            list += id + ",";

            setNullableProperty( properties, id + ":name", p.getName() );
            setNullableProperty( properties, id + ":ciManagementUrl", p.getCiManagementUrl() );
            setNullableProperty( properties, id + ":description", p.getDescription() );
            setNullableProperty( properties, id + ":issueTrackerUrl", p.getIssueTrackerUrl() );
            setNullableProperty( properties, id + ":repositoryUrl", p.getRepositoryUrl() );
            setNullableProperty( properties, id + ":snapshotRepositoryUrl", p.getSnapshotRepositoryUrl() );
            setNullableProperty( properties, id + ":scmUrl", p.getScmUrl() );
            setNullableProperty( properties, id + ":url", p.getUrl() );
            setNullableProperty( properties, id + ":version", p.getVersion() );

            for ( ExtensionModel model : p.getExtensionModels() )
            {
                Map<String, String> values = model.getValuesAsMap();

                for ( Map.Entry<String, String> entry : values.entrySet() )
                {
                    if ( entry.getValue() != null )
                    {
                        properties.setProperty( id + ":ext:" + model.getId() + ":" + entry.getKey(), entry.getValue() );
                    }
                }
            }
        }
        if ( list.endsWith( "," ) )
        {
            list = list.substring( 0, list.length() - 1 );
        }
        properties.setProperty( "projectList", list );

        File file = new File( dataLocation, "projects.properties" );
        file.getParentFile().mkdirs();
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream( file );

            properties.store( fos, null );
        }
        catch ( IOException e )
        {
        }
        finally
        {
            if ( fos != null )
            {
                try
                {
                    fos.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
    }

    private static void setNullableProperty( Properties properties, String key, String value )
    {
        if ( value != null )
        {
            properties.setProperty( key, value );
        }
    }

}
