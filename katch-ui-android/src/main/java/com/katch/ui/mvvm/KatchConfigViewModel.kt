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
package com.katch.ui.mvvm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.katch.core.model.KatchConfig
import com.katch.ui.KatchStatus
import com.katch.ui.KatchUI
import com.katch.ui.adapter.InterceptorModel
import com.katch.ui.adapter.ResponseModel
import com.katch.ui.model.KatchUIStatus
import com.katch.ui.selectedResponseIndexUI
import com.katch.ui.storage.KatchStorage

class KatchConfigViewModel
internal constructor(private val katchStorage: KatchStorage) : ViewModel() {

    companion object {
        private const val SERVER_POS = 0
    }

    val uiStatus = MutableLiveData<KatchUIStatus>()

    private lateinit var config: KatchConfig

    fun start() {
        val katch = KatchUI.getKatch()
        if (katch == null) {
            uiStatus.value = KatchUIStatus.ErrorProvider
            return
        }
        val _config = katch.config
        if (_config == null) {
            uiStatus.value = KatchUIStatus.ErrorConfig
            return
        }
        config = _config

        val status = katchStorage.getStatus()

        val uiInterceptors = config.interceptors.map { interceptor ->
            val path = interceptor.path
            // if no config then return interceptor value + 1 to adjust to ui model
            val selectedResponseIndex = status?.getSelectedResponseIndex(interceptor) ?: interceptor.selectedResponseIndexUI
            val uiResponses = mutableListOf<ResponseModel>()
            // first fixed option for server calls:
            uiResponses.add(ResponseModel("Real Server", SERVER_POS,
                    interceptor.identifier, selectedResponseIndex == SERVER_POS))
            // add other options from responses:
            uiResponses.addAll(interceptor.responses.mapIndexed { i, response ->
                val uiIndex = i + 1 // as 0 is reserved for fixed first item
                ResponseModel(response.name, uiIndex, interceptor.identifier, uiIndex == selectedResponseIndex)
            })

            InterceptorModel(interceptor.name, path, uiResponses)
        }

        uiStatus.value = KatchUIStatus.Init(config.status, uiInterceptors)
    }

    /**
     * Reset config to default values
     */
    fun resetConfig() {
        katchStorage.cleanAll()
    }

    /**
     * Every time we update one response, we update the entire Status map.
     * If there is no status for an interceptor, it will set the Real Server Response as default.
     */
    fun updateResponseSelected(response: ResponseModel) {
        val status = katchStorage.getStatus()
        val statusMap = mutableMapOf<String, Int>()
        config.interceptors.forEach { interceptor ->
            if (interceptor.identifier == response.interceptorId) {
                // override previous status
                statusMap[interceptor.identifier] = response.index
                // FIXME we are updating the instance but maybe we should recreate the config and set it again to Katch
                interceptor.selectedResponseIndex = response.index - 1 // adjust with -1 as "no response" selected is -1
            } else {
                // set previous state and by default select 0 which is real server
                statusMap[interceptor.identifier] = status?.getSelectedResponseIndex(interceptor) ?: interceptor.selectedResponseIndexUI
            }
        }
        katchStorage.saveStatus(KatchStatus(status?.enabled ?: true, statusMap))
    }

    fun enableStatus(isEnabled: Boolean) {
        katchStorage.getStatus()?.apply { enabled = isEnabled }?.let {
            katchStorage.saveStatus(it)
        }
    }
}