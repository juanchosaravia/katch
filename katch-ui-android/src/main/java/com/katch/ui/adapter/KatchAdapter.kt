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
package com.katch.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.katch.ui.R
import com.katch.ui.inflate

class KatchAdapter(private val listener: Action)
    : RecyclerView.Adapter<KatchAdapter.ResponseStatusViewHolder>() {

    interface Action {
        fun onResponseSelected(response: ResponseModel)
    }

    private val items: MutableList<InterceptorModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResponseStatusViewHolder(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ResponseStatusViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setInterceptors(interceptors: List<InterceptorModel>) {
        items.addAll(interceptors)
        notifyDataSetChanged()
    }

    inner class ResponseStatusViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.activity_katch_config_item)) {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvSubtitle = itemView.findViewById<TextView>(R.id.tvSubtitle)
        private val rgResponses = itemView.findViewById<RadioGroup>(R.id.rgResponses)

        fun bind(interceptor: InterceptorModel) {
            tvTitle.text = interceptor.title
            interceptor.subtitle?.let { tvSubtitle.text = it }

            for ((i, response) in interceptor.responses.withIndex()) {
                val radioButton = RadioButton(itemView.context)
                radioButton.text = response.title
                radioButton.tag = response
                radioButton.id = i + 100
                radioButton.isChecked = response.selected
                rgResponses.addView(radioButton)
            }
            rgResponses.setOnCheckedChangeListener { group, checkedId ->
                listener.onResponseSelected(group.findViewById<RadioButton>(checkedId).tag as ResponseModel)
            }
        }
    }
}