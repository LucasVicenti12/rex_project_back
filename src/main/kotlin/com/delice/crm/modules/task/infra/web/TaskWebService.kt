package com.delice.crm.modules.task.infra.web

import com.delice.crm.core.utils.enums.enumFromTypeValue
import com.delice.crm.core.utils.filter.parametersToMap
import com.delice.crm.modules.task.domain.entities.Task
import com.delice.crm.modules.task.domain.entities.TaskHistory
import com.delice.crm.modules.task.domain.usecase.TaskUseCase
import com.delice.crm.modules.task.domain.usecase.response.MessageTaskResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskByDateResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskPaginatedResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/task")
class TaskWebService(
    private val taskUseCase: TaskUseCase
) {
    @PostMapping("/create")
    fun createTask(
        @RequestBody task: Task
    ): ResponseEntity<TaskResponse> {
        val response = taskUseCase.createTask(task)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/update")
    fun updateTask(
        @RequestBody task: Task
    ): ResponseEntity<TaskResponse> {
        val response = taskUseCase.updateTask(task)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getByUUID")
    fun getTaskByUUID(
        @RequestParam(
            value = "uuid",
            required = true
        ) uuid: UUID
    ): ResponseEntity<TaskResponse> {
        val response = taskUseCase.getTaskByUUID(uuid)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/delete/{uuid}")
    fun deleteTask(
        @PathVariable(
            value = "uuid",
            required = true
        ) taskUUID: UUID
    ): ResponseEntity<MessageTaskResponse> {
        val response = taskUseCase.deleteTask(taskUUID)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/getPagination")
    fun getPaginatedTask(
        @RequestParam(
            value = "count",
            required = true
        ) count: Int,
        @RequestParam(
            value = "page",
            required = true
        ) page: Int,
        request: HttpServletRequest
    ): ResponseEntity<TaskPaginatedResponse> {
        val params = request.queryString.parametersToMap()

        val response = taskUseCase.getPaginatedTask(count, page, params)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/changeStatus/{uuid}")
    fun changeTaskStatus(
        @PathVariable(
            value = "uuid",
            required = true
        ) taskUUID: UUID,
        @RequestParam(
            value = "status",
            required = true
        ) status: Int
    ): ResponseEntity<TaskResponse> {
        val response = taskUseCase.changeTaskStatus(taskUUID, enumFromTypeValue(status))

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @PostMapping("/addHistory")
    fun addTaskHistory(
        @RequestBody history: TaskHistory
    ): ResponseEntity<TaskResponse> {
        val response = taskUseCase.addTaskHistory(history)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/taskByMonth/{year}/{month}")
    fun getTasksByMonth(
        @PathVariable(
            value = "year",
            required = true
        ) year: Int,
        @PathVariable(
            value = "month",
            required = true
        ) month: Int
    ): ResponseEntity<TaskByDateResponse>{
        val response = taskUseCase.getTasksByMonth(month, year)

        if (response.error != null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        return ResponseEntity
            .ok()
            .body(response)
    }

    @GetMapping("/nextTask")
    fun getMyNextTask(): ResponseEntity<TaskResponse>{
        val response = taskUseCase.getMyNextTask()

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