/*
 * Copyright 2018 Juan Ignacio Saravia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.katch.core.model

class Interceptor(
        val name: String,
        val path: String,
        val responses: Array<Response>, // using array for js
        val verb: Verb = Verb.ANY,
        val matcher: Matcher = Matcher.CONTAINS,
        var selectedResponseIndex: Int = NO_RESPONSE,
        val delay: Long = 0
) {
    companion object {
        const val NO_RESPONSE = -1
    }

    val identifier = name + path + verb

    fun validate(request: Request): Boolean {
        return matcher.matches(request.url, path)
                && (this.verb == Verb.ANY
                || request.verb == Verb.ANY
                || this.verb == request.verb)
    }

    fun getSelectedResponse(): Response? {
        if (selectedResponseIndex == NO_RESPONSE
                || selectedResponseIndex >= responses.size) return null
        return responses[selectedResponseIndex]
    }
}