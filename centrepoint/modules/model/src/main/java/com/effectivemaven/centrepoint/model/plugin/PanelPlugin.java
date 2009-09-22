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

import java.util.List;

import com.effectivemaven.centrepoint.model.Project;

/**
 * A Centrepoint plugin which provides information for displaying a panel of project information.
 * For information on how to write plugins, refer to the Centrepoint documentation. 
 */
public interface PanelPlugin
{

    /**
     * Get a list of items present in the panel.
     * @return the list of panel items
     */
    List<PanelItem> getItems( Project project );

    /**
     * The title to display for the panel.
     * @return the panel title
     */
    String getTitle( Project project );
}
