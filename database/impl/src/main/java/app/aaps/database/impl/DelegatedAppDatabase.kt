package app.aaps.database.impl

import app.aaps.database.entities.interfaces.DBEntry
import app.aaps.database.impl.daos.*
import app.aaps.database.impl.daos.delegated.*

internal class DelegatedAppDatabase(val changes: MutableList<DBEntry>, val database: AppDatabase) {

    val glucoseValueDao: GlucoseValueDao = DelegatedGlucoseValueDao(changes, database.glucoseValueDao)
    val therapyEventDao: TherapyEventDao = DelegatedTherapyEventDao(changes, database.therapyEventDao)
    val temporaryBasalDao: TemporaryBasalDao = DelegatedTemporaryBasalDao(changes, database.temporaryBasalDao)
    val bolusDao: BolusDao = DelegatedBolusDao(changes, database.bolusDao)
    val extendedBolusDao: ExtendedBolusDao = DelegatedExtendedBolusDao(changes, database.extendedBolusDao)
    val multiwaveBolusLinkDao: MultiwaveBolusLinkDao = DelegatedMultiwaveBolusLinkDao(changes, database.multiwaveBolusLinkDao)
    val totalDailyDoseDao: TotalDailyDoseDao = DelegatedTotalDailyDoseDao(changes, database.totalDailyDoseDao)
    val carbsDao: CarbsDao = DelegatedCarbsDao(changes, database.carbsDao)
    val temporaryTargetDao: TemporaryTargetDao = DelegatedTemporaryTargetDao(changes, database.temporaryTargetDao)
    val apsResultLinkDao: APSResultLinkDao = DelegatedAPSResultLinkDao(changes, database.apsResultLinkDao)
    val bolusCalculatorResultDao: BolusCalculatorResultDao = DelegatedBolusCalculatorResultDao(changes, database.bolusCalculatorResultDao)
    val effectiveProfileSwitchDao: EffectiveProfileSwitchDao = DelegatedEffectiveProfileSwitchDao(changes, database.effectiveProfileSwitchDao)
    val profileSwitchDao: ProfileSwitchDao = DelegatedProfileSwitchDao(changes, database.profileSwitchDao)
    val apsResultDao: APSResultDao = DelegatedAPSResultDao(changes, database.apsResultDao)
    val versionChangeDao: VersionChangeDao = DelegatedVersionChangeDao(changes, database.versionChangeDao)
    val userEntryDao: UserEntryDao = DelegatedUserEntryDao(changes, database.userEntryDao)
    val preferenceChangeDao: PreferenceChangeDao = DelegatedPreferenceChangeDao(changes, database.preferenceChangeDao)
    val foodDao: FoodDao = DelegatedFoodDao(changes, database.foodDao)
    val deviceStatusDao: DeviceStatusDao = DelegatedDeviceStatusDao(changes, database.deviceStatusDao)
    val offlineEventDao: OfflineEventDao = DelegatedOfflineEventDao(changes, database.offlineEventDao)
    val heartRateDao: HeartRateDao = DelegatedHeartRateDao(changes, database.heartRateDao)
    val tsunamiDao: TsunamiDao = DelegatedTsunamiDao(changes, database.tsunamiDao)
    fun clearAllTables() = database.clearAllTables()
}
