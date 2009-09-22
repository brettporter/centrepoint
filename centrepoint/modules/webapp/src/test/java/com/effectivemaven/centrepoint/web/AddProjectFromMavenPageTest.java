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
import java.util.Collections;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.tester.FormTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;

public class AddProjectFromMavenPageTest
    extends AbstractPageWithStoreTest
{
    @BeforeMethod
    public void clearProjects()
    {
        projectStore.setProjects( Collections.<Project>emptyList() );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test
    public void testRenderEmpty()
    {
        wicketTester.startPage( AddProjectFromMavenPage.class );

        wicketTester.assertRenderedPage( AddProjectFromMavenPage.class );
        wicketTester.assertComponent( "feedback", FeedbackPanel.class );
        wicketTester.assertComponent( "addMavenProjectForm", Form.class );

        wicketTester.assertNoErrorMessage();
        wicketTester.assertNoInfoMessage();

        wicketTester.assertComponent( "addMavenProjectForm:groupId", TextField.class );
        TextField<String> field =
            (TextField<String>) wicketTester.getComponentFromLastRenderedPage( "addMavenProjectForm:groupId" );
        assert field.getModelObject() == null;

        wicketTester.assertComponent( "addMavenProjectForm:artifactId", TextField.class );
        field = (TextField<String>) wicketTester.getComponentFromLastRenderedPage( "addMavenProjectForm:artifactId" );
        assert field.getModelObject() == null;
    }

    @Test
    public void testValidation()
    {
        wicketTester.startPage( AddProjectFromMavenPage.class );

        FormTester formTester = wicketTester.newFormTester( "addMavenProjectForm" );

        formTester.submit();

        wicketTester.assertRenderedPage( AddProjectFromMavenPage.class );
        wicketTester.assertErrorMessages( new String[] { "Field 'Group ID' is required.",
            "Field 'Artifact ID' is required." } );
    }

    @Test
    public void testAdditionWhenSingleVersionAvailable()
    {
        TestRepositoryService repositoryService = injector.getInstance( TestRepositoryService.class );

        repositoryService.setAvailableVersions( "groupIdValue", "artifactIdValue-single", Arrays.asList( "1.0-SNAPSHOT" ) );

        assert projectStore.getProjectById( "groupIdValue:artifactIdValue-single" ) == null;

        wicketTester.startPage( AddProjectFromMavenPage.class );

        FormTester formTester = wicketTester.newFormTester( "addMavenProjectForm" );
        formTester.setValue( "groupId", "groupIdValue" );
        formTester.setValue( "artifactId", "artifactIdValue-single" );

        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );

        PageParameters params = wicketTester.getLastRenderedPage().getPageParameters();
        assert "groupIdValue:artifactIdValue-single".equals( params.getString( "id" ) );

        Project project = projectStore.getProjectById( "groupIdValue:artifactIdValue-single" );
        assert project != null;
        assert "groupIdValue:artifactIdValue-single".equals( project.getId() );
    }

    @Test
    public void testAdditionWhenNoVersionAvailable()
    {
        assert projectStore.getProjectById( "groupIdValue:artifactIdValue-none" ) == null;

        wicketTester.startPage( AddProjectFromMavenPage.class );

        FormTester formTester = wicketTester.newFormTester( "addMavenProjectForm" );
        formTester.setValue( "groupId", "groupIdValue" );
        formTester.setValue( "artifactId", "artifactIdValue-none" );

        formTester.submit();

        wicketTester.assertRenderedPage( AddProjectFromMavenPage.class );
        wicketTester.assertErrorMessages( new String[] { "No artifacts found in the repository for the given Maven coordinate." } );

        assert projectStore.getProjectById( "groupIdValue:artifactIdValue-none" ) == null;
    }

    @Test
    public void testAdditionWhenMultipleVersionsAvailable()
    {
        TestRepositoryService repositoryService = injector.getInstance( TestRepositoryService.class );
        
        repositoryService.setAvailableVersions( "groupIdValue", "artifactIdValue-multi", Arrays.asList( "1.0", "1.1", "1.2" ) );

        assert projectStore.getProjectById( "groupIdValue:artifactIdValue-multi" ) == null;

        wicketTester.startPage( AddProjectFromMavenPage.class );

        FormTester formTester = wicketTester.newFormTester( "addMavenProjectForm" );
        formTester.setValue( "groupId", "groupIdValue" );
        formTester.setValue( "artifactId", "artifactIdValue-multi" );

        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );

        PageParameters params = wicketTester.getLastRenderedPage().getPageParameters();
        assert "groupIdValue:artifactIdValue-multi".equals( params.getString( "id" ) );

        Project project = projectStore.getProjectById( "groupIdValue:artifactIdValue-multi" );
        assert project != null;
        assert "groupIdValue:artifactIdValue-multi".equals( project.getId() );
        assert "1.2".equals( project.getVersion() );
    }
    
    @Test
    public void testAdditionWhenAlreadyPresent()
    {
        TestRepositoryService repositoryService = injector.getInstance( TestRepositoryService.class );

        repositoryService.setAvailableVersions( "groupIdValue", "artifactIdValue-single", Arrays.asList( "1.0-SNAPSHOT" ) );

        assert projectStore.getProjectById( "groupIdValue:artifactIdValue-single" ) == null;
        Project originalProject = new Project();
        originalProject.setId( "groupIdValue:artifactIdValue-single" );
        projectStore.store( originalProject );

        wicketTester.startPage( AddProjectFromMavenPage.class );

        FormTester formTester = wicketTester.newFormTester( "addMavenProjectForm" );
        formTester.setValue( "groupId", "groupIdValue" );
        formTester.setValue( "artifactId", "artifactIdValue-single" );

        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );

        PageParameters params = wicketTester.getLastRenderedPage().getPageParameters();
        assert "groupIdValue:artifactIdValue-single".equals( params.getString( "id" ) );

        Project project = projectStore.getProjectById( "groupIdValue:artifactIdValue-single" );
        assert project != null;
        assert project != originalProject;
        assert "groupIdValue:artifactIdValue-single".equals( project.getId() );
        assert "1.0-SNAPSHOT".equals( project.getVersion() );
    }
}
