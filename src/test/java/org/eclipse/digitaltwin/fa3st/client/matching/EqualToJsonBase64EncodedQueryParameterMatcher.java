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
package org.eclipse.digitaltwin.fa3st.client.matching;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.not;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;


public class EqualToJsonBase64EncodedQueryParameterMatcher implements ValueMatcher<Request> {

    private final String name;
    private final String expected;

    public EqualToJsonBase64EncodedQueryParameterMatcher(String parameterName, String expectedJson) {
        this.name = parameterName;
        this.expected = expectedJson;
    }


    @Override
    public MatchResult match(Request request) {

        if (!request.queryParameter(name).isPresent()
                || !request.queryParameter(name).isSingleValued()) {
            return MatchResult.noMatch();
        }
        String value = request.queryParameter(name).firstValue();
        try {
            String decodedValue = EncodingHelper.base64UrlDecode(value);
            return not(absent()).and(equalToJson(expected)).match(decodedValue);
        }
        catch (IllegalArgumentException e) {
            return MatchResult.noMatch();
        }
    }
}
