@file:Suppress("UNUSED_VARIABLE", "LocalVariableName")
package com.tencent.secprotocol

import com.github.unidbg.linux.android.dvm.DvmObject
import com.github.unidbg.linux.android.dvm.array.ArrayObject
import moe.fuqiuluo.unidbg.QSecVM

object ByteData {
    fun getByte(vm: QSecVM, uin: String, data: String, salt: ByteArray, guid: String): ByteArray {
        val context = vm.newInstance("android/content/Context", unique = true)
        val method = "getByte(Landroid/content/Context;JJJJLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)[B"
        val ByteData = vm.newInstance("com/tencent/secprotocol/ByteData", unique = true)
        val obj = ArrayObject.newStringArray(vm.vm, *arrayOf("1", "1", uin, data, guid, "0", "", "init", vm.envData.packageName + ":MSF"))
        return ByteData.callJniMethodObject<DvmObject<*>>(vm.emulator, method,
            context, 1L, 0L, 0L, 0L, obj, "", uin, salt
        ).value as ByteArray
    }
}