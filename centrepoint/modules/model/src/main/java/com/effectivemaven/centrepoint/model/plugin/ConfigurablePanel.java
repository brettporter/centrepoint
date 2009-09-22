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

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.Project;

/**
 * An interface that indicates the a Centrepoint plugin is configurable.
 * 
 * @param <T> the configuration model to use for the panel
 */
public interface ConfigurablePanel<T extends ExtensionModel>
{
    /**
     * Retrieve the configuration model from a given project. If not found, it should be created and added to the
     * project.
     * 
     * @param project the project for which to retrieve the configuration model
     * @return the configuration model that was found or created
     */
    T getModel( Project project );

    /**
     * A unique identifier for the panel, which should correspond to the value for {@link T#getId()}
     * 
     * @return the identifier
     */
    String getId();
}
