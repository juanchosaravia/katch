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
package com.katch.ui

import com.katch.core.model.KatchInterceptor

class KatchStatus(var enabled: Boolean,
                  val interceptorStatus: Map<String, Int> = mutableMapOf()) {

    /**
     * Returns the index of the selected response and 0 is for none of them
     */
    fun getSelectedResponseIndex(interceptor: KatchInterceptor): Int {
        // adjust model index with UI model index
        // if no status found, return the value set in the interceptor
        return interceptorStatus[interceptor.identifier] ?: interceptor.selectedResponseIndexUI
    }
}