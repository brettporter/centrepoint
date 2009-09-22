package com.effectivemaven.centrepoint.selenium;

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

import org.testng.annotations.Test;

public class AddProjectTest
    extends AbstractSeleniumTestCase
{
    @Test( dependsOnGroups = { "before-projects" } )
    public void addProject()
    {
        selenium.open( "/" );
        selenium.click( "//a[span='Add a New Project']" );
        selenium.waitForPageToLoad( "30000" );
        assert selenium.getTitle().equals( "Centrepoint :: Add Project" );
        selenium.type( "groupId", "org.codehaus.plexus" );
        selenium.type( "artifactId", "plexus-utils" );
        selenium.click( "//input[@value='Import']" );
        selenium.waitForPageToLoad( "30000" );
        assert selenium.getTitle().equals( "Centrepoint :: Plexus Common Utilities" );
        assert selenium.isTextPresent( "org.codehaus.plexus" );
    }

    @Test
    public void addProjectValidation()
    {
        selenium.open( "/project/add-maven" );
        selenium.click( "//input[@value='Import']" );
        selenium.waitForPageToLoad( "30000" );
        assert selenium.getTitle().equals( "Centrepoint :: Add Project" );
        assert selenium.isTextPresent( "Field 'Group ID' is required." );
        assert selenium.isTextPresent( "Field 'Artifact ID' is required." );
    }

    @Test( dependsOnMethods = { "addProject" } )
    public void viewProject()
    {
        selenium.open( "/" );
        selenium.click( "//a[span='Plexus Common Utilities']" );
        selenium.waitForPageToLoad( "30000" );
        assert selenium.getTitle().equals( "Centrepoint :: Plexus Common Utilities" );
    }
}
