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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.digitaltwin.fa3st.client.exception.InvalidPayloadException;
import org.eclipse.digitaltwin.fa3st.common.model.persistence.AssetAdministrationShellSearchCriteria;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.eclipse.digitaltwin.fa3st.common.util.StringHelper;


/**
 * Allows to filter Asset Administration Shells in an Asset Administration Shell repository based on
 * idShort and a List of AssetIdentification objects.
 */
public class AASSearchCriteria extends AssetAdministrationShellSearchCriteria implements SearchCriteria {

    public static final AASSearchCriteria DEFAULT = new AASSearchCriteria();

    @Override
    public String toQueryString() {
        String assetIdsString = "";
        if (Objects.nonNull(getAssetIds()) && !getAssetIds().isEmpty()) {
            try {
                assetIdsString = "assetIds=" + EncodingHelper.base64UrlEncode(
                        new ObjectMapper().writeValueAsString(getAssetIds()));
            }
            catch (JsonProcessingException e) {
                throw new InvalidPayloadException("Failed to serialize asset IDs", e);
            }
        }
        String idShortString = StringHelper.isBlank(getIdShort()) ? "" : "idShort=" + getIdShort();
        return Stream.of(assetIdsString, idShortString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));

    }

    public static class Builder extends AssetAdministrationShellSearchCriteria.AbstractBuilder<AASSearchCriteria, Builder> {
        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected AASSearchCriteria newBuildingInstance() {
            return new AASSearchCriteria();
        }
    }
}
