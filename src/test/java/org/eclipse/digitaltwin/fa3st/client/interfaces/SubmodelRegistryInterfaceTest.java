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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newSubmodelDescriptor;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class SubmodelRegistryInterfaceTest {

    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static SubmodelRegistryInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new SubmodelRegistryInterface(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testGetAll() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        Page<SubmodelDescriptor> expected = Page.of(List.of(newSubmodelDescriptor()), PagingMetadata.EMPTY);
        server.stubFor(get(urlPathEqualTo("/api/v3.0/submodel-descriptors"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        List<SubmodelDescriptor> actual = client.getAll();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testPost() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        SubmodelDescriptor expected = newSubmodelDescriptor();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/api/v3.0/submodel-descriptors"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(payload)));
        SubmodelDescriptor actual = client.post(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testGetById() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        SubmodelDescriptor expected = newSubmodelDescriptor();
        stubFor(get(urlPathEqualTo("/api/v3.0/submodel-descriptors/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));

        SubmodelDescriptor actual = client.get(expected.getId());
        assertEquals(expected, actual);
    }


    @Test
    public void testPut() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        SubmodelDescriptor expected = newSubmodelDescriptor();
        String payload = serializer.write(expected);
        stubFor(put(urlPathEqualTo("/api/v3.0/submodel-descriptors/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.put(expected);
    }


    @Test
    public void testDeleteById() throws ClientException, InterruptedException {
        String submodelDescriptorId = "http://example.org/submodel-descriptor/default";
        stubFor(delete(urlPathEqualTo("/api/v3.0/submodel-descriptors/" + EncodingHelper.base64UrlEncode(submodelDescriptorId)))
                .willReturn(aResponse().withStatus(204)));
        client.delete(submodelDescriptorId);
    }
}
