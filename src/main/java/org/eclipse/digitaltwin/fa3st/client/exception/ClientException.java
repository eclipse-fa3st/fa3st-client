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

/**
 * Parent Exception that is thrown if serialization fails or the server responds with an error.
 */
public abstract class ClientException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    protected ClientException(String message) {
        super(message);
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    protected ClientException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    protected ClientException(Throwable cause) {
        super(cause);
    }
}
