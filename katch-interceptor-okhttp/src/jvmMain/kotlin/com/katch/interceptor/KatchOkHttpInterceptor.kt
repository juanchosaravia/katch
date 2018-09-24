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
package com.katch.interceptor

import com.katch.core.Katch
import com.katch.core.model.KatchRequest
import com.katch.core.model.KatchVerb
import okhttp3.*

class KatchOkHttpInterceptor(val katch: Katch) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (katch.config?.status == true) {
            // katch enabled
            val verb = KatchVerb.valueOf(request.method())
            val response = katch.intercept(KatchRequest(request.url().toString(), verb))
            response?.let {
                val bodyString = it.bodyProvider?.getBodyString().orEmpty()
                return Response.Builder()
                    .code(it.statusCode)
                    .message(bodyString)
                    .request(request)
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), bodyString))
                    .addHeader("content-type", "application/json")
                    .build()
            }
        }
        return chain.proceed(request)
    }
}