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
package com.facebook.presto.catalog;

import com.facebook.airlift.log.Logger;
import com.facebook.presto.spi.catalog.CatalogConfigurationManager;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public class DbCatalogConfigurationManager
        implements CatalogConfigurationManager
{
    private static final Logger log = Logger.get(DbCatalogConfigurationManager.class);
    private final CatalogDao catalogDao;

    @Inject
    public DbCatalogConfigurationManager(CatalogDao catalogDao)
    {
        this.catalogDao = catalogDao;
    }

    @Override
    public Map<String, Map<String, String>> loadCatalogs()
    {
        List<CatalogInfo> catalogInfoList = catalogDao.listCatalog();
        Map<String, Map<String, String>> catalogs = catalogInfoList.stream()
                .collect(HashMap::new,
                        (catalogMap, info) -> {
                            String properties = info.getProperties();
                            Map<String, String> value = Arrays.stream(properties.split(";"))
                                    .collect(HashMap::new,
                                            (map, property) -> map.put(property.split("=")[0], property.split("=")[1]),
                                            Map::putAll);
                            catalogMap.put(info.getCatalog(), value);
                        },
                        Map::putAll);
        return ImmutableMap.copyOf(catalogs);
    }

    @Override
    public void addCatalog(String catalogName, Map<String, String> properties)
    {
        log.info("add catalog {} -- ", catalogName);
        String propertiesStr = Joiner.on(";").withKeyValueSeparator("=").join(properties);
        int result = catalogDao.insertCatalog(catalogName, propertiesStr);
        checkState(result == 1, "add catalog %s fail", catalogName);
    }

    @Override
    public void updateCatalog(String catalogName, Map<String, String> properties)
    {
        String propertiesStr = Joiner.on(";").withKeyValueSeparator("=").join(properties);
        int result = catalogDao.updateCatalog(catalogName, propertiesStr);
        checkState(result == 1, "update catalog %s fail", catalogName);
    }

    @Override
    public void deleteCatalog(String catalogName)
    {
        int result = catalogDao.deleteCatalog(catalogName);
        checkState(result == 1, "dynamic catalog does not contain %s", catalogName);
    }

    @Override
    public Optional<Map<String, String>> getCatalog(String catalogName)
    {
        String properties = catalogDao.getCatalogByCatalogName(catalogName);
        checkState(!Strings.isNullOrEmpty(properties), "Configuration for dynamic catalog %s does not exist", catalogName);
        Map<String, String> propertiesMap = Arrays.stream(properties.split(";"))
                .collect(HashMap::new,
                        (map, property) -> {
                            String[] value = property.split("=");
                            map.put(value[0], value[1]);
                        },
                        Map::putAll);
        return Optional.of(propertiesMap);
    }

    public static class CatalogInfo
    {
        private String catalog;
        private String properties;

        public CatalogInfo(@ColumnName("catalog_name") String catalog, @ColumnName("properties") String properties)
        {
            this.catalog = catalog;
            this.properties = properties;
        }

        public String getCatalog()
        {
            return catalog;
        }

        public String getProperties()
        {
            return properties;
        }
    }
}