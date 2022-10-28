package com.example.store

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.store.databinding.ItemStoreBinding

class StoreAdapter(
    private var store: MutableList<StoreEntity>,
    private var listener: OnClickListener
) : RecyclerView.Adapter<StoreAdapter.viewHolder>() {

    private lateinit var mContext: Context


    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity) {

            with(binding.root){
                setOnClickListener {
                    listener.onClick(storeEntity.id)
                }

                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }

            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var store = store.get(position)
        with(holder) {
            binding.txtName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            setListener(store)
            Glide.with(mContext).load(store.photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = store.size


    fun add(storeEntity: StoreEntity) {
        if(!store.contains(storeEntity)){
            store.add(storeEntity)
            notifyItemInserted(store.size-1)
        }

    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.store = stores
        notifyDataSetChanged()
    }

    fun update(storeEntity: StoreEntity) {
        val index = store.indexOf(storeEntity)
        if (index != 1) {
            store.set(index, storeEntity)
            notifyItemChanged(index)
        }

    }

    fun delete(storeEntity: StoreEntity) {
        val index = store.indexOf(storeEntity)
        if (index != 1) {
            store.removeAt(index)
            notifyItemRemoved(index)
        }

    }
}