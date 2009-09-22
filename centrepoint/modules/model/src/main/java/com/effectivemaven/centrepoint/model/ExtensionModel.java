package com.effectivemaven.centrepoint.model;

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
import java.util.Map;

/**
 * Marker interface for model classes that can be added to a project.
 */
public interface ExtensionModel
{
    /**
     * The identifier for the model within the project.
     * 
     * @return the identifier
     */
    public String getId();

    /**
     * Retrieve all values of the model as a map of strings for storing in the Centrepoint configuration.
     * 
     * @return the map of values
     */
    public Map<String, String> getValuesAsMap();

    /**
     * Store to the model the values from a map of strings which was retrieved from the Centrepoint configuration.
     * 
     * @param values
     */
    public void setValuesFromMap( Map<String, String> values );

    /**
     * A list of all possible keys that can be used in the map for {@link #setValuesFromMap(Map)} or
     * {@link #getValuesAsMap()}.
     * 
     * @return the list of keys
     */
    public List<String> getKeys();
}
