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
package com.itsxtt.patternlock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View

internal class Cell(context: Context,
                    var index: Int,
                    private var regularCellBackground: Drawable?,
                    private var regularDotColor: Int,
                    private var regularDotRadiusRatio: Float,
                    private var selectedCellBackground: Drawable?,
                    private var selectedDotColor: Int,
                    private var selectedDotRadiusRatio: Float,
                    private var errorCellBackground: Drawable?,
                    private var errorDotColor: Int,
                    private var errorDotRadiusRatio: Float,
                    private var successCellBackground: Drawable?,
                    private var successDotColor: Int,
                    private var successDotRadiusRatio: Float,
                    private var lineStyle: Int,
                    private var regularLineColor: Int,
                    private var errorLineColor: Int,
                    private var successLineColor: Int,
                    private var columnCount: Int,
                    private var indicatorSizeRatio: Float) : View(context) {

    private var currentState: State = State.REGULAR
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentDegree: Float = -1f
    private var indicatorPath: Path = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var cellWidth = MeasureSpec.getSize(widthMeasureSpec) / columnCount
        var cellHeight = cellWidth
        setMeasuredDimension(cellWidth, cellHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        when(currentState) {
            State.REGULAR -> drawDot(canvas, regularCellBackground, regularDotColor, regularDotRadiusRatio)
            State.SELECTED -> drawDot(canvas, selectedCellBackground, selectedDotColor, selectedDotRadiusRatio)
            State.ERROR -> drawDot(canvas, errorCellBackground, errorDotColor, errorDotRadiusRatio)
            State.SUCCESS -> drawDot(canvas, successCellBackground, successDotColor, successDotRadiusRatio)
        }
    }

    private fun drawDot(canvas: Canvas?,
                        background: Drawable?,
                        dotColor: Int,
                        radiusRation: Float) {
        var radius = getRadius()
        var centerX = width / 2
        var centerY = height / 2

        if (background is ColorDrawable) {
            paint.color = background.color
            paint.style = Paint.Style.FILL
            canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
        } else {
            background?.setBounds(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
            background?.draw(canvas!!)
        }

        paint.color = dotColor
        paint.style = Paint.Style.FILL
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius * radiusRation, paint)

        if (lineStyle == PatternLockView.LINE_STYLE_INDICATOR &&
                (currentState == State.SELECTED || currentState == State.ERROR)) {
            drawIndicator(canvas)
        }
    }

    private fun drawIndicator(canvas: Canvas?) {
        if (currentDegree != -1f) {
            if (indicatorPath.isEmpty) {
                indicatorPath.fillType = Path.FillType.WINDING
                val radius = getRadius()
                val height = radius * indicatorSizeRatio
                indicatorPath.moveTo((width / 2).toFloat() , radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + paddingTop)
                indicatorPath.lineTo((width /2).toFloat() - height, radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop)
                indicatorPath.lineTo((width / 2).toFloat() + height, radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop)
                indicatorPath.close()
            }

            if (currentState == State.SELECTED) {
                paint.color = regularLineColor
            } else if (currentState == State.SUCCESS) {
                paint.color = successLineColor
            } else {
                paint.color = errorLineColor
            }
            paint.style = Paint.Style.FILL

            canvas?.save()
            canvas?.rotate(currentDegree, (width / 2).toFloat(), (height / 2).toFloat())
            canvas?.drawPath(indicatorPath, paint)
            canvas?.restore()
        }
    }

    fun getRadius() : Int {
        return (Math.min(width, height) - (paddingLeft + paddingRight)) / 2
    }


    fun getCenter() : Point {
        var point = Point()
        point.x = left + (right - left) / 2
        point.y = top + (bottom - top) / 2
        return point
    }

    fun setState(state: State) {
        currentState = state
        invalidate()
    }

    fun setDegree(degree: Float) {
        currentDegree = degree
    }

    fun reset() {
        setState(State.REGULAR)
        currentDegree = -1f
    }

 }
