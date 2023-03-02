#include "local_asset_server.h"
#include <iostream>

namespace localServer {
	localServer::LocalAssetServer::~LocalAssetServer() {
		LOGI("LocalAssetServer desctructor");
		
		if (serverInstance.is_running()) {
			serverStop();
		}
	};

	void localServer::LocalAssetServer::serverStop() {
		LOGI("LocalAssetServer::serverStop()");
		if (serverInstance.is_running()) {
			serverInstance.stop();
			serverThread->join();
		}
	}

	bool localServer::LocalAssetServer::isServerRunning() {
		return serverInstance.is_running();
	}

	void localServer::LocalAssetServer::startServer(const char *basePathIn) {
		LOGI("LocalAssetServer::startServer('%s')", basePathIn);
		
		basePath = basePathIn;
		
		serverThread = new std::thread([&]() {
			LOGI("LocalAssetServer serverThread started");
			
			serverInstance.Get(R"(.*?)", [&](const Request &req, Response &res) {
				auto filePath = req.path;
				const char* filePathStr = filePath.c_str();
				
				LOGI("LocalAssetServer new request received: %s", filePathStr);
				
				const char* byteData = localServer::platform_getFileData(basePath, filePathStr);

				if (byteData) {
					LOGI("LocalAssetServer file '%s' data received", filePathStr);
					int byteDataLength = localServer::platform_getFileLength(basePath, filePathStr);
					LOGI("LocalAssetServer file '%s' data length: %d", filePathStr, byteDataLength);
					std::string mimeType = httplib::detail::find_content_type(filePath);
					LOGI("LocalAssetServer file '%s' mime type: %s", filePathStr, mimeType.c_str());
					res.set_content(byteData, byteDataLength, mimeType.c_str());
				} else {
					LOGW("LocalAssetServer file '%s' not found", filePathStr);
					std::string mimeType = httplib::detail::find_content_type(filePath);
					res.set_content("", 2, mimeType.c_str());
				}
			});

			LOGI("LocalAssetServer listening at %s:%d", host, port);
			serverInstance.listen(host, port);
		});
	}
}