package com.protone.eChatGPT.utils

class SelectProxyImp<T> : SelectProxy<T> {

    private val selectList by lazy { mutableListOf<T>() }

    override var onSelect: (Int, Boolean) -> Unit = { _, _ -> }
    override var indexOfItem: (T) -> Int = { -1 }

    override fun getSelected(): Collection<T> = selectList

    override fun isSelected(item: T): Boolean = selectList.contains(item)

    override fun select(position: Int, item: T) {
        val index = selectList.indexOf(item)
        if (index != -1) return
        onSelect(position, true)
        clearSelect()
        selectList.add(item)
    }

    override fun clearSelect() {
        selectList.iterator().also {
            while (it.hasNext()) {
                onSelect(indexOfItem(it.next()), false)
                it.remove()
            }
        }
    }

}

interface SelectProxy<T> {
    var onSelect: (Int, Boolean) -> Unit
    var indexOfItem: (T) -> Int
    fun getSelected(): Collection<T>
    fun isSelected(item: T): Boolean
    fun select(position: Int, item: T)
    fun clearSelect()
}