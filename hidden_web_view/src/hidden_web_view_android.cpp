#if defined(DM_PLATFORM_ANDROID)

#include <jni.h>
#include <stdlib.h>
#include <unistd.h>

#include <dmsdk/dlib/array.h>
#include <dmsdk/dlib/log.h>
#include <dmsdk/dlib/mutex.h>
#include <dmsdk/script/script.h>
#include <dmsdk/extension/extension.h>
#include <android_native_app_glue.h>

#include "hidden_web_view_common.h"

extern struct android_app* g_AndroidApp;

static JNIEnv* Attach() {
	JNIEnv* jniEnv = 0;
	g_AndroidApp->activity->vm->AttachCurrentThread(&jniEnv, NULL);
	
	return jniEnv;
}

static void Detach() {
	g_AndroidApp->activity->vm->DetachCurrentThread();
}

struct HiddenWebViewExtensionState {
	HiddenWebViewExtensionState() {
		memset(this, 0, sizeof(*this));
	}

	void clear() {
		cleanup(&webViewInfo);
		isUsed = false;
		requestId = 0;
	}

	hiddenWebView::HiddenWebViewInfo webViewInfo;
	bool isUsed;
	int requestId;
	jobject jniInterface;
	jmethodID loadGameMethod;
	jmethodID loadWebPageMethod;
	jmethodID executeScriptMethod;
	jmethodID addJavascriptChannelMethod;
	jmethodID centerWebViewMethod;
	jmethodID changeVisibilityMethod;
	jmethodID isInUseMethod;
	jmethodID setPositionAndSizeMethod;
	jmethodID setAcceptTouchEventsMethod;
	jmethodID setDebugEnabledMethod;
	jmethodID matchScreenSizeMethod;
	jmethodID setTouchInterceptorAreaMethod;
	dmMutex::HMutex mutex;
	dmArray<hiddenWebView::WebViewCommand> commands;
};

HiddenWebViewExtensionState webViewMain;

namespace hiddenWebView {
	int platform_LoadGame(lua_State* luaState, const char* gamePath) {
		int requestId = ++webViewMain.requestId;

		JNIEnv* env = Attach();
		jstring jgamePath = env->NewStringUTF(gamePath);
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.loadGameMethod, jgamePath, requestId);
		env->DeleteLocalRef(jgamePath);
		Detach();
		
