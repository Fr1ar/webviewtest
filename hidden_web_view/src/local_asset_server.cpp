#include "local_asset_server.h"
#include <iostream>

namespace localServer {
	localServer::LocalAssetServer::~LocalAssetServer() {
		if (serverInstance.is_running()) {
			serverStop();
		}
	};

	void localServer::LocalAssetServer::serverStop() {
		if (serverInstance.is_running()) {
			serverInstance.stop();
			serverThread->join();
		}
	}

	bool localServer::LocalAssetServer::isServerRunning() {
		return serverInstance.is_running();
	}

	void localServer::LocalAssetServer::startServer(const char *basePathIn) {
		basePath = basePathIn;
		
		serverThread = new std::thread([&]() {
			serverInstance.Get(R"(.*?)", [&](const Request &req, Response &res) {
				auto filePath = req.path;
				
				const char* byteData = localServer::platform_getFileData(basePath, filePath.c_str());

				if (byteData) {
					int byteDataLength = localServer::platform_getFileLength(basePath, filePath.c_str());
					std::string mimeType = httplib::detail::find_content_type(filePath);

					res.set_content(byteData, byteDataLength, mimeType.c_str());
				} else {
					std::string mimeType = httplib::detail::find_content_type(filePath);
					res.set_content("", 2, mimeType.c_str());
				}
				
			});

			serverInstance.listen(host, port);
		});
	}
}