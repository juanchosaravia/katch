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

import android.content.Context
import com.katch.core.Katch
import com.katch.core.model.KatchConfig
import com.katch.ui.storage.KatchSharedPreferenceStorage

/**
 * Sets the Katch instance you want to configure and update from the [KatchActivity].
 */
object KatchUI {
    private var katch: Katch? = null

    fun getKatch() = katch

    fun launchActivity(context: Context, katch: Katch) {
        this.katch = katch
        katch.config?.let {
            applyStatusToConfig(context, it)
        }
        context.startActivity(KatchActivity.createIntent(context))
    }

    fun applyStatusToConfig(context: Context, katchConfig: KatchConfig) {
        // TODO we should make this storage configurable (unify with the one used in ViewModel
        val status = KatchSharedPreferenceStorage(context).getStatus() ?: return

        katchConfig.interceptors.forEach { interceptor ->
            // adjust ui index with real model
            interceptor.selectedResponseIndex = status.getSelectedResponseIndex(interceptor) - 1
        }
    }
}