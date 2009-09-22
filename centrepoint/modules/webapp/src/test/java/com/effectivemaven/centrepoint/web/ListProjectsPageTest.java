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
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;

public class ListProjectsPageTest
    extends AbstractPageWithStoreTest
{
    @BeforeMethod
    public void clearProjects()
    {
        projectStore.setProjects( Collections.<Project>emptyList() );
    }
    
    @Test
    public void testRenderEmpty()
    {
        wicketTester.startPage( ListProjectsPage.class );

        wicketTester.assertRenderedPage( ListProjectsPage.class );
        wicketTester.assertComponent( "feedback", FeedbackPanel.class );

        wicketTester.assertNoErrorMessage();
        wicketTester.assertListView( "projects", Collections.emptyList() );
        wicketTester.assertInfoMessages( new String[] { "There are currently no projects." } );

        assertBookmarkablePageLink( "operations:0:operationLink", AddProjectFromMavenPage.class );
        wicketTester.assertLabel( "operations:0:operationLink:name", "Add a New Project" );
    }

    @Test
    public void testWithSampleProjects()
    {
        Project project1 = createProject( "id1", "name1", "description 1" );
        Project project2 = createProject( "id2", "name2", "description 2" );
        List<Project> projects = Arrays.asList( project1, project2 );
        projectStore.setProjects( projects );

        wicketTester.startPage( ListProjectsPage.class );

        wicketTester.assertRenderedPage( ListProjectsPage.class );
        wicketTester.assertComponent( "feedback", FeedbackPanel.class );

        wicketTester.assertNoErrorMessage();
        wicketTester.assertNoInfoMessage();

        wicketTester.assertListView( "projects", projects );

        PageParameters params = new PageParameters();
        params.add( "id", "id1" );
        assertBookmarkablePageLink( "projects:0:link", ViewProjectPage.class, params );
        wicketTester.assertLabel( "projects:0:link:name", "name1" );
        wicketTester.assertModelValue( "projects:0:description", "description 1" );
        params = new PageParameters();
        params.add( "id", "id2" );
        assertBookmarkablePageLink( "projects:1:link", ViewProjectPage.class, params );
        wicketTester.assertLabel( "projects:1:link:name", "name2" );
        wicketTester.assertModelValue( "projects:1:description", "description 2" );

        assertBookmarkablePageLink( "operations:0:operationLink", AddProjectFromMavenPage.class );
        wicketTester.assertLabel( "operations:0:operationLink:name", "Add a New Project" );
    }
}
