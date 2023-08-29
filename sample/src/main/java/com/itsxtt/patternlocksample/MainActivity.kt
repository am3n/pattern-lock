/**
 * Copyright 2018 itsxtt
 * Copyright 2023 Mobi Lab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itsxtt.patternlocksample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_PATTERN_TYPE = "type"

        const val TYPE_DEFAULT = 0
        const val TYPE_WITH_INDICATOR = 1
        const val TYPE_JD_STYLE = 2
        const val TYPE_9x9 = 3
        const val TYPE_SECURE_MODE = 4
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.defaultBtn).setOnClickListener { startPatternActivity(TYPE_DEFAULT) }
        findViewById<View>(R.id.jdStyleBtn).setOnClickListener { startPatternActivity(TYPE_JD_STYLE) }
        findViewById<View>(R.id.indicatorBtn).setOnClickListener { startPatternActivity(TYPE_WITH_INDICATOR) }
        findViewById<View>(R.id.nineBtn).setOnClickListener { startPatternActivity(TYPE_9x9) }
        findViewById<View>(R.id.secureModeBtn).setOnClickListener { startPatternActivity(TYPE_SECURE_MODE) }
    }

    private fun startPatternActivity(type: Int) {
        val intent = Intent(this, PatternLockActivity::class.java)
        intent.putExtra(KEY_PATTERN_TYPE, type)
        startActivity(intent)
    }
}
