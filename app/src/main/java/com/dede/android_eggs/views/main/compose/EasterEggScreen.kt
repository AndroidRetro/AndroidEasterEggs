package com.dede.android_eggs.views.main.compose

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.android_eggs.views.main.EasterEggModules
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg

@Composable
@Preview(showBackground = true)
fun EasterEggScreen(
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchFilter: String = "",
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val pureEasterEggs = remember(easterEggs) {
        EasterEggModules.providePureEasterEggList(easterEggs)
    }
    val searchText = remember(searchFilter) {
        searchFilter.trim().uppercase()
    }
    val searchMode = searchText.isNotBlank()
    val currentList = remember(searchText, searchMode, easterEggs, pureEasterEggs) {
        if (searchMode) {
            filterEasterEggs(context, pureEasterEggs, searchText)
        } else {
            easterEggs
        }
    }
    Surface(color = colorScheme.surface) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Crossfade(
                targetState = currentList.isEmpty(),
                modifier = Modifier.sizeIn(maxWidth = 560.dp),
                label = "EasterEggList",
            ) { isEmpty ->
                if (isEmpty) {
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(contentPadding)
                            .padding(top = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SearchOff,
                            contentDescription = null,
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(108.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (searchMode) {
                            items(items = currentList) {
                                EasterEggItem(it, enableItemAnim = true)
                            }
                        } else {
                            item("snapshot") {
                                AndroidSnapshotView()
                            }
                            item("wavy1") {
                                Wavy(res = R.drawable.ic_wavy_line)
                            }
                            items(items = currentList) {
                                EasterEggItem(it, enableItemAnim = false)
                            }
                            item("wavy2") {
                                Wavy(res = R.drawable.ic_wavy_line)
                            }
                            item("footer") {
                                ProjectDescription()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun filterEasterEggs(
    context: Context,
    pureEasterEggs: List<EasterEgg>,
    searchText: String,
): List<EasterEgg> {
    val isApiLevel = Regex("^\\d{1,2}$").matches(searchText)

    fun EasterEgg.matchVersionName(version: String): Boolean {
        val containsStart = EasterEggHelp.getVersionNameByApiLevel(apiLevel.first)
            .contains(version, true)
        return if (apiLevel.first == apiLevel.last) {
            containsStart
        } else {
            containsStart || EasterEggHelp.getVersionNameByApiLevel(apiLevel.last)
                .contains(version, true)
        }
    }
    return pureEasterEggs.filter {
        context.getString(it.nameRes).contains(searchText, true) ||
                context.getString(it.nicknameRes).contains(searchText, true) ||
                it.matchVersionName(searchText) ||
                (isApiLevel && it.apiLevel.contains(searchText.toIntOrNull() ?: -1))
    }
}
