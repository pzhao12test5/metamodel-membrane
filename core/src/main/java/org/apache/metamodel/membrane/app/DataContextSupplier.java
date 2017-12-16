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
package org.apache.metamodel.membrane.app;

import java.util.function.Supplier;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.factory.DataContextFactoryRegistryImpl;
import org.apache.metamodel.factory.DataContextProperties;

public class DataContextSupplier implements Supplier<DataContext> {

    private final String dataSourceName;
    private final DataContextProperties dataContextProperties;

    public DataContextSupplier(String dataSourceName, DataContextProperties dataContextProperties) {
        this.dataSourceName = dataSourceName;
        this.dataContextProperties = dataContextProperties;
    }

    @Override
    public DataContext get() {
        final DataContext dataContext = DataContextFactoryRegistryImpl.getDefaultInstance().createDataContext(
                dataContextProperties);
        return dataContext;
    }

    @Override
    public String toString() {
        return "DataContextSupplier[" + dataSourceName + "]";
    }
}
