package app.aaps.core.interfaces.workflow

import app.aaps.core.interfaces.iob.IobCobCalculator
import app.aaps.core.interfaces.overview.OverviewData
import app.aaps.core.interfaces.rx.events.Event

interface CalculationWorkflow {
    companion object {

        const val MAIN_CALCULATION = "calculation"
        const val HISTORY_CALCULATION = "history_calculation"
        const val JOB = "job"
        const val PASS = "pass"
    }

    enum class ProgressData(val pass: Int, val percentOfTotal: Int) {
        DRAW_BG(0, 1),
        PREPARE_TREATMENTS_DATA(1, 2),
        PREPARE_BASAL_DATA(2, 6),
        PREPARE_TEMPORARY_TARGET_DATA(3, 6),
        DRAW_TT(4, 1),
        IOB_COB_OREF(5, 75), //MP adjusted to fit in Tsu percentages
        PREPARE_IOB_AUTOSENS_DATA(6, 5),
        PREPARE_TSUNAMI_DATA(7, 1), //MP order here has to match order in CalculationWorkflowImpl.kt
        DRAW_TSUNAMI_DATA(8, 1), //MP order here has to match order in CalculationWorkflowImpl.kt
        DRAW_IOB(9, 1),
        DRAW_FINAL(10, 1);

        fun finalPercent(progress: Int): Int {
            var total = 0
            for (i in entries) if (i.pass < pass) total += i.percentOfTotal
            total += (percentOfTotal.toDouble() * progress / 100.0).toInt()
            return total
        }
    }

    fun stopCalculation(job: String, from: String)

    /**
     * Start calculation of data needed for displaying graphs
     *
     * @param job [MAIN_CALCULATION] or [HISTORY_CALCULATION]
     * @param iobCobCalculator different instance for [HistoryBrowseActivity]
     * @param overviewData different instance for [HistoryBrowseActivity]
     */
    fun runCalculation(
        job: String,
        iobCobCalculator: IobCobCalculator,
        overviewData: OverviewData,
        reason: String,
        end: Long,
        bgDataReload: Boolean,
        cause: Event?
    )

    /**
     * Update treatments in graph ofter new therapy event
     */
    fun runOnEventTherapyEventChange(overviewData: OverviewData)

    /**
     * Update graph ofter scale change
     * There may be me necessary display larger time interval thus run new calculation
     */
    fun runOnScaleChanged(iobCobCalculator: IobCobCalculator, overviewData: OverviewData)
}