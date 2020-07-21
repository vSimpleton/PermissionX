package oms.masm.library.permissionx

import androidx.fragment.app.FragmentActivity

/**
 * NAME: 柚子啊
 * DATE: 2020-05-14
 * DESC: PermissionX单例
 */

object PermissionX {

    fun init(activity: FragmentActivity) = PermissionBuilder(activity)

}