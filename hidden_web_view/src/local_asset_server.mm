#if defined(DM_PLATFORM_IOS) || defined(DM_PLATFORM_OSX) 

#import <Foundation/Foundation.h>

#include <iostream>
#include <fstream>
#include <vector>

#include "local_asset_server.h"

namespace localServer {
  const char* platform_getFileData(const char *basePathIn, const char* gamePath) {
    @try {
      NSString *filePath = [[NSBundle mainBundle] bundlePath];
      NSString *path = [NSString stringWithUTF8String: gamePath];
      NSString *gamePathNSBase = [filePath stringByAppendingPathComponent: [NSString stringWithUTF8String: basePathIn]];
      NSString *gamePathNS = [gamePathNSBase stringByAppendingPathComponent: path];

      NSLog(@"Trying find file with path: %@", gamePathNS);

      NSURL *gameUrl = [[NSURL alloc] initFileURLWithPath: gamePathNS];

      NSError *err = nil;
      NSData *fileData = [NSData dataWithContentsOfURL:gameUrl options:NSDataReadingUncached error:&err];
      
      if (err) {
        return nil;
      }
      
      NSUInteger length = [fileData length];
      char *bytes = new char[length];
      [fileData getBytes:bytes length:length];
      return bytes;
    }
    @catch (NSException *exception) {
      return nil; 
    }
  }

  int platform_getFileLength(const char *basePathIn, const char* gamePath) {
    @try {
      NSString *filePath = [[NSBundle mainBundle] bundlePath];
      NSString *path = [NSString stringWithUTF8String: gamePath];
      NSString *gamePathNSBase = [filePath stringByAppendingPathComponent: [NSString stringWithUTF8String: basePathIn]];
      NSString *gamePathNS = [gamePathNSBase stringByAppendingPathComponent: path];
      NSURL *gameUrl = [[NSURL alloc] initFileURLWithPath: gamePathNS];

      NSError *err = nil;
      NSData *fileData = [NSData dataWithContentsOfURL:gameUrl options:NSDataReadingUncached error:&err];

      if (err) {
        return 0;
      }

      NSUInteger length = [fileData length];

      return length;
    }
    @catch (NSException *exception) {
      return 0; 
    }
  }
}

#endif // DM_PLATFORM_IOS || DM_PLATFORM_OSX