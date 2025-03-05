package com.gahov.encrypted_notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gahov.encrypted_notes.feature.NotesViewModel

/**
 * Displays a top app bar with integrated search functionality and a menu.
 *
 * This composable shows an action bar that may include an optional back button,
 * a title, a search button to toggle a search field, and a menu button (three dots)
 * that opens a dialog to select an action (Export or Import Notes).
 *
 * @param titleText The title to be displayed in the app bar.
 * @param scrollBehavior Optional scroll behavior for the top app bar.
 * @param isBackButtonEnabled Whether a back button should be displayed.
 * @param onBackButtonClickListener Callback invoked when the back button is clicked.
 * @param isSearchButtonEnabled Whether the search functionality is enabled.
 * @param onSearchInputUpdate Callback invoked when the search input is updated.
 * @param isMenuEnabled Whether the menu button (three dots) is displayed.
 * @param onMenuCommand Callback invoked with the selected menu command ("export" or "import").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearchButton(
    titleText: String? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    isBackButtonEnabled: Boolean = false,
    onBackButtonClickListener: (() -> Unit)? = null,
    isSearchButtonEnabled: Boolean = false,
    onSearchInputUpdate: ((inputData: String) -> Unit)? = null,
    isMenuEnabled: Boolean = false,
    onMenuCommand: ((command: NotesViewModel.ActionCommand) -> Unit)? = null,
) {
    // Request focus for the search text field when it becomes enabled.
    val focusRequester = remember { FocusRequester() }
    // Controller for showing/hiding the software keyboard.
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    // Controls whether the search input field is enabled (visible and focusable).
    var searchFieldEnabled by remember { mutableStateOf(false) }
    // Controls the visibility of the menu dialog.
    var showMenuDialog by remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            if (isBackButtonEnabled) {
                BackButtonOfTopBar(onBackButtonClickListener)
            }
        },
        colors = topAppBarColors(),
        scrollBehavior = scrollBehavior,
        title = {
            if (isSearchButtonEnabled) {
                // When search is enabled, display both the title and the search input.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TitleOfTopBar(titleText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        // Display the editable search text field.
                        EditText(focusRequester, searchFieldEnabled, onSearchInputUpdate)
                    }
                }
            } else {
                // Display only the title when search is not enabled.
                TitleOfTopBar(titleText)
            }
        },
        actions = {
            if (isSearchButtonEnabled) {
                // Display the search button that toggles the search field visibility.
                SearchButtonOfTopBar {
                    searchFieldEnabled = !searchFieldEnabled
                    if (searchFieldEnabled) {
                        focusRequester.requestFocus()
                        softwareKeyboardController?.show()
                    } else {
                        focusRequester.freeFocus()
                        softwareKeyboardController?.hide()
                    }
                }
            }
            if (isMenuEnabled) {
                // Display the menu button (three dots) to open the menu dialog.
                IconButton(
                    onClick = { showMenuDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menu button",
                        tint = Color.White
                    )
                }
            }
        }
    )

    // Display the menu dialog if requested.
    if (showMenuDialog) {
        NotesMenuDialog(
            onDismiss = { showMenuDialog = false },
            onCommandSelected = { command ->
                onMenuCommand?.invoke(command)
                showMenuDialog = false
            }
        )
    }
}

/**
 * Displays the search button within the top app bar.
 *
 * @param onSearchButtonClickListener Callback invoked when the search button is clicked.
 */
@Composable
private fun SearchButtonOfTopBar(
    onSearchButtonClickListener: (() -> Unit)?
) {
    IconButton(
        onClick = onSearchButtonClickListener ?: {}
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search button",
            tint = Color.White
        )
    }
}

/**
 * Displays the title text in the top app bar.
 *
 * @param titleText The title text to be displayed.
 */
@Composable
private fun TitleOfTopBar(titleText: String?) {
    Text(
        modifier = Modifier.padding(start = 1.dp),
        text = titleText ?: "",
        fontSize = 18.sp,
        color = Color.White,
    )
}

/**
 * Displays the back button in the top app bar.
 *
 * @param onBackButtonClickListener Callback invoked when the back button is clicked.
 */
@Composable
private fun BackButtonOfTopBar(onBackButtonClickListener: (() -> Unit)?) {
    IconButton(
        onClick = onBackButtonClickListener ?: {}
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back button",
            tint = Color.White
        )
    }
}

/**
 * Provides the color scheme for the top app bar.
 *
 * Uses the primary color of the current theme for both the container and scrolled container.
 *
 * @return [TopAppBarColors] with custom color values.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.primary,
    )
}

/**
 * Displays an editable search text field.
 *
 * This composable shows a [BasicTextField] for search input. When the field is disabled,
 * it clears its content. Custom selection colors are applied to match the top app bar design.
 *
 * @param focusRequester Used to request focus for the text field.
 * @param searchFieldEnabled Indicates whether the search field is enabled.
 * @param onSearchInputUpdate Callback invoked when the text field's value changes.
 */
@Composable
private fun EditText(
    focusRequester: FocusRequester,
    searchFieldEnabled: Boolean,
    onSearchInputUpdate: ((inputData: String) -> Unit)?,
) {
    // Used to track if this is the first launch of the composable.
    var isFirstLaunch by remember { mutableStateOf(true) }

    // Local state for the text field value.
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }

    // Clear the input when the search field is disabled, but not on the first launch.
    LaunchedEffect(searchFieldEnabled) {
        if (!searchFieldEnabled) {
            if (isFirstLaunch) {
                isFirstLaunch = false
            } else {
                textFieldValue = TextFieldValue(text = "", selection = TextRange(0))
                onSearchInputUpdate?.invoke(textFieldValue.text)
            }
        }
    }

    // Define custom text selection colors for the text field.
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = LocalTextSelectionColors.current.backgroundColor
    )

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // When the search field is disabled, cover it to block any interaction.
        if (!searchFieldEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(false) { /* cover input when it is disabled */ }
            )
        }
        // Provide custom selection colors to the BasicTextField.
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onSearchInputUpdate?.invoke(newValue.text)
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                ),
                // Use a white cursor when enabled, transparent otherwise.
                cursorBrush = SolidColor(if (searchFieldEnabled) Color.White else Color.Transparent),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        }
        // Draw a horizontal divider below the text field.
        HorizontalDivider(
            color = if (searchFieldEnabled) Color.White else Color.Transparent,
            thickness = 1.dp,
        )
    }
}