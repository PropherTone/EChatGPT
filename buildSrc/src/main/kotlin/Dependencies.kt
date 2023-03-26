object OpenAiPack {
    const val openAiClientBOM = "com.aallam.openai:openai-client-bom:3.1.1"
    const val openAiClient = "com.aallam.openai:openai-client"
    const val ktorClientOkhttp = "io.ktor:ktor-client-okhttp"
}

object LifeCyclePack {
    private const val KTX_VERSION = "2.5.1"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$KTX_VERSION"
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:$KTX_VERSION"
    const val viewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$KTX_VERSION"
    const val lifecycleCompilerAPT = "androidx.lifecycle:lifecycle-compiler:$KTX_VERSION"
}

object DataBasePack {
    //DataStore本地持久化库
    const val dataStore = "androidx.datastore:datastore-preferences:1.0.0"

    //ROOM数据库
    private const val ROOM_VERSION = "2.5.0"
    const val roomCompiler = "androidx.room:room-compiler:$ROOM_VERSION"
    const val roomKtx = "androidx.room:room-ktx:$ROOM_VERSION"
    const val roomRuntime = "androidx.room:room-runtime:$ROOM_VERSION"
    const val roomPaging = "androidx.room:room-paging:$ROOM_VERSION"
}

object Libs {
    const val gson = "com.google.code.gson:gson:2.10.1"
    const val paging3 = "androidx.paging:paging-runtime:3.1.0"
}