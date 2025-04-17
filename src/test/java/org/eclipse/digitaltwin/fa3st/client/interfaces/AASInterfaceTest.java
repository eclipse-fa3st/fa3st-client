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

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.request;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_DISPOSITION;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.InvalidRequestException;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.InMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.TypedInMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AASInterfaceTest {
    private AASInterface aasInterface;
    private ApiSerializer serializer;
    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        URI serviceUri = URI.create(server.url("api/v3.0/aas"));
        serializer = new JsonApiSerializer();
        aasInterface = new AASInterface(serviceUri);
    }


    @Test
    public void testGetAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell expected = new DefaultAssetAdministrationShell();
        stubFor(request("GET", urlPathEqualTo("/api/v3.0/aas"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        AssetAdministrationShell actual = aasInterface.get();
        assertEquals(expected, actual);
    }


    @Test
    public void testPutAssetAdministrationShell() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetAdministrationShell requestAas = new DefaultAssetAdministrationShell();
        stubFor(put(urlPathEqualTo("/api/v3.0/aas"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(serializer.write(requestAas)))
                .willReturn(aResponse()
                        .withStatus(204)));
        aasInterface.put(requestAas);
    }


    @Test
    public void testGetAssetAdministrationShellAsReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Reference expected = new DefaultReference.Builder()
                .type(ReferenceTypes.MODEL_REFERENCE)
                .keys(new DefaultKey())
                .build();
        stubFor(get(urlPathEqualTo("/api/v3.0/aas/$reference"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        Reference actual = aasInterface.getAsReference();
        assertEquals(expected, actual);
    }


    @Test
    public void testGetAssetInformation() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetInformation expected = new DefaultAssetInformation();
        stubFor(get(urlPathEqualTo("/api/v3.0/aas/asset-information"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        AssetInformation actual = aasInterface.getAssetInformation();
        assertEquals(expected, actual);
    }


    @Test
    public void testPutAssetInformation() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        AssetInformation expected = new DefaultAssetInformation();
        stubFor(put(urlPathEqualTo("/api/v3.0/aas/asset-information"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(serializer.write(expected)))
                .willReturn(aResponse()
                        .withStatus(204)));
        aasInterface.putAssetInformation(expected);
    }


    @Test
    public void testGetThumbnail() throws InterruptedException, ClientException, InvalidRequestException {
        byte[] content = "thumbnail-content".getBytes();
        InMemoryFile expected = new InMemoryFile.Builder()
                .content(content)
                .path("thumbnail.png")
                .build();
        stubFor(get(urlPathEqualTo("/api/v3.0/aas/asset-information/thumbnail"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, ContentType.IMAGE_PNG.getMimeType())
                        .withHeader(CONTENT_DISPOSITION, String.format("attachment; fileName=\"%s\"", expected.getPath()))
                        .withBody(expected.getContent())));
        InMemoryFile actual = aasInterface.getThumbnail();
        assertEquals(expected, actual);
    }


    @Test
    public void testPutThumbnail() throws InterruptedException, ClientException, IOException {
        TypedInMemoryFile expected = new TypedInMemoryFile.Builder()
                .content("thumbnail-content".getBytes())
                .path("TestFile.png")
                .contentType("image/png")
                .build();
        stubFor(put(urlPathEqualTo("/api/v3.0/aas/asset-information/thumbnail"))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .withMultipartRequestBody(aMultipart()
                        .withName("fileName")
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(ContentType.TEXT_PLAIN.getMimeType()))
                        .withBody(equalTo(expected.getPath())))
                .withMultipartRequestBody(aMultipart()
                        .withName("file")
                        .withFileName(expected.getPath())
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(ContentType.IMAGE_PNG.getMimeType()))
                        .withBody(equalTo(new String(expected.getContent()))))
                .willReturn(aResponse()
                        .withStatus(204)));
        aasInterface.putThumbnail(expected);
    }


    @Test
    public void testDeleteThumbnail() throws InterruptedException, ClientException {
        stubFor(delete(urlPathEqualTo("/api/v3.0/aas/asset-information/thumbnail"))
                .willReturn(aResponse()
                        .withStatus(200)));
        aasInterface.deleteThumbnail();
    }


    @Test
    public void testGetAllSubmodelReferences() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<Reference> expected = Page.<Reference> builder()
                .result(new DefaultReference())
                .metadata(new PagingMetadata.Builder().build())
                .build();

        stubFor(get(urlPathEqualTo("/api/v3.0/aas/submodel-refs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));

        List<Reference> actual = aasInterface.getAllSubmodelReferences();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testPostSubmodelReference() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Reference expected = new DefaultReference();
        stubFor(post(urlPathEqualTo("/api/v3.0/aas/submodel-refs"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(serializer.write(expected)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(serializer.write(expected))));

        Reference actual = aasInterface.postSubmodelReference(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testDeleteSubmodelReference() throws InterruptedException, ClientException {
        String expected = "submodelId";
        stubFor(delete(urlPathEqualTo("/api/v3.0/aas/submodel-refs/" + EncodingHelper.base64Encode(expected)))
                .willReturn(aResponse().withStatus(204)));
        aasInterface.deleteSubmodelReference(expected);
    }


    @Test
    public void testDeleteSubmodel() throws InterruptedException, ClientException {
        String expected = "submodelId";
        stubFor(delete(urlPathEqualTo("/api/v3.0/aas/submodels/" + EncodingHelper.base64Encode(expected)))
                .willReturn(aResponse().withStatus(204)));
        aasInterface.deleteSubmodel(expected);
    }
}
