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

import java.net.http.HttpResponse;


/**
 * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been
 * provided.
 * The response must include a WWW-Authenticate header field containing a challenge applicable to the requested
 * resource.
 */
public class UnauthorizedException extends StatusCodeException {

    /**
     * Constructs a new exception.
     *
     * @param response the response representing the exception
     */
    public UnauthorizedException(HttpResponse<?> response) {
        super(response);
    }
}
