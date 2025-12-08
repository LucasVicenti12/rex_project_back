package com.delice.crm.modules.task.domain.usecase.implementation

import com.delice.crm.core.user.domain.repository.UserRepository
import com.delice.crm.core.utils.function.getCurrentUser
import com.delice.crm.core.utils.ordernation.OrderBy
import com.delice.crm.modules.task.domain.entities.Task
import com.delice.crm.modules.task.domain.entities.TaskHistory
import com.delice.crm.modules.task.domain.entities.TaskStatus
import com.delice.crm.modules.task.domain.exceptions.*
import com.delice.crm.modules.task.domain.repository.TaskRepository
import com.delice.crm.modules.task.domain.usecase.TaskUseCase
import com.delice.crm.modules.task.domain.usecase.response.MessageTaskResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskByDateResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskPaginatedResponse
import com.delice.crm.modules.task.domain.usecase.response.TaskResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskUseCaseImplementation(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : TaskUseCase {
    companion object {
        private val logger = LoggerFactory.getLogger(TaskUseCaseImplementation::class.java)
    }

    override fun createTask(task: Task): TaskResponse = try {
        val user = getCurrentUser()

        task.createdBy = user.getUserData()

        validateTask(task).let {
            if (it == null) {
                TaskResponse(
                    task = taskRepository.createTask(task)
                )
            } else {
                TaskResponse(
                    error = it
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_CREATE_TASK", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun updateTask(task: Task): TaskResponse = try {
        val user = getCurrentUser()

        task.modifiedBy = user.getUserData()

        validateTask(task).let {
            if (it == null) {
                val exists = taskRepository.getTaskByUUID(task.uuid!!)

                if (exists == null) {
                    TaskResponse(
                        error = TASK_NOT_FOUND
                    )
                } else {
                    TaskResponse(
                        task = taskRepository.updateTask(task)
                    )
                }
            } else {
                TaskResponse(
                    error = it
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_CREATE_TASK", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun getTaskByUUID(uuid: UUID): TaskResponse = try {
        TaskResponse(
            task = taskRepository.getTaskByUUID(uuid)
        )
    } catch (e: Exception) {
        logger.error("ERROR_IN_GET_TASK_BY_UUID", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun deleteTask(taskUUID: UUID): MessageTaskResponse = try {
        taskRepository.deleteTask(taskUUID)
        MessageTaskResponse(
            message = "Task deleted with success"
        )
    } catch (e: Exception) {
        logger.error("ERROR_IN_DELETE_TASK", e)
        MessageTaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun getPaginatedTask(count: Int, page: Int, orderBy: OrderBy?, params: Map<String, Any?>): TaskPaginatedResponse = try {
        TaskPaginatedResponse(
            tasks = taskRepository.getPaginatedTask(
                count = count,
                page = page,
                orderBy = orderBy,
                params = params
            )
        )
    } catch (e: Exception) {
        logger.error("ERROR_IN_PAGINATED_TASK", e)
        TaskPaginatedResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun changeTaskStatus(taskUUID: UUID, status: TaskStatus): TaskResponse = try {
        val task = taskRepository.getTaskByUUID(taskUUID)

        val user = getCurrentUser()

        when{
            task == null -> {
                TaskResponse(
                    error = TASK_NOT_FOUND
                )
            }
            task.status!! === TaskStatus.COMPLETED -> {
                TaskResponse(
                    error = TASK_ALREADY_COMPLETED
                )
            }
            else -> {
                TaskResponse(
                    task = taskRepository.changeTaskStatus(taskUUID, status, user.getUserData())
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_CHANGE_TASK_STATUS", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun addTaskHistory(history: TaskHistory): TaskResponse = try {
        val task = taskRepository.getTaskByUUID(history.taskUUID!!)

        val user = getCurrentUser()

        when{
            task == null -> {
                TaskResponse(
                    error = TASK_NOT_FOUND
                )
            }
            task.status!! === TaskStatus.COMPLETED -> {
                TaskResponse(
                    error = TASK_ALREADY_COMPLETED
                )
            }
            else -> {
                history.actionBy = user.getUserData()

                TaskResponse(
                    task = taskRepository.addTaskHistory(history)
                )
            }
        }
    } catch (e: Exception) {
        logger.error("ERROR_IN_ADD_TASK_HISTORY", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun getTasksByMonth(month: Int, year: Int): TaskByDateResponse = try {
        TaskByDateResponse(
            tasks = taskRepository.getTasksByMonth(month, year)
        )
    }catch (e: Exception){
        logger.error("ERROR_IN_GET_TASKS_MONTH", e)
        TaskByDateResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    override fun getMyNextTask(): TaskResponse = try {
        val user = getCurrentUser()

        TaskResponse(
            task = taskRepository.getMyNextTask(user.uuid)
        )
    }catch (e: Exception){
        logger.error("ERROR_IN_GET_MY_NEXT_TASK", e)
        TaskResponse(
            error = TASK_UNEXPECTED_ERROR
        )
    }

    private fun validateTask(task: Task): TaskException? = when {
        task.title.isNullOrBlank() -> {
            TASK_TITLE_IS_EMPTY
        }

        task.description.isNullOrBlank() -> {
            TASK_DESCRIPTION_IS_EMPTY
        }

        task.dueDate == null -> {
            TASK_DESCRIPTION_IS_EMPTY
        }

        task.responsible == null -> {
            TASK_RESPONSIBLE_IS_EMPTY
        }

        userRepository.getUserByUUID(task.responsible.uuid!!) == null -> {
            TASK_RESPONSIBLE_NOT_FOUND
        }

        else -> {
            null
        }
    }
}