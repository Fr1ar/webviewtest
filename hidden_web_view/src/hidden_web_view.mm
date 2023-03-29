#if defined(DM_PLATFORM_IOS) || defined(DM_PLATFORM_OSX) 

#include <dmsdk/dlib/array.h>
#include <dmsdk/dlib/log.h>
#include <dmsdk/dlib/mutex.h>
#include <dmsdk/graphics/graphics_native.h>
#include <dmsdk/graphics/graphics.h>
#include <dmsdk/script/script.h>
#include <dmsdk/extension/extension.h>

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
#import <WebKit/WKWebsiteDataStore.h>

#if defined(DM_PLATFORM_IOS)
#import <UIKit/UIKit.h>
#endif 

#include "hidden_web_view_common.h"

BOOL isViewInUse = FALSE;
BOOL isAcceptingTouchEvents = TRUE;

#if defined(DM_PLATFORM_IOS)
@interface WebViewDelegate : UIViewController <WKNavigationDelegate>
#elif defined(DM_PLATFORM_OSX)
@interface WebViewDelegate : NSViewController <WKNavigationDelegate>
#endif 
{
  @public void (^navigationHandler)(WKNavigationActionPolicy);
  @public int requestId;
}
@end

@interface JavaScriptChannel : NSObject <WKScriptMessageHandler>
{
  @public const char* channelName;
}
@end

struct WebViewExtensionState {
  WebViewExtensionState() {
    Clear();
  }

  void Clear() {
    cleanup(&webViewInfo);
    commands.SetSize(0);
  }

  hiddenWebView::HiddenWebViewInfo webViewInfo;
  CGRect touchInterceptorRect;
  WKWebView* view;
  WebViewDelegate* navigationDelegate;
  dmMutex::HMutex mutex;
  dmArray<hiddenWebView::WebViewCommand> commands;
};

WebViewExtensionState webViewMain;

@implementation JavaScriptChannel

- (void)userContentController:(WKUserContentController*)userContentController didReceiveScriptMessage:(WKScriptMessage*)message {
  hiddenWebView::CallbackInfo cbinfo;

  cbinfo.info = &webViewMain.webViewInfo;
  cbinfo.requestId = 0;
  cbinfo.gamePath = channelName;
  cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_JAVASCRIPT_CHANNEL_CALLBACK;
  cbinfo.resultData = [[NSString stringWithFormat:@"%@", message.body] UTF8String];

  executeLuaCallback(&cbinfo);
}

@end

@implementation WebViewDelegate

- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
  if (navigationHandler) {
    navigationHandler(WKNavigationActionPolicyCancel);
    navigationHandler = NULL;
  }

  NSString *url = navigationAction.request.URL.absoluteString;

  navigationHandler = decisionHandler;

  hiddenWebView::CallbackInfo cbinfo;
  
  cbinfo.info = &webViewMain.webViewInfo;
  cbinfo.requestId = 0;
  cbinfo.gamePath = [url UTF8String];
  cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_LOADING;
  cbinfo.resultData = 0;
  
  executeLuaCallback(&cbinfo);

  navigationHandler(WKNavigationActionPolicyAllow);
  navigationHandler = NULL;
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
  hiddenWebView::CallbackInfo cbinfo;

  cbinfo.info = &webViewMain.webViewInfo;
  cbinfo.requestId = 0;
  cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_LOADED;
  cbinfo.resultData = 0;

  executeLuaCallback(&cbinfo);
}

- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
  hiddenWebView::CallbackInfo cbinfo;

  cbinfo.info = &webViewMain.webViewInfo;
  cbinfo.requestId = 0;
  cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_GAME_ERROR;
  cbinfo.resultData = [error.localizedDescription UTF8String];

  executeLuaCallback(&cbinfo);
}

@end

