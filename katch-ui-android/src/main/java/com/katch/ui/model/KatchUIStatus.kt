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
package com.katch.ui.model

import com.katch.ui.adapter.InterceptorModel

sealed class KatchUIStatus {
    class Init(val enabled: Boolean,
               val interceptors: List<InterceptorModel>) : KatchUIStatus()

    object Enabled : KatchUIStatus()
    object Disabled : KatchUIStatus()
    object ErrorProvider : KatchUIStatus()
    object ErrorConfig : KatchUIStatus()
}