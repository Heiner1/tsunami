package info.nightscout.androidaps.plugins.iob.iobCobCalculator

import dagger.Reusable
import info.nightscout.androidaps.interfaces.IobCobCalculator
import info.nightscout.shared.logging.AAPSLogger
import info.nightscout.shared.logging.LTag
import info.nightscout.androidaps.utils.DateUtil
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToLong


@Reusable
class GlucoseStatusProvider @Inject constructor(
    private val aapsLogger: AAPSLogger,
    private val iobCobCalculator: IobCobCalculator,
    private val dateUtil: DateUtil
) {

    val glucoseStatusData: GlucoseStatus?
        get() = getGlucoseStatusData()

    fun getGlucoseStatusData(allowOldData: Boolean = false): GlucoseStatus? {
        val data = iobCobCalculator.ads.getBgReadingsDataTableCopy()
        val sizeRecords = data.size
        if (sizeRecords == 0) {
            aapsLogger.debug(LTag.GLUCOSE, "sizeRecords==0")
            return null
        }
        if (data[0].timestamp < dateUtil.now() - 7 * 60 * 1000L && !allowOldData) {
            aapsLogger.debug(LTag.GLUCOSE, "oldData")
            return null
        }
        val now = data[0]
        val nowDate = now.timestamp
        var change: Double
        if (sizeRecords == 1) {
            aapsLogger.debug(LTag.GLUCOSE, "sizeRecords==1")
            return GlucoseStatus(
                glucose = now.value,
                noise = 0.0,
                delta = 0.0,
                shortAvgDelta = 0.0,
                longAvgDelta = 0.0,
                date = nowDate,
                // MP curve analysis start
                autoISF_duration = 0.0,
                autoISF_average = now.value,
                bg_5minago = 0.0,
                insufficientsmoothingdata = true,
                bg_supersmooth_now = now.value,
                delta_supersmooth_now = 0.0,
                //MP curve analysis end
            ).asRounded()
        }
        val nowValueList = ArrayList<Double>()
        val lastDeltas = ArrayList<Double>()
        val shortDeltas = ArrayList<Double>()
        val longDeltas = ArrayList<Double>()

        // MP curve analysis start
        val o1_smoothbg: ArrayList<Double> = ArrayList() //MP array for 1st order Smoothed Blood Glucose
        val o2_smoothbg: ArrayList<Double> = ArrayList() //MP array for 2nd order Smoothed Blood Glucose
        val o2_smoothdelta: ArrayList<Double> = ArrayList() //MP array for 2nd order Smoothed delta
        val ssmooth_bg: ArrayList<Double> = ArrayList() //MP array for weighted averaged, super smoothed Blood Glucose
        val ssmooth_delta: ArrayList<Double> = ArrayList() //MP array for deltas of supersmoothed Blood Glucose
        var windowsize = 25 //MP number of bg readings to include in smoothing window
        val o1_weight = 0.4
        val o1_a = 0.5
        val o2_a = 0.4
        val o2_b = 1.0
        var insufficientsmoothingdata = false
        var deltascore: Double
        val deltathreshold = 7.0 //MP average delta above which deltascore will be 1.
        val weight = 0.15 //MP Weighting used for weighted averages
        //MP test variables
        //MP test variables
        var scoredivisor: Double
        //public long activity_pred_time = SafeParse.stringToLong(sp.getString(R.string.key_insulin_oref_peak,"45")); //MP Time in minutes from now to calculate insulin activity for
        //public long activity_pred_time = SafeParse.stringToLong(sp.getString(R.string.key_insulin_oref_peak,"45")); //MP Time in minutes from now to calculate insulin activity for
        //var activity_pred_time = 40L //MP Time in minutes from now to calculate insulin activity for

        //MP Tsunami Activity Engine end
        //MP curve analysis end
        // Use the latest sgv value in the now calculations
        val before = data[1]

        nowValueList.add(now.value)
        for (i in 1 until sizeRecords) {
            if (data[i].value > 38) {
                val then = data[i]
                val thenDate = then.timestamp

                val minutesAgo = ((nowDate - thenDate) / (1000.0 * 60)).roundToLong()
                // multiply by 5 to get the same units as delta, i.e. mg/dL/5m
                change = now.value - then.value
                val avgDel = change / minutesAgo * 5
                aapsLogger.debug(LTag.GLUCOSE, "$then minutesAgo=$minutesAgo avgDelta=$avgDel")

                // use the average of all data points in the last 2.5m for all further "now" calculations
                if (0 < minutesAgo && minutesAgo < 2.5) {
                    // Keep and average all values within the last 2.5 minutes
                    nowValueList.add(then.value)
                    now.value = average(nowValueList)
                    // short_deltas are calculated from everything ~5-15 minutes ago
                } else if (2.5 < minutesAgo && minutesAgo < 17.5) {
                    //console.error(minutesAgo, avgDelta);
                    shortDeltas.add(avgDel)
                    // last_deltas are calculated from everything ~5 minutes ago
                    if (2.5 < minutesAgo && minutesAgo < 7.5) {
                        lastDeltas.add(avgDel)
                    }
                    // long_deltas are calculated from everything ~20-40 minutes ago
                } else if (17.5 < minutesAgo && minutesAgo < 42.5) {
                    longDeltas.add(avgDel)
                } else {
                    // Do not process any more records after >= 42.5 minutes
                    break
                }
            }
        }
        val shortAverageDelta = average(shortDeltas)
        val delta = if (lastDeltas.isEmpty()) {
            shortAverageDelta
        } else {
            average(lastDeltas)
        }

        val status = GlucoseStatus(
            glucose = now.value,
            date = nowDate,
            noise = 0.0, //for now set to nothing as not all CGMs report noise
            shortAvgDelta = shortAverageDelta,
            delta = delta,
            longAvgDelta = average(longDeltas),
        )
        // autoISF === START
        // mod 7: calculate 2 variables for 5% range
        //  initially just test the handling of arguments
        // status.dura05 = 11d;
        // status.avg05 = 47.11d;
        //  mod 7a: now do the real maths
        // autoISF === START
        // mod 7: calculate 2 variables for 5% range
        //  initially just test the handling of arguments
        // status.dura05 = 11d;
        // status.avg05 = 47.11d;
        //  mod 7a: now do the real maths
        val bw = 0.05 // used for Eversense; may be lower for Dexcom

        var sumBG = now.value
        var oldavg = now.value
        var minutesdur = Math.round(0L / (1000.0 * 60))
        for (i in 1 until sizeRecords) {
            val then = data[i]
            val thenDate: Long = then.timestamp
            //  GZ mod 7c: stop the series if there was a CGM gap greater than 13 minutes, i.e. 2 regular readings
            if (Math.round((nowDate - thenDate) / (1000.0 * 60)) - minutesdur > 13) {
                break
            }
            if (then.value > oldavg * (1 - bw) && then.value < oldavg * (1 + bw)) {
                sumBG += then.value
                oldavg = sumBG / (i + 1)
                minutesdur = Math.round((nowDate - thenDate) / (1000.0 * 60))
            } else {
                break
            }
        }
        status.autoISF_average = oldavg
        status.autoISF_duration = minutesdur.toDouble()
        // autoISF === END

//################################# MP
//### DATA SMOOTHING CORE START ### MP
//################################# MP

//TODO: Decide what happens if there's insufficient data

// ADJUST SMOOTHING WINDOW TO ONLY INCLUDE VALID READINGS
        // Valid readings include:
        // - Values that actually exist (windowsize may not be larger than sizeRecords)
        // - Values that come in approx. every 5 min. If the time gap between two readings is larger, this is likely due to a sensor error or warmup of a new sensor.d
        // - Values that are not 38 mg/dl; 38 mg/dl reflects an xDrip error state (according to a comment in determine-basal.js)

        //MP: Adjust smoothing window if database size is smaller than the default value + 1 (+1 because the reading before the oldest reading to be smoothed will be used in the calculations
        if (sizeRecords < windowsize) { //MP standard smoothing window
            windowsize = sizeRecords //MP Adjust smoothing window to the size of database if it is smaller than the original window size
        }

        //MP: Adjust smoothing window further if a gap in the BG database is detected, e.g. due to sensor errors of sensor swaps, or if 38 mg/dl are reported (xDrip error state)

        //MP: Adjust smoothing window further if a gap in the BG database is detected, e.g. due to sensor errors of sensor swaps, or if 38 mg/dl are reported (xDrip error state)
        for (i in 0 until windowsize) {
            if (Math.round((data[i].timestamp - data[i + 1].timestamp) / (1000.0 * 60)) >= 12) { //MP: 12 min because a missed reading (i.e. readings coming in after 10 min) can occur for various reasons, like walking away from the phone or reinstalling AAPS
                //if (Math.round((data.get(i).date - data.get(i + 1).date) / 60000L) <= 7) { //MP crashes the app, useful for testing
                windowsize = i + 1 //MP: If time difference between two readings exceeds 7 min, adjust windowsize to *include* the more recent reading (i = reading; +1 because windowsize reflects number of valid readings);
                break
            } else if (data[i].value == 38.0) {
                windowsize = i //MP: 38 mg/dl reflects an xDrip error state; Chain of valid readings ends here, *exclude* this value (windowsize = i; i + 1 would include the current value)
                break
            }
        }

// CALCULATE SMOOTHING WINDOW - 1st order exponential smoothing

// CALCULATE SMOOTHING WINDOW - 1st order exponential smoothing
        o1_smoothbg.clear() // MP reset smoothed bg array

        if (windowsize >= 4) { //MP: Require a valid windowsize of at least 4 readings
            o1_smoothbg.add(data[windowsize - 1].value) //MP: Initialise smoothing with the oldest valid data point
            for (i in 0 until windowsize) { //MP calculate smoothed bg window of valid readings
                o1_smoothbg.add(
                    0,
                    o1_smoothbg[0] + o1_a * (data[windowsize - 1 - i].value - o1_smoothbg[0])
                ) //MP build array of 1st order smoothed bgs
            }
        } else {
            insufficientsmoothingdata = true
        }

// CALCULATE SMOOTHING WINDOW - 2nd order exponential smoothing
        o2_smoothbg.clear() // MP reset smoothed bg array
        o2_smoothdelta.clear() // MP reset smoothed delta array

        if (windowsize >= 4) { //MP: Require a valid windowsize of at least 4 readings
            o2_smoothbg.add(data[windowsize - 1].value) //MP Start 2nd order exponential data smoothing with the oldest valid bg
            o2_smoothdelta.add(data[windowsize - 2].value - data[windowsize - 1].value) //MP Start 2nd order exponential data smoothing with the oldest valid delta
            for (i in 0 until windowsize - 1) { //MP calculated smoothed bg window of last 1 h
                o2_smoothbg.add(
                    0,
                    o2_a * data[windowsize - 2 - i].value + (1 - o2_a) * (o2_smoothbg[0] + o2_smoothdelta[0])
                ) //MP build array of 2nd order smoothed bgs; windowsize-1 is the oldest valid bg value, so windowsize-2 is from when on the smoothing begins;
                o2_smoothdelta.add(
                    0,
                    o2_b * (o2_smoothbg[0] - o2_smoothbg[1]) + (1 - o2_b) * o2_smoothdelta[0]
                ) //MP build array of 1st order smoothed bgs
            }
        } else {
            insufficientsmoothingdata = true
        }

// CALCULATE SUPERSMOOTHED GLUCOSE & DELTAS
        ssmooth_bg.clear() // MP reset supersmoothed bg array
        ssmooth_delta.clear() // MP reset supersmoothed delta array

        if (!insufficientsmoothingdata) { //MP Build supersmoothed array only if there is enough valid readings
            for (i in o2_smoothbg.indices) { //MP calculated supersmoothed bg of all o1/o2 smoothed data available; o2 & o1 smoothbg array sizes are equal in size, so only one is used as a condition here
                ssmooth_bg.add(o1_weight * o1_smoothbg[i] + (1 - o1_weight) * o2_smoothbg[i]) //MP build array of supersmoothed bgs
            }
            for (i in 0 until ssmooth_bg.size - 1) {
                ssmooth_delta.add(ssmooth_bg[i] - ssmooth_bg[i + 1]) //MP build array of supersmoothed bg deltas
            }
        }

// MP Tsunami Activity Engine meal detection system
        if (!insufficientsmoothingdata) {
            deltascore = 0.0
            scoredivisor = 0.0
            for (i in 0 until Math.min(windowsize - 1, 6)) { //MP Dynamically adjust deltas to include
                deltascore += ssmooth_delta[i] * (1 - weight * i)
                scoredivisor += 1 - weight * i //MP weighted score
            }
            deltascore = deltascore / scoredivisor / deltathreshold //MP: Check how deltascore compares to the threshold
        } else {
            deltascore = 0.5 //MP If there's not enough data, set deltascore to 50%
        }

//MP report smoothing variables in glucose status
        status.bg_5minago =
            before.value //MP If the database contains more than one reading, return the value from 5 min ago

        status.insufficientsmoothingdata = insufficientsmoothingdata
        status.deltascore = deltascore
        if (!insufficientsmoothingdata) {
            status.bg_supersmooth_now = ssmooth_bg[0]
            status.delta_supersmooth_now = ssmooth_delta[0]
        } else { //todo: below is a quick solution, should probably be improved
            status.bg_supersmooth_now = data[0].value
            status.delta_supersmooth_now = data[0].value - data[1].value
        }
        // TODO: communicate to other code snippets / files that use smoothed data if no smoothing occurred due to insufficient dat
//############################### MP
//### DATA SMOOTHING CORE END ### MP
//############################### MP

        return status.also { aapsLogger.debug(LTag.GLUCOSE, it.log()) }.asRounded()
    }

    companion object {

        fun average(array: ArrayList<Double>): Double {
            var sum = 0.0
            if (array.size == 0) return 0.0
            for (value in array) {
                sum += value
            }
            return sum / array.size
        }
    }
}