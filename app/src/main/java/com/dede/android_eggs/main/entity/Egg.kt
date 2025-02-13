package com.dede.android_eggs.main.entity

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.SparseArray
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.append
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.requireDrawable


data class Egg(val easterEgg: EasterEgg) : VType {

    class VersionFormatter(
        @StringRes val nicknameRes: Int,
        vararg versionNames: CharSequence,
    ) {

        companion object {
            fun create(@StringRes nicknameRes: Int, apiLevel: IntRange): VersionFormatter {
                return if (apiLevel.first == apiLevel.last) {
                    VersionFormatter(
                        nicknameRes,
                        getVersionNameByApiLevel(apiLevel.first)
                    )
                } else {
                    VersionFormatter(
                        nicknameRes,
                        getVersionNameByApiLevel(apiLevel.first),
                        getVersionNameByApiLevel(apiLevel.last),
                    )
                }
            }
        }

        private val versionNames: Array<out CharSequence> = versionNames

        fun format(context: Context): CharSequence {
            val enDash = context.getString(R.string.char_en_dash)
            val nickname = context.getString(nicknameRes)
            val sb = StringBuilder()
            versionNames.joinTo(sb, separator = enDash)
            return context.getString(
                R.string.android_version_nickname_format,
                sb.toString(), nickname
            )
        }
    }

    class ApiVersionFormatter(
        private val apiRange: IntRange,
        private val versionNameStart: CharSequence,
        private val versionNameLast: CharSequence,
    ) {

        companion object {
            fun create(apiLevel: IntRange): ApiVersionFormatter {
                return ApiVersionFormatter(
                    apiLevel,
                    getVersionNameByApiLevel(apiLevel.first),
                    getVersionNameByApiLevel(apiLevel.last),
                )
            }
        }

        fun format(context: Context): CharSequence {
            val span = SpannableStringBuilder()
                .append(context.getString(R.string.android_version_format, versionNameStart))
            val italic = StyleSpan(Typeface.ITALIC)
            if (apiRange.first == apiRange.last) {
                span.append("\n")
                    .append(
                        context.getString(R.string.api_version_format, apiRange.first.toString()),
                        italic
                    )
            } else {
                val enDash = context.getString(R.string.char_en_dash)
                span.append(enDash)
                    .append(versionNameLast)
                    .append("\n")
                    .append(
                        context.getString(R.string.api_version_format, apiRange.join(enDash)),
                        italic,
                    )
            }
            return span
        }

        private fun IntRange.join(s: CharSequence): CharSequence {
            return StringBuilder()
                .append(first)
                .append(s)
                .append(last)
        }
    }

    companion object {
        const val VIEW_TYPE_EGG = 0
        const val VIEW_TYPE_WAVY = -1
        const val VIEW_TYPE_PREVIEW = 1
        const val VIEW_TYPE_EGG_GROUP = 2

        private val apiLevelArrays = SparseArray<String>()

        fun getVersionNameByApiLevel(level: Int): String {
            return apiLevelArrays[level]
                ?: throw IllegalArgumentException("Illegal Api level: $level")
        }

        init {
            apiLevelArrays[Build.VERSION_CODES.UPSIDE_DOWN_CAKE] = "14"
            apiLevelArrays[Build.VERSION_CODES.TIRAMISU] = "13"
            apiLevelArrays[Build.VERSION_CODES.S_V2] = "12L"
            apiLevelArrays[Build.VERSION_CODES.S] = "12"
            apiLevelArrays[Build.VERSION_CODES.R] = "11"
            apiLevelArrays[Build.VERSION_CODES.Q] = "10"
            apiLevelArrays[Build.VERSION_CODES.P] = "9"
            apiLevelArrays[Build.VERSION_CODES.O_MR1] = "8.1"
            apiLevelArrays[Build.VERSION_CODES.O] = "8.0"
            apiLevelArrays[Build.VERSION_CODES.N_MR1] = "7.1"
            apiLevelArrays[Build.VERSION_CODES.N] = "7.0"
            apiLevelArrays[Build.VERSION_CODES.M] = "6.0"
            apiLevelArrays[Build.VERSION_CODES.LOLLIPOP_MR1] = "5.1"
            apiLevelArrays[Build.VERSION_CODES.LOLLIPOP] = "5.0"
            apiLevelArrays[Build.VERSION_CODES.KITKAT_WATCH] = "4.4W"
            apiLevelArrays[Build.VERSION_CODES.KITKAT] = "4.4"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR2] = "4.3"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR1] = "4.2"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN] = "4.1"
            apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1] = "4.0.3"
            apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH] = "4.0"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR2] = "3.2"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR1] = "3.1"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB] = "3.0"
            apiLevelArrays[Build.VERSION_CODES.GINGERBREAD_MR1] = "2.3.3"
            apiLevelArrays[Build.VERSION_CODES.GINGERBREAD] = "2.3"
            apiLevelArrays[Build.VERSION_CODES.FROYO] = "2.2"
            apiLevelArrays[Build.VERSION_CODES.ECLAIR_MR1] = "2.1"
            apiLevelArrays[Build.VERSION_CODES.ECLAIR] = "2.0"
            apiLevelArrays[Build.VERSION_CODES.DONUT] = "1.6"
            apiLevelArrays[Build.VERSION_CODES.CUPCAKE] = "1.5"
            apiLevelArrays[Build.VERSION_CODES.BASE_1_1] = "1.1"
            apiLevelArrays[Build.VERSION_CODES.BASE] = "1.0"
        }

        fun Egg.getIcon(context: Context): Drawable {
            if (supportAdaptiveIcon) {
                val pathStr = IconShapePref.getMaskPath(context)
                return AlterableAdaptiveIconDrawable(context, iconRes, pathStr)
            }
            return context.requireDrawable(iconRes)
        }

        fun EasterEgg.toEgg(): Egg {
            return Egg(this)
        }

        fun BaseEasterEgg.toVTypeEgg(): VType {
            return when (this) {
                is EasterEgg -> toEgg()
                is EasterEggGroup -> EggGroup(eggs.map { it.toEgg() })
                else -> throw UnsupportedOperationException()
            }
        }
    }

    val id: Int get() = easterEgg.id
    val iconRes: Int @DrawableRes get() = easterEgg.iconRes
    val eggNameRes: Int @StringRes get() = easterEgg.nameRes
    val supportAdaptiveIcon: Boolean get() = easterEgg.supportAdaptiveIcon
    val targetClass: Class<out Activity>? get() = easterEgg.provideEasterEgg()

    val versionFormatter = VersionFormatter.create(easterEgg.nicknameRes, easterEgg.apiLevel)

    val apiVersionFormatter = ApiVersionFormatter.create(easterEgg.apiLevel)

    override val viewType: Int = VIEW_TYPE_EGG
}

class EggGroup(val child: List<Egg>, var selectedIndex: Int = 0) : VType {

    val selectedEgg: Egg get() = child[selectedIndex]

    override val viewType: Int = Egg.VIEW_TYPE_EGG_GROUP
}

class Wavy(val wavyRes: Int, val repeat: Boolean = false) : VType {
    override val viewType: Int = Egg.VIEW_TYPE_WAVY
}
