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
package com.katch.core

import com.katch.core.model.*

class RealKatch(
        override var config: Config? = null,
        // TODO move this logger to a multiplatform project
        private val logger: Logger? = null) : Katch {

    override fun intercept(request: Request): Response? {
        val localConfig = config
        if (localConfig == null || !localConfig.status) {
            logger?.log("Katch is disabled.")
            return null
        }

        val interceptor = localConfig.interceptors.firstOrNull { it.validate(request) }
        if (interceptor == null) {
            logger?.log("Katch: No interceptor found for Request: $request")
            return null
        }

        val response = interceptor.getSelectedResponse()
        if (response == null) {
            logger?.log("Katch: Request intercepted but no response selected or configured. Request: $request")
            return null
        }

        val delay = resolveDelay(interceptor, response)
        return response.copy(delay = delay)
    }

    private fun resolveDelay(interceptor: Interceptor, response: Response): Long {
        if (response.delay > 0) return response.delay
        if (interceptor.delay > 0) return interceptor.delay
        return config?.globalDelay ?: 0
    }

}