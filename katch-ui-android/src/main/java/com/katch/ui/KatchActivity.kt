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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.katch.ui.adapter.KatchAdapter
import com.katch.ui.adapter.ResponseModel
import com.katch.ui.model.KatchUIStatus
import com.katch.ui.mvvm.KatchConfigViewModel
import com.katch.ui.mvvm.KatchViewModelFactory
import com.katch.ui.storage.KatchSharedPreferenceStorage
import kotlinx.android.synthetic.main.activity_katch_config.*

class KatchActivity : AppCompatActivity(), KatchAdapter.Action {

    companion object {
        @JvmStatic
        fun createIntent(context: Context) = Intent(context, KatchActivity::class.java)
    }

    private lateinit var recyclerViewKatchConfig: RecyclerView
    private lateinit var katchAdapter: KatchAdapter
    private var viewModel: KatchConfigViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katch_config)
        setSupportActionBar(toolbar)

        katchAdapter = KatchAdapter(this@KatchActivity)
        recyclerViewKatchConfig = findViewById<RecyclerView>(R.id.rvStatus).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = katchAdapter
        }

        viewModel = ViewModelProviders.of(this,
                KatchViewModelFactory(KatchSharedPreferenceStorage(this)))
                .get(KatchConfigViewModel::class.java)

        viewModel?.uiStatus?.observe(this, Observer { status ->
            when (status) {
                is KatchUIStatus.Init -> init(status)
                is KatchUIStatus.ErrorConfig,
                is KatchUIStatus.ErrorProvider -> showError()
            }
        })
        viewModel?.start()
    }

    override fun onResponseSelected(response: ResponseModel) {
        viewModel?.updateResponseSelected(response)
    }

    private fun init(status: KatchUIStatus.Init) {
        katchAdapter.setInterceptors(status.interceptors)
    }

    private fun showError() {
        toast("Missed Katch instance in KatchUI or config not set in Katch instance.")
    }

}
