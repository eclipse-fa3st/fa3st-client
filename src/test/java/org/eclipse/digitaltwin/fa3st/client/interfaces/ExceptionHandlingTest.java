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
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static org.junit.Assert.assertThrows;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.fa3st.client.exception.*;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ExceptionHandlingTest {
    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static SubmodelRepositoryInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new SubmodelRepositoryInterface(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testGetListBadRequestException() {
        mockStatusCode(400);
        assertThrows(BadRequestException.class, () -> {
            client.getAll();
        });
    }


    @Test
    public void testPutForbiddenException() {
        mockStatusCode(403);

        assertThrows(ForbiddenException.class, () -> {
            client.put("path", new DefaultSubmodel());
        });
    }


    @Test
    public void testGetNotFoundException() {
        mockStatusCode(404);
        assertThrows(NotFoundException.class, () -> {
            client.getSubmodelInterface("wrongId").get();
        });
    }


    @Test
    public void testPostConflictException() {
        mockStatusCode(409);
        assertThrows(ConflictException.class, () -> {
            client.post(new DefaultSubmodel.Builder().id("id").build());
        });
    }


    @Test
    public void testDeleteInternalServerErrorException() {
        mockStatusCode(500);

        assertThrows(InternalServerErrorException.class, () -> {
            client.delete("path");
        });
    }


    private void mockStatusCode(int code) {
        server.stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(code)));
    }
}
