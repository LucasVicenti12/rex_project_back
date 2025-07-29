package com.delice.crm.modules.kanban.domain.exceptions

import com.delice.crm.core.utils.exception.DefaultError

val KANBAN_UNEXPECTED = KanbanExceptions("KANBAN_UNEXPECTED", "An unexpected error has occurred")
val KANBAN_CANNOT_DO_ACTION = KanbanExceptions("KANBAN_CANNOT_DO_ACTION", "You cannot do this action")

val KANBAN_CODE_IS_EMPTY = KanbanExceptions("KANBAN_CODE_IS_EMPTY", "The code is empty")
val KANBAN_TITLE_IS_EMPTY = KanbanExceptions("KANBAN_TITLE_IS_EMPTY", "The title is empty")

val KANBAN_TAG_DESCRIPTION_IS_EMPTY = KanbanExceptions("KANBAN_TAG_DESCRIPTION_IS_EMPTY", "The tag description is empty")
val KANBAN_TAG_COLOR_IS_EMPTY = KanbanExceptions("KANBAN_TAG_COLOR_IS_EMPTY", "The tag color is empty")
val KANBAN_TAG_BOARD_UUID_IS_EMPTY = KanbanExceptions("KANBAN_TAG_BOARD_UUID_IS_EMPTY", "The tag is not attached in a board")

val KANBAN_CARD_BOARD_UUID_IS_EMPTY = KanbanExceptions("KANBAN_CARD_BOARD_UUID_IS_EMPTY", "The card is not attached in a board")
val KANBAN_CARD_COLUMN_UUID_IS_EMPTY = KanbanExceptions("KANBAN_CARD_COLUMN_UUID_IS_EMPTY", "The card is not attached in a column")

val KANBAN_COLUMN_BOARD_UUID_IS_EMPTY = KanbanExceptions("KANBAN_COLUMN_BOARD_UUID_IS_EMPTY", "The column is not attached in a board")

val KANBAN_BOARD_ALREADY_EXISTS = KanbanExceptions("KANBAN_BOARD_ALREADY_EXISTS", "The board with this code already exists")
val KANBAN_CARD_ALREADY_EXISTS = KanbanExceptions("KANBAN_CARD_ALREADY_EXISTS", "The card with this code already exists")
val KANBAN_COLUMN_ALREADY_EXISTS = KanbanExceptions("KANBAN_COLUMN_ALREADY_EXISTS", "The column with this code already exists")

val KANBAN_BOARD_NOT_FOUND = KanbanExceptions("KANBAN_BOARD_NOT_FOUND", "The board not found")
val KANBAN_CARD_NOT_FOUND = KanbanExceptions("KANBAN_CARD_NOT_FOUND", "The card not found")
val KANBAN_COLUMN_NOT_FOUND = KanbanExceptions("KANBAN_COLUMN_NOT_FOUND", "The column not found")
val KANBAN_TAG_NOT_FOUND = KanbanExceptions("KANBAN_TAG_NOT_FOUND", "The tag not found")

class KanbanExceptions(code: String, message: String): DefaultError(code, message)