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
val KANBAN_USER_NOT_FOUND = KanbanExceptions("KANBAN_USER_NOT_FOUND", "The user not found")
val KANBAN_COLUMN_RULE_NOT_FOUND = KanbanExceptions("KANBAN_COLUMN_RULE_NOT_FOUND", "The column rule not found")

val KANBAN_COLUMN_INDEX_REPEATED = KanbanExceptions("KANBAN_COLUMN_INDEX_REPEATED", "The index cannot be repeated in order")

val KANBAN_COLUMN_RULE_TYPE_IS_EMPTY = KanbanExceptions("KANBAN_COLUMN_RULE_TYPE_IS_EMPTY", "The type is empty")
val KANBAN_COLUMN_RULE_TAG_IS_EMPTY = KanbanExceptions("KANBAN_COLUMN_RULE_TAG_IS_EMPTY", "The tag is empty")
val KANBAN_COLUMN_RULE_EMAIL_IS_EMPTY = KanbanExceptions("KANBAN_COLUMN_RULE_EMAIL_IS_EMPTY", "The email is empty")
val KANBAN_COLUMN_RULE_USER_IS_EMPTY = KanbanExceptions("KANBAN_COLUMN_RULE_USER_IS_EMPTY", "The user is empty")
val KANBAN_COLUMN_RULE_DATA_INVALID = KanbanExceptions("KANBAN_COLUMN_RULE_DATA_INVALID", "The data rule is invalid")

val KANBAN_ALLOWED_COLUMNS_IS_EMPTY = KanbanExceptions("KANBAN_ALLOWED_COLUMNS_IS_EMPTY", "Allowed columns is empty")

val KANBAN_COLUMN_RULE_TYPE_IS_INVALID = KanbanExceptions("KANBAN_COLUMN_RULE_TYPE_IS_INVALID", "The rule type is invalid")
val KANBAN_COLUMN_MOVE_IN_SAME_COLUMN = KanbanExceptions("KANBAN_COLUMN_MOVE_IN_SAME_COLUMN", "You can't move to the same column")

class KanbanExceptions(code: String, message: String): DefaultError(code, message)