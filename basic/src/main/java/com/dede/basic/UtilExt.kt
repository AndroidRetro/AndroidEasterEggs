@file:JvmName("UtilExt")

package com.dede.basic

import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * 计算Emoji的Unicode
 */
fun getEmojiUnicode(
    emoji: CharSequence,
    separator: CharSequence = "\\u",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    temp: MutableList<CharSequence>? = null,
): CharSequence {
    val list: MutableList<CharSequence> = if (temp != null) {
        temp.clear();temp
    } else ArrayList()
    var offset = 0
    while (offset < emoji.length) {
        val codePoint = Character.codePointAt(emoji, offset)
        offset += Character.charCount(codePoint)
        if (codePoint == 0xFE0F) {
            // the codepoint is a emoji style standardized variation selector
            continue
        }
        list.add("%04x".format(codePoint))
    }
    return list.joinToString(separator = separator, prefix = prefix, postfix = postfix)
}


val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    ).roundToInt()

val Number.dpf: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    )