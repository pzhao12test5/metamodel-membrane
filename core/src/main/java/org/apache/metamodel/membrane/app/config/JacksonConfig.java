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
package org.apache.metamodel.membrane.app.config;

import org.apache.metamodel.membrane.swagger.invoker.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    // use the JSON class from swagger-codegen
    private static final ObjectMapper OBJECT_MAPPER = new JSON().getContext(Object.class);;

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        return getObjectMapper();
    }
}
