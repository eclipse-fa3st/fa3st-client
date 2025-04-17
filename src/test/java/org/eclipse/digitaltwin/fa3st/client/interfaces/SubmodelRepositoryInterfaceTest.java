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
package org.eclipse.digitaltwin.fa3st.client.interfaces;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newList;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newPage;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newProperty;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newSubmodel;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.client.query.SubmodelSearchCriteria;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.exception.ValueMappingException;
import org.eclipse.digitaltwin.fa3st.common.model.IdShortPath;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;
import org.eclipse.digitaltwin.fa3st.common.model.value.Datatype;
import org.eclipse.digitaltwin.fa3st.common.model.value.ElementValue;
import org.eclipse.digitaltwin.fa3st.common.model.value.PropertyValue;
import org.eclipse.digitaltwin.fa3st.common.model.value.mapper.ElementValueMapper;
import org.eclipse.digitaltwin.fa3st.common.typing.ElementValueTypeInfo;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.eclipse.digitaltwin.fa3st.common.util.ReferenceBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class SubmodelRepositoryInterfaceTest {
    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static SubmodelRepositoryInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new SubmodelRepositoryInterface(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testGetAll() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Submodel> expected = newPage(3, false, Submodel.class);
        server.stubFor(get(urlPathEqualTo("/api/v3.0/submodels"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        List<Submodel> actual = client.getAll();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testGetAllReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        List<Reference> expected = newList(3, Submodel.class).stream()
                .map(ReferenceBuilder::forSubmodel)
                .toList();
        stubFor(get(urlPathEqualTo("/api/v3.0/submodels/$reference"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(Page.of(expected, PagingMetadata.EMPTY)))));

        List<Reference> actual = client.getAllReferences(SubmodelSearchCriteria.DEFAULT);
        assertEquals(expected, actual);
    }


    @Test
    public void testPost() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel expected = newSubmodel();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/api/v3.0/submodels"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(payload)));
        Submodel actual = client.post(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testDelete() throws InterruptedException, ClientException {
        String submodelId = "http://example.org/submodel/default";
        stubFor(delete(urlPathEqualTo("/api/v3.0/submodels/" + EncodingHelper.base64UrlEncode(submodelId)))
                .willReturn(aResponse().withStatus(204)));
        client.delete(submodelId);
    }


    @Test
    public void testGetSubmodel() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel expected = newSubmodel();
        stubFor(get(urlPathEqualTo("/api/v3.0/submodels/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));

        Submodel actual = client.getSubmodelInterface(expected.getId()).get();
        assertEquals(expected, actual);
    }


    @Test
    public void testGetSubmodelElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, ValueMappingException {
        String submodelId = "http://example.org/submodel/default";
        SubmodelElement submodelElement = newProperty();
        ElementValue expected = ElementValueMapper.toValue(submodelElement);
        server.stubFor(get(urlPathEqualTo(
                String.format("/api/v3.0/submodels/%s/submodel-elements/%s/$value",
                        EncodingHelper.base64UrlEncode(submodelId),
                        submodelElement.getIdShort())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));

        ElementValue actual = client.getSubmodelInterface(submodelId).getElementValue(
                IdShortPath.builder()
                        .idShort(submodelElement.getIdShort())
                        .build(),
                ElementValueTypeInfo.builder()
                        .datatype(Datatype.STRING)
                        .type(PropertyValue.class)
                        .build());
        assertEquals(expected, actual);
    }


    @Test
    public void testPatchSubmodelElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, ValueMappingException {
        String submodelId = "http://example.org/submodel/default";
        SubmodelElement submodelElement = newProperty();
        ElementValue expected = ElementValueMapper.toValue(submodelElement);
        String payload = serializer.write(expected);
        server.stubFor(patch(urlPathEqualTo(
                String.format("/api/v3.0/submodels/%s/submodel-elements/%s/$value",
                        EncodingHelper.base64UrlEncode(submodelId),
                        submodelElement.getIdShort())))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.getSubmodelInterface(submodelId).patchElementValue(
                IdShortPath.builder()
                        .idShort(submodelElement.getIdShort())
                        .build(),
                expected);
    }
}
