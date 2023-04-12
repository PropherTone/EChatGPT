package com.protone.eChatGPT.bean


class ChatNodeList : List<ChatNodeList.ChatNode> {

    class ChatNode(chatItem: ChatItem) {
        var previous: ChatNode? = null
        var next: ChatNode? = null
        val data: ChatItem = chatItem
    }

    class ChatNodeContainer {
        private var headNode: ChatNode? = null
        private var curNode: ChatNode? = null
        private var _size = -1

        fun insertEnd(chatNode: ChatNode) {
            if (checkCursor(chatNode)) return
            curNode?.next = chatNode
            chatNode.previous = curNode
            curNode = chatNode
            ++_size
        }

        fun indexOf(chatItem: ChatItem): Int {
            return indexOf(chatItem, headNode, 0)
        }

        private fun indexOf(chatItem: ChatItem, chatNode: ChatNode?, size: Int): Int {
            if (chatNode?.data == null) return size
            if (chatNode.data == chatItem) return size
            if (chatNode.next == null) return -1
            return indexOf(chatItem, chatNode.next, size + 1)
        }

        fun get(index: Int): ChatNode? {
            return get(headNode, index, 0)
        }

        private fun get(chatNode: ChatNode?, index: Int, size: Int): ChatNode? {
            if (size > index) return null
            if (size == index) return chatNode
            return get(chatNode?.next, index, size + 1)
        }

        val size get() = _size

        private fun checkCursor(chatNode: ChatNode): Boolean {
            return if (headNode == null) {
                headNode = chatNode
                curNode = chatNode
                _size = 0
                true
            } else false
        }
    }

    private val nodes by lazy { mutableListOf<ChatNodeContainer>() }

    private var _size = -1

    override val size: Int
        get() = _size

    fun add(chatNodeContainer: ChatNodeContainer) {
        nodes.add(chatNodeContainer)
        _size += chatNodeContainer.size
    }

    fun addAll(list: Collection<ChatNodeContainer>) {
        nodes.addAll(list)
        _size += list.sumOf { it.size }
    }

    override fun get(index: Int): ChatNode {
        var indexT = -1
        nodes.forEachIndexed { i, chatNodeContainer ->
            indexT += chatNodeContainer.size
            if (indexT >= index) {
                nodes[i].get(indexT - index) ?: throw IndexOutOfBoundsException()
                return@forEachIndexed
            }
        }
        throw IndexOutOfBoundsException()
    }

    override fun isEmpty(): Boolean = nodes.isEmpty()


    override fun iterator(): Iterator<ChatNode> {
        TODO("Not yet implemented")
    }

    override fun listIterator(): ListIterator<ChatNode> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<ChatNode> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<ChatNode> {
        TODO("Not yet implemented")
    }

    override fun lastIndexOf(element: ChatNode): Int {
        TODO("Not yet implemented")
    }

    override fun indexOf(element: ChatNode): Int {
        var index = -1
        nodes.forEach {
            index += it.size
            it.indexOf(element.data)
        }
        return index
    }

    override fun containsAll(elements: Collection<ChatNode>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: ChatNode): Boolean {
        TODO("Not yet implemented")
    }


}
