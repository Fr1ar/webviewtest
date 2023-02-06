# Описание работы с webview

## API

```lua
local function webview_callback(self, id, type, data)
	print("webview_callback", type)
	
	if type == hidden_web_view.CALLBACK_RESULT_GAME_LOADED then
		print("CALLBACK_RESULT_GAME_LOADED")
	elseif type == hidden_web_view.CALLBACK_RESULT_GAME_LOADING then
		print("CALLBACK_RESULT_GAME_LOADING")
	elseif type == hidden_web_view.CALLBACK_RESULT_GAME_ERROR then
		print("CALLBACK_RESULT_GAME_ERROR")
	elseif type == hidden_web_view.CALLBACK_RESULT_JAVASCRIPT_OK then
		print("CALLBACK_RESULT_JAVASCRIPT_OK")
	elseif type == hidden_web_view.CALLBACK_RESULT_JAVASCRIPT_ERROR then
		print("CALLBACK_RESULT_JAVASCRIPT_ERROR")
	else
		print("Unknown callback type")
	end
end

if webview_lock == nil then
		webview_lock = hidden_web_view.create(webview_callback)
		hidden_web_view.match_screen_size()
		hidden_web_view.set_touch_interceptor_area(50, 50, 200, 300)
		hidden_web_view.set_position_and_size(50, 50, 200, 300)
		hidden_web_view.change_visibility(1) -- android only - open
		hidden_web_view.open_game("assets/d.html")
		hidden_web_view.execute_script("testFunction")

		
		hidden_web_view.set_accept_touch_events(0)
		hidden_web_view.set_accept_touch_events(1)

		hidden_web_view.destroy() -- ios only - close
		hidden_web_view.change_visibility(0) -- android only - close
	end
```

Для корректной работы библиотеки необходимо отметить для android манифест, который содержится в папке екстеншена

Так же необходимо правильно добавлять html ресурсы в проект:

Сначала добавить в проект bundle_resources или если еще не добавили, или добавлять туда игры по строгому пути

Android - {название папки bundle_resources}/android/assets/{путь который будет в lua в вызове hidden_web_view.open_game}
IOS/MacOS - {название папки bundle_resources}/ios/{путь который будет в lua в вызове hidden_web_view.open_game}