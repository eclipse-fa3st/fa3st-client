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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newPage;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.client.query.SearchCriteria;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.OutputModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class BaseInterfaceTest {
    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static BaseInterfaceImpl client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new BaseInterfaceImpl(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testDeserializeCustomPage() throws SerializationException, ClientException, UnsupportedModifierException, JsonProcessingException {
        Page<Submodel> expected = newPage(3, true, Submodel.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(serializer.write(expected));
        ((ObjectNode) node.get("paging_metadata")).put("custom_field", "foo");
        server.stubFor(get(urlPathEqualTo("/api/v3.0"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(mapper.writeValueAsString(node))));

        Page<Submodel> actual = client.getPage(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.DEFAULT,
                new PagingInfo.Builder()
                        .limit(5)
                        .build(),
                Submodel.class);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolvePathOnly() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        String path = "/foo/bar";
        URI expected = new URI(baseUri + path);
        URI actual = client.testResolve(new URI(baseUri), path);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolveBaseUrlEndingWithSlash() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        String path = "/foo/bar";
        URI expected = new URI(baseUri + path);
        URI actual = client.testResolve(new URI(baseUri + "/"), path);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolveNullPath() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        URI expected = new URI(baseUri);
        URI actual = client.testResolve(new URI(baseUri), null);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolveEmptyPath() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        URI expected = new URI(baseUri);
        URI actual = client.testResolve(new URI(baseUri), "");
        assertEquals(expected, actual);
    }


    @Test
    public void testResolveQueryOnly() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        String path = "?a=true&b=1.0";
        URI expected = new URI(baseUri + path);
        URI actual = client.testResolve(new URI(baseUri), path);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolvePathWithQuery() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        String path = "/foo/bar?a=true&b=1.0";
        URI expected = new URI(baseUri + path);
        URI actual = client.testResolve(new URI(baseUri), path);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolvePathWithSpecialCharacter() throws URISyntaxException {
        String baseUri = "https://example.org/test";
        String path = "/submodels/base64..../submodel-elements/list[1].property";
        URI expected = new URI("https://example.org/test/submodels/base64..../submodel-elements/list%5B1%5D.property");
        URI actual = client.testResolve(new URI(baseUri), path);
        assertEquals(expected, actual);
    }

    public class BaseInterfaceImpl extends BaseInterface {

        public BaseInterfaceImpl(URI endpoint) {
            super(endpoint);
        }


        public URI testResolve(URI baseUri, String path) {
            return resolve(baseUri, path);
        }
    }
}
