/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.metamodel.membrane.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.membrane.app.DataContextTraverser;
import org.apache.metamodel.membrane.app.TenantContext;
import org.apache.metamodel.membrane.app.TenantRegistry;
import org.apache.metamodel.membrane.swagger.model.GetTableResponse;
import org.apache.metamodel.membrane.swagger.model.GetTableResponseColumns;
import org.apache.metamodel.schema.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = { "/{tenant}/{dataContext}/schemas/{schema}/tables/{table}",
        "/{tenant}/{dataContext}/s/{schema}/t/{table}" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class TableController {

    private final TenantRegistry tenantRegistry;

    @Autowired
    public TableController(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public GetTableResponse get(@PathVariable("tenant") String tenantId,
            @PathVariable("dataContext") String dataSourceName, @PathVariable("schema") String schemaId,
            @PathVariable("table") String tableId) {
        final TenantContext tenantContext = tenantRegistry.getTenantContext(tenantId);
        final DataContext dataContext = tenantContext.getDataSourceRegistry().openDataContext(dataSourceName);

        final DataContextTraverser traverser = new DataContextTraverser(dataContext);

        final Table table = traverser.getTable(schemaId, tableId);

        final String tenantName = tenantContext.getTenantName();
        final UriBuilder uriBuilder = UriBuilder.fromPath("/{tenant}/{dataContext}/s/{schema}/t/{table}/c/{column}");

        final String tableName = table.getName();
        final String schemaName = table.getSchema().getName();
        final List<GetTableResponseColumns> columnsLinks = table.getColumnNames().stream().map(c -> {
            final String uri = uriBuilder.build(tenantName, dataSourceName, schemaName, tableName, c).toString();
            return new GetTableResponseColumns().name(c).uri(uri);
        }).collect(Collectors.toList());

        final GetTableResponse resp = new GetTableResponse();
        resp.type("table");
        resp.name(tableName);
        resp.schema(schemaName);
        resp.datasource(dataSourceName);
        resp.tenant(tenantName);
        resp.columns(columnsLinks);
        return resp;
    }
}
