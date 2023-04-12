package com.protone.eChatGPT.utils

import com.google.gson.*
import com.protone.eChatGPT.bean.ChatItem
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private val gson by lazy {
    GsonBuilder()
        .registerTypeAdapter(
            ChatItem.ChatTarget::class.java,
            ChatTargetSerializer()
        )
        .registerTypeAdapter(
            ChatItem.ChatTarget::class.java,
            ChatTargetDeserializer()
        ).create()
}

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

class ParameterizedTypeImp(private val raw: Class<*>, private val type: Array<Type>) :
    ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> = type
    override fun getRawType(): Type = raw
    override fun getOwnerType(): Type? = null
}

class ChatTargetSerializer : JsonSerializer<ChatItem.ChatTarget?> {
    override fun serialize(
        src: ChatItem.ChatTarget?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(when (src) {
            is ChatItem.ChatTarget.AI -> {
                buildString {
                    append("AI|")
                    append(src.userId)
                }
            }
            is ChatItem.ChatTarget.HUMAN -> {
                buildString {
                    append("HUMAN|")
                    append(src.systemId)
                    append("|")
                    append(src.isSystem)
                }
            }
            else -> null
        })
    }

}

class ChatTargetDeserializer : JsonDeserializer<ChatItem.ChatTarget?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ChatItem.ChatTarget? {
        return json?.asString?.let {
            val split = it.split("|")
            if (split.size >= 2) {
                when (split[0]) {
                    "AI" -> ChatItem.ChatTarget.AI(split[1])
                    "HUMAN" -> ChatItem.ChatTarget.HUMAN(split[1], split[2].toBoolean())
                    else -> null
                }
            } else null
        }
    }


}