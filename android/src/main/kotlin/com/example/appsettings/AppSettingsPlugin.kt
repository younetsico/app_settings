package com.example.appsettings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


class AppSettingsPlugin() : MethodCallHandler, FlutterPlugin, ActivityAware {
    /// Private variable to hold instance of Registrar for creating Intents.
    private lateinit var activity: Activity

    /// Private method to open device settings window
    private fun openSettings(url: String, asAnotherTask: Boolean = false, uri: Uri? = null) {
        try {
            val intent = if (uri != null) Intent(url, uri) else Intent(url)
            if (asAnotherTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.activity.startActivity(intent)
        } catch (e: Exception) {
            // Default to APP Settings if setting activity fails to load/be available on device
            openAppSettings(asAnotherTask)
        }
    }

    private fun openAppSettings(asAnotherTask: Boolean = false) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        if (asAnotherTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", this.activity.packageName, null)
        intent.data = uri
        this.activity.startActivity(intent)
    }

    /// Main constructor to setup the Registrar
    constructor(registrar: Registrar) : this() {
        this.activity = registrar.activity()
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "app_settings")
            channel.setMethodCallHandler(AppSettingsPlugin(registrar))
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(binding.binaryMessenger, "app_settings")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {

    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivity() {
    }

    /// Handler method to manage method channel calls.
    override fun onMethodCall(call: MethodCall, result: Result) {

        val asAnotherTask = call.argument("asAnotherTask") ?: false

        if (call.method == "wifi") {
            openSettings(Settings.ACTION_WIFI_SETTINGS, asAnotherTask)
        } else if (call.method == "location") {
            openSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS, asAnotherTask)
        } else if (call.method == "security") {
            openSettings(Settings.ACTION_SECURITY_SETTINGS, asAnotherTask)
        } else if (call.method == "bluetooth") {
            openSettings(Settings.ACTION_BLUETOOTH_SETTINGS, asAnotherTask)
        } else if (call.method == "data_roaming") {
            openSettings(Settings.ACTION_DATA_ROAMING_SETTINGS, asAnotherTask)
        } else if (call.method == "date") {
            openSettings(Settings.ACTION_DATE_SETTINGS, asAnotherTask)
        } else if (call.method == "display") {
            openSettings(Settings.ACTION_DISPLAY_SETTINGS, asAnotherTask)
        } else if (call.method == "notification") {
            if (Build.VERSION.SDK_INT >= 21) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, this.activity.packageName)
                if (asAnotherTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.activity.startActivity(intent)
            } else {
                openSettings(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS, asAnotherTask)
            }
        } else if (call.method == "nfc") {
            openSettings(Settings.ACTION_NFC_SETTINGS, asAnotherTask)
        } else if (call.method == "sound") {
            openSettings(Settings.ACTION_SOUND_SETTINGS, asAnotherTask)
        } else if (call.method == "internal_storage") {
            openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS, asAnotherTask)
        } else if (call.method == "battery_optimization") {
            openSettings(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS, asAnotherTask)
        } else if (call.method == "vpn") {
            if (Build.VERSION.SDK_INT >= 24) {
                openSettings(Settings.ACTION_VPN_SETTINGS, asAnotherTask)
            } else {
                openSettings("android.net.vpn.SETTINGS", asAnotherTask)
            }
        } else if (call.method == "app_settings") {
            openAppSettings(asAnotherTask)
        } else if (call.method == "device_settings") {
            openSettings(Settings.ACTION_SETTINGS, asAnotherTask)
        } else if (call.method == "system_alert_settings") {
            openSettings(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, asAnotherTask, uri = Uri.parse("package:" + this.activity.packageName))
        }
    }
}