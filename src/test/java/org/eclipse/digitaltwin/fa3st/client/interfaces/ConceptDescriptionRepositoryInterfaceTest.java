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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newConceptDescription;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newPage;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ConceptDescriptionRepositoryInterfaceTest {
    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static ConceptDescriptionRepositoryInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new ConceptDescriptionRepositoryInterface(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testGetAll() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<ConceptDescription> expected = newPage(3, false, ConceptDescription.class);
        server.stubFor(get(urlPathEqualTo("/api/v3.0/concept-descriptions"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        List<ConceptDescription> actual = client.getAll();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testPost() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ConceptDescription expected = newConceptDescription();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/api/v3.0/concept-descriptions"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(payload)));
        ConceptDescription actual = client.post(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testGetById() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ConceptDescription expected = newConceptDescription();
        stubFor(get(urlPathEqualTo("/api/v3.0/concept-descriptions/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));

        ConceptDescription actual = client.get(expected.getId());
        assertEquals(expected, actual);
    }


    @Test
    public void testDelete() throws InterruptedException, ClientException {
        String conceptDescriptionId = "http://example.org/concept-description/default";
        stubFor(delete(urlPathEqualTo("/api/v3.0/concept-descriptions/" + EncodingHelper.base64UrlEncode(conceptDescriptionId)))
                .willReturn(aResponse().withStatus(204)));
        client.delete(conceptDescriptionId);
    }
}
