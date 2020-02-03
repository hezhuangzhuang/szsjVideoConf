package com.hw.baselibrary.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hw.baselibrary.common.BaseApp

/**
 *author：pc-20171125
 *data:2019/11/8 09:19
 *
 * 剪贴板相关工具类
 *
 */
object ClipboardUtils {
    private fun ClipboardUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    fun copyText(text: CharSequence) {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        cm.primaryClip = ClipData.newPlainText("text", text)
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    fun getText(): CharSequence? {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).coerceToText(BaseApp.context)
        } else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    fun copyUri(uri: Uri) {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        cm.primaryClip = ClipData.newUri(BaseApp.context.getContentResolver(), "uri", uri)
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    fun getUri(): Uri? {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).uri
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    fun copyIntent(intent: Intent) {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        cm.primaryClip = ClipData.newIntent("intent", intent)
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    fun getIntent(): Intent? {
        val cm = BaseApp.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).intent
        } else null
    }
}