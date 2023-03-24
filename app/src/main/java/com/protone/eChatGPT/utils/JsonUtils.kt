package com.protone.eChatGPT.utils

import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private val gson by lazy { GsonBuilder().create() }

fun Any.toJson(): String {
    return gson.toJson(this, this::class.java)
}

fun <C> String.toEntity(clazz: Class<C>?): C {
    return gson.fromJson(this, clazz)
}

fun <T> Collection<*>.listToJson(clazz: Class<T>): String {
    return gson.toJson(this, ParameterizedTypeImp(Collection::class.java, arrayOf(clazz)))
}

fun <T> String.jsonToList(clazz: Class<T>): Collection<T> {
    return gson.fromJson(this, ParameterizedTypeImp(Collection::class.java, arrayOf(clazz)))
}

class ParameterizedTypeImp(private val raw: Class<*>, val type: Array<Type>) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> = type
    override fun getRawType(): Type = raw
    override fun getOwnerType(): Type? = null
}