package com.defenderbot.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.defenderbot.R
import com.defenderbot.adapter.ChildListAdapter
import com.defenderbot.model.ChildListBean
import kotlinx.android.synthetic.main.fragment_accedentreport.*
import kotlinx.android.synthetic.main.fragment_accedentreport.view.*
import java.util.ArrayList


class AccedentReportFragment : Fragment(), View.OnClickListener,ChildListAdapter.OnItemClick {


    //    var view: View? = null
    lateinit var viewLay: View
    internal lateinit var mAdapter: ChildListAdapter
    internal var childlist: MutableList<ChildListBean> = ArrayList<ChildListBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewLay = inflater.inflate(R.layout.fragment_accedentreport, container, false)
        setOnclick()
        //        getHomeData();
        return viewLay
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setOnclick() {
        setRecyclerView()
    }

    override fun onClick(v: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showOrderDetail(position: Int, dataItem: ChildListBean) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal fun setRecyclerView() {
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        childlist.add(ChildListBean("",""))
        mAdapter = ChildListAdapter(childlist, this.activity!!, this)
        viewLay.recycler_childlist.setHasFixedSize(true)
        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        viewLay.recycler_childlist.setLayoutManager(mLayoutManager)
        viewLay.recycler_childlist.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity!!, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
        viewLay.recycler_childlist.setItemAnimator(androidx.recyclerview.widget.DefaultItemAnimator())
        viewLay.recycler_childlist.setAdapter(mAdapter)

        }
        //        setValue(ResponseValue.getwritersList);
//        callWritersListAPI()
    }



