package com.protone.eChatGPT.repository.dataStore

import kotlin.reflect.KProperty

interface Delegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}