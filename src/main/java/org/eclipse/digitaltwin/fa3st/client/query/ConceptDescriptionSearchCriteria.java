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
package org.eclipse.digitaltwin.fa3st.client.query;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.eclipse.digitaltwin.fa3st.common.util.ReferenceHelper;


/**
 * Allows to filter Concept Descriptions in a Concept Description Repository based on idShort, isCaseOf and
 * dataSpecification.
 */
public class ConceptDescriptionSearchCriteria extends org.eclipse.digitaltwin.fa3st.common.model.persistence.ConceptDescriptionSearchCriteria implements SearchCriteria {

    public static final ConceptDescriptionSearchCriteria DEFAULT = new ConceptDescriptionSearchCriteria();

    @Override
    public String toQueryString() {
        String isCaseOfString = getIsCaseOf() == null ? ""
                : "isCaseOf=" + EncodingHelper.base64Encode(ReferenceHelper.toString(getIsCaseOf()));
        String idShortString = getIdShort() == null ? "" : "idShort=" + getIdShort();
        String dataSpecificationRefString = getDataSpecification() == null ? ""
                : "dataSpecificationRef=" + EncodingHelper.base64Encode(ReferenceHelper.toString(getDataSpecification()));

        return Stream.of(isCaseOfString, idShortString, dataSpecificationRefString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }

    public static class Builder
            extends org.eclipse.digitaltwin.fa3st.common.model.persistence.ConceptDescriptionSearchCriteria.AbstractBuilder<ConceptDescriptionSearchCriteria, Builder> {
        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected ConceptDescriptionSearchCriteria newBuildingInstance() {
            return new ConceptDescriptionSearchCriteria();
        }
    }
}
