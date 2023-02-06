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
#include <android/asset_manager.h>
#include <android/log.h>
#include "local_asset_server.h"

extern struct android_app* g_AndroidApp;

namespace localServer {
	std::vector<char> getFileData(const char *basePathIn, const char* gamePath) {
		AAssetManager* manager = g_AndroidApp->activity->assetManager; 

		const char* filename;
		std::vector<char> buffer;

		std::string searchFileName(basePathIn);
		searchFileName.append(gamePath);
		searchFileName = std::regex_replace(searchFileName, std::regex("//"), "/");

		AAsset *asset = AAssetManager_open(manager, searchFileName.c_str(), AASSET_MODE_STREAMING);

		if (!asset) {
			return buffer;
		}

		off64_t length = AAsset_getLength64(asset);
		off64_t remaining = AAsset_getRemainingLength64(asset);

		size_t Mb = 1000 * 1024;
		size_t currChunk;

		buffer.reserve(length);

		while (remaining != 0) {
			if (remaining >= Mb) {
				currChunk = Mb;
			} else {
				currChunk = remaining;
			}

			char chunk[currChunk];

			if (AAsset_read(asset, chunk, currChunk) > 0) {
				buffer.insert(buffer.end(),chunk, chunk + currChunk);
				remaining = AAsset_getRemainingLength64(asset);
			}
		}

		AAsset_close(asset);

		return buffer;
	}
	
	const char* platform_getFileData(const char *basePathIn, const char* gamePath) {
		std::vector<char> buffer = getFileData(basePathIn, gamePath);

		if (buffer.empty()) {
			return new char[0];
		}
		
		char *result = new char[buffer.size()];
		std::copy(buffer.begin(), buffer.end(), result);

		return result;
	}

	int platform_getFileLength(const char *basePathIn, const char* gamePath) {
		std::vector<char> buffer = getFileData(basePathIn, gamePath);

		return buffer.size();
	}
}

#endif // DM_PLATFORM_ANDROID