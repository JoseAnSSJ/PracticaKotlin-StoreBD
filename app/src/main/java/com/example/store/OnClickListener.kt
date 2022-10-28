package com.example.store

interface OnClickListener {

    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}