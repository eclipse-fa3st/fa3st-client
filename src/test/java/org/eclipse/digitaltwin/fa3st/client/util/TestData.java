/**
 * Copyright (c) 2025 the Eclipse FA³ST Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.digitaltwin.fa3st.client.util;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelDescriptor;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;


public class TestData {

    private static Map<Class, Supplier> PRODUCERS = Map.of(
            AssetAdministrationShell.class, TestData::newAAS,
            ConceptDescription.class, TestData::newConceptDescription,
            Submodel.class, TestData::newSubmodel,
            AssetAdministrationShellDescriptor.class, TestData::newAASDescriptor);

    public static <T extends Identifiable> List<T> newList(int size, Class<T> type) {
        return IntStream.rangeClosed(1, size)
                .mapToObj(x -> {
                    T element = (T) PRODUCERS.get(type).get();
                    element.setId(element.getId() + x);
                    element.setIdShort(element.getId() + x);
                    return element;
                })
                .toList();
    }


    public static <T extends Identifiable> Page<T> newPage(int size, boolean hasMoreEntries, Class<T> type) {
        return Page.of(
                newList(size, type),
                hasMoreEntries
                        ? PagingMetadata.builder()
                                .cursor("nextCursor")
                                .build()
                        : PagingMetadata.EMPTY);
    }


    public static AssetAdministrationShell newAAS() {
        return new DefaultAssetAdministrationShell.Builder()
                .id("http://example.org/aas/default")
                .idShort("defaultAAS")
                .build();
    }


    public static Submodel newSubmodel() {
        return new DefaultSubmodel.Builder()
                .id("http://example.org/submodel/default")
                .idShort("defaultSubmodel")
                .build();
    }


    public static ConceptDescription newConceptDescription() {
        return new DefaultConceptDescription.Builder()
                .id("http://example.org/concept-description/default")
                .id("defaultConceptDescription")
                .build();
    }


    public static Property newProperty() {
        return new DefaultProperty.Builder()
                .idShort("defaultProperty")
                .valueType(DataTypeDefXsd.STRING)
                .value("example value")
                .build();
    }


    public static AssetAdministrationShellDescriptor newAASDescriptor() {
        return new DefaultAssetAdministrationShellDescriptor.Builder()
                .id("http://example.org/aas-descriptor/default")
                .idShort("defaultAASDescriptor")
                .build();
    }


    public static SubmodelDescriptor newSubmodelDescriptor() {
        return new DefaultSubmodelDescriptor.Builder()
                .id("http://example.org/submodel-descriptor/default")
                .idShort("defaultSubmodelDescriptor")
                .build();
    }
}
