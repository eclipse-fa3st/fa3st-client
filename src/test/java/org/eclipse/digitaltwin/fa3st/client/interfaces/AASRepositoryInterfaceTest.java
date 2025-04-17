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
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newAAS;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newList;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.client.matching.EqualToJsonBase64EncodedQueryParameterMatcher;
import org.eclipse.digitaltwin.fa3st.client.query.AASSearchCriteria;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;
import org.eclipse.digitaltwin.fa3st.common.model.asset.AssetIdentification;
import org.eclipse.digitaltwin.fa3st.common.model.asset.GlobalAssetIdentification;
import org.eclipse.digitaltwin.fa3st.common.model.asset.SpecificAssetIdentification;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.eclipse.digitaltwin.fa3st.common.util.ReferenceBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AASRepositoryInterfaceTest {

    private static final ApiSerializer serializer = new JsonApiSerializer();

    private AASRepositoryInterface aasRepositoryInterface;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        aasRepositoryInterface = new AASRepositoryInterface(URI.create(server.url("/example/api/v3.0")));
    }


    @Test
    public void testGetAASPage() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<AssetAdministrationShell> expected = Page.<AssetAdministrationShell> builder()
                .result(newList(2, AssetAdministrationShell.class))
                .metadata(new PagingMetadata.Builder()
                        .cursor("nextCursor")
                        .build())
                .build();
        stubFor(get(urlPathEqualTo("/example/api/v3.0/shells"))
                .withQueryParam("limit", equalTo(Integer.toString(expected.getContent().size())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        Page<AssetAdministrationShell> actual = aasRepositoryInterface.get(
                new PagingInfo.Builder()
                        .limit((long) expected.getContent().size())
                        .build());
        assertEquals(expected, actual);
    }


    @Test
    public void testGetAllAssetAdministrationShellsWithPagingWithSearchCriteria()
            throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, JsonProcessingException {
        final String cursor = "example-cursor";
        final String idShort = "example-id";
        Page<AssetAdministrationShell> expected = Page.<AssetAdministrationShell> builder()
                .result(newList(2, AssetAdministrationShell.class))
                .metadata(PagingMetadata.EMPTY)
                .build();
        List<AssetIdentification> assetIdentifiers = List.of(
                new GlobalAssetIdentification.Builder()
                        .value("assetLink1")
                        .build(),
                new SpecificAssetIdentification.Builder()
                        .key("specificAssetId")
                        .value("assetLink2")
                        .build());
        server.stubFor(get(urlPathEqualTo("/example/api/v3.0/shells"))
                .withQueryParam("limit", equalTo(Integer.toString(expected.getContent().size())))
                .withQueryParam("cursor", equalTo(EncodingHelper.base64UrlEncode(cursor)))
                .withQueryParam("idShort", equalTo(idShort))
                .andMatching(new EqualToJsonBase64EncodedQueryParameterMatcher(
                        "assetIds",
                        new ObjectMapper().writeValueAsString(assetIdentifiers)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        Page<AssetAdministrationShell> actual = aasRepositoryInterface.get(
                new PagingInfo.Builder()
                        .cursor(cursor)
                        .limit((long) expected.getContent().size())
                        .build(),
                new AASSearchCriteria.Builder()
                        .assetIds(assetIdentifiers)
                        .idShort(idShort)
                        .build());
        assertEquals(expected, actual);
    }


    @Test
    public void postAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell expected = newAAS();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/example/api/v3.0/shells"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(payload)));
        AssetAdministrationShell actual = aasRepositoryInterface.post(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testGetAllAssetAdministrationShellsAsReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Reference> expected = Page.<Reference> builder()
                .result(newList(3, AssetAdministrationShell.class).stream()
                        .map(ReferenceBuilder::forAas)
                        .toList())
                .metadata(PagingMetadata.EMPTY)
                .build();
        String serializedAasReferenceList = serializer.write(expected);
        stubFor(get(urlPathEqualTo("/example/api/v3.0/shells/$reference"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializedAasReferenceList)));

        List<Reference> actual = aasRepositoryInterface.getAllAsReference();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testDelete() throws InterruptedException, ClientException {
        String aasId = "http://example.org/aas/example";
        stubFor(delete(urlPathEqualTo("/example/api/v3.0/shells/" + EncodingHelper.base64UrlEncode(aasId)))
                .willReturn(aResponse()
                        .withStatus(204)));
        aasRepositoryInterface.delete(aasId);
    }


    @Test
    public void testGetAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell expected = newAAS();
        stubFor(get(urlPathEqualTo("/example/api/v3.0/shells/" + EncodingHelper.base64UrlEncode(expected.getId())))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));

        AssetAdministrationShell actual = aasRepositoryInterface.getAASInterface(expected.getId()).get();

        assertEquals(expected, actual);
    }


    private static StringValuePattern equalToJsonBase64Encoded(String expectedJson) {
        return new StringValuePattern(expectedJson) {

            @Override
            public MatchResult match(String value) {
                String actualJson = EncodingHelper.base64UrlDecode(value);
                return equalToJson(expectedJson).match(actualJson);
            }
        };
    }
}