		return requestId;
	}

	int platform_LoadWebPage(lua_State* luaState, const char* gamePath) {
		int requestId = ++webViewMain.requestId;

		JNIEnv* env = Attach();
		jstring jgamePath = env->NewStringUTF(gamePath);
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.loadWebPageMethod, jgamePath, requestId);
		env->DeleteLocalRef(jgamePath);
		Detach();

		return requestId;
	}

	int platform_ExecuteScript(lua_State* luaState,  const char* script) {
		int scriptId = ++webViewMain.requestId;

		JNIEnv* env = Attach();
		jstring jscript = env->NewStringUTF(script);
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.executeScriptMethod, jscript, scriptId);
		Detach();
		
		return scriptId;
	}

	int platform_AddJavascriptChannel(lua_State* luaState,  const char* channelName) {
		dmLogInfo("MMMMMM BEFORE CALL");

		int scriptId = ++webViewMain.requestId;
		
		JNIEnv* env = Attach();
		jstring channel = env->NewStringUTF(channelName);
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.addJavascriptChannelMethod, channel, scriptId);
		Detach();

		dmLogInfo("MMMMMM AFTER CALL");

		return scriptId;
	}

	int platform_ChangeVisibility(lua_State* luaState, int visible) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.changeVisibilityMethod, visible);
		Detach();
		
		return 0;
	}

	int platform_IsInUse(lua_State* luaState) {
		JNIEnv* env = Attach();
		int visible = env->CallIntMethod(webViewMain.jniInterface, webViewMain.isInUseMethod);
		Detach();
		
		return visible;
	}

	int platform_SetPositionAndSize(lua_State* luaState, double x, double y, double width, double height) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.setPositionAndSizeMethod, x, y, width, height);
		Detach();
		
		return 0;
	}

	int platform_SetTouchInterceptorArea(lua_State* luaState, double x, double y, double width, double height) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.setTouchInterceptorAreaMethod, x, y, width, height);
		Detach();

		return 0;
	}

	int platform_MatchScreenSize(lua_State* luaState) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.matchScreenSizeMethod);
		Detach();
		
		return 0;
	}

	int platform_Create(lua_State* luaState, hiddenWebView::HiddenWebViewInfo* _info) {
		webViewMain.webViewInfo = *_info;
		
		return 0;
	}

	int platform_Destroy(lua_State* luaState) {
		return 0;
	}

	int platform_SetAcceptTouchEvents(lua_State* luaState, int accept) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.setAcceptTouchEventsMethod, accept);
		Detach();
		
		return 0;
	}

	int platform_SetDebugEnabled(lua_State* luaState, int flag) {
		JNIEnv* env = Attach();
		env->CallVoidMethod(webViewMain.jniInterface, webViewMain.setDebugEnabledMethod, flag);
		Detach();

		return 0;
	}

	static char* CopyString(JNIEnv* env, jstring s) {
		const char* javastring = env->GetStringUTFChars(s, 0);
		
		char* copy = strdup(javastring);
		env->ReleaseStringUTFChars(s, javastring);
		
		return copy;
	}

	static void QueueCommand(hiddenWebView::WebViewCommand* cmd) {
		DM_MUTEX_SCOPED_LOCK(webViewMain.mutex);
		
		if (webViewMain.commands.Full()) {
			webViewMain.commands.OffsetCapacity(8);
		}
		
		webViewMain.commands.Push(*cmd);
	}

	#ifdef __cplusplus
	extern "C" {
	#endif
		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_FakeWebViewActivity_onPageFinished(JNIEnv* env, jobject, jstring url, jint id) {
			WebViewCommand cmd;
			cmd.type = CMD_LOAD_OK;
			cmd.requestId = id;
			cmd.url = CopyString(env, url);
			QueueCommand(&cmd);
		}

		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_FakeWebViewActivity_onReceivedError(JNIEnv* env, jobject, jstring url, jstring errorMessage, jint id) {
			WebViewCommand cmd;
			cmd.type = CMD_LOAD_ERROR;
			cmd.requestId = id;
			cmd.url = CopyString(env, url);
			QueueCommand(&cmd);
		}

		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_DefoldWebViewInterface_onScriptFinished(JNIEnv* env, jobject, jstring result, jint id) {
			WebViewCommand cmd;
			cmd.type = CMD_SCRIPT_OK;
			cmd.requestId = id;
			cmd.url = 0;
			cmd.payload = CopyString(env, result);
			QueueCommand(&cmd);
		}

		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_DefoldWebViewInterface_onScriptCallback(JNIEnv* env, jobject, jstring type, jstring data) {
			WebViewCommand cmd;
			cmd.type = CMD_SCRIPT_CALLBACK;
			cmd.requestId = 0;
			cmd.url = CopyString(env, type);
			cmd.payload = CopyString(env, data);
			QueueCommand(&cmd);
		}

		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_FakeWebViewActivity_onScriptFailed(JNIEnv* env, jobject, jstring error, jint id) {
			WebViewCommand cmd;
			cmd.type = CMD_SCRIPT_ERROR;
			cmd.requestId = id;
			cmd.url = 0;
			cmd.payload = CopyString(env, error);
			QueueCommand(&cmd);
		}

		JNIEXPORT void JNICALL Java_com_blitz_hiddenwebview_FakeWebViewActivity_onPageLoading(JNIEnv* env, jobject, jstring url, jint id) {
			WebViewCommand cmd;
			cmd.type = CMD_LOADING;
			cmd.requestId = id;
			cmd.url = CopyString(env, url);
			QueueCommand(&cmd);
		}
	#ifdef __cplusplus
	}
	#endif

	dmExtension::Result Platform_AppInitialize(dmExtension::AppParams* params) {
		webViewMain.mutex = dmMutex::New();
		webViewMain.commands.SetCapacity(8);

		JNIEnv* env = Attach();

		jclass activity_class = env->FindClass("android/app/NativeActivity");
		jmethodID get_class_loader = env->GetMethodID(activity_class,"getClassLoader", "()Ljava/lang/ClassLoader;");
		jobject cls = env->CallObjectMethod(g_AndroidApp->activity->clazz, get_class_loader);
		jclass class_loader = env->FindClass("java/lang/ClassLoader");
		jmethodID find_class = env->GetMethodID(class_loader, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
		
		jstring str_class_name = env->NewStringUTF("com.blitz.hiddenwebview.DefoldWebViewInterface");
		
		jclass webview_class = (jclass)env->CallObjectMethod(cls, find_class, str_class_name);
		env->DeleteLocalRef(str_class_name);

		dmLogInfo("MMMMMM BEFORE INIT 1");
		
		webViewMain.loadWebPageMethod = env->GetMethodID(webview_class, "loadWebPage", "(Ljava/lang/String;I)V");
		webViewMain.loadGameMethod = env->GetMethodID(webview_class, "loadGame", "(Ljava/lang/String;I)V");
		webViewMain.executeScriptMethod = env->GetMethodID(webview_class, "executeScript", "(Ljava/lang/String;I)V");
		webViewMain.addJavascriptChannelMethod = env->GetMethodID(webview_class, "addJavascriptChannel", "(Ljava/lang/String;I)V");
		webViewMain.centerWebViewMethod = env->GetMethodID(webview_class, "centerWebView", "()V");
		webViewMain.changeVisibilityMethod = env->GetMethodID(webview_class, "changeVisibility", "(I)V");
		webViewMain.setPositionAndSizeMethod = env->GetMethodID(webview_class, "setPositionAndSize", "(DDDD)V");
		webViewMain.isInUseMethod = env->GetMethodID(webview_class, "isInUse", "()I");
		webViewMain.setAcceptTouchEventsMethod = env->GetMethodID(webview_class, "acceptTouchEvents", "(I)V");
		webViewMain.matchScreenSizeMethod = env->GetMethodID(webview_class, "matchScreenSize", "()V");
		webViewMain.setTouchInterceptorAreaMethod = env->GetMethodID(webview_class, "setTouchInterceptor", "(DDDD)V");
		webViewMain.setDebugEnabledMethod = env->GetMethodID(webview_class, "setDebugEnabled", "(I)V");

		dmLogInfo("MMMMMM AFTER INIT");
		
		jmethodID jni_constructor = env->GetMethodID(webview_class, "<init>", "()V");

		dmLogInfo("MMMMMM BEFORE CONSTRUCTOR");
		
		webViewMain.jniInterface = env->NewGlobalRef(env->NewObject(webview_class, jni_constructor, g_AndroidApp->activity->clazz, 1));

		dmLogInfo("MMMMMM AFTER CONSTRUCTOR");
		
		Detach();

		dmLogInfo("MMMMMM AFTER INIT 2");
		
		return dmExtension::RESULT_OK;
	}

	dmExtension::Result Platform_Initialize(dmExtension::Params* params) {
		return dmExtension::RESULT_OK;
	}

	dmExtension::Result Platform_AppFinalize(dmExtension::AppParams* params) {
		JNIEnv* env = Attach();
		env->DeleteGlobalRef(webViewMain.jniInterface);
		Detach();
		
		webViewMain.jniInterface = NULL;

		dmMutex::Delete(webViewMain.mutex);

		return dmExtension::RESULT_OK;
	}

	dmExtension::Result Platform_Finalize(dmExtension::Params* params) {
		DM_MUTEX_SCOPED_LOCK(webViewMain.mutex);
		
		for (uint32_t i = 0; i != webViewMain.commands.Size(); ++i) {
			const WebViewCommand& cmd = webViewMain.commands[i];
			if (cmd.url) {
				free((void*)cmd.url);
			}
		}
		
		webViewMain.commands.SetSize(0);
		
		return dmExtension::RESULT_OK;
	}

	dmExtension::Result Platform_Update(dmExtension::Params* params) {
		if (webViewMain.commands.Empty()) {
			return dmExtension::RESULT_OK;
		}

		DM_MUTEX_SCOPED_LOCK(webViewMain.mutex);
		
		for (uint32_t i = 0; i != webViewMain.commands.Size(); ++i) {
			const WebViewCommand& cmd = webViewMain.commands[i];

			hiddenWebView::CallbackInfo cbinfo;
			
			switch (cmd.type) {
				case CMD_LOADING:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = cmd.url;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_LOADING;
				cbinfo.resultData = 0;
				executeLuaCallback(&cbinfo);
				break;

				case CMD_LOAD_OK:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = cmd.url;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_LOADED;
				cbinfo.resultData = 0;
				executeLuaCallback(&cbinfo);
			
				break;

				case CMD_LOAD_ERROR:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = cmd.url;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_ERROR;
				cbinfo.resultData = (const char*) cmd.payload;
				executeLuaCallback(&cbinfo);
				break;

				case CMD_SCRIPT_OK:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = 0;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_JAVASCRIPT_OK;
				cbinfo.resultData = (const char*) cmd.payload;
				executeLuaCallback(&cbinfo);
				break;

				case CMD_SCRIPT_CALLBACK:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = (const char*) cmd.url;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK;
				cbinfo.resultData = (const char*) cmd.payload;
				executeLuaCallback(&cbinfo);
				break;

				case CMD_SCRIPT_ERROR:
				cbinfo.info = &webViewMain.webViewInfo;
				cbinfo.requestId = cmd.requestId;
				cbinfo.gamePath = 0;
				cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_JAVASCRIPT_ERROR;
				cbinfo.resultData = (const char*) cmd.payload;
				executeLuaCallback(&cbinfo);
				break;

				default:
				assert(false);
			}
			
			if (cmd.url) {
				free((void*)cmd.url);
			}
			if (cmd.payload) {
				free(cmd.payload);
			}
		}
		
		webViewMain.commands.SetSize(0);
		
		return dmExtension::RESULT_OK;
	}

}

#endif // DM_PLATFORM_ANDROID