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

import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import javax.inject.Provider;

import static java.util.Objects.requireNonNull;

public class H2CatalogDaoProvider
        implements Provider<H2CatalogDao>
{
    private final H2CatalogDao dao;

    @Inject
    public H2CatalogDaoProvider(DbCatalogConfig config)
    {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(requireNonNull(config.getCatalogConfigDbUrl(), "catalog.config-db-url is null"));
        // TODO: this should use onDemand()
        this.dao = Jdbi.create(ds)
                .installPlugin(new SqlObjectPlugin())
                .open()
                .attach(H2CatalogDao.class);
    }

    @Override
    public H2CatalogDao get()
    {
        return dao;
    }
}