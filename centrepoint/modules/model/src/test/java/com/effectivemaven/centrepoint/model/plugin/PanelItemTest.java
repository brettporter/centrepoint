package com.effectivemaven.centrepoint.model.plugin;

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

public class PanelItemTest
{
    @Test
    public void testAccessors()
    {
        PanelItem project = new PanelItem( "name", "url" );

        assert "name".equals( project.getName() );
        assert "url".equals( project.getUrl() );
    }

    @Test
    public void testMissingURL()
    {
        PanelItem project = new PanelItem( "name" );

        assert "name".equals( project.getName() );
        assert project.getUrl() == null;
    }

}