#if defined(DM_PLATFORM_IOS)
@implementation UIView (SDExtension)
- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event ; {
  // TRUE - значит обрабатывает это view
  // FALSE - передается child view

  BOOL isWebView = [self isKindOfClass:[WKWebView class]];

  NSString* defoldSurfaceClassName = @"EAGLView";
  NSString* name = NSStringFromClass([self class]);

  const char *c_defoldSurfaceClassName = [defoldSurfaceClassName UTF8String];
  const char *c_name = [name UTF8String];

  BOOL isDrawLayer = strcmp(c_defoldSurfaceClassName,  c_name) == 0;

  if (isWebView) {
    return isAcceptingTouchEvents && CGRectContainsPoint(self.bounds, point);
  } else if (isDrawLayer && isViewInUse) {
    UIView* glview = (UIView*)dmGraphics::GetNativeiOSUIView();

    CGPoint localCoordinates = [webViewMain.view convertPoint:point toView:webViewMain.view.superview];
    CGPoint globalCoordinates = CGPointMake(
      localCoordinates.x - webViewMain.view.frame.origin.x, 
      localCoordinates.y - webViewMain.view.frame.origin.y
    );
    
    return isAcceptingTouchEvents ? 
        !CGRectContainsPoint(webViewMain.touchInterceptorRect, globalCoordinates) : 
        CGRectContainsPoint(self.bounds, point);
  } else {
    return CGRectContainsPoint(self.bounds, point);
  }
}

@end
#endif 

static char* CopyString(NSString* s) {
  const char* osstring = [s UTF8String];
  char* copy = strdup(osstring);
  return copy;
}

static void QueueCommand(hiddenWebView::WebViewCommand* cmd) {
  dmMutex::ScopedLock lk(webViewMain.mutex);
  
  if (webViewMain.commands.Full()) {
    webViewMain.commands.OffsetCapacity(8);
  }
  
  webViewMain.commands.Push(*cmd);
}

namespace hiddenWebView {
  int platform_Create(lua_State* L, hiddenWebView::HiddenWebViewInfo* info) {
    webViewMain.webViewInfo = *info;
    
    #if defined(DM_PLATFORM_IOS)
    UIScreen* screen = [UIScreen mainScreen];

    WKUserContentController* userContentController = [[WKUserContentController alloc] init];
    WKWebViewConfiguration* configuration = [[WKWebViewConfiguration alloc] init];
    configuration.userContentController = userContentController;
    configuration.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;
    [configuration.preferences setValue:@YES forKey:@"allowFileAccessFromFileURLs"];
    [configuration setValue:@YES forKey:@"allowUniversalAccessFromFileURLs"];
    
    WKWebView *view = [[WKWebView alloc] initWithFrame:screen.bounds configuration:configuration];
    #elif defined(DM_PLATFORM_OSX)
    CGRect gameFrame = [dmGraphics::GetNativeOSXNSView() frame];

    WKUserContentController* userContentController = [[WKUserContentController alloc] init];
    WKWebViewConfiguration* configuration = [[WKWebViewConfiguration alloc] init];
    configuration.userContentController = userContentController;
    configuration.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;
    [configuration.preferences setValue:@YES forKey:@"allowFileAccessFromFileURLs"];
    [configuration setValue:@YES forKey:@"allowUniversalAccessFromFileURLs"];
    
    WKWebView *view = [[WKWebView alloc] initWithFrame:gameFrame configuration:configuration];
    #endif

    WebViewDelegate* navigationDelegate = [WebViewDelegate alloc];
    navigationDelegate->navigationHandler = NULL;
    navigationDelegate->requestId = 0;
    view.navigationDelegate = navigationDelegate;

    webViewMain.view = view;
    webViewMain.navigationDelegate = navigationDelegate;
    
    #if defined(DM_PLATFORM_IOS)
    UIView * topView = [[[[UIApplication sharedApplication] keyWindow] rootViewController] view];
    #elif defined(DM_PLATFORM_OSX)
    NSView * topView = [[[NSApplication sharedApplication] keyWindow] contentView];
    #endif

    #if defined(DM_PLATFORM_IOS)
    for (UIView *i in topView.subviews) {
      i.opaque = NO;
    }
    
    [topView insertSubview:view atIndex:0];
    #elif defined(DM_PLATFORM_OSX)    
    [topView addSubview:view];
    #endif
    
    view.hidden = TRUE;
    isViewInUse = TRUE;

    return 1;
  }

