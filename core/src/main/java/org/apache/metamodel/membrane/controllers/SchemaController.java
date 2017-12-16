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
import org.apache.metamodel.membrane.swagger.model.GetSchemaResponse;
import org.apache.metamodel.membrane.swagger.model.GetSchemaResponseTables;
import org.apache.metamodel.schema.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = { "/{tenant}/{dataContext}/schemas/{schema}",
        "/{tenant}/{dataContext}/s/{schema}" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class SchemaController {

    private final TenantRegistry tenantRegistry;

    @Autowired
    public SchemaController(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public GetSchemaResponse get(@PathVariable("tenant") String tenantId,
            @PathVariable("dataContext") String dataSourceName, @PathVariable("schema") String schemaId) {
        final TenantContext tenantContext = tenantRegistry.getTenantContext(tenantId);
        final DataContext dataContext = tenantContext.getDataSourceRegistry().openDataContext(dataSourceName);

        final DataContextTraverser traverser = new DataContextTraverser(dataContext);

        final Schema schema = traverser.getSchema(schemaId);
        final String tenantName = tenantContext.getTenantName();
        final UriBuilder uriBuilder = UriBuilder.fromPath("/{tenant}/{dataContext}/s/{schema}/t/{table}");

        final String schemaName = schema.getName();
        final List<GetSchemaResponseTables> tableLinks = schema.getTableNames().stream().map(t -> {
            final String uri = uriBuilder.build(tenantName, dataSourceName, schemaName, t).toString();
            return new GetSchemaResponseTables().name(String.valueOf(t)).uri(uri);
        }).collect(Collectors.toList());

        final GetSchemaResponse resp = new GetSchemaResponse();
        resp.type("schema");
        resp.name(schemaName);
        resp.datasource(dataSourceName);
        resp.tenant(tenantName);
        resp.tables(tableLinks);
        return resp;
    }
}
