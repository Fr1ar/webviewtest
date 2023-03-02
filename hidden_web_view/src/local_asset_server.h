#pragma once
#include <thread>
#include "httplib.h"
#include <dmsdk/dlib/log.h>
#include <android/log.h>

#define LOG_TAG "asset-web-server"
#define LOGI(...) \
((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...) \
((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))

using namespace httplib;

namespace localServer {
	const char* platform_getFileData(const char *basePathIn, const char* filePath);
	int platform_getFileLength(const char *basePathIn, const char* filePath);

	class LocalAssetServer {
	public:
		LocalAssetServer() {};
		~LocalAssetServer();

		void startServer(const char *basePathIn);
		void serverStop();
		bool isServerRunning();
	private:
		Server serverInstance;
		const char *host = "localhost";
		int port = 8808;
		std::thread *serverThread;
		const char *contentType = "application/json";
		const char *basePath = "";
	};
}