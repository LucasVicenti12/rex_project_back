package com.delice.crm.modules.kanban.infra.web

import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.kanban.domain.entities.*
import com.delice.crm.modules.kanban.domain.usecase.KanbanUseCase
import com.delice.crm.modules.kanban.domain.usecase.response.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/kanban")
class KanbanWebService(
    private val kanbanUseCase: KanbanUseCase
) {
    @PostMapping("/saveBoard")
    fun saveBoard(@RequestBody board: Board): ResponseEntity<BoardResponse> {
        val response = kanbanUseCase.saveBoard(board)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/saveCard")
    fun saveCard(@RequestBody card: Card): ResponseEntity<CardResponse> {
        val response = kanbanUseCase.saveCard(card)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/saveColumn")
    fun saveColumn(@RequestBody column: Column): ResponseEntity<ColumnResponse> {
        val response = kanbanUseCase.saveColumn(column)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/saveTag")
    fun saveTag(@RequestBody tag: Tag): ResponseEntity<TagResponse> {
        val response = kanbanUseCase.saveTag(tag)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/board/{uuid}")
    fun getBoardByUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<BoardResponse> {
        val response = kanbanUseCase.getBoardByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/card/{uuid}")
    fun getCardByUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<CardResponse> {
        val response = kanbanUseCase.getCardByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/column/{uuid}")
    fun getColumnByUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<ColumnResponse> {
        val response = kanbanUseCase.getColumnByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/board/code/{code}")
    fun getBoardByCode(
        @PathVariable(
            name = "code",
            required = true,
        ) code: String
    ): ResponseEntity<BoardResponse> {
        val response = kanbanUseCase.getBoardByCode(code)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/cardByBoardUUID/{uuid}")
    fun getCardsByBoardUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<CardListResponse> {
        val response = kanbanUseCase.getCardsByBoardUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/columnByBoardUUID/{uuid}")
    fun getColumnsByBoardUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<ColumnListResponse> {
        val response = kanbanUseCase.getColumnsByBoardUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/tagByBoardUUID/{uuid}")
    fun getTagsByBoardUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<TagListResponse> {
        val response = kanbanUseCase.getTagsByBoardUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/getPagination")
    fun getBoardPagination(
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        request: HttpServletRequest
    ): ResponseEntity<BoardPaginationResponse> {
        val params = request.queryString.parametersToMap()

        val response = kanbanUseCase.getBoardPagination(page, count, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @DeleteMapping("/deleteTagByUUID/{uuid}")
    fun deleteTagByUUID(
        @PathVariable(
            value = "uuid",
            required = true
        ) tagUUID: UUID
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.deleteTagByUUID(tagUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @DeleteMapping("/deleteColumnByUUID/{uuid}")
    fun deleteColumnByUUID(
        @PathVariable(
            value = "uuid",
            required = true
        ) tagUUID: UUID
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.deleteColumnByUUID(tagUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/reorderColumns")
    fun reorderColumns(
        @RequestBody columns: List<Column>
    ): ResponseEntity<ColumnListResponse> {
        val response = kanbanUseCase.reorderColumns(columns)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/saveColumnRule")
    fun saveColumnRule(
        @RequestBody columnRule: ColumnRule
    ): ResponseEntity<ColumnRuleResponse> {
        val response = kanbanUseCase.saveColumnRule(columnRule)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/saveAllowedColumns/{uuid}")
    fun saveAllowedColumns(
        @PathVariable(
            name = "uuid",
            required = true
        ) uuid: UUID,
        @RequestBody columns: List<UUID>
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.saveAllowedColumns(uuid, columns)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getColumnRuleByUUID/{uuid}")
    fun getColumnRuleByUUID(
        @PathVariable(
            name = "uuid",
            required = true,
        ) uuid: UUID
    ): ResponseEntity<ColumnRuleResponse> {
        val response = kanbanUseCase.getColumnRuleByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/deleteAllowedColumnUUID/{mainColumnUUID}/{columnUUID}")
    fun deleteAllowedColumnUUID(
        @PathVariable(
            value = "mainColumnUUID",
            required = true
        ) mainColumnUUID: UUID,
        @PathVariable(
            value = "columnUUID",
            required = true
        ) columnUUID: UUID
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.deleteAllowedColumnUUID(mainColumnUUID, columnUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @DeleteMapping("/deleteColumnRuleByUUID/{uuid}")
    fun deleteColumnRuleByUUID(
        @PathVariable(
            value = "uuid",
            required = true
        ) tagUUID: UUID
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.deleteColumnRuleByUUID(tagUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/moveCardToColumn/{cardUUID}/{columnUUID}")
    fun moveCardToColumn(
        @PathVariable(
            value = "cardUUID",
            required = true
        ) cardUUID: UUID,
        @PathVariable(
            value = "columnUUID",
            required = true
        ) columnUUID: UUID
    ): ResponseEntity<CardListResponse> {
        val response = kanbanUseCase.moveCardToColumn(
            cardUUID,
            columnUUID
        )

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/setDefaultColumn/{boardUUID}/{columnUUID}")
    fun setDefaultColumn(
        @PathVariable(
            value = "boardUUID",
            required = true
        ) boardUUID: UUID,
        @PathVariable(
            value = "columnUUID",
            required = true
        ) columnUUID: UUID
    ): ResponseEntity<ColumnListResponse> {
        val response = kanbanUseCase.setDefaultColumn(
            boardUUID,
            columnUUID
        )

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/recreateCards/{boardUUID}")
    fun recreateCards(
        @PathVariable(
            value = "boardUUID",
            required = true
        ) boardUUID: UUID
    ): ResponseEntity<MessageBoardResponse> {
        val response = kanbanUseCase.recreateCards(
            boardUUID
        )

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }
}