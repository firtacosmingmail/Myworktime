package com.fcc.myworktime.ui.projects

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fcc.myworktime.R
import com.fcc.myworktime.databinding.FragmentProjectsBinding
import com.fcc.myworktime.ui.utils.EventData
import com.fcc.myworktime.ui.utils.LifeCycleOwnerFragment
import com.fcc.myworktime.ui.utils.MainView
import com.fcc.myworktime.ui.utils.TextListAdapter
import com.fcc.myworktime.utils.AutoClearedValue
import com.fcc.myworktime.utils.Messages
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


/**
 * Created by firta on 8/31/2017.
 * The fragment that will implement the [ProjectsView]
 */
class ProjectsFragment: LifeCycleOwnerFragment(), ProjectsView {
    private lateinit var binding: AutoClearedValue<FragmentProjectsBinding>

    private var lifeCycleEvents = PublishSubject.create<EventData>()


    private var confirmationDlgOkClicked = PublishSubject.create<Any>()
    private var adapter: TextListAdapter = TextListAdapter()

    @Inject lateinit var presenter:ProjectsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val b: FragmentProjectsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_projects,container, false)
        binding = AutoClearedValue(this, b)

        b.listProj.adapter = adapter

        presenter.bindView(this)

        lifeCycleEvents.onNext(EventData(savedInstanceState, MainView.EVENT_CREATED))
        return b.root
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifeCycleEvents.onNext(EventData(null, MainView.EVENT_ACTIVITY_ATTACHED))
    }


    override fun onPause() {
        lifeCycleEvents.onNext(EventData(null, MainView.EVENT_PAUSED))

        super.onPause()
    }

    override fun onResume() {
        lifeCycleEvents.onNext(EventData(null, MainView.EVENT_RESUMED))
        super.onResume()
    }

    override fun onDestroyView() {
        lifeCycleEvents.onNext(EventData(null, MainView.EVENT_DESTROYED))
        super.onDestroyView()
    }

    override fun viewEvent(): Observable<EventData> {
        return lifeCycleEvents
    }

    override fun projectClicked(): Observable<Int> {
        return adapter.itemClickedEvent
    }

    override fun observableFabClick(): Observable<Any> {
        return RxView.clicks(binding.get()!!.fab as View)
    }

    override fun observableConfirmDlgOkClick(): Observable<Any> {
        return confirmationDlgOkClicked
    }

    override fun observableDeleteItem(): Observable<Int> {
        return adapter.deleteClickedEvent
    }
    override fun observableEditItem(): Observable<Int> {
        return adapter.editClickedEvent
    }

    override fun displayLoading() {
        binding.get()!!.fab.visibility = View.INVISIBLE
        binding.get()!!.listProj.visibility = View.INVISIBLE
        binding.get()!!.txtError.visibility = View.INVISIBLE
        binding.get()!!.loading.visibility = View.VISIBLE
    }


    override fun displayConfirmationDlg(title:String, message:String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, { _, _ -> confirmationDlgOkClicked.onNext("") })
                .setNegativeButton(android.R.string.no, null).show()    }

    override fun deleteItemAtPosition(position: Int) {
        adapter.removeItem(position)
    }
    override  fun hideLoading(){
        binding.get()!!.loading.visibility = View.INVISIBLE
    }


    override fun displayMessage(message: String) {
        binding.get()!!.txtError.visibility = View.VISIBLE
        binding.get()!!.txtError.text = message
    }

    override fun displayList(items: List<String>) {
        binding.get()!!.listProj.visibility = View.VISIBLE
        adapter.setItems(items)
    }

    override fun addProject(title: String) {
        adapter.addItem(title)
    }
    override fun displayFab(){
        binding.get()!!.fab.visibility = View.VISIBLE
    }
}