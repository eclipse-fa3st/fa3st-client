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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newAASDescriptor;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellDescriptor;
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


public class AASRegistryInterfaceTest {
    private AASRegistryInterface aasRegistryInterface;
    private ApiSerializer serializer;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        URI serviceUri = URI.create(server.url("/example/api/v3.0"));
        aasRegistryInterface = new AASRegistryInterface(serviceUri);
        serializer = new JsonApiSerializer();
    }


    @Test
    public void testGetAllAssetAdministrationShellDescriptors() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        Page<AssetAdministrationShellDescriptor> expected = Page.<AssetAdministrationShellDescriptor> builder()
                .result(newAASDescriptor())
                .metadata(new PagingMetadata.Builder().build())
                .build();
        stubFor(get(urlPathEqualTo("/example/api/v3.0/shell-descriptors"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        List<AssetAdministrationShellDescriptor> actual = aasRegistryInterface.getAll();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testPost() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        AssetAdministrationShellDescriptor expected = newAASDescriptor();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(payload)));

        AssetAdministrationShellDescriptor actual = aasRegistryInterface.post(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testGet() throws SerializationException, ClientException, InterruptedException, UnsupportedModifierException {
        AssetAdministrationShellDescriptor expected = newAASDescriptor();
        stubFor(get(urlPathEqualTo("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        AssetAdministrationShellDescriptor actual = aasRegistryInterface.get(expected.getId());
        assertEquals(expected, actual);
    }


    @Test
    public void testPut() throws ClientException, SerializationException, InterruptedException, UnsupportedModifierException {
        AssetAdministrationShellDescriptor expected = newAASDescriptor();
        String payload = serializer.write(expected);
        stubFor(put(urlPathEqualTo("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        aasRegistryInterface.put(expected);
    }


    @Test
    public void testDelete() throws ClientException, InterruptedException {
        String aasId = "http://example.org/aas/default";
        stubFor(delete(urlPathEqualTo("/example/api/v3.0/shell-descriptors/" + EncodingHelper.base64UrlEncode(aasId)))
                .willReturn(aResponse().withStatus(204)));
        aasRegistryInterface.delete(aasId);
    }

}
