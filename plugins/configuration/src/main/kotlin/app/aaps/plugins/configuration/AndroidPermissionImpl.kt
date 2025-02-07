package app.aaps.plugins.configuration

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import app.aaps.core.interfaces.androidPermissions.AndroidPermission
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.interfaces.notifications.Notification
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.pump.VirtualPump
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.ui.dialogs.OKDialog
import app.aaps.core.ui.toast.ToastUtils
import app.aaps.plugins.configuration.activities.DaggerAppCompatActivityWithResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPermissionImpl @Inject constructor(
    private val rh: ResourceHelper,
    private val activePlugin: ActivePlugin,
    private val config: Config
) : AndroidPermission {

    private var permissionBatteryOptimizationFailed = false

    @SuppressLint("BatteryLife")
    override fun askForPermission(activity: FragmentActivity, permissions: Array<String>) {
        var test = false
        var testBattery = false
        for (s in permissions) {
            test = test || ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED
            if (s == Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
                val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                val packageName = activity.packageName
                testBattery = testBattery || !powerManager.isIgnoringBatteryOptimizations(packageName)
            }
        }
        if (test) {
            if (activity is DaggerAppCompatActivityWithResult)
                try {
                    activity.requestMultiplePermissions?.launch(permissions)
                } catch (_: IllegalStateException) {
                    ToastUtils.errorToast(activity, rh.gs(R.string.error_asking_for_permissions))
                }
        }
        if (testBattery) {
            try {
                if (activity is DaggerAppCompatActivityWithResult)
                    try {
                        activity.callForBatteryOptimization?.launch(null)
                    } catch (_: IllegalStateException) {
                        ToastUtils.errorToast(activity, rh.gs(R.string.error_asking_for_permissions))
                    }
            } catch (_: ActivityNotFoundException) {
                permissionBatteryOptimizationFailed = true
                OKDialog.show(activity, rh.gs(R.string.permission), rh.gs(R.string.alert_dialog_permission_battery_optimization_failed)) { activity.recreate() }
            }
        }
    }

    override fun askForPermission(activity: FragmentActivity, permission: String) = askForPermission(activity, arrayOf(permission))

    override fun permissionNotGranted(context: Context, permission: String): Boolean {
        var selfCheck = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (permission == Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (!permissionBatteryOptimizationFailed) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val packageName = context.packageName
                selfCheck = selfCheck && powerManager.isIgnoringBatteryOptimizations(packageName)
            }
        }
        return !selfCheck
    }

    @Synchronized
    override fun notifyForSMSPermissions(activity: FragmentActivity) {
        if (permissionNotGranted(activity, Manifest.permission.RECEIVE_SMS))
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_SMS,
                text = rh.gs(app.aaps.core.ui.R.string.smscommunicator_missingsmspermission),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) {
                askForPermission(
                    activity,
                    arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_MMS)
                )
            }
        else activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_SMS)
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    override fun notifyForBtConnectPermission(activity: FragmentActivity) {
        if (activePlugin.activePump !is VirtualPump)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                //  Manifest.permission.BLUETOOTH_CONNECT
                if (permissionNotGranted(activity, Manifest.permission.BLUETOOTH_CONNECT) || permissionNotGranted(activity, Manifest.permission.BLUETOOTH_SCAN))
                    activePlugin.activeOverview.addNotification(
                        id = Notification.PERMISSION_BT,
                        text = rh.gs(app.aaps.core.ui.R.string.need_connect_permission),
                        level = Notification.URGENT,
                        actionButtonId = R.string.request
                    ) { askForPermission(activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)) }
                else {
                    activity.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_BT)
                }
            }
    }

    @Synchronized
    override fun notifyForBatteryOptimizationPermission(activity: FragmentActivity) {
        if (permissionNotGranted(activity, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS))
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_BATTERY,
                text = rh.gs(R.string.need_whitelisting, rh.gs(config.appName)),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) { askForPermission(activity, arrayOf(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) }
        else activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_BATTERY)
    }

    @Synchronized override fun notifyForStoragePermission(activity: FragmentActivity) {
        if (permissionNotGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE))
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_STORAGE,
                text = rh.gs(R.string.need_storage_permission),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) { askForPermission(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) }
        else activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_STORAGE)
    }

    @Synchronized override fun notifyForLocationPermissions(activity: FragmentActivity) {
        if (permissionNotGranted(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
            permissionNotGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_LOCATION,
                text = rh.gs(R.string.need_location_permission),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) {
                askForPermission(
                    activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else if (permissionNotGranted(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_LOCATION,
                text = rh.gs(R.string.need_background_location_permission),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) {
                askForPermission(activity, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
        } else activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_LOCATION)
    }

    @Synchronized override fun notifyForSystemWindowPermissions(activity: FragmentActivity) {
        // Check if Android Q or higher
        if (!Settings.canDrawOverlays(activity))
            activePlugin.activeOverview.addNotification(
                id = Notification.PERMISSION_SYSTEM_WINDOW,
                text = rh.gs(R.string.need_location_permission),
                level = Notification.URGENT,
                actionButtonId = R.string.request
            ) {
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.packageName)
                )
                activity.startActivity(intent)
            }
        else activePlugin.activeOverview.dismissNotification(Notification.PERMISSION_SYSTEM_WINDOW)
    }
}