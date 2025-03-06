package com.gahov.encrypted_notes.arch.command

/**
 * Represents commands for actions that can be performed on notes.
 *
 * Use this sealed interface to distinguish between different commands,
 * such as importing or exporting notes.
 */
sealed interface ActionCommand {
    /**
     * Command for importing notes.
     */
    data object Import : ActionCommand

    /**
     * Command for exporting notes.
     */
    data object Export : ActionCommand
}