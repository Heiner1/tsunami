package info.nightscout.pump.medtrum

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import app.aaps.core.data.model.BS
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.data.pump.defs.ManufacturerType
import app.aaps.core.data.pump.defs.PumpDescription
import app.aaps.core.data.pump.defs.PumpType
import app.aaps.core.data.pump.defs.TimeChangeType
import app.aaps.core.data.time.T
import app.aaps.core.interfaces.constraints.ConstraintsChecker
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.notifications.Notification
import app.aaps.core.interfaces.objects.Instantiator
import app.aaps.core.interfaces.plugin.PluginDescription
import app.aaps.core.interfaces.profile.Profile
import app.aaps.core.interfaces.pump.DetailedBolusInfo
import app.aaps.core.interfaces.pump.Medtrum
import app.aaps.core.interfaces.pump.Pump
import app.aaps.core.interfaces.pump.PumpEnactResult
import app.aaps.core.interfaces.pump.PumpPluginBase
import app.aaps.core.interfaces.pump.PumpSync
import app.aaps.core.interfaces.pump.TemporaryBasalStorage
import app.aaps.core.interfaces.pump.actions.CustomAction
import app.aaps.core.interfaces.pump.actions.CustomActionType
import app.aaps.core.interfaces.pump.defs.fillFor
import app.aaps.core.interfaces.queue.Callback
import app.aaps.core.interfaces.queue.CommandQueue
import app.aaps.core.interfaces.queue.CustomCommand
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.rx.AapsSchedulers
import app.aaps.core.interfaces.rx.bus.RxBus
import app.aaps.core.interfaces.rx.events.EventAppExit
import app.aaps.core.interfaces.rx.events.EventDismissNotification
import app.aaps.core.interfaces.rx.events.EventOverviewBolusProgress
import app.aaps.core.interfaces.ui.UiInteraction
import app.aaps.core.interfaces.utils.DateUtil
import app.aaps.core.interfaces.utils.DecimalFormatter
import app.aaps.core.interfaces.utils.fabric.FabricPrivacy
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.IntKey
import app.aaps.core.keys.Preferences
import app.aaps.core.objects.constraints.ConstraintObject
import app.aaps.core.ui.dialogs.OKDialog
import app.aaps.core.ui.toast.ToastUtils
import app.aaps.core.validators.ValidatingEditTextPreference
import app.aaps.core.validators.preferences.AdaptiveIntPreference
import info.nightscout.pump.medtrum.comm.enums.MedtrumPumpState
import info.nightscout.pump.medtrum.comm.enums.ModelType
import info.nightscout.pump.medtrum.services.MedtrumService
import info.nightscout.pump.medtrum.ui.MedtrumOverviewFragment
import info.nightscout.pump.medtrum.util.MedtrumSnUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton class MedtrumPlugin @Inject constructor(
    aapsLogger: AAPSLogger,
    rh: ResourceHelper,
    commandQueue: CommandQueue,
    private val preferences: Preferences,
    private val constraintChecker: ConstraintsChecker,
    private val aapsSchedulers: AapsSchedulers,
    private val rxBus: RxBus,
    private val context: Context,
    private val fabricPrivacy: FabricPrivacy,
    private val dateUtil: DateUtil,
    private val medtrumPump: MedtrumPump,
    private val uiInteraction: UiInteraction,
    private val pumpSync: PumpSync,
    private val temporaryBasalStorage: TemporaryBasalStorage,
    private val decimalFormatter: DecimalFormatter,
    private val instantiator: Instantiator
) : PumpPluginBase(
    PluginDescription()
        .mainType(PluginType.PUMP)
        .fragmentClass(MedtrumOverviewFragment::class.java.name)
        .pluginIcon(app.aaps.core.ui.R.drawable.ic_medtrum_128)
        .pluginName(R.string.medtrum)
        .shortName(R.string.medtrum_pump_shortname)
        .preferencesId(R.xml.pref_medtrum_pump)
        .description(R.string.medtrum_pump_description), aapsLogger, rh, commandQueue
), Pump, Medtrum {

    private val disposable = CompositeDisposable()
    private var medtrumService: MedtrumService? = null

    override fun onStart() {
        super.onStart()
        aapsLogger.debug(LTag.PUMP, "MedtrumPlugin onStart()")
        medtrumPump.loadVarsFromSP()
        val intent = Intent(context, MedtrumService::class.java)
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        disposable += rxBus
            .toObservable(EventAppExit::class.java)
            .observeOn(aapsSchedulers.io)
            .subscribe({ context.unbindService(mConnection) }, fabricPrivacy::logException)

        // Force enable pump unreachable alert due to some failure modes of Medtrum pump
        preferences.put(BooleanKey.AlertPumpUnreachable, true)
    }

    override fun onStop() {
        aapsLogger.debug(LTag.PUMP, "MedtrumPlugin onStop()")
        context.unbindService(mConnection)
        disposable.clear()
        super.onStop()
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            aapsLogger.debug(LTag.PUMP, "Service is disconnected")
            medtrumService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            aapsLogger.debug(LTag.PUMP, "Service is connected")
            val mLocalBinder = service as MedtrumService.LocalBinder
            medtrumService = mLocalBinder.serviceInstance
        }
    }

    fun getService(): MedtrumService? {
        return medtrumService
    }

    override fun preprocessPreferences(preferenceFragment: PreferenceFragmentCompat) {
        super.preprocessPreferences(preferenceFragment)

        preprocessSerialSettings(preferenceFragment)
        preprocessAlarmSettings(preferenceFragment)
        preprocessMaxInsulinSettings(preferenceFragment)
        preprocessConnectionAlertSettings(preferenceFragment)
        preprocessPumpWarningSettings(preferenceFragment)
    }

    private fun preprocessSerialSettings(preferenceFragment: PreferenceFragmentCompat) {
        val serialSetting = preferenceFragment.findPreference<EditTextPreference>(rh.gs(R.string.key_sn_input))
        serialSetting?.apply {
            isEnabled = !isInitialized()
            setOnBindEditTextListener { editText ->
                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(newValue: Editable?) {
                        val newSN = newValue?.toString()?.toLongOrNull(radix = 16) ?: 0
                        val newDeviceType = MedtrumSnUtil().getDeviceTypeFromSerial(newSN)
                        editText.error = if (newDeviceType == ModelType.INVALID) {
                            rh.gs(R.string.sn_input_invalid)
                        } else {
                            null
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Nothing to do here
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Nothing to do here
                    }
                })
            }
            setOnPreferenceChangeListener { _, newValue ->
                val newSN = (newValue as? String)?.toLongOrNull(radix = 16) ?: 0
                val newDeviceType = MedtrumSnUtil().getDeviceTypeFromSerial(newSN)

                when {
                    newDeviceType == ModelType.INVALID                               -> {
                        preferenceFragment.activity?.let { activity ->
                            OKDialog.show(activity, rh.gs(R.string.sn_input_title), rh.gs(R.string.sn_input_invalid))
                        }
                        false
                    }

                    medtrumPump.pumpType(newDeviceType) == PumpType.MEDTRUM_UNTESTED -> {
                        preferenceFragment.activity?.let { activity ->
                            OKDialog.show(activity, rh.gs(R.string.sn_input_title), rh.gs(R.string.pump_unsupported, newDeviceType.toString()))
                        }
                        false
                    }

                    else                                                             -> true
                }
            }
        }
    }

    private fun preprocessAlarmSettings(preferenceFragment: PreferenceFragmentCompat) {
        val alarmSetting = preferenceFragment.findPreference<ListPreference>(rh.gs(R.string.key_alarm_setting))
        val allAlarmEntries = preferenceFragment.resources.getStringArray(R.array.alarmSettings)
        val allAlarmValues = preferenceFragment.resources.getStringArray(R.array.alarmSettingsValues)

        if (allAlarmEntries.size < 8 || allAlarmValues.size < 8) {
            aapsLogger.error(LTag.PUMP, "Alarm settings array is not complete")
        } else {
            when (medtrumPump.pumpType()) {
                PumpType.MEDTRUM_NANO, PumpType.MEDTRUM_300U -> {
                    alarmSetting?.apply {
                        entries = arrayOf(allAlarmEntries[6], allAlarmEntries[7]) // "Beep", "Silent"
                        entryValues = arrayOf(allAlarmValues[6], allAlarmValues[7]) // "6", "7"
                    }
                }

                else                                         -> {
                    // Use default
                }
            }
        }
    }

    private fun preprocessMaxInsulinSettings(preferenceFragment: PreferenceFragmentCompat) {
        val hourlyMaxSetting = preferenceFragment.findPreference<ValidatingEditTextPreference>(rh.gs(R.string.key_hourly_max_insulin))
        val dailyMaxSetting = preferenceFragment.findPreference<ValidatingEditTextPreference>(rh.gs(R.string.key_daily_max_insulin))

        val hourlyMaxValue = hourlyMaxSetting?.text?.toIntOrNull() ?: 0
        val newDailyMaxMinValue = if (hourlyMaxValue > 20) hourlyMaxValue else 20
        dailyMaxSetting?.setMinNumber(newDailyMaxMinValue)

        val pumpTypeSettings = when (medtrumPump.pumpType()) {
            PumpType.MEDTRUM_NANO -> Pair(40, 180) // maxHourlyMax, maxDailyMax
            PumpType.MEDTRUM_300U -> Pair(60, 270)
            else                  -> Pair(40, 180)
        }

        hourlyMaxSetting?.apply {
            setMaxNumber(pumpTypeSettings.first)
            val hourlyCurrentValue = text?.toIntOrNull() ?: 0
            if (hourlyCurrentValue > pumpTypeSettings.first) {
                text = pumpTypeSettings.first.toString()
            }
        }

        dailyMaxSetting?.apply {
            setMaxNumber(pumpTypeSettings.second)
            val dailyCurrentValue = text?.toIntOrNull() ?: 0
            when {
                dailyCurrentValue < newDailyMaxMinValue     -> text = newDailyMaxMinValue.toString()
                dailyCurrentValue > pumpTypeSettings.second -> text = pumpTypeSettings.second.toString()
            }
        }
    }

    private fun preprocessConnectionAlertSettings(preferenceFragment: PreferenceFragmentCompat) {
        val unreachableAlertSetting = preferenceFragment.findPreference<SwitchPreference>(BooleanKey.AlertPumpUnreachable.key)
        val unreachableThresholdSetting = preferenceFragment.findPreference<AdaptiveIntPreference>(IntKey.AlertsPumpUnreachableThreshold.key)

        unreachableAlertSetting?.apply {
            isSelectable = false
            summary = rh.gs(R.string.enable_pump_unreachable_alert_summary)
        }

        unreachableThresholdSetting?.apply {
            val currentValue = text
            summary = "${rh.gs(R.string.pump_unreachable_threshold_minutes_summary)}\n${currentValue}"
        }
    }

    private fun preprocessPumpWarningSettings(preferenceFragment: PreferenceFragmentCompat) {
        val patchExpirationPref = preferenceFragment.findPreference<SwitchPreference>(rh.gs(R.string.key_patch_expiration))
        val pumpWarningNotificationPref = preferenceFragment.findPreference<SwitchPreference>(rh.gs(R.string.key_pump_warning_notification))
        val pumpWarningExpiryHourPref = preferenceFragment.findPreference<ValidatingEditTextPreference>(rh.gs(R.string.key_pump_warning_expiry_hour))

        pumpWarningExpiryHourPref?.isEnabled = patchExpirationPref?.isChecked == true && pumpWarningNotificationPref?.isChecked == true
    }

    override fun isInitialized(): Boolean {
        return medtrumPump.pumpState > MedtrumPumpState.EJECTED && medtrumPump.pumpState < MedtrumPumpState.STOPPED
    }

    override fun isSuspended(): Boolean {
        return medtrumPump.pumpState < MedtrumPumpState.ACTIVE || medtrumPump.pumpState > MedtrumPumpState.ACTIVE_ALT
    }

    override fun isBusy(): Boolean {
        return false
    }

    override fun isConnected(): Boolean {
        // This is a workaround to prevent AAPS to trigger connects when we have no patch activated
        return if (!isInitialized()) {
            true
        } else {
            medtrumService?.isConnected == true
        }
    }

    override fun isConnecting(): Boolean = medtrumService?.isConnecting == true
    override fun isHandshakeInProgress(): Boolean = false

    override fun finishHandshaking() {
        // Unused
    }

    override fun connect(reason: String) {
        if (isInitialized()) {
            aapsLogger.debug(LTag.PUMP, "Medtrum connect - reason:$reason")
            if (medtrumService != null) {
                aapsLogger.debug(LTag.PUMP, "Medtrum connect - Attempt connection!")
                val success = medtrumService?.connect(reason) == true
                if (!success) ToastUtils.errorToast(context, app.aaps.core.ui.R.string.ble_not_supported_or_not_paired)
            }
        }
    }

    override fun disconnect(reason: String) {
        if (isInitialized()) {
            aapsLogger.debug(LTag.PUMP, "Medtrum disconnect from: $reason")
            medtrumService?.disconnect(reason)
        }
    }

    override fun stopConnecting() {
        if (isInitialized()) {
            aapsLogger.debug(LTag.PUMP, "Medtrum stopConnecting")
            medtrumService?.stopConnecting()
        }
    }

    override fun getPumpStatus(reason: String) {
        aapsLogger.debug(LTag.PUMP, "Medtrum getPumpStatus - reason:$reason")
        if (isInitialized()) {
            val connectionOK = medtrumService?.readPumpStatus() ?: false
            if (connectionOK == false) {
                aapsLogger.error(LTag.PUMP, "Medtrum getPumpStatus failed")
            }
        }
    }

    override fun setNewBasalProfile(profile: Profile): PumpEnactResult {
        // New profile will be set when patch is activated
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(true).enacted(true)

        return if (medtrumService?.updateBasalsInPump(profile) == true) {
            rxBus.send(EventDismissNotification(Notification.FAILED_UPDATE_PROFILE))
            uiInteraction.addNotificationValidFor(Notification.PROFILE_SET_OK, rh.gs(app.aaps.core.ui.R.string.profile_set_ok), Notification.INFO, 60)
            instantiator.providePumpEnactResult().success(true).enacted(true)
        } else {
            uiInteraction.addNotification(Notification.FAILED_UPDATE_PROFILE, rh.gs(app.aaps.core.ui.R.string.failed_update_basal_profile), Notification.URGENT)
            instantiator.providePumpEnactResult()
        }
    }

    override fun isThisProfileSet(profile: Profile): Boolean {
        if (!isInitialized()) return true
        var result = false
        val profileBytes = medtrumPump.buildMedtrumProfileArray(profile)
        if (profileBytes?.size == medtrumPump.actualBasalProfile.size) {
            result = true
            for (i in profileBytes.indices) {
                if (profileBytes[i] != medtrumPump.actualBasalProfile[i]) {
                    result = false
                    break
                }
            }
        }
        return result
    }

    override fun lastDataTime(): Long = medtrumPump.lastConnection
    override val baseBasalRate: Double
        get() = medtrumPump.baseBasalRate

    override val reservoirLevel: Double
        get() = medtrumPump.reservoir

    override val batteryLevel: Int
        get() = 0 // We cannot determine battery level (yet)

    @Synchronized
    override fun deliverTreatment(detailedBolusInfo: DetailedBolusInfo): PumpEnactResult {
        // Insulin value must be greater than 0
        require(detailedBolusInfo.carbs == 0.0) { detailedBolusInfo.toString() }
        require(detailedBolusInfo.insulin > 0) { detailedBolusInfo.toString() }

        aapsLogger.debug(LTag.PUMP, "deliverTreatment: " + detailedBolusInfo.insulin + "U")
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)
        detailedBolusInfo.insulin = constraintChecker.applyBolusConstraints(ConstraintObject(detailedBolusInfo.insulin, aapsLogger)).value()
        aapsLogger.debug(LTag.PUMP, "deliverTreatment: Delivering bolus: " + detailedBolusInfo.insulin + "U")
        val t = EventOverviewBolusProgress.Treatment(0.0, 0, detailedBolusInfo.bolusType == BS.Type.SMB, detailedBolusInfo.id)
        val connectionOK = medtrumService?.setBolus(detailedBolusInfo, t) == true
        val result = instantiator.providePumpEnactResult()
        result.success = connectionOK && abs(detailedBolusInfo.insulin - t.insulin) < pumpDescription.bolusStep
        result.bolusDelivered = t.insulin
        if (!result.success) {
            result.comment(medtrumPump.bolusErrorReason ?: rh.gs(R.string.bolus_error_reason_pump_error))
        } else {
            result.comment(app.aaps.core.ui.R.string.ok)
        }
        aapsLogger.debug(LTag.PUMP, "deliverTreatment: OK. Success: ${result.success} Asked: ${detailedBolusInfo.insulin} Delivered: ${result.bolusDelivered}")
        return result
    }

    override fun stopBolusDelivering() {
        if (!isInitialized()) return

        aapsLogger.info(LTag.PUMP, "stopBolusDelivering")
        medtrumService?.stopBolus()
    }

    @Synchronized
    override fun setTempBasalAbsolute(absoluteRate: Double, durationInMinutes: Int, profile: Profile, enforceNew: Boolean, tbrType: PumpSync.TemporaryBasalType): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)

        aapsLogger.info(LTag.PUMP, "setTempBasalAbsolute - absoluteRate: $absoluteRate, durationInMinutes: $durationInMinutes, enforceNew: $enforceNew")
        // round rate to pump rate
        val pumpRate = constraintChecker.applyBasalConstraints(ConstraintObject(absoluteRate, aapsLogger), profile).value()
        temporaryBasalStorage.add(PumpSync.PumpState.TemporaryBasal(dateUtil.now(), T.mins(durationInMinutes.toLong()).msecs(), pumpRate, true, tbrType, 0L, 0L))
        val connectionOK = medtrumService?.setTempBasal(pumpRate, durationInMinutes) == true
        return if (connectionOK
            && medtrumPump.tempBasalInProgress
            && abs(medtrumPump.tempBasalAbsoluteRate - pumpRate) <= 0.05
        ) {

            instantiator.providePumpEnactResult().success(true).enacted(true).duration(durationInMinutes).absolute(medtrumPump.tempBasalAbsoluteRate)
                .isPercent(false)
                .isTempCancel(false)
        } else {
            aapsLogger.error(
                LTag.PUMP,
                "setTempBasalAbsolute failed, connectionOK: $connectionOK, tempBasalInProgress: ${medtrumPump.tempBasalInProgress}, tempBasalAbsoluteRate: ${medtrumPump.tempBasalAbsoluteRate}"
            )
            instantiator.providePumpEnactResult().success(false).enacted(false).comment("Medtrum setTempBasalAbsolute failed")
        }
    }

    override fun setTempBasalPercent(percent: Int, durationInMinutes: Int, profile: Profile, enforceNew: Boolean, tbrType: PumpSync.TemporaryBasalType): PumpEnactResult {
        aapsLogger.info(LTag.PUMP, "setTempBasalPercent - percent: $percent, durationInMinutes: $durationInMinutes, enforceNew: $enforceNew")
        return instantiator.providePumpEnactResult().success(false).enacted(false).comment("Medtrum driver does not support percentage temp basals")
    }

    override fun setExtendedBolus(insulin: Double, durationInMinutes: Int): PumpEnactResult {
        aapsLogger.info(LTag.PUMP, "setExtendedBolus - insulin: $insulin, durationInMinutes: $durationInMinutes")
        return instantiator.providePumpEnactResult().success(false).enacted(false).comment("Medtrum driver does not support extended boluses")
    }

    override fun cancelTempBasal(enforceNew: Boolean): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)

        aapsLogger.info(LTag.PUMP, "cancelTempBasal - enforceNew: $enforceNew")
        val connectionOK = medtrumService?.cancelTempBasal() == true
        return if (connectionOK && !medtrumPump.tempBasalInProgress) {
            instantiator.providePumpEnactResult().success(true).enacted(true).isTempCancel(true)
        } else {
            aapsLogger.error(LTag.PUMP, "cancelTempBasal failed, connectionOK: $connectionOK, tempBasalInProgress: ${medtrumPump.tempBasalInProgress}")
            instantiator.providePumpEnactResult().success(false).enacted(false).comment("Medtrum cancelTempBasal failed")
        }
    }

    override fun cancelExtendedBolus(): PumpEnactResult {
        return instantiator.providePumpEnactResult()
    }

    override fun getJSONStatus(profile: Profile, profileName: String, version: String): JSONObject {
        val now = System.currentTimeMillis()
        if (medtrumPump.lastConnection + 60 * 60 * 1000L < System.currentTimeMillis()) {
            return JSONObject()
        }
        val pumpJson = JSONObject()
        val status = JSONObject()
        val extended = JSONObject()
        try {
            status.put(
                "status", if (!isSuspended()) "normal"
                else if (isInitialized() && isSuspended()) "suspended"
                else "no active patch"
            )
            status.put("timestamp", dateUtil.toISOString(medtrumPump.lastConnection))
            if (medtrumPump.lastBolusTime != 0L) {
                extended.put("lastBolus", dateUtil.dateAndTimeString(medtrumPump.lastBolusTime))
                extended.put("lastBolusAmount", medtrumPump.lastBolusAmount)
            }
            val tb = pumpSync.expectedPumpState().temporaryBasal
            if (tb != null) {
                extended.put("TempBasalAbsoluteRate", tb.convertedToAbsolute(now, profile))
                extended.put("TempBasalStart", dateUtil.dateAndTimeString(tb.timestamp))
                extended.put("TempBasalRemaining", tb.plannedRemainingMinutes)
            }
            extended.put("BaseBasalRate", baseBasalRate)
            try {
                extended.put("ActiveProfile", profileName)
            } catch (ignored: Exception) {
                // Ignore
            }
            pumpJson.put("status", status)
            pumpJson.put("extended", extended)
            pumpJson.put("reservoir", medtrumPump.reservoir.toInt())
            pumpJson.put("clock", dateUtil.toISOString(now))
        } catch (e: JSONException) {
            aapsLogger.error(LTag.PUMP, "Unhandled exception: $e")
        }
        return pumpJson
    }

    override fun manufacturer(): ManufacturerType {
        return ManufacturerType.Medtrum
    }

    override fun model(): PumpType {
        return medtrumPump.pumpType()
    }

    override fun serialNumber(): String {
        // Load from SP here, because this value will be get before pump is initialized
        return medtrumPump.pumpSNFromSP.toString(radix = 16)
    }

    override val pumpDescription: PumpDescription
        get() = PumpDescription().fillFor(medtrumPump.pumpType())

    override fun shortStatus(veryShort: Boolean): String {
        var ret = ""
        if (medtrumPump.lastConnection != 0L) {
            val agoMillis = System.currentTimeMillis() - medtrumPump.lastConnection
            val agoMin = (agoMillis / 60.0 / 1000.0).toInt()
            ret += "LastConn: $agoMin minAgo\n"
        }
        if (medtrumPump.lastBolusTime != 0L)
            ret += "LastBolus: ${decimalFormatter.to2Decimal(medtrumPump.lastBolusAmount)}U @${DateFormat.format("HH:mm", medtrumPump.lastBolusTime)}\n"

        if (medtrumPump.tempBasalInProgress)
            ret += "Temp: ${medtrumPump.temporaryBasalToString()}\n"

        ret += "Res: ${decimalFormatter.to0Decimal(medtrumPump.reservoir)}U\n"
        return ret
    }

    override val isFakingTempsByExtendedBoluses: Boolean = false

    override fun loadTDDs(): PumpEnactResult {
        return instantiator.providePumpEnactResult() // Note: Can implement this if we implement history fully (no priority)
    }

    override fun getCustomActions(): List<CustomAction>? {
        return null
    }

    override fun executeCustomAction(customActionType: CustomActionType) {
        // Unused
    }

    override fun executeCustomCommand(customCommand: CustomCommand): PumpEnactResult? {
        return null
    }

    override fun canHandleDST(): Boolean {
        return true
    }

    override fun timezoneOrDSTChanged(timeChangeType: TimeChangeType) {
        medtrumPump.needCheckTimeUpdate = true
        if (isInitialized()) {
            commandQueue.updateTime(object : Callback() {
                override fun run() {
                    if (!this.result.success) {
                        aapsLogger.error(LTag.PUMP, "Medtrum time update failed")
                        // Only notify here on failure (connection may be failed), service will handle success
                        medtrumService?.timeUpdateNotification(false)
                    }
                }
            })
        }
    }

    // Medtrum interface
    override fun loadEvents(): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)
        val connectionOK = medtrumService?.loadEvents() == true
        return instantiator.providePumpEnactResult().success(connectionOK)
    }

    override fun setUserOptions(): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)
        val connectionOK = medtrumService?.setUserSettings() == true
        return instantiator.providePumpEnactResult().success(connectionOK)
    }

    override fun clearAlarms(): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)
        val connectionOK = medtrumService?.clearAlarms() == true
        return instantiator.providePumpEnactResult().success(connectionOK)
    }

    override fun deactivate(): PumpEnactResult {
        val connectionOK = medtrumService?.deactivatePatch() == true
        return instantiator.providePumpEnactResult().success(connectionOK)
    }

    override fun updateTime(): PumpEnactResult {
        if (!isInitialized()) return instantiator.providePumpEnactResult().success(false).enacted(false)
        val connectionOK = medtrumService?.updateTimeIfNeeded() == true
        return instantiator.providePumpEnactResult().success(connectionOK)
    }
}
