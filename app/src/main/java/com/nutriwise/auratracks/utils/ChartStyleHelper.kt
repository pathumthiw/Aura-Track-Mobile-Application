package com.nutriwise.auratracks.utils

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nutriwise.auratracks.R


object ChartStyleHelper {
    
    // App theme colors
    private const val PRIMARY_BLUE = "#2196F3"
    private const val PRIMARY_BLUE_LIGHT = "#BBDEFB"
    private const val SECONDARY_GREEN = "#4CAF50"
    private const val ACCENT_ORANGE = "#FF9800"
    private const val ACCENT_PURPLE = "#9C27B0"
    private const val ACCENT_TEAL = "#009688"
    private const val TEXT_PRIMARY = "#212121"
    private const val TEXT_SECONDARY = "#757575"
    private const val GRID_COLOR = "#F0F0F0"
    private const val AXIS_COLOR = "#E0E0E0"
    
    // Chart color palette
    val CHART_COLORS = listOf(
        PRIMARY_BLUE,
        SECONDARY_GREEN,
        ACCENT_ORANGE,
        ACCENT_PURPLE,
        ACCENT_TEAL,
        "#607D8B", // Blue Grey
        "#795548", // Brown
        "#E91E63", // Pink
        "#3F51B5", // Indigo
        "#00BCD4"  // Cyan
    )
    

    fun styleLineChart(chart: com.github.mikephil.charting.charts.LineChart) {
        // Basic chart configuration
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setDragEnabled(true)
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)
        
        // Legend styling
        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 12f
        legend.textColor = Color.parseColor(TEXT_SECONDARY)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        
        // X-axis styling
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = Color.parseColor(AXIS_COLOR)
        xAxis.granularity = 1f
        
        // Left Y-axis styling
        val leftAxis = chart.axisLeft
        leftAxis.textSize = 12f
        leftAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor(GRID_COLOR)
        leftAxis.axisLineColor = Color.parseColor(AXIS_COLOR)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.valueFormatter = PercentageValueFormatter()
        
        // Disable right Y-axis
        chart.axisRight.isEnabled = false
        
        // Animation
        chart.animateX(1000)
    }
    

    fun styleBarChart(chart: com.github.mikephil.charting.charts.BarChart) {
        // Basic chart configuration
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setDragEnabled(true)
        chart.setScaleEnabled(true)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        
        // Legend styling
        val legend = chart.legend
        legend.form = Legend.LegendForm.SQUARE
        legend.textSize = 12f
        legend.textColor = Color.parseColor(TEXT_SECONDARY)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        
        // X-axis styling
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = Color.parseColor(AXIS_COLOR)
        xAxis.granularity = 1f
        xAxis.setLabelCount(5, false)
        
        // Left Y-axis styling
        val leftAxis = chart.axisLeft
        leftAxis.textSize = 12f
        leftAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor(GRID_COLOR)
        leftAxis.axisLineColor = Color.parseColor(AXIS_COLOR)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.valueFormatter = PercentageValueFormatter()
        
        // Disable right Y-axis
        chart.axisRight.isEnabled = false
        
        // Animation
        chart.animateY(1000)
    }
    

    fun stylePieChart(chart: com.github.mikephil.charting.charts.PieChart) {
        // Basic chart configuration
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setDrawHoleEnabled(true)
        chart.holeRadius = 40f
        chart.transparentCircleRadius = 45f
        chart.setDrawCenterText(true)
        chart.centerText = "Categories"
        chart.setCenterTextSize(14f)
        chart.setCenterTextColor(Color.parseColor(TEXT_SECONDARY))
        
        // Legend styling
        val legend = chart.legend
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 12f
        legend.textColor = Color.parseColor(TEXT_SECONDARY)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        
        // Animation
        chart.animateY(1000)
    }
    

    fun styleRadarChart(chart: com.github.mikephil.charting.charts.RadarChart) {
        // Basic chart configuration
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setRotationEnabled(false)
        chart.setDrawWeb(true)
        
        // Web styling
        chart.webLineWidth = 1f
        chart.webColor = Color.parseColor(GRID_COLOR)
        chart.webLineWidthInner = 1f
        chart.webColorInner = Color.parseColor(GRID_COLOR)
        chart.webAlpha = 100
        
        // Legend styling
        val legend = chart.legend
        legend.textSize = 12f
        legend.textColor = Color.parseColor(TEXT_SECONDARY)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        
        // Y-axis styling
        val yAxis = chart.yAxis
        yAxis.textSize = 10f
        yAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        yAxis.setDrawLabels(true)
        yAxis.setLabelCount(5, false)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.valueFormatter = PercentageValueFormatter()
        
        // X-axis styling
        val xAxis = chart.xAxis
        xAxis.textSize = 10f
        xAxis.textColor = Color.parseColor(TEXT_SECONDARY)
        
        // Animation
        chart.animateXY(1000, 1000)
    }
    

    fun createStyledLineDataSet(entries: List<com.github.mikephil.charting.data.Entry>, label: String, color: String): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = Color.parseColor(color)
            valueTextColor = Color.parseColor(TEXT_SECONDARY)
            valueTextSize = 10f
            lineWidth = 3f
            circleRadius = 6f
            setCircleColor(Color.parseColor(color))
            setCircleHoleColor(Color.WHITE)
            setDrawFilled(true)
            fillColor = Color.parseColor(color)
            fillAlpha = 30
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
    }
    

    fun createStyledBarDataSet(entries: List<com.github.mikephil.charting.data.BarEntry>, label: String, colors: List<String>): BarDataSet {
        return BarDataSet(entries, label).apply {
            this.colors = colors.map { Color.parseColor(it) }
            valueTextColor = Color.parseColor(TEXT_SECONDARY)
            valueTextSize = 10f
            setDrawValues(true)
        }
    }
    

    fun createStyledPieDataSet(entries: List<com.github.mikephil.charting.data.PieEntry>, label: String, colors: List<String>): PieDataSet {
        return PieDataSet(entries, label).apply {
            this.colors = colors.map { Color.parseColor(it) }
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            setDrawValues(true)
        }
    }
    

    fun createStyledRadarDataSet(entries: List<com.github.mikephil.charting.data.RadarEntry>, label: String, color: String): RadarDataSet {
        return RadarDataSet(entries, label).apply {
            this.color = Color.parseColor(color)
            fillColor = Color.parseColor(color)
            fillAlpha = 50
            lineWidth = 2f
            setDrawFilled(true)
            setDrawValues(false)
        }
    }
    

    fun getCategoryColor(category: String): String {
        return when (category.lowercase()) {
            "health", "fitness", "exercise" -> SECONDARY_GREEN
            "productivity", "work", "focus" -> PRIMARY_BLUE
            "mindfulness", "meditation", "yoga" -> ACCENT_PURPLE
            "social", "friends", "family" -> ACCENT_ORANGE
            "learning", "education", "reading" -> ACCENT_TEAL
            else -> PRIMARY_BLUE
        }
    }
    

    fun getPerformanceColor(completionRate: Float): String {
        return when {
            completionRate >= 80 -> SECONDARY_GREEN
            completionRate >= 60 -> ACCENT_ORANGE
            else -> "#F44336" // Red
        }
    }
    

    private class PercentageValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "${value.toInt()}%"
        }
    }
}
