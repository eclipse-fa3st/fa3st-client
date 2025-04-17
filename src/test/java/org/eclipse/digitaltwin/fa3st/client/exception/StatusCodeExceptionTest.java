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
package org.eclipse.digitaltwin.fa3st.client.exception;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertThrows;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.fa3st.client.interfaces.SubmodelRepositoryInterface;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class StatusCodeExceptionTest {
    @Rule
    public WireMockRule server = new WireMockRule();
    private static SubmodelRepositoryInterface concreteSubclass;

    @Before
    public void setup() throws IOException {
        concreteSubclass = new SubmodelRepositoryInterface(URI.create(server.url("api/v3.0")));
    }


    @Test
    public void testBadRequest() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(400)));
        assertThrows(BadRequestException.class, () -> concreteSubclass.getAll());
        assertThrows(BadRequestException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testUnauthorized() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(401)));
        assertThrows(UnauthorizedException.class, () -> concreteSubclass.getAll());
        assertThrows(UnauthorizedException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testForbidden() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(403)));
        assertThrows(ForbiddenException.class, () -> concreteSubclass.getAll());
        assertThrows(ForbiddenException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testNotFound() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(404)));
        assertThrows(NotFoundException.class, () -> concreteSubclass.getAll());
        assertThrows(NotFoundException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testMethodNotAllowed() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(405)));
        assertThrows(UnsupportedStatusCodeException.class, () -> concreteSubclass.getAll());
        assertThrows(MethodNotAllowedException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testConflict() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(409)));
        assertThrows(UnsupportedStatusCodeException.class, () -> concreteSubclass.getAll());
        assertThrows(ConflictException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }


    @Test
    public void testInternalServerError() {
        stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(500)));
        assertThrows(InternalServerErrorException.class, () -> concreteSubclass.getAll());
        assertThrows(InternalServerErrorException.class, () -> concreteSubclass.post(new DefaultSubmodel()));
    }
}
