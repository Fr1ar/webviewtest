#ifndef HIDDEN_WEBVIEW_COMMON_H
#define HIDDEN_WEBVIEW_COMMON_H

#include <dmsdk/script/script.h>
#include <dmsdk/extension/extension.h>

#include "local_asset_server.h"

namespace hiddenWebView {
	enum CallbackResult {
		CALLBACK_RESULT_GAME_LOADED = 0,
		CALLBACK_RESULT_GAME_LOADING = 2,
		CALLBACK_RESULT_GAME_ERROR = -1,
		CALLBACK_RESULT_JAVASCRIPT_OK = 1,
		CALLBACK_RESULT_JAVASCRIPT_ERROR = -2,
		CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK = 3,
	};

	enum CommandType {
		CMD_LOAD_OK,
		CMD_LOAD_ERROR,
		CMD_SCRIPT_OK,
		CMD_SCRIPT_CALLBACK,
		CMD_SCRIPT_ERROR,
		CMD_LOADING,
	};

	struct WebViewCommand {
		WebViewCommand() {
			memset(this, 0, sizeof(WebViewCommand));
		}
		
		CommandType type;
		int requestId;
		void* payload;
		const char* url;
	};

	struct HiddenWebViewInfo {
		lua_State* luaState;
		int selfReference;
		int callbackReference;

		HiddenWebViewInfo() : 
			luaState(0), 
			selfReference(LUA_NOREF),
			callbackReference(LUA_NOREF) {}
	};

	struct CallbackInfo {
		HiddenWebViewInfo* info;
		int requestId;
		CallbackResult resultCode;
		const char* gamePath;
		const char* resultData;

		CallbackInfo()
			: info(0)
			, requestId(0)
			, resultCode(CALLBACK_RESULT_GAME_LOADED)
			, gamePath(0)
			, resultData(0)
			{}
		};

		void executeLuaCallback(CallbackInfo* cbinfo);
		void cleanup(HiddenWebViewInfo* info);
		
		int platform_LoadGame(lua_State* luaState, const char* gamePath);
		int platform_LoadWebPage(lua_State* luaState, const char* gamePath);
		int platform_ExecuteScript(lua_State* luaState,  const char* script);
		int platform_AddJavascriptChannel(lua_State* luaState,  const char* channelName);
		int platform_ChangeVisibility(lua_State* luaState, int visible);
		int platform_IsInUse(lua_State* luaState);
		int platform_SetPositionAndSize(lua_State* luaState, double x, double y, double width, double height);
		int platform_SetAcceptTouchEvents(lua_State* luaState, int accept);
		int platform_SetTouchInterceptorArea(lua_State* luaState, double x, double y, double width, double height);
		int platform_MatchScreenSize(lua_State* luaState);
		int platform_Create(lua_State* luaState, hiddenWebView::HiddenWebViewInfo* _info);
		int platform_Destroy(lua_State* luaState);
		int platform_SetDebugEnabled(lua_State* luaState, int flag);

		dmExtension::Result Platform_AppInitialize(dmExtension::AppParams* params);
		dmExtension::Result Platform_Initialize(dmExtension::Params* params);
		dmExtension::Result Platform_Finalize(dmExtension::Params* params);
		dmExtension::Result Platform_AppFinalize(dmExtension::AppParams* params);
		dmExtension::Result Platform_Update(dmExtension::Params* params);
	}

#endif // HIDDEN_WEBVIEW_COMMON_H
	