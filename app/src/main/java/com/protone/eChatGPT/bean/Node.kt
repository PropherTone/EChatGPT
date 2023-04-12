package com.protone.eChatGPT.bean

abstract class Node<T>(data: T) {
    var previous: Node<T>? = null
    var next: Node<T>? = null
    var data: T? = data
}