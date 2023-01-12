package com.ecommerce.zellanything.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.ecommerce.zellanything.R
import com.ecommerce.zellanything.firestore.FirestoreClass
import com.ecommerce.zellanything.models.Product
import com.ecommerce.zellanything.ui.activities.CartListActivity
import com.ecommerce.zellanything.ui.activities.SettingsActivity
import com.ecommerce.zellanything.ui.adapters.DashboardItemsListAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.*

class DashboardFragment : BaseFragment() {

    private var adapter: DashboardItemsListAdapter? = null

    var currentItemsLoaded = listOf<Product>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DashboardItemsListAdapter(requireActivity(), emptyList())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dashboard_menu, menu)

        val myActionMenuItem = menu.findItem(R.id.search_dashboard)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String): Boolean {
                val filteredItems = currentItemsLoaded.filter {
                    it.title.toLowerCase(Locale.getDefault())
                        .contains(text.toLowerCase(Locale.getDefault()))
                }
                adapter?.updateItems(filteredItems)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemList(dashboardItemList: ArrayList<Product>) {
        hideProgressDialog()
        currentItemsLoaded = dashboardItemList
        if (dashboardItemList.size > 0) {
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)
            rv_dashboard_items.adapter = adapter
            adapter?.updateItems(dashboardItemList.toList())

        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

}