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

enum class Matcher(private val matcherFunction: (String, String) -> Boolean) {
    CONTAINS({ url: String, path: String ->
        url.contains(path)
    }),
    END_WITH({ url: String, path: String ->
        url.endsWith(path)
    }),
    REGEX({ url: String, path: String ->
        path.toRegex().containsMatchIn(url)
    });

    fun matches(url: String, path: String): Boolean {
        return matcherFunction(url, path)
    }
}