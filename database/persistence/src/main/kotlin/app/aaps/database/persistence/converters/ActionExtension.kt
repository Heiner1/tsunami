package app.aaps.database.persistence.converters

import app.aaps.core.data.ue.Action
import app.aaps.database.entities.UserEntry

fun UserEntry.Action.fromDb(): Action =
    when (this) {
        UserEntry.Action.BOLUS                           -> Action.BOLUS
        UserEntry.Action.BOLUS_CALCULATOR_RESULT         -> Action.BOLUS_CALCULATOR_RESULT
        UserEntry.Action.BOLUS_CALCULATOR_RESULT_REMOVED -> Action.BOLUS_CALCULATOR_RESULT_REMOVED
        UserEntry.Action.SMB                             -> Action.SMB
        UserEntry.Action.BOLUS_ADVISOR                   -> Action.BOLUS_ADVISOR
        UserEntry.Action.EXTENDED_BOLUS                  -> Action.EXTENDED_BOLUS
        UserEntry.Action.SUPERBOLUS_TBR                  -> Action.SUPERBOLUS_TBR
        UserEntry.Action.CARBS                           -> Action.CARBS
        UserEntry.Action.EXTENDED_CARBS                  -> Action.EXTENDED_CARBS
        UserEntry.Action.TEMP_BASAL                      -> Action.TEMP_BASAL
        UserEntry.Action.TT                              -> Action.TT
        UserEntry.Action.NEW_PROFILE                     -> Action.NEW_PROFILE
        UserEntry.Action.CLONE_PROFILE                   -> Action.CLONE_PROFILE
        UserEntry.Action.STORE_PROFILE                   -> Action.STORE_PROFILE
        UserEntry.Action.PROFILE_SWITCH                  -> Action.PROFILE_SWITCH
        UserEntry.Action.PROFILE_SWITCH_CLONED           -> Action.PROFILE_SWITCH_CLONED
        UserEntry.Action.CLOSED_LOOP_MODE                -> Action.CLOSED_LOOP_MODE
        UserEntry.Action.LGS_LOOP_MODE                   -> Action.LGS_LOOP_MODE
        UserEntry.Action.OPEN_LOOP_MODE                  -> Action.OPEN_LOOP_MODE
        UserEntry.Action.LOOP_DISABLED                   -> Action.LOOP_DISABLED
        UserEntry.Action.LOOP_ENABLED                    -> Action.LOOP_ENABLED
        UserEntry.Action.LOOP_CHANGE                     -> Action.LOOP_CHANGE
        UserEntry.Action.LOOP_REMOVED                    -> Action.LOOP_REMOVED
        UserEntry.Action.RECONNECT                       -> Action.RECONNECT
        UserEntry.Action.DISCONNECT                      -> Action.DISCONNECT
        UserEntry.Action.RESUME                          -> Action.RESUME
        UserEntry.Action.SUSPEND                         -> Action.SUSPEND
        UserEntry.Action.HW_PUMP_ALLOWED                 -> Action.HW_PUMP_ALLOWED
        UserEntry.Action.CLEAR_PAIRING_KEYS              -> Action.CLEAR_PAIRING_KEYS
        UserEntry.Action.ACCEPTS_TEMP_BASAL              -> Action.ACCEPTS_TEMP_BASAL
        UserEntry.Action.CANCEL_TEMP_BASAL               -> Action.CANCEL_TEMP_BASAL
        UserEntry.Action.CANCEL_BOLUS                    -> Action.CANCEL_BOLUS
        UserEntry.Action.CANCEL_EXTENDED_BOLUS           -> Action.CANCEL_EXTENDED_BOLUS
        UserEntry.Action.CANCEL_TT                       -> Action.CANCEL_TT
        UserEntry.Action.CAREPORTAL                      -> Action.CAREPORTAL
        UserEntry.Action.SITE_CHANGE                     -> Action.SITE_CHANGE
        UserEntry.Action.RESERVOIR_CHANGE                -> Action.RESERVOIR_CHANGE
        UserEntry.Action.CALIBRATION                     -> Action.CALIBRATION
        UserEntry.Action.PRIME_BOLUS                     -> Action.PRIME_BOLUS
        UserEntry.Action.TREATMENT                       -> Action.TREATMENT
        UserEntry.Action.CAREPORTAL_NS_REFRESH           -> Action.CAREPORTAL_NS_REFRESH
        UserEntry.Action.PROFILE_SWITCH_NS_REFRESH       -> Action.PROFILE_SWITCH_NS_REFRESH
        UserEntry.Action.TREATMENTS_NS_REFRESH           -> Action.TREATMENTS_NS_REFRESH
        UserEntry.Action.TT_NS_REFRESH                   -> Action.TT_NS_REFRESH
        UserEntry.Action.AUTOMATION_REMOVED              -> Action.AUTOMATION_REMOVED
        UserEntry.Action.BG_REMOVED                      -> Action.BG_REMOVED
        UserEntry.Action.CAREPORTAL_REMOVED              -> Action.CAREPORTAL_REMOVED
        UserEntry.Action.EXTENDED_BOLUS_REMOVED          -> Action.EXTENDED_BOLUS_REMOVED
        UserEntry.Action.FOOD_REMOVED                    -> Action.FOOD_REMOVED
        UserEntry.Action.PROFILE_REMOVED                 -> Action.PROFILE_REMOVED
        UserEntry.Action.PROFILE_SWITCH_REMOVED          -> Action.PROFILE_SWITCH_REMOVED
        UserEntry.Action.RESTART_EVENTS_REMOVED          -> Action.RESTART_EVENTS_REMOVED
        UserEntry.Action.TREATMENT_REMOVED               -> Action.TREATMENT_REMOVED
        UserEntry.Action.BOLUS_REMOVED                   -> Action.BOLUS_REMOVED
        UserEntry.Action.CARBS_REMOVED                   -> Action.CARBS_REMOVED
        UserEntry.Action.TEMP_BASAL_REMOVED              -> Action.TEMP_BASAL_REMOVED
        UserEntry.Action.TT_REMOVED                      -> Action.TT_REMOVED
        UserEntry.Action.NS_PAUSED                       -> Action.NS_PAUSED
        UserEntry.Action.NS_RESUME                       -> Action.NS_RESUME
        UserEntry.Action.NS_QUEUE_CLEARED                -> Action.NS_QUEUE_CLEARED
        UserEntry.Action.NS_SETTINGS_COPIED              -> Action.NS_SETTINGS_COPIED
        UserEntry.Action.ERROR_DIALOG_OK                 -> Action.ERROR_DIALOG_OK
        UserEntry.Action.ERROR_DIALOG_MUTE               -> Action.ERROR_DIALOG_MUTE
        UserEntry.Action.ERROR_DIALOG_MUTE_5MIN          -> Action.ERROR_DIALOG_MUTE_5MIN
        UserEntry.Action.OBJECTIVE_STARTED               -> Action.OBJECTIVE_STARTED
        UserEntry.Action.OBJECTIVE_UNSTARTED             -> Action.OBJECTIVE_UNSTARTED
        UserEntry.Action.OBJECTIVES_SKIPPED              -> Action.OBJECTIVES_SKIPPED
        UserEntry.Action.STAT_RESET                      -> Action.STAT_RESET
        UserEntry.Action.DELETE_LOGS                     -> Action.DELETE_LOGS
        UserEntry.Action.DELETE_FUTURE_TREATMENTS        -> Action.DELETE_FUTURE_TREATMENTS
        UserEntry.Action.EXPORT_SETTINGS                 -> Action.EXPORT_SETTINGS
        UserEntry.Action.IMPORT_SETTINGS                 -> Action.IMPORT_SETTINGS
        UserEntry.Action.SELECT_DIRECTORY                -> Action.SELECT_DIRECTORY
        UserEntry.Action.RESET_DATABASES                 -> Action.RESET_DATABASES
        UserEntry.Action.RESET_APS_RESULTS               -> Action.RESET_APS_RESULTS
        UserEntry.Action.CLEANUP_DATABASES               -> Action.CLEANUP_DATABASES
        UserEntry.Action.EXPORT_DATABASES                -> Action.EXPORT_DATABASES
        UserEntry.Action.IMPORT_DATABASES                -> Action.IMPORT_DATABASES
        UserEntry.Action.OTP_EXPORT                      -> Action.OTP_EXPORT
        UserEntry.Action.OTP_RESET                       -> Action.OTP_RESET
        UserEntry.Action.STOP_SMS                        -> Action.STOP_SMS
        UserEntry.Action.FOOD                            -> Action.FOOD
        UserEntry.Action.EXPORT_CSV                      -> Action.EXPORT_CSV
        UserEntry.Action.START_AAPS                      -> Action.START_AAPS
        UserEntry.Action.EXIT_AAPS                       -> Action.EXIT_AAPS
        UserEntry.Action.PLUGIN_ENABLED                  -> Action.PLUGIN_ENABLED
        UserEntry.Action.PLUGIN_DISABLED                 -> Action.PLUGIN_DISABLED
        UserEntry.Action.TSUNAMI                         -> Action.TSUNAMI
        UserEntry.Action.TSUNAMI_BOLUS                   -> Action.TSUNAMI_BOLUS
        UserEntry.Action.CANCEL_TSUNAMI                  -> Action.CANCEL_TSUNAMI
        UserEntry.Action.CANCEL_TSUNAMI_BOLUS            -> Action.CANCEL_TSUNAMI_BOLUS
        UserEntry.Action.UNKNOWN                         -> Action.UNKNOWN
    }

