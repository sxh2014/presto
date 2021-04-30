/*
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
package com.facebook.presto.spi.catalog;

import java.util.Map;
import java.util.Optional;

public interface CatalogConfigurationManager
{
    /**
     * load all catalog properties
     */
    Map<String, Map<String, String>> loadCatalogs();

    /**
     * add catalog
     */
    void addCatalog(String catalogName, Map<String, String> properties);

    void updateCatalog(String catalogName, Map<String, String> properties);

    void deleteCatalog(String catalogName);

    Optional<Map<String, String>> getCatalog(String catalogName);
}