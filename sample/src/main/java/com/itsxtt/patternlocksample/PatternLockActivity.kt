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

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.itsxtt.patternlock.PatternLockView


class PatternLockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var type = intent.getIntExtra(MainActivity.KEY_PATTERN_TYPE, MainActivity.TYPE_DEFAULT)
        when(type) {
            MainActivity.TYPE_DEFAULT -> {
                setContentView(R.layout.activity_pattern_default)
                findViewById<PatternLockView>(R.id.defaultPatternLockView).setOnPatternListener(listener)
            }
            MainActivity.TYPE_JD_STYLE -> {
                setContentView(R.layout.activity_pattern_jd)
                findViewById<PatternLockView>(R.id.jdPatternLockView).setOnPatternListener(listener)
            }
            MainActivity.TYPE_WITH_INDICATOR -> {
                setContentView(R.layout.activity_pattern_with_indicator)
                findViewById<PatternLockView>(R.id.indicatorPatternLockView).setOnPatternListener(listener)
            }
            MainActivity.TYPE_9x9 -> {
                setContentView(R.layout.activity_pattern_9x9)
                findViewById<PatternLockView>(R.id.ninePatternLockView).setOnPatternListener(listener)
            }
            MainActivity.TYPE_SECURE_MODE -> {
                setContentView(R.layout.activity_pattern_default)
                findViewById<PatternLockView>(R.id.defaultPatternLockView).enableSecureMode()
                        findViewById<PatternLockView>(R.id.defaultPatternLockView).setOnPatternListener(listener)
            }
        }

    }

    private var listener  = object : PatternLockView.OnPatternListener {

        override fun onStarted() {
            super.onStarted()
        }

        override fun onProgress(ids: ArrayList<Int>) {
            super.onProgress(ids)
        }

        override fun onComplete(ids: ArrayList<Int>): Boolean {
            var isCorrect = TextUtils.equals("123", getPatternString(ids))
            var tip: String
            if (isCorrect) {
                tip = "correct:" + getPatternString(ids)
            } else {
                tip = "error:" + getPatternString(ids)
            }
            //Toast.makeText(this@PatternLockActivity, tip, Toast.LENGTH_SHORT).show()
            return isCorrect
        }
    }

    private fun getPatternString(ids: ArrayList<Int>) : String {
        var result = ""
        for (id in ids) {
            result += id.toString()
        }
        return result
    }
}
