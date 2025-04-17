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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_DISPOSITION;
import static org.apache.commons.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newProperty;
import static org.eclipse.digitaltwin.fa3st.client.util.TestData.newSubmodel;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.aas4j.v3.model.ExecutionState;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.InvalidRequestException;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.exception.ValueMappingException;
import org.eclipse.digitaltwin.fa3st.common.model.IdShortPath;
import org.eclipse.digitaltwin.fa3st.common.model.InMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.TypedInMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingMetadata;
import org.eclipse.digitaltwin.fa3st.common.model.value.Datatype;
import org.eclipse.digitaltwin.fa3st.common.model.value.ElementValue;
import org.eclipse.digitaltwin.fa3st.common.model.value.PropertyValue;
import org.eclipse.digitaltwin.fa3st.common.model.value.mapper.ElementValueMapper;
import org.eclipse.digitaltwin.fa3st.common.typing.ElementValueTypeInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.util.UriUtils;


public class SubmodelInterfaceTest {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static SubmodelInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new SubmodelInterface(URI.create(server.url("/api/v3.0/submodel")));
    }


    private static Submodel newSubmodelWithElements() {
        Submodel result = newSubmodel();
        result.getSubmodelElements().add(
                new DefaultEntity.Builder()
                        .idShort("entityId")
                        .build());
        result.getSubmodelElements().add(
                new DefaultOperation.Builder()
                        .idShort("operationId")
                        .build());
        return result;
    }


    @Test
    public void testGetDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel expected = newSubmodelWithElements();
        server.stubFor(get(urlPathEqualTo("/api/v3.0/submodel"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        Submodel actual = client.get();
        assertEquals(expected, actual);
    }


    @Test
    public void testPut() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel expected = newSubmodelWithElements();
        String payload = serializer.write(expected);
        stubFor(put(urlPathEqualTo("/api/v3.0/submodel"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.put(expected);
    }


    @Test
    public void testPatchDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Submodel expected = newSubmodelWithElements();
        String payload = serializer.write(expected);
        stubFor(patch(urlPathEqualTo("/api/v3.0/submodel"))
                .withQueryParam("level", equalTo("core"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.patch(expected);
    }


    @Test
    public void testGetAllElements() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        Page<SubmodelElement> expected = Page.of(
                List.of(newProperty()),
                PagingMetadata.EMPTY);
        server.stubFor(get(urlPathEqualTo("/api/v3.0/submodel/submodel-elements"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        List<SubmodelElement> actual = client.getAllElements();
        assertEquals(expected.getContent(), actual);
    }


    @Test
    public void testPostElement() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement expected = newProperty();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/api/v3.0/submodel/submodel-elements"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(payload)));
        SubmodelElement actual = client.postElement(expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testGetElementDefault() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement expected = newProperty();
        server.stubFor(get(urlPathEqualTo("/api/v3.0/submodel/submodel-elements/" + expected.getIdShort()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        SubmodelElement actual = client.getElement(
                IdShortPath.builder()
                        .idShort(expected.getIdShort())
                        .build());
        assertEquals(expected, actual);
    }


    @Test
    public void testGetElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, ValueMappingException {
        SubmodelElement submodelElement = newProperty();
        ElementValue expected = ElementValueMapper.toValue(submodelElement);
        server.stubFor(get(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/$value", submodelElement.getIdShort())))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withStatus(200)
                        .withBody(serializer.write(expected))));

        ElementValue actual = client.getElementValue(
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
    public void testPatchElementValue() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, ValueMappingException {
        SubmodelElement submodelElement = newProperty();
        ElementValue expected = ElementValueMapper.toValue(submodelElement);
        String payload = serializer.write(expected);
        server.stubFor(patch(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/$value", submodelElement.getIdShort())))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.patchElementValue(
                IdShortPath.builder()
                        .idShort(submodelElement.getIdShort())
                        .build(),
                expected);
    }


    @Test
    public void testPostElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement expected = newProperty();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        String payload = serializer.write(expected);
        stubFor(post(urlPathEqualTo("/api/v3.0/submodel/submodel-elements/" + encodeForUrl(idShortPath)))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(payload)));
        SubmodelElement actual = client.postElement(idShortPath, expected);
        assertEquals(expected, actual);
    }


    @Test
    public void testPutElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement expected = newProperty();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        String payload = serializer.write(expected);
        stubFor(put(urlPathEqualTo("/api/v3.0/submodel/submodel-elements/" + encodeForUrl(idShortPath)))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.putElement(idShortPath, expected);
    }


    @Test
    public void testPatchElementByPath() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        SubmodelElement expected = newProperty();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        String payload = serializer.write(expected);
        stubFor(patch(urlPathEqualTo("/api/v3.0/submodel/submodel-elements/" + encodeForUrl(idShortPath)))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(payload))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.patchElement(idShortPath, expected);
    }


    @Test
    public void testDeleteElement() throws InterruptedException, ClientException {
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        stubFor(delete(urlPathEqualTo("/api/v3.0/submodel/submodel-elements/" + encodeForUrl(idShortPath)))
                .willReturn(aResponse().withStatus(204)));
        client.deleteElement(idShortPath);
    }


    @Test
    public void testGetAttachment() throws InterruptedException, ClientException, InvalidRequestException {
        byte[] content = "attachment-content".getBytes();
        InMemoryFile expected = new InMemoryFile.Builder()
                .content(content)
                .path("attachment.pdf")
                .build();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        stubFor(get(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", encodeForUrl(idShortPath))))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/pdf")
                        .withHeader(CONTENT_DISPOSITION, String.format("attachment; fileName=\"%s\"", expected.getPath()))
                        .withBody(expected.getContent())));
        InMemoryFile actual = client.getAttachment(idShortPath);
        assertEquals(expected, actual);
    }

    //    @Test
    //    public void testGetAttachmentLive() throws InterruptedException, ClientException, InvalidRequestException {
    //        byte[] content = "attachment-content".getBytes();
    //        InMemoryFile requestAttachment = new InMemoryFile.Builder()
    //                .content(content)
    //                .path("attachment.pdf")
    //                .build();
    //
    //        Buffer buffer = new Buffer();
    //        buffer.write(requestAttachment.getContent());
    //        MockResponse response = new MockResponse()
    //                .setResponseCode(200)
    //                .addHeader(CONTENT_TYPE, "application/pdf")
    //                .addHeader(CONTENT_DISPOSITION, "attachment; fileName=\"attachment.pdf\"")
    //                .setBody(buffer);
    //
    //        server.enqueue(response);
    //
    //        IdShortPath idShort = IdShortPath.parse("idShort");
    //        InMemoryFile responseAttachment = submodelInterface.getAttachment(idShort);
    //        RecordedRequest request = server.takeRequest();
    //
    //        assertEquals("GET", request.getMethod());
    //        assertEquals(0, request.getBodySize());
    //        assertEquals(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", idShort), request.getPath());
    //        assertEquals(requestAttachment, responseAttachment);
    //    }


    @Test
    public void testPutAttachment() throws InterruptedException, ClientException, IOException {
        TypedInMemoryFile expected = new TypedInMemoryFile.Builder()
                .content("thumbnail-content".getBytes())
                .path("TestFile.png")
                .contentType("image/png")
                .build();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        stubFor(put(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", encodeForUrl(idShortPath))))
                .withMultipartRequestBody(aMultipart()
                        .withName("fileName")
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.TEXT_PLAIN.withCharset(DEFAULT_CHARSET).toString()))
                        .withBody(equalTo(expected.getPath())))
                .withMultipartRequestBody(aMultipart()
                        .withName("file")
                        .withFileName(expected.getPath())
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.IMAGE_PNG.toString()))
                        .withBody(equalTo(new String(expected.getContent()))))
                .willReturn(aResponse()
                        .withStatus(204)));
        client.putAttachment(idShortPath, expected);
    }


    @Test
    public void testDeleteFileByPath() throws InterruptedException, ClientException {
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("property")
                .build();
        stubFor(delete(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/attachment", encodeForUrl(idShortPath))))
                .willReturn(aResponse().withStatus(200)));
        client.deleteAttachment(idShortPath);
    }


    @Test
    public void testInvokeOperationSync() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException, DatatypeConfigurationException {
        OperationRequest request = new DefaultOperationRequest.Builder()
                .clientTimeoutDuration(DatatypeFactory.newInstance().newDuration(1000))
                .inputArguments(List.of(
                        new DefaultOperationVariable.Builder()
                                .value(newProperty())
                                .build()))
                .build();
        OperationResult expected = new DefaultOperationResult.Builder()
                .success(true)
                .executionState(ExecutionState.COMPLETED)
                .outputArguments(List.of(
                        new DefaultOperationVariable.Builder()
                                .value(newProperty())
                                .build()))
                .build();
        IdShortPath idShortPath = new IdShortPath.Builder()
                .idShort("list")
                .index(1)
                .idShort("operation")
                .build();
        stubFor(post(urlPathEqualTo(String.format("/api/v3.0/submodel/submodel-elements/%s/invoke", encodeForUrl(idShortPath))))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(ContentType.APPLICATION_JSON.getMimeType()))
                .withRequestBody(equalToJson(serializer.write(request)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(serializer.write(expected))));
        OperationResult actual = client.invokeOperationSync(
                idShortPath,
                request.getInputArguments(),
                request.getClientTimeoutDuration());
        assertEquals(expected, actual);
    }


    private static String encodeForUrl(IdShortPath idShortPath) {
        return UriUtils.encodePath(idShortPath.toString(), DEFAULT_CHARSET);
    }
}
