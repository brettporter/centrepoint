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

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.effectivemaven.centrepoint.maven.MavenProjectImporter;
import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.Project;
import com.google.inject.Inject;

/**
 * Page for importing a project into Centrepoint from Maven. The user is prompted for the group ID and artifact ID, and
 * a version is selected from a Maven repository. The POM is then imported and stored via the importer service.
 */
public class AddProjectFromMavenPage
    extends TemplatePage
{
    /**
     * Constructor.
     */
    public AddProjectFromMavenPage()
    {
        super();
        
        setPageTitle( "Add Project" );

        add( new FeedbackPanel( "feedback" ) );

        add( new InputForm( "addMavenProjectForm" ) );
    }

    @SuppressWarnings("serial")
    private static class InputFormModel
        implements Serializable
    {
        String groupId;

        String artifactId;
    }

    @SuppressWarnings("serial")
    private static class InputForm
        extends Form<InputFormModel>
    {
        /** Maven repository service for querying. */
        @Inject
        private RepositoryService repositoryService;

        /** Maven project importer service for converting a Maven project to a Centrepoint project and storing. */
        @Inject
        private MavenProjectImporter importer;

        public InputForm( String name )
        {
            super( name, new CompoundPropertyModel<InputFormModel>( new InputFormModel() ) );

            // form fields
            TextField<String> groupIdTextField = new TextField<String>( "groupId" );
            groupIdTextField.setLabel( new Model<String>( "Group ID" ) );
            groupIdTextField.setRequired( true );
            add( groupIdTextField );

            TextField<String> artifactIdTextField = new TextField<String>( "artifactId" );
            artifactIdTextField.setLabel( new Model<String>( "Artifact ID" ) );
            artifactIdTextField.setRequired( true );
            add( artifactIdTextField );
        }

        @Override
        protected void onSubmit()
        {
            InputFormModel model = getModelObject();

            String groupId = model.groupId;
            String artifactId = model.artifactId;

            // find out the appropriate version to use
            List<String> versions = repositoryService.getAvailableVersions( groupId, artifactId );

            if ( versions.isEmpty() )
            {
                error( "No artifacts found in the repository for the given Maven coordinate." );
                return;
            }

            // default is the latest version (rely on sort ordering)
            String version = versions.get( versions.size() - 1 );

            // import the project given the selected co-ordinates
            Project project = importer.importMavenProject( groupId, artifactId, version );

            // forward to the project viewing page for the newly imported project
            PageParameters pageParameters = new PageParameters();
            pageParameters.add( "id", project.getId() );
            setResponsePage( ViewProjectPage.class, pageParameters );
        }
    }
}
