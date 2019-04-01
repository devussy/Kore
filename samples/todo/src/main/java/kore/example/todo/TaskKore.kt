package kore.example.todo

import kore.BaseViewData
import kore.Kore
import kore.Reducer
import java.util.*

class TaskKore : Kore<TaskKore.Action, TaskKore.ViewData>() {

    override fun createInitialViewData(): ViewData = ViewData()

    override fun reduce(): Reducer<Action, ViewData> = { oldViewData, action ->
        when (action) {
            is Action.AddTask -> {
                oldViewData.copy(
                    state = State.TASK_ADDED,
                    tasks = oldViewData.tasks.plus(
                        Task(false, "Task ${oldViewData.tasks.size + 1}", Calendar.getInstance().time)
                    )
                )
            }
            is Action.ChangeTaskState -> {
                val newTask = oldViewData.tasks.toMutableList()
                newTask[action.position].isCompleted = action.isChecked

                oldViewData.copy(
                    state = State.TASK_STATE_CHANGED,
                    tasks = newTask
                )
            }
        }
    }

    enum class State {
        INIT,
        TASK_ADDED,
        TASK_STATE_CHANGED
    }

    sealed class Action {
        object AddTask : Action()
        data class ChangeTaskState(val position: Int, val isChecked: Boolean) : Action()
    }

    data class ViewData(
        val state: State = State.INIT,
        val tasks: List<Task> = emptyList()
    ) : BaseViewData
}
