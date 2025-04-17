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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author jab
 */
public class Constants {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final String URI_PATH_SEPERATOR = "/";

    public static final String PATH_AAS = "aas";
    public static final String PATH_ATTACHMENT = "attachment";
    public static final String PATH_ASSET_INFORMATION = "asset-information";
    public static final String PATH_CONCEPT_DESCRIPTIONS = "concept-descriptions";
    public static final String PATH_DESCRIPTION = "description";
    public static final String PATH_INVOKE = "invoke";
    public static final String PATH_INVOKE_ASYNC = "invoke-async";
    public static final String PATH_LOOKUP = "lookup";
    public static final String PATH_MODIFIER_REFERENCE = "$reference";
    public static final String PATH_MODIFIER_METADATA = "$metadata";
    public static final String PATH_MODIFIER_VALUE = "$value";
    public static final String PATH_MODIFIER_PATH = "$path";
    public static final String PATH_OPERATION_STATUS = "operation-status";
    public static final String PATH_OPERATION_RESULTS = "operation-results";
    public static final String PATH_PACKAGES = "packages";
    public static final String PATH_SERIALIZATION = "serialization";
    public static final String PATH_SHELLS = "shells";
    public static final String PATH_SHELL_DESCRIPTORS = "shell-descriptors";
    public static final String PATH_SUBMODEL_REFS = "submodel-refs";
    public static final String PATH_SUBMODEL = "submodel";
    public static final String PATH_SUBMODELS = "submodels";
    public static final String PATH_SUBMODEL_DESCRIPTORS = "submodel-descriptors";
    public static final String PATH_SUBMODEL_ELEMENTS = "submodel-elements";
    public static final String PATH_THUMBNAIL = "thumbnail";

    public static final String QUERY_PARAMETER_ASSET_IDS = "assetIds";
    public static final String QUERY_PARAMETER_ID_SHORT = "idShort";
    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_CURSOR = "cursor";
    public static final String QUERY_PARAMETER_LEVEL = "level";
    public static final String QUERY_PARAMETER_EXTENT = "extent";
    public static final String QUERY_PARAMETER_IS_CASE_OF = "isCaseOf";
    public static final String QUERY_PARAMETER_DATA_SPECIFICATION_REF = "dataSpecificationRef";
    public static final String QUERY_PARAMETER_AAS_ID = "aasId";
    public static final String QUERY_PARAMETER_AAS_IDS = "aasIds";
    public static final String QUERY_PARAMETER_SUBMODEL_IDS = "submodelIds";
    public static final String QUERY_PARAMETER_INCLUDE_CONCEPT_DESCRIPTIONS = "includeConceptDescriptions";
}
