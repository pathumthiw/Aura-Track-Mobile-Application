package com.nutriwise.auratracks.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.nutriwise.auratracks.R
import com.nutriwise.auratracks.adapters.HabitPerformanceAdapter
import com.nutriwise.auratracks.data.SharedPreferencesHelper
import com.nutriwise.auratracks.databinding.FragmentHabitAnalyticsBinding
import com.nutriwise.auratracks.models.*
import com.nutriwise.auratracks.utils.HabitChartManager
import com.nutriwise.auratracks.utils.ChartStyleHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying comprehensive habit analytics with charts
 */
class HabitAnalyticsFragment : Fragment() {
    
    private var _binding: FragmentHabitAnalyticsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var chartManager: HabitChartManager
    private lateinit var habitPerformanceAdapter: HabitPerformanceAdapter
    
    private var currentTimePeriod = TimePeriod.MONTH
    private var overallAnalytics: OverallAnalytics? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            if (!isAdded || context == null) return
            
            prefsHelper = SharedPreferencesHelper(requireContext())
            chartManager = HabitChartManager(prefsHelper)
            
            setupToolbar()
            setupTimePeriodToggle()
            setupRecyclerView()
            setupCharts()
            loadAnalytics()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadAnalytics()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    
    private fun setupTimePeriodToggle() {
        binding.timePeriodToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentTimePeriod = when (checkedId) {
                    R.id.btn_week -> TimePeriod.WEEK
                    R.id.btn_month -> TimePeriod.MONTH
                    R.id.btn_quarter -> TimePeriod.QUARTER
                    R.id.btn_year -> TimePeriod.YEAR
                    else -> TimePeriod.MONTH
                }
                loadAnalytics()
            }
        }
    }
    
    private fun setupRecyclerView() {
        habitPerformanceAdapter = HabitPerformanceAdapter(emptyList())
        binding.habitPerformanceRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitPerformanceAdapter
        }
    }
    
    private fun setupCharts() {
        setupLineChart()
        setupBarChart()
        setupPieChart()
    }
    
    private fun setupLineChart() {
        ChartStyleHelper.styleLineChart(binding.lineChart)
    }
    
    private fun setupBarChart() {
        ChartStyleHelper.styleBarChart(binding.barChart)
    }
    
    private fun setupPieChart() {
        ChartStyleHelper.stylePieChart(binding.pieChart)
    }
    
    private fun loadAnalytics() {
        try {
            overallAnalytics = chartManager.generateOverallAnalytics(currentTimePeriod)
            overallAnalytics?.let { analytics ->
                if (analytics.totalHabits == 0) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    updateOverallStats(analytics)
                    updateCharts(analytics)
                    updateHabitPerformance(analytics.habitAnalytics)
                }
            } ?: showEmptyState()
        } catch (e: Exception) {
            e.printStackTrace()
            showEmptyState()
        }
    }
    
    private fun updateOverallStats(analytics: OverallAnalytics) {
        binding.tvTotalHabits.text = analytics.totalHabits.toString()
        binding.tvCompletionRate.text = "${analytics.averageCompletionRate.toInt()}%"
        binding.tvTotalCompletions.text = analytics.totalCompletions.toString()
        
        val trendText = when (analytics.monthlyTrend) {
            TrendDirection.IMPROVING -> "📈 Improving"
            TrendDirection.DECLINING -> "📉 Declining"
            TrendDirection.STABLE -> "➡️ Stable"
            TrendDirection.NO_DATA -> "📊 No Data"
        }
        binding.tvTrend.text = trendText
    }
    
    private fun updateCharts(analytics: OverallAnalytics) {
        updateLineChart(analytics)
        updateBarChart(analytics)
        updatePieChart(analytics)
    }
    
    private fun updateLineChart(analytics: OverallAnalytics) {
        if (analytics.habitAnalytics.isEmpty()) return
        
        // Use the best performing habit for the line chart
        val bestHabit = analytics.habitAnalytics.maxByOrNull { it.completionRate }
        if (bestHabit != null) {
            val chartConfig = chartManager.generateCompletionLineChart(bestHabit, currentTimePeriod)
            val dataPoints = chartConfig.dataPoints
            
            val entries = dataPoints.mapIndexed { index, point ->
                Entry(index.toFloat(), point.value)
            }
            
            val dataSet = ChartStyleHelper.createStyledLineDataSet(entries, bestHabit.habitName, bestHabit.category.color)
            
            val lineData = LineData(dataSet)
            binding.lineChart.data = lineData
            
            // Update X-axis labels
            val labels = dataPoints.map { it.label }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.lineChart.xAxis.setLabelCount(labels.size, false)
            
            binding.lineChart.invalidate()
        }
    }
    
    private fun updateBarChart(analytics: OverallAnalytics) {
        val chartConfig = chartManager.generateWeeklyBarChart(analytics)
        val dataPoints = chartConfig.dataPoints
        
        if (dataPoints.isEmpty()) return
        
        val entries = dataPoints.mapIndexed { index, point ->
            BarEntry(index.toFloat(), point.value)
        }
        
        val colors = dataPoints.map { it.color ?: "#2196F3" }
        val dataSet = ChartStyleHelper.createStyledBarDataSet(entries, "Completion Rate", colors)
        
        val barData = BarData(dataSet)
        binding.barChart.data = barData
        
        // Update X-axis labels
        val labels = dataPoints.map { it.label }
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.xAxis.setLabelCount(labels.size, false)
        
        binding.barChart.invalidate()
    }
    
    private fun updatePieChart(analytics: OverallAnalytics) {
        val chartConfig = chartManager.generateCategoryPieChart(analytics)
        val dataPoints = chartConfig.dataPoints
        
        if (dataPoints.isEmpty()) return
        
        val entries = dataPoints.map { point ->
            PieEntry(point.value, point.label)
        }
        
        val colors = dataPoints.map { it.color ?: "#2196F3" }
        val dataSet = ChartStyleHelper.createStyledPieDataSet(entries, "", colors)
        
        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        
        binding.pieChart.invalidate()
    }
    
    private fun updateHabitPerformance(habitAnalytics: List<HabitAnalytics>) {
        habitPerformanceAdapter.updateHabits(habitAnalytics)
    }
    
    private fun showEmptyState() {
        try {
            // Hide charts and stats
            binding.statsContainer.visibility = View.GONE
            binding.chartsContainer.visibility = View.GONE
            binding.habitPerformanceRecycler.visibility = View.GONE
            
            // Show empty state message
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.emptyStateTitle.text = "No Habits Yet"
            binding.emptyStateMessage.text = "Start tracking your habits to see analytics and insights here!"
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun hideEmptyState() {
        try {
            // Show charts and stats
            binding.statsContainer.visibility = View.VISIBLE
            binding.chartsContainer.visibility = View.VISIBLE
            binding.habitPerformanceRecycler.visibility = View.VISIBLE
            
            // Hide empty state message
            binding.emptyStateContainer.visibility = View.GONE
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
