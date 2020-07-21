package oms.masm.library.permissionx

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

/**
 * NAME: 柚子啊
 * DATE: 2020-07-21
 * DESC: Basic interfaces for developers to use PermissionX functions.
 */

class PermissionBuilder internal constructor(private val activity: FragmentActivity) {

    private var explainReasonCallback: Callback? = null
    private var forwardToSettingsCallback: Callback? = null
    private var requestCallback: RequestCallback? = null
    private var showRequestReasonAtFirst = false

    /**
     * 是否需要弹出解释权限申请的原因
     * 可用于被用户拒绝后需要重新弹出权限申请前的解释
     */
    fun shouldExplainRequestReason(block: Callback): PermissionBuilder {
        explainReasonCallback = block
        return this
    }

    /**
     * 用于监听被用户永久拒绝的权限（拒绝且不再询问）
     * 引导用户去设置中手动打开
     */
    fun shouldForwardToSettings(block: Callback): PermissionBuilder {
        forwardToSettingsCallback = block
        return this
    }

    /**
     * 申请权限前向用户解释原因
     */
    fun explainRequestReasonAtFirst(): PermissionBuilder {
        showRequestReasonAtFirst = true
        return this
    }

    fun showRequestReasonDialog(permissions: List<String>, message: String, positiveText: String) {
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setCancelable(false)
            setPositiveButton(positiveText) { _, _ ->
                requestAgain(permissions)
            }
            show()
        }
    }

    fun requestAgain(permissions: List<String>) {
        requestCallback?.let {
            request(*permissions.toTypedArray(), callback = it)
        }
    }

    fun showForwardToSettingsDialog(message: String, positiveText: String, negativeText: String? = null) {
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setCancelable(false)
            setPositiveButton(positiveText) { _, _ ->
                forwardToSettings()
            }
            negativeText?.let {
                setNegativeButton(it, null)
            }
            show()
        }
    }

    fun forwardToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        getInvisibleFragment().startActivityForResult(intent, SETTINGS_CODE)
    }

    fun request(vararg permissions: String, callback: RequestCallback) {
        requestCallback = callback
        if (showRequestReasonAtFirst && explainReasonCallback != null) {
            showRequestReasonAtFirst = false
            explainReasonCallback?.let { it(permissions.toMutableList()) }
        } else {
            getInvisibleFragment().requestNow(this, explainReasonCallback, forwardToSettingsCallback, callback, *permissions)
        }
    }

    private fun getInvisibleFragment(): InvisibleFragment {
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        return if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
    }

}