  static void DestroyWebView() {
    isViewInUse = FALSE;
    
    cleanup(&webViewMain.webViewInfo);
    WKWebView* view = webViewMain.view;
    
    #if defined(DM_PLATFORM_OSX)
    NSWindow *window = dmGraphics::GetNativeOSXNSWindow();
    if ([window firstResponder] == view) {
      [window makeFirstResponder:dmGraphics::GetNativeOSXNSView()];
    }
    #endif
    
    [view removeFromSuperview];
    [view release];
    
    webViewMain.view = NULL;
  }

  int platform_Destroy(lua_State* luaState) {
    DestroyWebView();
    return 0;
  }

  int platform_LoadGame(lua_State* luaState, const char* gamePath) {
    if (@available(iOS 9.0, *)) {
      NSSet* cacheDataTypes = [WKWebsiteDataStore allWebsiteDataTypes];
      WKWebsiteDataStore* dataStore = [WKWebsiteDataStore defaultDataStore];
      NSDate* dateFrom = [NSDate dateWithTimeIntervalSince1970:0];

      [dataStore removeDataOfTypes:cacheDataTypes
      modifiedSince:dateFrom
      completionHandler:^{}];
    }
    
    webViewMain.view.hidden = FALSE;
    isViewInUse = TRUE;
   
    NSString *path = [NSString stringWithUTF8String: gamePath];
    NSString *basePath = @"http://localhost:8808/";
    NSString *finalPath = [basePath stringByAppendingString:path];

    NSURL* ns_url = [NSURL URLWithString: finalPath];
    NSURLRequest* request = [NSURLRequest requestWithURL: ns_url];
    [webViewMain.view loadRequest:request];
    
    return ++webViewMain.navigationDelegate->requestId;
  }

  int platform_LoadWebPage(lua_State* luaState, const char* gamePath) {
    if (@available(iOS 9.0, *)) {
      NSSet* cacheDataTypes = [WKWebsiteDataStore allWebsiteDataTypes];
      WKWebsiteDataStore* dataStore = [WKWebsiteDataStore defaultDataStore];
      NSDate* dateFrom = [NSDate dateWithTimeIntervalSince1970:0];

      [dataStore removeDataOfTypes:cacheDataTypes
      modifiedSince:dateFrom
      completionHandler:^{}];
    }
    
    webViewMain.view.hidden = FALSE;
    isViewInUse = TRUE;

    NSString *path = [NSString stringWithUTF8String: gamePath];

    NSURL* ns_url = [NSURL URLWithString: path];
    NSURLRequest* request = [NSURLRequest requestWithURL: ns_url];
    [webViewMain.view loadRequest:request];

    return ++webViewMain.navigationDelegate->requestId;
  }

  int platform_SetAcceptTouchEvents(lua_State* luaState, int accept) {
    isAcceptingTouchEvents = accept == 1 ? TRUE : FALSE;

    return 1;
  }

  int platform_MatchScreenSize(lua_State* luaState) {
    #if defined(DM_PLATFORM_IOS)
    UIScreen* screen = [UIScreen mainScreen];
    
    CGRect gameFrame = screen.bounds;
    #elif defined(DM_PLATFORM_OSX)
    CGRect gameFrame = [dmGraphics::GetNativeOSXNSView() frame];
    #endif
    
    webViewMain.view.frame = gameFrame;
    
    return 0;
  }

  int platform_ExecuteScript(lua_State* luaState,  const char* script) {
    int scriptId = ++webViewMain.navigationDelegate->requestId;
    
    [webViewMain.view evaluateJavaScript:[NSString stringWithUTF8String: script] completionHandler:^(NSObject *resultObject, NSError *error) {
      NSString *result = [NSString stringWithFormat:@"%@", resultObject];

      WebViewCommand cmd;
      cmd.type = (result != nil) ? CMD_SCRIPT_OK : CMD_SCRIPT_ERROR;
      cmd.requestId = scriptId;
      cmd.url = 0;
      cmd.payload = (void*) ((result != nil) ? CopyString(result) : "Error string unavailable on iOS");
      
      QueueCommand(&cmd);
    }];

    return scriptId;
  }

