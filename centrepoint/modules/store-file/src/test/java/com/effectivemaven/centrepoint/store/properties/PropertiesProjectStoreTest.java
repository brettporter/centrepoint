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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PropertiesProjectStoreTest
{
    private PropertiesProjectStore store;
    private Injector injector;

    @BeforeMethod
    public void setUp()
    {
        injector = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                File file = new File( "target/test-data" );
                bindConstant().annotatedWith( DataLocation.class ).to( file.getAbsolutePath() );
                new File( file, "projects.properties" ).delete();

                bind( PluginManager.class ).to( TestPluginManager.class );
            }
        } );

        store = injector.getInstance( PropertiesProjectStore.class );
    }

    @Test
    public void testStoreMinimalProject()
    {
        Project project = new Project();
        project.setId( "id" );
        store.store( project );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 1;
        assert project == projects.iterator().next();
        assert project == store.getProjectById( "id" );

        store = injector.getInstance( PropertiesProjectStore.class );

        projects = store.getAllProjects();
        assert projects.size() == 1;
        assert "id".equals( store.getProjectById( "id" ).getId() );
    }

    @Test
    public void testStoreCompleteProject()
    {
        Project project = createCompleteProject( "complete" );

        store.store( project );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 1;
        assert project == projects.iterator().next();
        assert project == store.getProjectById( "complete" );

        store = injector.getInstance( PropertiesProjectStore.class );

        projects = store.getAllProjects();
        assert projects.size() == 1;
        assertOriginalProject( "complete", store.getProjectById( "complete" ) );
    }

    @Test
    public void testStoreModifiedProject()
    {
        Project project = new Project();
        project.setId( "id" );
        store.store( project );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 1;
        assert project == projects.iterator().next();
        assert project == store.getProjectById( "id" );

        projects = store.getAllProjects();
        assert projects.size() == 1;
        assert "id".equals( store.getProjectById( "id" ).getId() );

        project = createCompleteProject( "id" );

        store.store( project );

        projects = store.getAllProjects();
        assert projects.size() == 1;
        assert project == projects.iterator().next();
        assert project == store.getProjectById( "id" );
        assertOriginalProject( "id", store.getProjectById( "id" ) );

        store = injector.getInstance( PropertiesProjectStore.class );

        projects = store.getAllProjects();
        assert projects.size() == 1;
        assertOriginalProject( "id", store.getProjectById( "id" ) );
    }

    @Test
    public void testStoreMultipleCompleteProject()
    {
        Project project1 = createCompleteProject( "complete" );
        store.store( project1 );

        Project project2 = createCompleteProject( "complete2" );
        store.store( project2 );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 2;
        assert project1 == store.getProjectById( "complete" );
        assert project2 == store.getProjectById( "complete2" );

        store = injector.getInstance( PropertiesProjectStore.class );

        projects = store.getAllProjects();
        assert projects.size() == 2;
        assertOriginalProject( "complete", store.getProjectById( "complete" ) );
        assertOriginalProject( "complete2", store.getProjectById( "complete2" ) );
    }

    @Test
    public void testStoreMultipleCompleteProjectSomeMissingPluginConfig()
    {
        Project project1 = createCompleteProject( "complete" );
        store.store( project1 );

        Project project2 = createCompleteProject( "complete2" );
        TestModel model = (TestModel) project2.getExtensionModel( "test" );
        model.key2 = null;
        store.store( project2 );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 2;
        assert project1 == store.getProjectById( "complete" );
        assert project2 == store.getProjectById( "complete2" );

        store = injector.getInstance( PropertiesProjectStore.class );

        projects = store.getAllProjects();
        assert projects.size() == 2;
        assertOriginalProject( "complete", store.getProjectById( "complete" ) );

        project2 = store.getProjectById( "complete2" );
        assertOriginalProjectWithoutPluginConfig( "complete2", project2 );
        
        model = (TestModel) project2.getExtensionModel( "test" );
        assert "value1".equals( model.key1 );
        assert model.key2 == null;
    }

    @Test
    public void readProjectsFromStore()
    {
        Injector injector = Guice.createInjector( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                File file = new File( "src/test/data" );
                bindConstant().annotatedWith( DataLocation.class ).to( file.getAbsolutePath() );

                bind( PluginManager.class ).to( TestPluginManager.class );
            }
        } );

        store = injector.getInstance( PropertiesProjectStore.class );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 2;
        assertProject( "complete", store.getProjectById( "complete" ) );
        assertProject( "complete2", store.getProjectById( "complete2" ) );
    }

    private void assertProject( String id, Project project )
    {
        assert id.equals( project.getId() );
        assert "nameX".equals( project.getName() );
        assert "descriptionX".equals( project.getDescription() );
        assert "urlX".equals( project.getUrl() );
        assert "issueTrackerUrlX".equals( project.getIssueTrackerUrl() );
        assert "scmUrlX".equals( project.getScmUrl() );
        assert "versionX".equals( project.getVersion() );
        assert "ciManagementUrlX".equals( project.getCiManagementUrl() );
        assert "repositoryUrlX".equals( project.getRepositoryUrl() );
        assert "snapshotRepositoryUrlX".equals( project.getSnapshotRepositoryUrl() );

        MavenCoordinates maven = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert maven != null;
        assert "groupIdX".equals( maven.getGroupId() );
        assert "artifactIdX".equals( maven.getArtifactId() );

        TestModel otherModel = (TestModel) project.getExtensionModel( "test" );
        assert otherModel != null;
        assert "value1X".equals( otherModel.key1 );
        assert "value2X".equals( otherModel.key2 );
    }

    private void assertOriginalProjectWithoutPluginConfig( String id, Project project )
    {
        assert id.equals( project.getId() );
        assert "name".equals( project.getName() );
        assert "description".equals( project.getDescription() );
        assert "url".equals( project.getUrl() );
        assert "issueTrackerUrl".equals( project.getIssueTrackerUrl() );
        assert "scmUrl".equals( project.getScmUrl() );
        assert "version".equals( project.getVersion() );
        assert "ciManagementUrl".equals( project.getCiManagementUrl() );
        assert "repositoryUrl".equals( project.getRepositoryUrl() );
        assert "snapshotRepositoryUrl".equals( project.getSnapshotRepositoryUrl() );

        MavenCoordinates maven = (MavenCoordinates) project.getExtensionModel( "maven" );
        assert maven != null;
        assert "groupId".equals( maven.getGroupId() );
        assert "artifactId".equals( maven.getArtifactId() );
    }

    private void assertOriginalProject( String id, Project project )
    {
        assertOriginalProjectWithoutPluginConfig( id, project );

        TestModel otherModel = (TestModel) project.getExtensionModel( "test" );
        assert otherModel != null;
        assert "value1".equals( otherModel.key1 );
        assert "value2".equals( otherModel.key2 );
    }

    private static Project createCompleteProject( String id )
    {
        Project project = new Project();
        project.setId( id );
        project.setCiManagementUrl( "ciManagementUrl" );
        project.setDescription( "description" );
        project.setIssueTrackerUrl( "issueTrackerUrl" );
        project.setName( "name" );
        project.setRepositoryUrl( "repositoryUrl" );
        project.setSnapshotRepositoryUrl( "snapshotRepositoryUrl" );
        project.setScmUrl( "scmUrl" );
        project.setUrl( "url" );
        project.setVersion( "version" );

        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setArtifactId( "artifactId" );
        coordinates.setGroupId( "groupId" );
        project.addExtensionModel( coordinates );

        TestModel model = new TestModel();
        model.key1 = "value1";
        model.key2 = "value2";
        project.addExtensionModel( model );

        return project;
    }

    private static class TestModel
        implements ExtensionModel
    {
        private String key1;

        private String key2;

        public String getId()
        {
            return "test";
        }

        public List<String> getKeys()
        {
            return Arrays.asList( "key1", "key2" );
        }

        public Map<String, String> getValuesAsMap()
        {
            Map<String, String> values = new HashMap<String, String>();
            values.put( "key1", key1 );
            values.put( "key2", key2 );
            return values;
        }

        public void setValuesFromMap( Map<String, String> values )
        {
            key1 = values.get( "key1" );
            key2 = values.get( "key2" );
        }
    }

    private static class TestPlugin
        implements ConfigurablePanel<TestModel>
    {
        public String getId()
        {
            return "test";
        }

        public TestModel getModel( Project project )
        {
            TestModel extensionModel = (TestModel) project.getExtensionModel( getId() );
            if ( extensionModel == null )
            {
                extensionModel = new TestModel();
                project.addExtensionModel( extensionModel );
            }
            return extensionModel;
        }
    }

    static class TestPluginManager
        implements PluginManager
    {
        private Map<String, ConfigurablePanel<? extends ExtensionModel>> plugins =
            new HashMap<String, ConfigurablePanel<? extends ExtensionModel>>();

        public TestPluginManager()
        {
            TestPlugin plugin = new TestPlugin();
            plugins.put( plugin.getId(), plugin );
        }

        public ConfigurablePanel<? extends ExtensionModel> getConfigurablePlugin( String panel )
        {
            throw new UnsupportedOperationException();
        }

        public Collection<ConfigurablePanel<? extends ExtensionModel>> getConfigurablePlugins()
        {
            return plugins.values();
        }

        public Collection<PanelPlugin> getPlugins()
        {
            throw new UnsupportedOperationException();
        }
    }
}
