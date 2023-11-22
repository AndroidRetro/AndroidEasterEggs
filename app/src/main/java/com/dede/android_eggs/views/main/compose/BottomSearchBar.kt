@file:OptIn(ExperimentalAnimationApi::class)

package com.dede.android_eggs.views.main.compose

import android.util.Log
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import kotlinx.coroutines.flow.catch

@Composable
@Preview
fun BottomSearchBar(
    visibleState: MutableState<Boolean> = mutableStateOf(true),
    onSearch: ((text: String) -> Unit)? = null,
) {
    var visible by visibleState
    var backProgress by remember { mutableFloatStateOf(0f) }
    PredictiveBackHandler(enabled = visible) { flow ->
        flow.catch {
            Log.d("BottomSearchBar", "onCancel", it)
            animate(backProgress, 0f) { value, _ ->
                backProgress = value
            }
        }.collect { event ->
            backProgress = event.progress
        }
        visible = false
    }
    LaunchedEffect(visible) {
        if (visible) {
            backProgress = 0f
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        BottomSearchBar(
            visible,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = 1F - (0.1F * backProgress),
                    scaleY = 1F - (0.1F * backProgress),
                    transformOrigin = remember { TransformOrigin(0.5f, 1f) },
                ),
            shape = RoundedCornerShape(
                topStart = (28 * backProgress).dp,
                topEnd = (28 * backProgress).dp
            ),
            onClose = { visible = false },
            onSearch = { onSearch?.invoke(it) }
        )
    }
}

@Composable
private fun BottomSearchBar(
    visible: Boolean,
    modifier: Modifier,
    shape: Shape,
    onSearch: (text: String) -> Unit,
    onClose: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(visible) {
        if (visible) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
        }
    }
    Surface(
        modifier = Modifier
            .then(modifier)
            .imePadding(),
        shape = shape,
        color = colorScheme.surfaceColorAtElevation(4.dp),
        contentColor = colorScheme.onSurface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
            value = searchText,
            onValueChange = {
                searchText = it
                onSearch.invoke(searchText)
            },
            placeholder = {
                Text(text = stringResource(R.string.label_search_hint))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch.invoke(searchText)
            }),
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false)
                    ) {
                        onSearch.invoke("")
                        onClose.invoke()
                        keyboardController?.hide()
                    })
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchText.isNotBlank(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    Icon(imageVector = Icons.Rounded.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false)
                        ) {
                            searchText = ""
                            onSearch.invoke(searchText)
                        })
                }
            })
    }
}