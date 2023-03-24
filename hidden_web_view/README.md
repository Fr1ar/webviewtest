# Описание работы с hidden_web_view

## Предварительная настройка

Для работы расширения необходимо внести в `AndroidManifest.xml` изменения:

- Добавить класс приложения и тэг `hasCode`:
```xml
    <application
        ...
        android:name="com.blitz.hiddenwebview.ApplicationController"
        android:hasCode="true"
        ...>
```

- Назначить прозрачную тему для `DefoldActivity`:
```xml
        <activity
            ...
            android:name="com.dynamo.android.DefoldActivity"
            android:theme="@style/Theme.AppCompat.Translucent">
```

- Добавить meta-data `alpha.transparency` в тэг `application`:
```xml
    <application>
        ...
        <meta-data
            android:name="alpha.transparency"
            android:value="true" />
        ...
    </application>
```

- Добавить саму тему `Theme.AppCompat.Translucent` в файл `themes.xml`:
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.AppCompat.Translucent" parent="@style/Theme.AppCompat.Light.NoActionBar">
        <item name="android:background">@android:color/transparent</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:colorBackgroundCacheHint">@null</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowAnimationStyle">@android:style/Animation</item>
    </style>
</resources>
```

- Добавить зависимость в `build.gradle`:
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.4.1'
}
```

- Добавить html/js ресурсы в проект:
    - Открыть файл `game.project`, `Project` -> `Bundle Resources`, задать каталог `/bundle_resources`
    - Создать каталог `bundle_resources` в корне проекта
    - Для Android - `/bundle_resources/android/assets/{путь который будет в lua в вызове hidden_web_view.open_game}`
    - Для IOS/MacOS - `/bundle_resources}/ios/{путь который будет в lua в вызове hidden_web_view.open_game}`

- Пример кода для запуска игры Hoop Rush:
```lua
function on_html_loaded()
    print("HiddenWebView: on_html_loaded")
    
    local params = {
        game_url = "hoopRush" .. "/index.html",
        has_remote_view = false,
        local_instance = {
            pos = {
                left = 0,
                top = 0,
                width = 1,
                height = 1,
            }
        }
    }
    
    local param_string = json.encode(params)
    local touch_area_height = 0.6
    hidden_web_view.set_touch_interceptor_area(0, 0, 1.0, touch_area_height)

    local js = "__Start(" .. param_string .. ")"
    print("HiddenWebView execute_script: "..js)
    hidden_web_view.execute_script(js)
end


function on_js_callback(data)
    pprint("HiddenWebView: on_js_callback", data)
end

function on_webview_callback(id, type, data)
    print("HiddenWebView: on_webview_callback", type)

    if type == hidden_web_view.CALLBACK_RESULT_GAME_LOADED then
        on_html_loaded()
    elseif type == hidden_web_view.CALLBACK_RESULT_GAME_LOADING then
        print("HiddenWebView: CALLBACK_RESULT_GAME_LOADING")
    elseif type == hidden_web_view.CALLBACK_RESULT_GAME_ERROR then
        print("HiddenWebView: CALLBACK_RESULT_GAME_ERROR")
    elseif type == hidden_web_view.CALLBACK_RESULT_JAVASCRIPT_OK then
        print("HiddenWebView: CALLBACK_RESULT_JAVASCRIPT_OK")
    elseif type == hidden_web_view.CALLBACK_RESULT_JAVASCRIPT_ERROR then
        print("HiddenWebView: CALLBACK_RESULT_JAVASCRIPT_ERROR")
        pprint(data)
    elseif type == hidden_web_view.CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK then
        print("HiddenWebView: CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK:")
        on_js_callback(data)
    else
        print("HiddenWebView: Unknown callback type")
    end
end

function create_webview()
    local instance = hidden_web_view.create(function(self_, id, type, data)
        on_webview_callback(id, type, data)
    end)
    
    local sysinfo = sys.get_sys_info()
    if sysinfo.system_name == "Android" then
        hidden_web_view.start_server("webGL_games/")
    else
        hidden_web_view.start_server("assets/webGL_games/")
    end

    hidden_web_view.match_screen_size()

    hidden_web_view.change_visibility(1) -- android only - open
    hidden_web_view.set_debug_enabled(1) -- android only - set debug for chrome

    hidden_web_view.add_javascript_channel("logging")
    hidden_web_view.add_javascript_channel("_OnNewScore")
    hidden_web_view.add_javascript_channel("_OnBotScoreOneWorld")
    hidden_web_view.add_javascript_channel("_OnChangeHealth")
    hidden_web_view.add_javascript_channel("_OnCommand")
    hidden_web_view.add_javascript_channel("_OnSceneLoaded")
    hidden_web_view.add_javascript_channel("_OnGameFinished")
    hidden_web_view.add_javascript_channel("_OnConsole")
    hidden_web_view.add_javascript_channel("_OnUX")
    
    hidden_web_view.open_game("main.html")
    hidden_web_view.set_accept_touch_events(1)
end
```
