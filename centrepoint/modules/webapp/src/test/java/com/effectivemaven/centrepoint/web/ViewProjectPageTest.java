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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.MemoryProjectStore;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class ViewProjectPageTest
    extends AbstractPageWithStoreTest
{
    @BeforeClass( dependsOnMethods = { "createInjector" } )
    public void createSampleProjects()
    {
        Project project = createProject( "idValue", "the name", "the description" );
        project.setVersion( "1.2" );
        project.setUrl( "http://www.effectivemaven.com/" );

        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setGroupId( "myGroupId" );
        coordinates.setArtifactId( "myArtifactId" );
        project.addExtensionModel( coordinates );

        projectStore.setProjects( Arrays.asList( project ) );
    }

    @Test
    public void testWithoutIdParameter()
    {
        wicketTester.startPage( ViewProjectPage.class );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testWithNonExistantProject()
    {
        PageParameters params = new PageParameters();
        params.add( "id", "wrongId" );
        wicketTester.startPage( ViewProjectPage.class, params );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testRender()
    {
        PageParameters params = new PageParameters();
        params.add( "id", "idValue" );
        wicketTester.startPage( ViewProjectPage.class, params );

        assertProject();

        wicketTester.assertInvisible( "panels" );
    }

    private void assertProject()
    {
        wicketTester.assertRenderedPage( ViewProjectPage.class );
        wicketTester.assertModelValue( "title", "Centrepoint :: the name" );
        wicketTester.assertLabel( "name", "the name" );
        wicketTester.assertLabel( "version", "1.2" );
        wicketTester.assertModelValue( "description", "the description" );
        wicketTester.assertModelValue( "url", "http://www.effectivemaven.com/" );
        wicketTester.assertInvisible( "scmUrl" );
        wicketTester.assertInvisible( "issueTrackerUrl" );
        wicketTester.assertLabel( "maven|groupId", "myGroupId" );
        wicketTester.assertLabel( "maven|artifactId", "myArtifactId" );
    }

    @Test
    public void testRenderWithNonConfigurablePlugin()
    {
        Module module = new PageWithStoreModule()
        {
            @Override
            public void configure()
            {
                super.configure();
                ClassLoader classLoader = createClassLoader( "non-config-plugin" );
                bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( classLoader );
            }
        };

        Injector injector = Guice.createInjector( module );

        projectStore = injector.getInstance( MemoryProjectStore.class );
        createSampleProjects();

        wicketTester = new WicketTester( new CentrepointApplication( injector ) );

        PageParameters params = new PageParameters();
        params.add( "id", "idValue" );
        wicketTester.startPage( ViewProjectPage.class, params );

        assertProject();

        wicketTester.assertInvisible( "panels:0:editPanelLink" );
        
        wicketTester.assertVisible( "panels" );
        wicketTester.assertLabel( "panels:0:name", "title" );
        assertHideableExternalLink( "panels:0:items:0:link", "My Link", "http://www.example.com/" );
    }

    @Test
    public void testRenderWithConfigurablePlugin()
    {
        Module module = new PageWithStoreModule()
        {
            @Override
            public void configure()
            {
                super.configure();
                ClassLoader classLoader = createClassLoader( "config-plugin" );
                bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( classLoader );
            }
        };

        Injector injector = Guice.createInjector( module );

        PluginManager pluginManager = injector.getInstance( PluginManager.class );

        projectStore = injector.getInstance( MemoryProjectStore.class );
        createSampleProjects();

        Project project = projectStore.getProjectById( "idValue" );
        ConfigurablePanel<? extends ExtensionModel> plugin = pluginManager.getConfigurablePlugin( "my-plugin" );
        ExtensionModel model = plugin.getModel( project );

        Map<String, String> values = new HashMap<String, String>();
        values.put( "link", "linkValue" );
        model.setValuesFromMap( values );

        wicketTester = new WicketTester( new CentrepointApplication( injector ) );

        PageParameters params = new PageParameters();
        params.add( "id", "idValue" );
        wicketTester.startPage( ViewProjectPage.class, params );

        assertProject();

        params.add( "panel", "my-plugin" );
        assertBookmarkablePageLink( "panels:0:editPanelLink", EditPanelConfigurationPage.class, params );

        wicketTester.assertVisible( "panels" );
        wicketTester.assertLabel( "panels:0:name", "title-configurable" );
        assertHideableExternalLink( "panels:0:items:0:link", "My Link", "linkValue" );
    }

    private void assertHideableExternalLink( String path, String name, String url )
    {
        wicketTester.assertComponent( path, HideableExternalLink.class );
        HideableExternalLink link = (HideableExternalLink) wicketTester.getComponentFromLastRenderedPage( path );
        assert name.equals( link.getLabel().getObject() );
        wicketTester.assertModelValue( path, url );
    }
}
