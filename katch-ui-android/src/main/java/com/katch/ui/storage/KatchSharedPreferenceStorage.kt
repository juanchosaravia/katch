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
package com.katch.ui.storage

import android.content.Context
import android.content.SharedPreferences
import com.katch.ui.KatchStatus

class KatchSharedPreferenceStorage(context: Context) : KatchStorage {

    companion object {
        private const val STORAGE_NAME = "com.katch.storage.status"
        private const val STATUS_STORAGE_KEY = "STATUS_STORAGE_KEY"
    }

    private val pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)

    override fun saveStatus(status: KatchStatus) {
        val editor = pref.edit()
        editor.clear().apply()
        editor.saveStatus(status.enabled)
        status.interceptorStatus.forEach { (key, index) ->
            editor.saveInterceptorResponseIndex(key, index)
        }
    }

    override fun getStatus(): KatchStatus? {
        if (!pref.contains(STATUS_STORAGE_KEY)) return null

        val statusEnabled = pref.getBoolean(STATUS_STORAGE_KEY, true)
        val statusInterceptors = pref.all.asSequence()
                .filter { it.value is Int }
                .map { it.key to (it.value as Int) }
                .toMap()

        return KatchStatus(statusEnabled, statusInterceptors)
    }

    override fun cleanAll() {
        pref.edit().clear().apply()
    }

    private fun SharedPreferences.Editor.saveStatus(enabled: Boolean) {
        putBoolean(STATUS_STORAGE_KEY, enabled)
    }

    private fun SharedPreferences.Editor.saveInterceptorResponseIndex(key: String, index: Int) {
        putInt(key, index).apply()
    }
}