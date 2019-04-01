package kore.example.todo


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kore.R
import io.reactivex.disposables.CompositeDisposable
import kore.Ignore
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_tasks.*

/**
 * A simple [Fragment] subclass.
 *
 */
class StatisticsFragment : Fragment() {

    private val kore: TaskKore by lazy { getActivityKore(activity!!, TaskKore::class.java) }

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        disposables.add(
            kore.bind { viewData ->
                when (viewData.state) {
                    TaskKore.State.TASK_ADDED,
                    TaskKore.State.TASK_STATE_CHANGED -> {
                        displayStatistics(viewData.tasks)
                    }
                    else -> Ignore
                }
            }
        )

        displayStatistics(kore.lastViewData?.tasks ?: emptyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun displayStatistics(tasks: List<Task>) {
        val totalTasksCount = tasks.count()
        val completeTasksCount = tasks.filter { it.isCompleted }.count()
        val completePercent = completeTasksCount.toFloat() / Math.max(totalTasksCount, 1) * 100

        this@StatisticsFragment.totalTasksCount.text = "$totalTasksCount 개"
        this@StatisticsFragment.completeTasksCount.text = "$completeTasksCount 개"
        this@StatisticsFragment.completePercent.text = "${String.format("%.2f", completePercent)} % 완료"
    }
}
