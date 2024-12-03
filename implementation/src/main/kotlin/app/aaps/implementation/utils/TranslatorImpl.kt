package app.aaps.implementation.utils

import app.aaps.core.data.model.OE
import app.aaps.core.data.model.TE
import app.aaps.core.data.model.TT
import app.aaps.core.data.ue.Action
import app.aaps.core.data.ue.Sources
import app.aaps.core.data.ue.ValueWithUnit
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.utils.Translator
import dagger.Reusable
import javax.inject.Inject

@Reusable
class TranslatorImpl @Inject internal constructor(
    private val rh: ResourceHelper
) : Translator {

    override fun translate(action: Action): String = when (action) {
        Action.BOLUS                           -> rh.gs(app.aaps.core.ui.R.string.uel_bolus)
        Action.BOLUS_CALCULATOR_RESULT         -> rh.gs(app.aaps.core.ui.R.string.uel_bolus_calculator)
        Action.BOLUS_CALCULATOR_RESULT_REMOVED -> rh.gs(app.aaps.core.ui.R.string.uel_bolus_calculator)
        Action.SMB                             -> rh.gs(app.aaps.core.ui.R.string.smb_shortname)
        Action.BOLUS_ADVISOR                   -> rh.gs(app.aaps.core.ui.R.string.uel_bolus_advisor)
        Action.EXTENDED_BOLUS                  -> rh.gs(app.aaps.core.ui.R.string.uel_extended_bolus)
        Action.SUPERBOLUS_TBR                  -> rh.gs(app.aaps.core.ui.R.string.uel_superbolus_tbr)
        Action.CARBS                           -> rh.gs(app.aaps.core.ui.R.string.uel_carbs)
        Action.EXTENDED_CARBS                  -> rh.gs(app.aaps.core.ui.R.string.uel_extended_carbs)
        Action.TEMP_BASAL                      -> rh.gs(app.aaps.core.ui.R.string.uel_temp_basal)
        Action.TT                              -> rh.gs(app.aaps.core.ui.R.string.uel_tt)
        Action.NEW_PROFILE                     -> rh.gs(app.aaps.core.ui.R.string.uel_new_profile)
        Action.CLONE_PROFILE                   -> rh.gs(app.aaps.core.ui.R.string.uel_clone_profile)
        Action.STORE_PROFILE                   -> rh.gs(app.aaps.core.ui.R.string.uel_store_profile)
        Action.PROFILE_SWITCH                  -> rh.gs(app.aaps.core.ui.R.string.uel_profile_switch)
        Action.PROFILE_SWITCH_CLONED           -> rh.gs(app.aaps.core.ui.R.string.uel_profile_switch_cloned)
        Action.CLOSED_LOOP_MODE                -> rh.gs(app.aaps.core.ui.R.string.uel_closed_loop_mode)
        Action.LGS_LOOP_MODE                   -> rh.gs(app.aaps.core.ui.R.string.uel_lgs_loop_mode)
        Action.OPEN_LOOP_MODE                  -> rh.gs(app.aaps.core.ui.R.string.uel_open_loop_mode)
        Action.LOOP_DISABLED                   -> rh.gs(app.aaps.core.ui.R.string.uel_loop_disabled)
        Action.LOOP_ENABLED                    -> rh.gs(app.aaps.core.ui.R.string.uel_loop_enabled)
        Action.RECONNECT                       -> rh.gs(app.aaps.core.ui.R.string.uel_reconnect)
        Action.DISCONNECT                      -> rh.gs(app.aaps.core.ui.R.string.uel_disconnect)
        Action.RESUME                          -> rh.gs(app.aaps.core.ui.R.string.uel_resume)
        Action.SUSPEND                         -> rh.gs(app.aaps.core.ui.R.string.uel_suspend)
        Action.HW_PUMP_ALLOWED                 -> rh.gs(app.aaps.core.ui.R.string.uel_hw_pump_allowed)
        Action.CLEAR_PAIRING_KEYS              -> rh.gs(app.aaps.core.ui.R.string.uel_clear_pairing_keys)
        Action.ACCEPTS_TEMP_BASAL              -> rh.gs(app.aaps.core.ui.R.string.uel_accepts_temp_basal)
        Action.CANCEL_TEMP_BASAL               -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_temp_basal)
        Action.CANCEL_BOLUS                    -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_bolus)
        Action.CANCEL_EXTENDED_BOLUS           -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_extended_bolus)
        Action.CANCEL_TT                       -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_tt)
        Action.CAREPORTAL                      -> rh.gs(app.aaps.core.ui.R.string.uel_careportal)
        Action.SITE_CHANGE                     -> rh.gs(app.aaps.core.ui.R.string.uel_site_change)
        Action.RESERVOIR_CHANGE                -> rh.gs(app.aaps.core.ui.R.string.uel_reservoir_change)
        Action.CALIBRATION                     -> rh.gs(app.aaps.core.ui.R.string.uel_calibration)
        Action.PRIME_BOLUS                     -> rh.gs(app.aaps.core.ui.R.string.uel_prime_bolus)
        Action.TREATMENT                       -> rh.gs(app.aaps.core.ui.R.string.uel_treatment)
        Action.CAREPORTAL_NS_REFRESH           -> rh.gs(app.aaps.core.ui.R.string.uel_careportal_ns_refresh)
        Action.PROFILE_SWITCH_NS_REFRESH       -> rh.gs(app.aaps.core.ui.R.string.uel_profile_switch_ns_refresh)
        Action.TREATMENTS_NS_REFRESH           -> rh.gs(app.aaps.core.ui.R.string.uel_treatments_ns_refresh)
        Action.TT_NS_REFRESH                   -> rh.gs(app.aaps.core.ui.R.string.uel_tt_ns_refresh)
        Action.AUTOMATION_REMOVED              -> rh.gs(app.aaps.core.ui.R.string.uel_automation_removed)
        Action.BG_REMOVED                      -> rh.gs(app.aaps.core.ui.R.string.uel_bg_removed)
        Action.CAREPORTAL_REMOVED              -> rh.gs(app.aaps.core.ui.R.string.uel_careportal_removed)
        Action.BOLUS_REMOVED                   -> rh.gs(app.aaps.core.ui.R.string.uel_bolus_removed)
        Action.CARBS_REMOVED                   -> rh.gs(app.aaps.core.ui.R.string.uel_carbs_removed)
        Action.TEMP_BASAL_REMOVED              -> rh.gs(app.aaps.core.ui.R.string.uel_temp_basal_removed)
        Action.EXTENDED_BOLUS_REMOVED          -> rh.gs(app.aaps.core.ui.R.string.uel_extended_bolus_removed)
        Action.FOOD                            -> rh.gs(app.aaps.core.ui.R.string.uel_food)
        Action.FOOD_REMOVED                    -> rh.gs(app.aaps.core.ui.R.string.uel_food_removed)
        Action.PROFILE_REMOVED                 -> rh.gs(app.aaps.core.ui.R.string.uel_profile_removed)
        Action.PROFILE_SWITCH_REMOVED          -> rh.gs(app.aaps.core.ui.R.string.uel_profile_switch_removed)
        Action.RESTART_EVENTS_REMOVED          -> rh.gs(app.aaps.core.ui.R.string.uel_restart_events_removed)
        Action.TREATMENT_REMOVED               -> rh.gs(app.aaps.core.ui.R.string.uel_treatment_removed)
        Action.TT_REMOVED                      -> rh.gs(app.aaps.core.ui.R.string.uel_tt_removed)
        Action.NS_PAUSED                       -> rh.gs(app.aaps.core.ui.R.string.uel_ns_paused)
        Action.NS_RESUME                       -> rh.gs(app.aaps.core.ui.R.string.uel_ns_resume)
        Action.NS_QUEUE_CLEARED                -> rh.gs(app.aaps.core.ui.R.string.uel_ns_queue_cleared)
        Action.NS_SETTINGS_COPIED              -> rh.gs(app.aaps.core.ui.R.string.uel_ns_settings_copied)
        Action.ERROR_DIALOG_OK                 -> rh.gs(app.aaps.core.ui.R.string.uel_error_dialog_ok)
        Action.ERROR_DIALOG_MUTE               -> rh.gs(app.aaps.core.ui.R.string.uel_error_dialog_mute)
        Action.ERROR_DIALOG_MUTE_5MIN          -> rh.gs(app.aaps.core.ui.R.string.uel_error_dialog_mute_5min)
        Action.OBJECTIVE_STARTED               -> rh.gs(app.aaps.core.ui.R.string.uel_objective_started)
        Action.OBJECTIVE_UNSTARTED             -> rh.gs(app.aaps.core.ui.R.string.uel_objective_unstarted)
        Action.OBJECTIVES_SKIPPED              -> rh.gs(app.aaps.core.ui.R.string.uel_objectives_skipped)
        Action.STAT_RESET                      -> rh.gs(app.aaps.core.ui.R.string.uel_stat_reset)
        Action.DELETE_LOGS                     -> rh.gs(app.aaps.core.ui.R.string.uel_delete_logs)
        Action.DELETE_FUTURE_TREATMENTS        -> rh.gs(app.aaps.core.ui.R.string.uel_delete_future_treatments)
        Action.EXPORT_SETTINGS                 -> rh.gs(app.aaps.core.ui.R.string.uel_export_settings)
        Action.IMPORT_SETTINGS                 -> rh.gs(app.aaps.core.ui.R.string.uel_import_settings)
        Action.SELECT_DIRECTORY                -> rh.gs(app.aaps.core.ui.R.string.uel_select_directory)
        Action.RESET_DATABASES                 -> rh.gs(app.aaps.core.ui.R.string.uel_reset_databases)
        Action.RESET_APS_RESULTS               -> rh.gs(app.aaps.core.ui.R.string.uel_reset_aps_results)
        Action.CLEANUP_DATABASES               -> rh.gs(app.aaps.core.ui.R.string.uel_cleanup_databases)
        Action.EXPORT_DATABASES                -> rh.gs(app.aaps.core.ui.R.string.uel_export_databases)
        Action.IMPORT_DATABASES                -> rh.gs(app.aaps.core.ui.R.string.uel_import_databases)
        Action.OTP_EXPORT                      -> rh.gs(app.aaps.core.ui.R.string.uel_otp_export)
        Action.OTP_RESET                       -> rh.gs(app.aaps.core.ui.R.string.uel_otp_reset)
        Action.EXPORT_CSV                      -> rh.gs(app.aaps.core.ui.R.string.uel_export_csv)
        Action.STOP_SMS                        -> rh.gs(app.aaps.core.ui.R.string.uel_stop_sms)
        Action.START_AAPS                      -> rh.gs(app.aaps.core.ui.R.string.uel_start_aaps)
        Action.EXIT_AAPS                       -> rh.gs(app.aaps.core.ui.R.string.uel_exit_aaps)
        Action.PLUGIN_ENABLED                  -> rh.gs(app.aaps.core.ui.R.string.uel_plugin_enabled)
        Action.PLUGIN_DISABLED                 -> rh.gs(app.aaps.core.ui.R.string.uel_plugin_disabled)
        Action.LOOP_CHANGE                     -> rh.gs(app.aaps.core.ui.R.string.uel_loop_change)
        Action.LOOP_REMOVED                    -> rh.gs(app.aaps.core.ui.R.string.uel_loop_removed)
        Action.TSUNAMI                         -> rh.gs(app.aaps.core.ui.R.string.uel_tsunami)
        Action.TSUNAMI_BOLUS                   -> rh.gs(app.aaps.core.ui.R.string.uel_tsunami)
        Action.CANCEL_TSUNAMI                  -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_tsunami)
        Action.CANCEL_TSUNAMI_BOLUS            -> rh.gs(app.aaps.core.ui.R.string.uel_cancel_tsunami_bolus)
        Action.UNKNOWN                         -> rh.gs(app.aaps.core.ui.R.string.unknown)
    }

    override fun translate(units: ValueWithUnit?): String = when (units) {
        is ValueWithUnit.Gram        -> rh.gs(app.aaps.core.ui.R.string.shortgram)
        is ValueWithUnit.Hour        -> rh.gs(app.aaps.core.interfaces.R.string.shorthour)
        is ValueWithUnit.Insulin     -> rh.gs(app.aaps.core.ui.R.string.insulin_unit_shortname)
        is ValueWithUnit.Mgdl        -> rh.gs(app.aaps.core.ui.R.string.mgdl)
        is ValueWithUnit.Minute      -> rh.gs(app.aaps.core.interfaces.R.string.shortminute)
        is ValueWithUnit.Mmoll       -> rh.gs(app.aaps.core.ui.R.string.mmol)
        is ValueWithUnit.Percent     -> rh.gs(app.aaps.core.ui.R.string.shortpercent)
        is ValueWithUnit.UnitPerHour -> rh.gs(app.aaps.core.ui.R.string.profile_ins_units_per_hour)
        else                         -> ""
    }

    override fun translate(meterType: TE.MeterType?): String = when (meterType) {
        TE.MeterType.FINGER -> rh.gs(app.aaps.core.ui.R.string.glucosetype_finger)
        TE.MeterType.SENSOR -> rh.gs(app.aaps.core.ui.R.string.glucosetype_sensor)
        TE.MeterType.MANUAL -> rh.gs(app.aaps.core.ui.R.string.manual)

        else                -> rh.gs(app.aaps.core.ui.R.string.unknown)
    }

    override fun translate(type: TE.Type?): String = when (type) {
        TE.Type.FINGER_STICK_BG_VALUE   -> rh.gs(app.aaps.core.ui.R.string.careportal_bgcheck)
        TE.Type.SNACK_BOLUS             -> rh.gs(app.aaps.core.ui.R.string.careportal_snackbolus)
        TE.Type.MEAL_BOLUS              -> rh.gs(app.aaps.core.ui.R.string.careportal_mealbolus)
        TE.Type.CORRECTION_BOLUS        -> rh.gs(app.aaps.core.ui.R.string.careportal_correctionbolus)
        TE.Type.CARBS_CORRECTION        -> rh.gs(app.aaps.core.ui.R.string.careportal_carbscorrection)
        TE.Type.BOLUS_WIZARD            -> rh.gs(app.aaps.core.ui.R.string.boluswizard)
        TE.Type.COMBO_BOLUS             -> rh.gs(app.aaps.core.ui.R.string.careportal_combobolus)
        TE.Type.ANNOUNCEMENT            -> rh.gs(app.aaps.core.ui.R.string.careportal_announcement)
        TE.Type.SETTINGS_EXPORT         -> rh.gs(app.aaps.core.ui.R.string.careportal_settings_export)
        TE.Type.NOTE                    -> rh.gs(app.aaps.core.ui.R.string.careportal_note)
        TE.Type.QUESTION                -> rh.gs(app.aaps.core.ui.R.string.careportal_question)
        TE.Type.EXERCISE                -> rh.gs(app.aaps.core.ui.R.string.careportal_exercise)
        TE.Type.CANNULA_CHANGE          -> rh.gs(app.aaps.core.ui.R.string.careportal_pump_site_change)
        TE.Type.PUMP_BATTERY_CHANGE     -> rh.gs(app.aaps.core.ui.R.string.pump_battery_change)
        TE.Type.SENSOR_STARTED          -> rh.gs(app.aaps.core.ui.R.string.careportal_cgmsensorstart)
        TE.Type.SENSOR_STOPPED          -> rh.gs(app.aaps.core.ui.R.string.careportal_cgm_sensor_stop)
        TE.Type.SENSOR_CHANGE           -> rh.gs(app.aaps.core.ui.R.string.cgm_sensor_insert)
        TE.Type.INSULIN_CHANGE          -> rh.gs(app.aaps.core.ui.R.string.careportal_insulin_cartridge_change)
        TE.Type.DAD_ALERT               -> rh.gs(app.aaps.core.ui.R.string.careportal_dad_alert)
        TE.Type.TEMPORARY_BASAL_START   -> rh.gs(app.aaps.core.ui.R.string.careportal_tempbasalstart)
        TE.Type.TEMPORARY_BASAL_END     -> rh.gs(app.aaps.core.ui.R.string.careportal_tempbasalend)
        TE.Type.PROFILE_SWITCH          -> rh.gs(app.aaps.core.ui.R.string.careportal_profileswitch)
        TE.Type.TEMPORARY_TARGET        -> rh.gs(app.aaps.core.ui.R.string.temporary_target)
        TE.Type.TEMPORARY_TARGET_CANCEL -> rh.gs(app.aaps.core.ui.R.string.careportal_temporarytargetcancel)
        TE.Type.APS_OFFLINE             -> rh.gs(app.aaps.core.ui.R.string.careportal_openapsoffline)
        TE.Type.NS_MBG                  -> rh.gs(app.aaps.core.ui.R.string.careportal_mbg)
        /*
                TE.Type.TEMPORARY_BASAL         -> TODO()
                TE.Type.TUBE_CHANGE             -> TODO()
                TE.Type.FALLING_ASLEEP          -> TODO()
                TE.Type.BATTERY_EMPTY           -> TODO()
                TE.Type.RESERVOIR_EMPTY         -> TODO()
                TE.Type.OCCLUSION               -> TODO()
                TE.Type.PUMP_STOPPED            -> TODO()
                TE.Type.PUMP_STARTED            -> TODO()
                TE.Type.PUMP_PAUSED             -> TODO()
                TE.Type.WAKING_UP               -> TODO()
                TE.Type.SICKNESS                -> TODO()
                TE.Type.STRESS                  -> TODO()
                TE.Type.PRE_PERIOD              -> TODO()
                TE.Type.ALCOHOL                 -> TODO()
                TE.Type.CORTISONE               -> TODO()
                TE.Type.FEELING_LOW             -> TODO()
                TE.Type.FEELING_HIGH            -> TODO()
                TE.Type.LEAKING_INFUSION_SET    -> TODO()
         */
        TE.Type.NONE                    -> rh.gs(app.aaps.core.ui.R.string.unknown)

        else                            -> rh.gs(app.aaps.core.ui.R.string.unknown)
    }

    override fun translate(reason: TT.Reason?): String = when (reason) {
        TT.Reason.CUSTOM       -> rh.gs(app.aaps.core.ui.R.string.custom)
        TT.Reason.HYPOGLYCEMIA -> rh.gs(app.aaps.core.ui.R.string.hypo)
        TT.Reason.EATING_SOON  -> rh.gs(app.aaps.core.ui.R.string.eatingsoon)
        TT.Reason.ACTIVITY     -> rh.gs(app.aaps.core.ui.R.string.activity)
        TT.Reason.AUTOMATION   -> rh.gs(app.aaps.core.ui.R.string.automation)
        TT.Reason.WEAR         -> rh.gs(app.aaps.core.ui.R.string.wear)

        else                   -> rh.gs(app.aaps.core.ui.R.string.unknown)
    }

    override fun translate(reason: OE.Reason?): String = when (reason) {
        OE.Reason.SUSPEND         -> rh.gs(app.aaps.core.ui.R.string.uel_suspend)
        OE.Reason.DISABLE_LOOP    -> rh.gs(app.aaps.core.ui.R.string.disableloop)
        OE.Reason.DISCONNECT_PUMP -> rh.gs(app.aaps.core.ui.R.string.uel_disconnect)
        OE.Reason.OTHER           -> rh.gs(app.aaps.core.ui.R.string.uel_other)

        else                      -> rh.gs(app.aaps.core.ui.R.string.unknown)
    }

    override fun translate(source: Sources): String = when (source) {
        /*
        Sources.TreatmentDialog                    -> TODO()
        Sources.InsulinDialog                      -> TODO()
        Sources.CarbDialog                         -> TODO()
        Sources.WizardDialog                       -> TODO()
        Sources.QuickWizard                        -> TODO()
        Sources.ExtendedBolusDialog                -> TODO()
        Sources.TTDialog                           -> TODO()
        Sources.ProfileSwitchDialog                -> TODO()
        Sources.LoopDialog                         -> TODO()
        Sources.TempBasalDialog                    -> TODO()
        Sources.CalibrationDialog                  -> TODO()
        Sources.FillDialog                         -> TODO()

        Sources.BgCheck                            -> TODO()
        Sources.SensorInsert                       -> TODO()
        Sources.BatteryChange                      -> TODO()
        Sources.Note                               -> TODO()
        Sources.Exercise                           -> TODO()
        Sources.Question                           -> TODO()
        Sources.Announcement                       -> TODO()
        Sources.Actions                            -> TODO()
        Sources.BG                                 -> TODO()
        Sources.LocalProfile                       -> TODO()
        Sources.Maintenance                        -> TODO()
        Sources.NSProfile                          -> TODO()
        Sources.Objectives                         -> TODO()
        Sources.Treatments                         -> TODO()
        Sources.Food                               -> TODO()
        Sources.ConfigBuilder                      -> TODO()
        Sources.Overview                           -> TODO()
        Sources.Stats                              -> TODO()
        Sources.TreatmentDialog                    -> TODO()
        Sources.InsulinDialog                      -> TODO()
        Sources.CarbDialog                         -> TODO()
        Sources.WizardDialog                       -> TODO()
        Sources.QuickWizard                        -> TODO()
        Sources.ExtendedBolusDialog                -> TODO()
        Sources.TTDialog                           -> TODO()
        Sources.ProfileSwitchDialog                -> TODO()
        Sources.LoopDialog                         -> TODO()
        Sources.TempBasalDialog                    -> TODO()
        Sources.CalibrationDialog                  -> TODO()
        Sources.FillDialog                         -> TODO()
        Sources.BgCheck                            -> TODO()
        Sources.SensorInsert                       -> TODO()
        Sources.BatteryChange                      -> TODO()
        Sources.Note                               -> TODO()
        Sources.Exercise                           -> TODO()
        Sources.Question                           -> TODO()
        Sources.Announcement                       -> TODO()
        Sources.Actions                            -> TODO()
        Sources.BG                                 -> TODO()
        Sources.Dexcom                             -> TODO()
        Sources.Eversense                          -> TODO()
        Sources.Glimp                              -> TODO()
        Sources.MM640g                             -> TODO()
        Sources.NSClientSource                     -> TODO()
        Sources.PocTech                            -> TODO()
        Sources.Aidex                              -> TODO()
        Sources.Tomato                             -> TODO()
        Sources.Xdrip                              -> TODO()
        Sources.LocalProfile                       -> TODO()
        Sources.Maintenance                        -> TODO()
        Sources.NSProfile                          -> TODO()
        Sources.Objectives                         -> TODO()
        Sources.Dana                               -> TODO()
        Sources.DanaR                              -> TODO()
        Sources.DanaRC                             -> TODO()
        Sources.DanaRv2                            -> TODO()
        Sources.DanaRS                             -> TODO()
        Sources.DanaI                              -> TODO()
        Sources.DiaconnG8                          -> TODO()
        Sources.Insight                            -> TODO()
        Sources.Combo                              -> TODO()
        Sources.Medtronic                          -> TODO()
        Sources.Omnipod                            -> TODO()
        Sources.OmnipodEros                        -> TODO()
        Sources.OmnipodDash                        -> TODO()
        Sources.MDI                                -> TODO()
        Sources.VirtualPump                        -> TODO()
        Sources.Treatments                         -> TODO()
        Sources.Food                               -> TODO()
        Sources.ConfigBuilder                      -> TODO()
        Sources.Overview                           -> TODO()
        Sources.Stats                              -> TODO()
        Sources.Aaps                               -> TODO()
        */
        Sources.Automation     -> rh.gs(app.aaps.core.ui.R.string.automation)
        Sources.SettingsExport -> rh.gs(app.aaps.core.ui.R.string.settingsexport)
        Sources.Autotune       -> rh.gs(app.aaps.core.ui.R.string.autotune)
        Sources.Loop           -> rh.gs(app.aaps.core.ui.R.string.loop)
        Sources.NSClient       -> rh.gs(app.aaps.core.ui.R.string.ns)
        Sources.Pump           -> rh.gs(app.aaps.core.ui.R.string.pump)
        Sources.SMS            -> rh.gs(app.aaps.core.ui.R.string.sms)
        Sources.Wear           -> rh.gs(app.aaps.core.ui.R.string.wear)
        Sources.Unknown        -> rh.gs(app.aaps.core.ui.R.string.unknown)

        else                   -> source.name
    }
}
