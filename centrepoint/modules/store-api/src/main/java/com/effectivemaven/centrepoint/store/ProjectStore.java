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

import java.util.Collection;

import com.effectivemaven.centrepoint.model.Project;

/**
 * A very simple project store mechanism.
 */
public interface ProjectStore
{
    /**
     * Retrieve all projects in their entirety.
     * @return the project list
     */
    Collection<Project> getAllProjects();

    /**
     * Retrieve a specific project.
     * @param id the identifier of the project to retrieve
     * @return the project, or <code>null</code> if not found
     */
    Project getProjectById( String id );

    /**
     * Store the project. 
     * @param project the project to store
     */
    void store( Project project );
}
