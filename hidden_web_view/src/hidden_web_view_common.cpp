#if defined(DM_PLATFORM_ANDROID) || defined(DM_PLATFORM_IOS) || defined (DM_PLATFORM_OSX)

#include <assert.h>
#include <dmsdk/dlib/log.h>

#include "hidden_web_view_common.h"
#include "local_asset_server.h"

namespace hiddenWebView {
	void executeLuaCallback(CallbackInfo* callback) {
		if (callback->info->callbackReference == LUA_NOREF) {
			dmLogError("No callback set");
		}

		lua_State* luaState = callback->info->luaState;
		int latestLuaValue = lua_gettop(luaState);

		lua_rawgeti(luaState, LUA_REGISTRYINDEX, callback->info->callbackReference);
		lua_rawgeti(luaState, LUA_REGISTRYINDEX, callback->info->selfReference);
		
		lua_pushvalue(luaState, -1);

		dmScript::SetInstance(luaState);

		if (!dmScript::IsInstanceValid(luaState)) {
			dmLogError("Could not run WebView callback because the instance has been deleted.");
			
			lua_pop(luaState, 2);
			assert(latestLuaValue == lua_gettop(luaState));
			
			return;
		}

		lua_pushnumber(luaState, (lua_Number)callback->requestId);
		lua_pushnumber(luaState, (lua_Number)callback->resultCode);

		lua_newtable(luaState);

		lua_pushstring(luaState, "url");
		
		if (callback->gamePath) {
			lua_pushstring(luaState, callback->gamePath);
		} else {
			lua_pushnil(luaState);
		}
		
		lua_rawset(luaState, -3);

		lua_pushstring(luaState, "result");

		if (callback->resultData) {
			lua_pushstring(luaState, callback->resultData);
		} else {
			lua_pushnil(luaState);
		}
		
		lua_rawset(luaState, -3);

		int methodResult = lua_pcall(luaState, 4, 1, 0);
		
		if (methodResult != 0) {
			dmLogError("Error running WebView callback: %s", lua_tostring(luaState, -1));
			lua_pop(luaState, 1);
		} else {
			lua_pop(luaState, 1);
		}
		
		assert(latestLuaValue == lua_gettop(luaState));
	}

	void cleanup(HiddenWebViewInfo* info) {
		if (info->callbackReference != LUA_NOREF) {
			dmScript::Unref(info->luaState, LUA_REGISTRYINDEX, info->callbackReference);
		}
		
		if (info->selfReference != LUA_NOREF) {
			dmScript::Unref(info->luaState, LUA_REGISTRYINDEX, info->selfReference);
		}
		
		info->luaState = 0;
		info->callbackReference = LUA_NOREF;
		info->selfReference = LUA_NOREF;
	}

	localServer::LocalAssetServer* server;

	static int createWebView(lua_State* luaState) {
		int top = lua_gettop(luaState);

		luaL_checktype(luaState, 1, LUA_TFUNCTION);
		lua_pushvalue(luaState, 1);

		HiddenWebViewInfo info;
		
		info.callbackReference = dmScript::Ref(luaState, LUA_REGISTRYINDEX);
		
		dmScript::GetInstance(luaState);
		
		info.selfReference = dmScript::Ref(luaState, LUA_REGISTRYINDEX);
		info.luaState = dmScript::GetMainThread(luaState);

		int webview_id = platform_Create(luaState, &info); 
		lua_pushnumber(luaState, webview_id);

		assert(top + 1 == lua_gettop(luaState));
		
		return 1;
	}

	static int destroyWebView(lua_State* luaState) {
		int top = lua_gettop(luaState);

		int result = platform_Destroy(luaState);
		lua_pushnumber(luaState, result);

		assert(top + 1 == lua_gettop(luaState));

		delete server;
		
		return 1;
	}

	void parseMethodParameters(lua_State* luaState, int argumentindex) {
		luaL_checktype(luaState, argumentindex, LUA_TTABLE);
		lua_pushvalue(luaState, argumentindex);
		lua_pushnil(luaState);
		
		while (lua_next(luaState, -2)) {
			const char* attr = lua_tostring(luaState, -2);
			
			lua_pop(luaState, 1);
		}
		
		lua_pop(luaState,  1);
	}

	static int openGame(lua_State* luaState) {
		int top = lua_gettop(luaState);
		
		const char* requestString = luaL_checkstring(luaState, 1);

		if (top >= 3 && !lua_isnil(luaState, 3)) {
			parseMethodParameters(luaState, 3);
		}

		int requestId = platform_LoadGame(luaState, requestString);
		lua_pushnumber(luaState, requestId);

		assert(top + 1 == lua_gettop(luaState));
		
		return 1;
	}

	static int openWebPage(lua_State* luaState) {
		int top = lua_gettop(luaState);

		const char* requestString = luaL_checkstring(luaState, 1);

		if (top >= 3 && !lua_isnil(luaState, 3)) {
			parseMethodParameters(luaState, 3);
		}

		int requestId = platform_LoadWebPage(luaState, requestString);
		lua_pushnumber(luaState, requestId);

		assert(top + 1 == lua_gettop(luaState));

		return 1;
	}

	static int startServer(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const char* baseUrl = luaL_checkstring(luaState, 1);

		lua_pushnumber(luaState, 0);

		server = new localServer::LocalAssetServer();
		server->startServer(baseUrl);

		assert(top + 1 == lua_gettop(luaState));

		return 1;
	}