fun Action.toDb(): UserEntry.Action =
    when (this) {
        Action.BOLUS                           -> UserEntry.Action.BOLUS
        Action.BOLUS_CALCULATOR_RESULT         -> UserEntry.Action.BOLUS_CALCULATOR_RESULT
        Action.BOLUS_CALCULATOR_RESULT_REMOVED -> UserEntry.Action.BOLUS_CALCULATOR_RESULT_REMOVED
        Action.SMB                             -> UserEntry.Action.SMB
        Action.BOLUS_ADVISOR                   -> UserEntry.Action.BOLUS_ADVISOR
        Action.EXTENDED_BOLUS                  -> UserEntry.Action.EXTENDED_BOLUS
        Action.SUPERBOLUS_TBR                  -> UserEntry.Action.SUPERBOLUS_TBR
        Action.CARBS                           -> UserEntry.Action.CARBS
        Action.EXTENDED_CARBS                  -> UserEntry.Action.EXTENDED_CARBS
        Action.TEMP_BASAL                      -> UserEntry.Action.TEMP_BASAL
        Action.TT                              -> UserEntry.Action.TT
        Action.NEW_PROFILE                     -> UserEntry.Action.NEW_PROFILE
        Action.CLONE_PROFILE                   -> UserEntry.Action.CLONE_PROFILE
        Action.STORE_PROFILE                   -> UserEntry.Action.STORE_PROFILE
        Action.PROFILE_SWITCH                  -> UserEntry.Action.PROFILE_SWITCH
        Action.PROFILE_SWITCH_CLONED           -> UserEntry.Action.PROFILE_SWITCH_CLONED
        Action.CLOSED_LOOP_MODE                -> UserEntry.Action.CLOSED_LOOP_MODE
        Action.LGS_LOOP_MODE                   -> UserEntry.Action.LGS_LOOP_MODE
        Action.OPEN_LOOP_MODE                  -> UserEntry.Action.OPEN_LOOP_MODE
        Action.LOOP_DISABLED                   -> UserEntry.Action.LOOP_DISABLED
        Action.LOOP_ENABLED                    -> UserEntry.Action.LOOP_ENABLED
        Action.LOOP_CHANGE                     -> UserEntry.Action.LOOP_CHANGE
        Action.LOOP_REMOVED                    -> UserEntry.Action.LOOP_REMOVED
        Action.RECONNECT                       -> UserEntry.Action.RECONNECT
        Action.DISCONNECT                      -> UserEntry.Action.DISCONNECT
        Action.RESUME                          -> UserEntry.Action.RESUME
        Action.SUSPEND                         -> UserEntry.Action.SUSPEND
        Action.HW_PUMP_ALLOWED                 -> UserEntry.Action.HW_PUMP_ALLOWED
        Action.CLEAR_PAIRING_KEYS              -> UserEntry.Action.CLEAR_PAIRING_KEYS
        Action.ACCEPTS_TEMP_BASAL              -> UserEntry.Action.ACCEPTS_TEMP_BASAL
        Action.CANCEL_TEMP_BASAL               -> UserEntry.Action.CANCEL_TEMP_BASAL
        Action.CANCEL_BOLUS                    -> UserEntry.Action.CANCEL_BOLUS
        Action.CANCEL_EXTENDED_BOLUS           -> UserEntry.Action.CANCEL_EXTENDED_BOLUS
        Action.CANCEL_TT                       -> UserEntry.Action.CANCEL_TT
        Action.CAREPORTAL                      -> UserEntry.Action.CAREPORTAL
        Action.SITE_CHANGE                     -> UserEntry.Action.SITE_CHANGE
        Action.RESERVOIR_CHANGE                -> UserEntry.Action.RESERVOIR_CHANGE
        Action.CALIBRATION                     -> UserEntry.Action.CALIBRATION
        Action.PRIME_BOLUS                     -> UserEntry.Action.PRIME_BOLUS
        Action.TREATMENT                       -> UserEntry.Action.TREATMENT
        Action.CAREPORTAL_NS_REFRESH           -> UserEntry.Action.CAREPORTAL_NS_REFRESH
        Action.PROFILE_SWITCH_NS_REFRESH       -> UserEntry.Action.PROFILE_SWITCH_NS_REFRESH
        Action.TREATMENTS_NS_REFRESH           -> UserEntry.Action.TREATMENTS_NS_REFRESH
        Action.TT_NS_REFRESH                   -> UserEntry.Action.TT_NS_REFRESH
        Action.AUTOMATION_REMOVED              -> UserEntry.Action.AUTOMATION_REMOVED
        Action.BG_REMOVED                      -> UserEntry.Action.BG_REMOVED
        Action.CAREPORTAL_REMOVED              -> UserEntry.Action.CAREPORTAL_REMOVED
        Action.EXTENDED_BOLUS_REMOVED          -> UserEntry.Action.EXTENDED_BOLUS_REMOVED
        Action.FOOD_REMOVED                    -> UserEntry.Action.FOOD_REMOVED
        Action.PROFILE_REMOVED                 -> UserEntry.Action.PROFILE_REMOVED
        Action.PROFILE_SWITCH_REMOVED          -> UserEntry.Action.PROFILE_SWITCH_REMOVED
        Action.RESTART_EVENTS_REMOVED          -> UserEntry.Action.RESTART_EVENTS_REMOVED
        Action.TREATMENT_REMOVED               -> UserEntry.Action.TREATMENT_REMOVED
        Action.BOLUS_REMOVED                   -> UserEntry.Action.BOLUS_REMOVED
        Action.CARBS_REMOVED                   -> UserEntry.Action.CARBS_REMOVED
        Action.TEMP_BASAL_REMOVED              -> UserEntry.Action.TEMP_BASAL_REMOVED
        Action.TT_REMOVED                      -> UserEntry.Action.TT_REMOVED
        Action.NS_PAUSED                       -> UserEntry.Action.NS_PAUSED
        Action.NS_RESUME                       -> UserEntry.Action.NS_RESUME
        Action.NS_QUEUE_CLEARED                -> UserEntry.Action.NS_QUEUE_CLEARED
        Action.NS_SETTINGS_COPIED              -> UserEntry.Action.NS_SETTINGS_COPIED
        Action.ERROR_DIALOG_OK                 -> UserEntry.Action.ERROR_DIALOG_OK
        Action.ERROR_DIALOG_MUTE               -> UserEntry.Action.ERROR_DIALOG_MUTE
        Action.ERROR_DIALOG_MUTE_5MIN          -> UserEntry.Action.ERROR_DIALOG_MUTE_5MIN
        Action.OBJECTIVE_STARTED               -> UserEntry.Action.OBJECTIVE_STARTED
        Action.OBJECTIVE_UNSTARTED             -> UserEntry.Action.OBJECTIVE_UNSTARTED
        Action.OBJECTIVES_SKIPPED              -> UserEntry.Action.OBJECTIVES_SKIPPED
        Action.STAT_RESET                      -> UserEntry.Action.STAT_RESET
        Action.DELETE_LOGS                     -> UserEntry.Action.DELETE_LOGS
        Action.DELETE_FUTURE_TREATMENTS        -> UserEntry.Action.DELETE_FUTURE_TREATMENTS
        Action.EXPORT_SETTINGS                 -> UserEntry.Action.EXPORT_SETTINGS
        Action.IMPORT_SETTINGS                 -> UserEntry.Action.IMPORT_SETTINGS
        Action.SELECT_DIRECTORY                -> UserEntry.Action.SELECT_DIRECTORY
        Action.RESET_DATABASES                 -> UserEntry.Action.RESET_DATABASES
        Action.RESET_APS_RESULTS               -> UserEntry.Action.RESET_APS_RESULTS
        Action.CLEANUP_DATABASES               -> UserEntry.Action.CLEANUP_DATABASES
        Action.EXPORT_DATABASES                -> UserEntry.Action.EXPORT_DATABASES
        Action.IMPORT_DATABASES                -> UserEntry.Action.IMPORT_DATABASES
        Action.OTP_EXPORT                      -> UserEntry.Action.OTP_EXPORT
        Action.OTP_RESET                       -> UserEntry.Action.OTP_RESET
        Action.STOP_SMS                        -> UserEntry.Action.STOP_SMS
        Action.FOOD                            -> UserEntry.Action.FOOD
        Action.EXPORT_CSV                      -> UserEntry.Action.EXPORT_CSV
        Action.START_AAPS                      -> UserEntry.Action.START_AAPS
        Action.EXIT_AAPS                       -> UserEntry.Action.EXIT_AAPS
        Action.PLUGIN_ENABLED                  -> UserEntry.Action.PLUGIN_ENABLED
        Action.PLUGIN_DISABLED                 -> UserEntry.Action.PLUGIN_DISABLED
        Action.TSUNAMI                         -> UserEntry.Action.TSUNAMI
        Action.TSUNAMI_BOLUS                   -> UserEntry.Action.TSUNAMI_BOLUS
        Action.CANCEL_TSUNAMI                  -> UserEntry.Action.CANCEL_TSUNAMI
        Action.CANCEL_TSUNAMI_BOLUS            -> UserEntry.Action.CANCEL_TSUNAMI_BOLUS
        Action.UNKNOWN                         -> UserEntry.Action.UNKNOWN
    }
