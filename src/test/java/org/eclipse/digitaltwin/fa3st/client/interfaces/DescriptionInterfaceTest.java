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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.eclipse.digitaltwin.fa3st.client.exception.ClientException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.ApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.ServiceDescription;
import org.eclipse.digitaltwin.fa3st.common.model.ServiceSpecificationProfile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class DescriptionInterfaceTest {

    private static final ApiSerializer serializer = new JsonApiSerializer();
    private static DescriptionInterface client;

    @Rule
    public WireMockRule server = new WireMockRule();

    @Before
    public void setup() throws IOException {
        client = new DescriptionInterface(URI.create(server.url("/api/v3.0")));
    }


    @Test
    public void testGet() throws SerializationException, InterruptedException, ClientException, UnsupportedModifierException {
        ServiceDescription expected = ServiceDescription.builder()
                .profile(ServiceSpecificationProfile.AAS_FULL)
                .profile(ServiceSpecificationProfile.AAS_REPOSITORY_FULL)
                .build();
        stubFor(get(urlPathEqualTo("/api/v3.0/description"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(serializer.write(expected))));
        ServiceDescription actual = client.get();
        assertEquals(expected, actual);
    }
}
