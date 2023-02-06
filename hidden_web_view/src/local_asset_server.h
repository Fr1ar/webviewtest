#pragma once
#include <thread>
#include "httplib.h"
#include <dmsdk/dlib/log.h>

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