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

import java.util.Collection;

import com.effectivemaven.centrepoint.model.ExtensionModel;

/**
 * Centrepoint plugin manager. Implementations are responsible for locating all available plugins and either pre-loading
 * or lazy-loading them when requested by the available methods.
 */
public interface PluginManager
{
    /**
     * Retrieve all plugins available (whether they are configurable or not).
     * 
     * @return the list of plugins
     */
    Collection<PanelPlugin> getPlugins();

    /**
     * Retrieve all plugins available that take configuration options.
     * 
     * @return the list of configurable plugins
     */
    Collection<ConfigurablePanel<? extends ExtensionModel>> getConfigurablePlugins();

    /**
     * Retrieve a specific configurable plugin, based on the ID of the configuration model.
     * 
     * @param id the ID of the plugin's configuration model, which should match the value of
     *            {@link ConfigurablePanel#getId()}
     * @return the plugin, or <tt>null</tt> if not found
     */
    ConfigurablePanel<? extends ExtensionModel> getConfigurablePlugin( String id );
}