	static int executeScript(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const char* code = luaL_checkstring(luaState, 1);

		int scriptId = platform_ExecuteScript(luaState, code);
		lua_pushnumber(luaState, scriptId);

		assert(top + 1 == lua_gettop(luaState));
		
		return 1;
	}

	static int addJavaScriptChannel(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const char* channelName = luaL_checkstring(luaState, 1);

		int result = platform_AddJavascriptChannel(luaState, channelName);
		lua_pushnumber(luaState, result);

		assert(top + 1 == lua_gettop(luaState));

		return 1;
	}

	static int setVisible(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const int visible = luaL_checknumber(luaState, 1);

		platform_ChangeVisibility(luaState, visible);

		assert(top == lua_gettop(luaState));
		
		return 0;
	}

	static int isVisible(lua_State* luaState) {
		int top = lua_gettop(luaState);

		int visible = platform_IsInUse(luaState);
		lua_pushnumber(luaState, visible);

		assert(top + 1 == lua_gettop(luaState));
		
		return 1;
	}

	static int setPositionAndSize(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const double x = luaL_checknumber(luaState, 1);
		const double y = luaL_checknumber(luaState, 2);
		const double width = luaL_checknumber(luaState, 3);
		const double height = luaL_checknumber(luaState, 4);

		platform_SetPositionAndSize(luaState, x, y, width, height);

		assert(top == lua_gettop(luaState));
		
		return 0;
	}

	static int matchScreenSize(lua_State* luaState) {
		int top = lua_gettop(luaState);
		
		platform_MatchScreenSize(luaState);

		assert(top == lua_gettop(luaState));

		return 0;
	}

	static int setDebugEnabled(lua_State* luaState) {
		int top = lua_gettop(luaState);

		const int flag = luaL_checknumber(luaState, 1);
		platform_SetDebugEnabled(luaState, flag);

		assert(top == lua_gettop(luaState));

		return 0;
	}

	static int setTouchInterceptorArea(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const double x = luaL_checknumber(luaState, 1);
		const double y = luaL_checknumber(luaState, 2);
		const double width = luaL_checknumber(luaState, 3);
		const double height = luaL_checknumber(luaState, 4);

		platform_SetTouchInterceptorArea(luaState, x, y, width, height);

		assert(top == lua_gettop(luaState));

		return 0;
	}

	static int setAcceptTouchEvents(lua_State* luaState) {
		int top = lua_gettop(luaState);
		const int accept = luaL_checknumber(luaState, 1);

		platform_SetAcceptTouchEvents(luaState, accept);

		assert(top == lua_gettop(luaState));

		return 0;
	}

	static const luaL_reg WebView_methods[] = {
		{"create", createWebView},
		{"destroy", destroyWebView},
		{"open_game", openGame},
		{"set_debug_enabled", setDebugEnabled},
		{"open_web_page", openWebPage},
		{"start_server", startServer},
		{"execute_script", executeScript},
		{"add_javascript_channel", addJavaScriptChannel},
		{"change_visibility", setVisible},
		{"is_in_use", isVisible},
		{"set_position_and_size", setPositionAndSize},
		{"set_accept_touch_events", setAcceptTouchEvents},
		{"set_touch_interceptor_area", setTouchInterceptorArea},
		{"match_screen_size", matchScreenSize},
		{0, 0}
	};

	static void LuaInit(lua_State* luaState) {
		int top = lua_gettop(luaState);
		luaL_register(luaState, "hidden_web_view", WebView_methods);

		#define SETCONSTANT(name) \
		lua_pushnumber(luaState, (lua_Number) name); \
		lua_setfield(luaState, -2, #name);\

		SETCONSTANT(CALLBACK_RESULT_GAME_LOADED)
		SETCONSTANT(CALLBACK_RESULT_GAME_LOADING)
		SETCONSTANT(CALLBACK_RESULT_GAME_ERROR)
		SETCONSTANT(CALLBACK_RESULT_JAVASCRIPT_OK)
		SETCONSTANT(CALLBACK_RESULT_JAVASCRIPT_ERROR)
		SETCONSTANT(CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK)

		#undef SETCONSTANT

		lua_pop(luaState, 1);
		
		assert(top == lua_gettop(luaState));
	}

	static dmExtension::Result WebView_AppInitialize(dmExtension::AppParams* params) {
		return Platform_AppInitialize(params);
	}

	static dmExtension::Result WebView_AppFinalize(dmExtension::AppParams* params) {
		return Platform_AppFinalize(params);
	}

	static dmExtension::Result WebView_Initialize(dmExtension::Params* params) {
		LuaInit(params->m_L);

		return Platform_Initialize(params);
	}

	static dmExtension::Result WebView_Finalize(dmExtension::Params* params) {
		return Platform_Finalize(params);
	}

	static dmExtension::Result WebView_Update(dmExtension::Params* params) {
		return Platform_Update(params);
	}

	DM_DECLARE_EXTENSION(hidden_web_view, "hidden_web_view", WebView_AppInitialize, WebView_AppFinalize, WebView_Initialize, WebView_Update, 0, WebView_Finalize)
	
}
#endif // DM_PLATFORM_ANDROID || DM_PLATFORM_IOS || DM_PLATFORM_OSX