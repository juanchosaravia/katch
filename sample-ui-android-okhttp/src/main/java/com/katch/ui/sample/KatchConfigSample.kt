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
package com.katch.ui.sample

import com.katch.core.model.KatchConfig
import com.katch.core.model.KatchInterceptor
import com.katch.core.model.KatchResponse
import com.katch.core.model.Verb

object KatchConfigSample {

    fun getConfig() = KatchConfig(
        interceptors = arrayOf(
            KatchInterceptor(
                "Sample 1", "/path/to/some/api",
                responses = arrayOf(
                    KatchResponse("mock success", 200),
                    KatchResponse("mock success 2", 201),
                    KatchResponse("mock error", 500)
                ),
                verb = Verb.GET
            ),
            KatchInterceptor(
                "Sample 2", "/path/to/another/api",
                responses = arrayOf(
                    KatchResponse("mock success", 200),
                    KatchResponse("mock error", 500)
                ),
                verb = Verb.GET,
                selectedResponseIndex = 1 // by default first mock
            )
        )
    )
}