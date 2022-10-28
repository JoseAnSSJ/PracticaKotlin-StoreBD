package com.example.store

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.store.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.btnSave.setOnClickListener {
            var storeEntity = StoreEntity(
                name = binding.edtName.text.toString().trim()
            )

            thread{
               StoreAplication.database.storeDao().addStore(storeEntity)
            }

            mAdapter.add(storeEntity)
        }*/

        binding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()

    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (args != null) {
            fragment.arguments = args
        }


        fragmentTransaction.add(R.id.contentMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        hidenPop()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_colums))
        getAllStores()

        binding.recycler.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getAllStores() {
        doAsync {
            val stores = StoreAplication.database.storeDao().getAllStore()
            uiThread {
                Log.i("prueba", stores.toString())
                mAdapter.setStores(stores)

            }
        }

    }


    /*
    OnClickListenenr
    */
    override fun onClick(storeId: Long) {
        val arg = Bundle()
        arg.putLong(getString(R.string.arg_id), storeId)
        launchEditFragment(arg)

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreAplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                Log.i("prueba1", storeEntity.toString())
                updateStore(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val item: Array<String> = resources.getStringArray(R.array.array_opcion_items)
        MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_opcion_title)
            .setItems(item) { dialogInterface, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWeb(storeEntity.webSite)
                }
            }.show()


    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        starIntent(callIntent)
    }

    private fun goToWeb(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val webIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            starIntent(webIntent)
        }

    }

    private fun starIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.main_error_resolve, Toast.LENGTH_LONG).show()
        }
    }

    private fun confirmDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.dialog_delete))
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                doAsync {
                    StoreAplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                null
            }.show()

    }

    /*
    MainAux
    */

    override fun hidenPop(visibility: Boolean) {
        if (visibility) {
            binding.fab.show()
        } else {
            binding.fab.hide()
        }
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)

    }
}