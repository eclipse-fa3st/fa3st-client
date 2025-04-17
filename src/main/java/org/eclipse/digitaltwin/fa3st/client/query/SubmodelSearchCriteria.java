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


/**
 * Allows to filter Submodels in a Submodel repository based on
 * semanticId and idShort.
 */
public class SubmodelSearchCriteria extends org.eclipse.digitaltwin.fa3st.common.model.persistence.SubmodelSearchCriteria implements SearchCriteria {
    public static SubmodelSearchCriteria DEFAULT = new SubmodelSearchCriteria();

    @Override
    public String toQueryString() {
        String semanticIdString = getSemanticId() == null ? ""
                : "semanticId=" + getSemanticId().getKeys().stream().map(Object::toString).collect(Collectors.joining(","));
        String idShortString = getIdShort() == null ? ""
                : "idShort=" + getIdShort();

        return Stream.of(semanticIdString, idShortString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }

    public static class Builder extends org.eclipse.digitaltwin.fa3st.common.model.persistence.SubmodelSearchCriteria.AbstractBuilder<SubmodelSearchCriteria, Builder> {
        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected SubmodelSearchCriteria newBuildingInstance() {
            return new SubmodelSearchCriteria();
        }
    }
}
