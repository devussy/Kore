package kore.example.todo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.kore.R
import io.reactivex.disposables.CompositeDisposable
import kore.Ignore
import kore.example.todo.TaskKore.Action.AddTask
import kore.example.todo.TaskKore.Action.ChangeTaskState
import kore.example.todo.TaskKore.State.TASK_ADDED
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.android.synthetic.main.item_task.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TasksFragment : Fragment() {

    private val kore: TaskKore by lazy { getActivityKore(activity!!, TaskKore::class.java) }

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskList.adapter = TaskAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disposables.add(
            kore.bind { viewData ->
                when (viewData.state) {
                    TASK_ADDED -> {
                        (taskList.adapter as? TaskAdapter)?.tasks = viewData.tasks
                    }
                    else -> Ignore
                }
            }
        )

        addTaskBtn.setOnClickListener {
            kore.acceptAction(AddTask)
        }

        (taskList.adapter as? TaskAdapter)?.tasks = kore.lastViewData?.tasks ?: emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }


    inner class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        var tasks: List<Task> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewHolder =
            TaskViewHolder()

        override fun getItemCount(): Int = tasks.size

        override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
            tasks.getOrNull(position)?.let {
                holder.checkBox.isChecked = it.isCompleted
                holder.title.text = it.title
            }
        }

        inner class TaskViewHolder : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_task, null).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.toPx(48f))
        }) {
            val checkBox: CheckBox = itemView.checkBox
            val title: AppCompatTextView = itemView.title

            init {
                itemView.setOnClickListener { checkBox.performClick() }
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    kore.acceptAction(ChangeTaskState(adapterPosition, isChecked))
                }
            }
        }
    }
}
