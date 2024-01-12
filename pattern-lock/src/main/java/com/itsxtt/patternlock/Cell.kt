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
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.min

internal class Cell @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var index: Int = 0

    private var regularCellBackground: Drawable? = null
    private var regularDotColor: Int = 0
    private var regularDotRadiusRatio: Float = 0f
    private var selectedCellBackground: Drawable? = null
    private var selectedDotColor: Int = 0
    private var selectedDotRadiusRatio: Float = 0f
    private var errorCellBackground: Drawable? = null
    private var errorDotColor: Int = 0
    private var errorDotRadiusRatio: Float = 0f
    private var successCellBackground: Drawable? = null
    private var successDotColor: Int = 0
    private var successDotRadiusRatio: Float = 0f
    private var lineStyle: Int = 0
    private var regularLineColor: Int = 0
    private var errorLineColor: Int = 0
    private var successLineColor: Int = 0
    private var columnCount: Int = 0
    private var indicatorSizeRatio: Float = 0f

    private var currentState: State = State.REGULAR
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentDegree: Float = -1f
    private var indicatorPath: Path = Path()

    fun init(
        index: Int,
        regularCellBackground: Drawable?,
        regularDotColor: Int,
        regularDotRadiusRatio: Float,
        selectedCellBackground: Drawable?,
        selectedDotColor: Int,
        selectedDotRadiusRatio: Float,
        errorCellBackground: Drawable?,
        errorDotColor: Int,
        errorDotRadiusRatio: Float,
        successCellBackground: Drawable?,
        successDotColor: Int,
        successDotRadiusRatio: Float,
        lineStyle: Int,
        regularLineColor: Int,
        errorLineColor: Int,
        successLineColor: Int,
        columnCount: Int,
        indicatorSizeRatio: Float
    ) {
        this.index = index
        this.regularCellBackground = regularCellBackground
        this.regularDotColor = regularDotColor
        this.regularDotRadiusRatio = regularDotRadiusRatio
        this.selectedCellBackground = selectedCellBackground
        this.selectedDotColor = selectedDotColor
        this.selectedDotRadiusRatio = selectedDotRadiusRatio
        this.errorCellBackground = errorCellBackground
        this.errorDotColor = errorDotColor
        this.errorDotRadiusRatio = errorDotRadiusRatio
        this.successCellBackground = successCellBackground
        this.successDotColor = successDotColor
        this.successDotRadiusRatio = successDotRadiusRatio
        this.lineStyle = lineStyle
        this.regularLineColor = regularLineColor
        this.errorLineColor = errorLineColor
        this.successLineColor = successLineColor
        this.columnCount = columnCount
        this.indicatorSizeRatio = indicatorSizeRatio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cellWidth = MeasureSpec.getSize(widthMeasureSpec) / columnCount
        setMeasuredDimension(cellWidth, cellWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        when (currentState) {
            State.REGULAR -> drawDot(canvas, regularCellBackground, regularDotColor, regularDotRadiusRatio)
            State.SELECTED -> drawDot(canvas, selectedCellBackground, selectedDotColor, selectedDotRadiusRatio)
            State.ERROR -> drawDot(canvas, errorCellBackground, errorDotColor, errorDotRadiusRatio)
            State.SUCCESS -> drawDot(canvas, successCellBackground, successDotColor, successDotRadiusRatio)
        }
    }

    private fun drawDot(
        canvas: Canvas?,
        background: Drawable?,
        dotColor: Int,
        radiusRation: Float
    ) {
        val radius = getRadius()
        val centerX = width / 2
        val centerY = height / 2

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
            (currentState == State.SELECTED || currentState == State.ERROR)
        ) {
            drawIndicator(canvas)
        }
    }

    private fun drawIndicator(canvas: Canvas?) {
        if (currentDegree != -1f) {
            if (indicatorPath.isEmpty) {
                indicatorPath.fillType = Path.FillType.WINDING
                val radius = getRadius()
                val height = radius * indicatorSizeRatio
                indicatorPath.moveTo(
                    (width / 2).toFloat(),
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + paddingTop
                )
                indicatorPath.lineTo(
                    (width / 2).toFloat() - height,
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop
                )
                indicatorPath.lineTo(
                    (width / 2).toFloat() + height,
                    radius * (1 - selectedDotRadiusRatio - indicatorSizeRatio) / 2 + height + paddingTop
                )
                indicatorPath.close()
            }

            paint.color = when (currentState) {
                State.SELECTED -> regularLineColor
                State.SUCCESS -> successLineColor
                else -> errorLineColor
            }
            paint.style = Paint.Style.FILL

            canvas?.save()
            canvas?.rotate(currentDegree, (width / 2).toFloat(), (height / 2).toFloat())
            canvas?.drawPath(indicatorPath, paint)
            canvas?.restore()
        }
    }

    fun getRadius(): Int {
        return (min(width, height) - (paddingLeft + paddingRight)) / 2
    }


    fun getCenter(): Point {
        val point = Point()
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

    fun setSuccessDotColor(@ColorInt color: Int) {
        this.successDotColor = color
    }

}
