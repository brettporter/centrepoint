package com.effectivemaven.centrepoint.store;

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
import java.util.Collection;
import java.util.List;

import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.Project;

public class MemoryProjectStoreTest
{
    @Test
    public void testStore()
    {
        MemoryProjectStore store = new MemoryProjectStore();

        Project project = new Project();
        project.setId( "id" );
        store.store( project );

        Collection<Project> projects = store.getAllProjects();
        assert projects.size() == 1;
        assert project == projects.iterator().next();
        assert project == store.getProjectById( "id" );
    }
    
    @Test
    public void testSetProjects()
    {
        MemoryProjectStore store = new MemoryProjectStore();

        Project project1 = new Project();
        project1.setId( "id" );
        
        Project project2 = new Project();
        project2.setId( "id2" );
     
        List<Project> projects = Arrays.asList( project1, project2 );
        store.setProjects( projects );
        
        assert projects.size() == 2;
        assert projects.contains( project1 );
        assert projects.contains( project2 );
    }
}