  int platform_AddJavascriptChannel(lua_State* luaState,  const char* channelName) {
    NSString *ns_ChannelName = [[NSString alloc] initWithUTF8String: channelName];
    
    JavaScriptChannel* channel = [JavaScriptChannel alloc];
    channel->channelName = channelName;
    
    [webViewMain.view.configuration.userContentController addScriptMessageHandler:channel name:ns_ChannelName];
    
    NSString* wrapperSource = [NSString stringWithFormat:@"window.%@ = webkit.messageHandlers.%@;", 
      ns_ChannelName, ns_ChannelName];
    
    WKUserScript* wrapperScript = [[WKUserScript alloc] initWithSource:wrapperSource 
      injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:NO];
    [webViewMain.view.configuration.userContentController addUserScript:wrapperScript];

    return 0;
  }

  int platform_ChangeVisibility(lua_State* luaState, int visible) {
    webViewMain.view.hidden = (BOOL)!visible;
    isViewInUse = visible;
    
    return 0;
  }

  int platform_SetDebugEnabled(lua_State* luaState, int flag) {
    // ignore for IOS/OSX
    return 0;
  }

  int platform_IsInUse(lua_State* luaState) {
    return webViewMain.view.isHidden ? 0 : 1;
  }

  int platform_SetPositionAndSize(lua_State* luaState, double x, double y, double width, double height) {
    #if defined(DM_PLATFORM_IOS)
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    #elif defined(DM_PLATFORM_OSX)
    CGRect screenRect = [dmGraphics::GetNativeOSXNSView() frame];
    #endif

    CGFloat frameWidth = screenRect.size.width;
    CGFloat frameHeight = screenRect.size.height;

    CGRect frame = CGRectMake(
      screenRect.origin.x + frameWidth * x,
      #if defined(DM_PLATFORM_OSX)
      screenRect.origin.y + screenRect.size.height - frameHeight * height - frameHeight * y,
      #else
      screenRect.origin.y + frameHeight * y,
      #endif
      frameWidth * width,
      frameHeight * height
    );

    webViewMain.view.frame = frame;

    return 0;
  }

  int platform_SetTouchInterceptorArea(lua_State* luaState, double x, double y, double width, double height) {
    #if defined(DM_PLATFORM_IOS)
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    #elif defined(DM_PLATFORM_OSX)
    CGRect screenRect = [dmGraphics::GetNativeOSXNSView() frame];
    #endif

    CGFloat frameWidth = screenRect.size.width;
    CGFloat frameHeight = screenRect.size.height;

    webViewMain.touchInterceptorRect = CGRectMake(
      screenRect.origin.x + frameWidth * x,
      #if defined(DM_PLATFORM_OSX)
      screenRect.origin.y + screenRect.size.height - frameHeight * height - frameHeight * y,
      #else
      screenRect.origin.y + frameHeight * y,
      #endif
      frameWidth * width,
      frameHeight * height
    );

    return 0;
  }

  dmExtension::Result Platform_AppInitialize(dmExtension::AppParams* params) {
    webViewMain.Clear();
    webViewMain.mutex = dmMutex::New();
    webViewMain.commands.SetCapacity(8);

    return dmExtension::RESULT_OK;
  }

  dmExtension::Result Platform_AppFinalize(dmExtension::AppParams* params) {
    dmMutex::Delete(webViewMain.mutex);
    return dmExtension::RESULT_OK;
  }

  dmExtension::Result Platform_Initialize(dmExtension::Params* params) {
    return dmExtension::RESULT_OK;
  }

  dmExtension::Result Platform_Finalize(dmExtension::Params* params) {
    DestroyWebView();

    dmMutex::ScopedLock lk(webViewMain.mutex);
    
    for (uint32_t i=0; i != webViewMain.commands.Size(); ++i) {
      const hiddenWebView::WebViewCommand& cmd = webViewMain.commands[i];
      
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

    dmMutex::ScopedLock lk(webViewMain.mutex);
    
    for (uint32_t i = 0; i != webViewMain.commands.Size(); ++i) {
      const WebViewCommand& cmd = webViewMain.commands[i];

      hiddenWebView::CallbackInfo cbinfo;
      
      switch (cmd.type) {
        case CMD_SCRIPT_OK:
        cbinfo.info = &webViewMain.webViewInfo;
        cbinfo.requestId = cmd.requestId;
        cbinfo.gamePath = 0;
        cbinfo.resultCode = hiddenWebView::CALLBACK_RESULT_JAVASCRIPT_OK;
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

#endif // DM_PLATFORM_IOS || DM_PLATFORM_OSX