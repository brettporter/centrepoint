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

import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;

import com.effectivemaven.centrepoint.maven.repository.LocalRepository;
import com.effectivemaven.centrepoint.maven.repository.RemoteRepositoryUrl;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.effectivemaven.centrepoint.store.properties.DataLocation;
import com.effectivemaven.centrepoint.store.properties.PropertiesProjectStore;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * Main Wicket application object for Centrepoint.
 */
public class CentrepointApplication
    extends WebApplication
{
    /** An optional Guice module to use for testing purposes. */
    private final Injector injector;
    
    protected final Logger logger = Logger.getLogger( CentrepointApplication.class.getName() );

    /**
     * Constructor.
     */
    public CentrepointApplication()
    {
        this( null );
    }

    /**
     * Constructor.
     * 
     * @param injector an optional Guice module to use
     */
    CentrepointApplication( Injector injector )
    {
        super();

        this.injector = injector;
    }

    @Override
    protected void init()
    {
        super.init();

        ServletContext servletContext = getWicketFilter().getFilterConfig().getServletContext();

        final String dataLocation = servletContext.getInitParameter( "dataLocation" );

        final String localRepository = servletContext.getInitParameter( "localRepository" );

        final String remoteRepository = servletContext.getInitParameter( "remoteRepository" );

        Injector injector = this.injector;
        if ( injector == null )
        {
            injector = Guice.createInjector( new AbstractModule()
            {
                @Override
                protected void configure()
                {
                    if ( dataLocation != null )
                    {
                        logger.info( "Using custom data location: " + dataLocation );
                        bindConstant().annotatedWith( DataLocation.class ).to( dataLocation );
                    }
                    
                    if ( remoteRepository != null )
                    {
                        logger.info( "Using custom remote repository: " + remoteRepository );
                        bindConstant().annotatedWith( RemoteRepositoryUrl.class ).to( remoteRepository );
                    }
                    
                    if ( localRepository != null )
                    {
                        logger.info( "Using custom local repository: " + localRepository );
                        bindConstant().annotatedWith( LocalRepository.class ).to( localRepository );
                    }

                    bind( ProjectStore.class ).to( PropertiesProjectStore.class ).in( Scopes.SINGLETON );
                    bind( PluginManager.class ).to( PluginManagerImpl.class ).in( Scopes.SINGLETON );
                    ClassLoader loader = getClass().getClassLoader();
                    bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( loader );
                }
            } );
        }

        // Use Guice to inject components
        addComponentInstantiationListener( new GuiceComponentInjector( this, injector ) );

        // Configure project viewing under the /project/NUMBER path
        mount( new MixedParamUrlCodingStrategy( "/project", ViewProjectPage.class, new String[] { "id" } ) );
        mount( new MixedParamUrlCodingStrategy( "/edit-panel", EditPanelConfigurationPage.class, new String[] { "id",
            "panel" } ) );
        // Configure a "friendly" URL for adding projects from Maven POMs under the /project/add-maven path
        mountBookmarkablePage( "/project/add-maven", null, AddProjectFromMavenPage.class );
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage()
    {
        return ListProjectsPage.class;
    }
}
