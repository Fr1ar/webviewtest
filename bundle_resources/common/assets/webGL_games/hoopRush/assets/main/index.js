System.register("chunks:///_virtual/BackgroundController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(e){"use strict";var t,r,o,n,i,l,a,s,u,c,h,p;return{setters:[function(e){t=e.applyDecoratedDescriptor,r=e.inheritsLoose,o=e.initializerDefineProperty,n=e.assertThisInitialized},function(e){i=e.cclegacy,l=e._decorator,a=e.Texture2D,s=e.Material,u=e.Color,c=e.MeshRenderer,h=e.Component},function(e){p=e.Constants}],execute:function(){var f,d,g,b,C,m,y,v,M;i._RF.push({},"ad494POf3hFr6R/5bz3tH2h","BackgroundController",void 0);var x=l.ccclass,T=l.property;e("BackgroundController",(f=x("BackgroundController"),d=T({type:a}),g=T(s),b=T({type:u}),f((y=t((m=function(e){function t(){for(var t,r=arguments.length,i=new Array(r),l=0;l<r;l++)i[l]=arguments[l];return t=e.call.apply(e,[this].concat(i))||this,o(t,"bgTextures",y,n(t)),o(t,"floorMaterial",v,n(t)),o(t,"floorColors",M,n(t)),t._mesh=void 0,t}r(t,e);var i=t.prototype;return i.onLoad=function(){var e;this._mesh=null!=(e=this.node.getComponentInChildren(c))?e:this.node.getComponent(c),this._mesh.enabled=!1},i.SpawnBG=function(){var e=p.RandomFixedSeed.Rand(this.bgTextures.length);this.setTexture(this._mesh,this.bgTextures[e]),this._mesh.enabled=!0,this.setMainColorMaterial(this.floorMaterial,this.floorColors[e]),2==e&&this.node.setPosition(0,-60,-410)},i.setTexture=function(e,t){var r;null==e||null==(r=e.getMaterial(0))||r.setProperty("mainTexture",t)},i.setMainColorMaterial=function(e,t){e.setProperty("mainColor",t)},t}(h)).prototype,"bgTextures",[d],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),v=t(m.prototype,"floorMaterial",[g],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),M=t(m.prototype,"floorColors",[b],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),C=m))||C));i._RF.pop()}}}));

System.register("chunks:///_virtual/BotController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(t){"use strict";var o,n,e,i,r;return{setters:[function(t){o=t.inheritsLoose},function(t){n=t.cclegacy,e=t._decorator,i=t.Component},function(t){r=t.Constants}],execute:function(){var s;n._RF.push({},"0e3402QL4JA6KOtUEb0CwYZ","BotController",void 0);var l=e.ccclass;t("BotController",l("BotController")(s=function(t){function n(){for(var o,n=arguments.length,e=new Array(n),i=0;i<n;i++)e[i]=arguments[i];return(o=t.call.apply(t,[this].concat(e))||this)._canStep=!0,o._botSkill=0,o}o(n,t);var e=n.prototype;return e.botStart=function(){var t=this;this._canStep=!0,this._botSkill=r.gameController.BotSkill,this.scheduleOnce((function(){t.botStep()}),r.gameController.IsTraining?r.BOT_TRAINING_START_DELAY:r.BOT_START_DELAY)},e.botStep=function(){var t=this;if(this._canStep){var o=r.target.node.getWorldPosition().subtract(r.opponentTorus.node.position),n=0;if(this._botSkill>=r.RandomNotFixedSeed.range(-.1,1.5)||this._botSkill>=.9){var e;if(o.y>=-.5&&o.x>=0)null==(e=r.opponentTorus)||e.Launch(r.MOVE_DIR.Right);else if(o.y>=-.5&&o.x<=0){var i;null==(i=r.opponentTorus)||i.Launch(r.MOVE_DIR.Left)}n=o.y>1?r.RandomNotFixedSeed.range(r.BOT_STEP_DELAY_MIN_1,r.BOT_STEP_DELAY_MIN_2):r.RandomNotFixedSeed.range(r.BOT_STEP_DELAY_MAX_1,r.BOT_STEP_DELAY_MAX_2)}else{var s,l;if(r.RandomNotFixedSeed.range(0,1)>.5)null==(s=r.opponentTorus)||s.Launch(r.MOVE_DIR.Right);else null==(l=r.opponentTorus)||l.Launch(r.MOVE_DIR.Left);n=r.RandomNotFixedSeed.range(.3,1)}this.scheduleOnce((function(){t.botStep()}),n)}},e.botStop=function(){this._canStep=!1},e.onDestroy=function(){this.botStop()},e.onDisable=function(){this.botStop()},n}(i))||s);n._RF.pop()}}}));

System.register("chunks:///_virtual/CameraController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(n){"use strict";var t,o,i,e,r,s,a,l,u,c;return{setters:[function(n){t=n.applyDecoratedDescriptor,o=n.inheritsLoose,i=n.initializerDefineProperty,e=n.assertThisInitialized},function(n){r=n.cclegacy,s=n._decorator,a=n.Animation,l=n.Vec3,u=n.Component},function(n){c=n.Constants}],execute:function(){var p,h,m,y,C;r._RF.push({},"16a02a11HVKD7C4ydSSnPjW","CameraController",void 0);var f=s.ccclass,d=s.property;n("CameraController",(p=f("CameraController"),h=d({type:a}),p((C=t((y=function(n){function t(){for(var t,o=arguments.length,r=new Array(o),s=0;s<o;s++)r[s]=arguments[s];return t=n.call.apply(n,[this].concat(r))||this,i(t,"Animation",C,e(t)),t._prevMainTorusPos=void 0,t}o(t,n);var r=t.prototype;return r.update=function(n){this.setCameraPosition_Y()},r.PlayShakeAnimation=function(){this.Animation.play("shake")},r.setCameraPosition_Y=function(){if(null!=this._prevMainTorusPos){var n=c.mainTorus.node.getPosition().y-this._prevMainTorusPos.y,t=new l(this.node.position.x,this.node.position.y+n/c.CAMERA_MOVE_COEFF,this.node.position.z);this.node.setPosition(t)}this._prevMainTorusPos=c.mainTorus.node.getPosition()},t}(u)).prototype,"Animation",[h],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),m=y))||m));r._RF.pop()}}}));

System.register("chunks:///_virtual/ComplimentPopup.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(t){"use strict";var i,e,n,o,r,a,s,p,u,l,c,m,h;return{setters:[function(t){i=t.applyDecoratedDescriptor,e=t.inheritsLoose,n=t.initializerDefineProperty,o=t.assertThisInitialized},function(t){r=t.cclegacy,a=t._decorator,s=t.Sprite,p=t.Animation,u=t.SpriteFrame,l=t.Quat,c=t.Vec3,m=t.Component},function(t){h=t.Constants}],execute:function(){var f,d,y,S,P,b,v,w,C;r._RF.push({},"1335fYVzPhLfpJCa3T/B9gV","ComplimentPopup",void 0);var g=a.ccclass,F=a.property;t("ComplimentPopup",(f=g("ComplimentPopup"),d=F({type:s}),y=F({type:p}),S=F({type:u}),f((v=i((b=function(t){function i(){for(var i,e=arguments.length,r=new Array(e),a=0;a<e;a++)r[a]=arguments[a];return i=t.call.apply(t,[this].concat(r))||this,n(i,"mainSprite",v,o(i)),n(i,"animation",w,o(i)),n(i,"mainSpriteFrames",C,o(i)),i._countShow=0,i}e(i,t);var r=i.prototype;return r.start=function(){h.ComplimentPopup=this,this.Disable()},r.Show=function(){this._countShow++,this.node.active=!0,this.setPositionAndRotation();var t=this._countShow%this.mainSpriteFrames.length;this.mainSprite.spriteFrame=this.mainSpriteFrames[t],this.animation.play(h.COMPLIMENT_ENABLE_ANIMATION)},r.setPositionAndRotation=function(){var t=new l;0==h.RandomNotFixedSeed.Rand(2)?(this.node.setPosition(new c(-5,100,0)),l.fromEuler(t,0,0,5)):(this.node.setPosition(new c(5,100,0)),l.fromEuler(t,0,0,-5)),this.node.setRotation(t)},r.Disable=function(){this.node.active=!1},i}(m)).prototype,"mainSprite",[d],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),w=i(b.prototype,"animation",[y],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),C=i(b.prototype,"mainSpriteFrames",[S],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),P=b))||P));r._RF.pop()}}}));

System.register("chunks:///_virtual/Constants.ts",["cc","./CustomRandom.ts"],(function(_){"use strict";var E,O,T;return{setters:[function(_){E=_.cclegacy,O=_.Vec3},function(_){T=_.default}],execute:function(){var n,t,S,A,P;E._RF.push({},"4d469WYnV1JS4w7x6VGpUJQ","Constants",void 0),function(_){_[_.SOLO=0]="SOLO",_[_.WITH_BOT=1]="WITH_BOT",_[_.PVP=2]="PVP"}(n||(n={})),function(_){_[_.MAIN=0]="MAIN",_[_.OPPONENT=1]="OPPONENT"}(t||(t={})),function(_){_[_.Left=0]="Left",_[_.Right=1]="Right"}(S||(S={})),function(_){_.LIGHT="lightImpact",_.MEDIUM="mediumImpact",_.HEAVY="heavyImpact"}(A||(A={})),function(_){_.START_TOUCH="start_touch"}(P||(P={}));var o=_("Constants",(function(){}));o.gameController=void 0,o.cameraController=void 0,o.mainTorus=void 0,o.opponentTorus=void 0,o.fakeTorus=void 0,o.target=void 0,o.MainCanvas=void 0,o.MainCamera=void 0,o.ComplimentPopup=void 0,o.RandomFixedSeed=new T(111),o.RandomNotFixedSeed=new T,o.GAME_MODE=n,o.TORUS_TYPE=t,o.MOVE_DIR=S,o.HAPTIC_TYPE=A,o.GAME_EVENT=P,o.LEFT_BOARDER_NAME="left",o.RIGHT_BOARDER_NAME="right",o.UX_TORUS_HIT='"play_hoops_random_hit"',o.UX_TORUS_LAUNCH='"play_hoops_launch"',o.ADD_SCORE=10,o.TOUCH_DELAY=.15,o.MAIN_TORUS_SPAWN_POS=new O(-2.5,1.8,0),o.OPPONENT_TORUS_SPAWN_POS=new O(2.5,1.8,0),o.TORUS_SPAWN_CENTER_POS=new O(0,1.8,0),o.TORUS_FORCE=700,o.TORUS_PHYSICS_GROUP_MAIN=2,o.TORUS_PHYSICS_GROUP_OPPONENT=4,o.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER=.5,o.LEADER_POS_Y=1.8,o.PHYSICS_FIXED_TIME_STEP=1/60,o.PHYSICS_MAX_SUB_STEPS=5,o.MAX_ACCUMULATOR=.2,o.SPHERE_SPAWN_POS_X=[-2.5,0,2.5],o.SPHERE_SPAWN_POS_Y=[5,7,9],o.SPHERE_ENABLE_ANIMATION="sphere_enable",o.SPHERE_TRIGGER_DELAY=.2,o.COMPLIMENT_ENABLE_ANIMATION="compliment_enable",o.COMPLIMENT_POPUP_DURATION=1.5,o.BOT_START_DELAY=4,o.BOT_TRAINING_START_DELAY=12,o.BOT_STEP_DELAY_MIN_1=.2,o.BOT_STEP_DELAY_MIN_2=.3,o.BOT_STEP_DELAY_MAX_1=.3,o.BOT_STEP_DELAY_MAX_2=.5,o.CAMERA_MOVE_COEFF=6,E._RF.pop()}}}));

System.register("chunks:///_virtual/CustomRandom.ts",["./rollupPluginModLoBabelHelpers.js","cc"],(function(e){"use strict";var t,n,s;return{setters:[function(e){t=e.createClass},function(e){n=e.cclegacy,s=e._decorator}],execute:function(){var r;n._RF.push({},"9c6a8rwrYVGZpOCTLjZ/Rz6","CustomRandom",void 0);var i=s.ccclass;e("default",i("CustomRandom")(r=function(){function e(e){this.seed=void 0,this.seed=e,this.seed||0==this.seed||(this.seed=(new Date).getTime())}var n=e.prototype;return n.range=function(e,t){return this.seed||0==this.seed||(this.seed=(new Date).getTime()),t=t||1,e=e||0,this.seed=(9301*this.seed+49297)%233280,e+this.seed/233280*(t-e)},n.Rand=function(e){return Math.floor(this.range(0,e))},n.RandInArray=function(e,t,n){return(e=(e=e.filter((function(e){return e!==t}))).filter((function(e){return e!==n})))[this.Rand(e.length)]},t(e,[{key:"value",get:function(){return this.range(0,1)}}]),e}())||r);n._RF.pop()}}}));

System.register("chunks:///_virtual/GameController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./BotController.ts","./Constants.ts","./TouchController.ts","./TorusSpawner.ts","./BackgroundController.ts","./TrainingController.ts","./CameraController.ts"],(function(e){"use strict";var n,t,o,r,i,a,l,s,c,u,d,h,p,b,m,C,f,S,w;return{setters:[function(e){n=e.applyDecoratedDescriptor,t=e.inheritsLoose,o=e.initializerDefineProperty,r=e.assertThisInitialized},function(e){i=e.cclegacy,a=e._decorator,l=e.Camera,s=e.Canvas,c=e.Prefab,u=e.PhysicsSystem,d=e.math,h=e.Component},function(e){p=e.BotController},function(e){b=e.Constants},function(e){m=e.TouchController},function(e){C=e.TorusSpawner},function(e){f=e.BackgroundController},function(e){S=e.TrainingController},function(e){w=e.CameraController}],execute:function(){var M,O,g,y,G,T,v,B,z,_,E,I,P,k,D,L,A,H,W,x,R;i._RF.push({},"72945lhD2RGPaT16DxHpjWT","GameController",void 0);var F=a.ccclass,X=a.property;e("GameController",(M=F("GameController"),O=X({type:p}),g=X({type:m}),y=X({type:w}),G=X({type:S}),T=X({type:C}),v=X({type:f}),B=X({type:l}),z=X({type:s}),_=X({type:c}),M((P=n((I=function(e){function n(){for(var n,t=arguments.length,i=new Array(t),a=0;a<t;a++)i[a]=arguments[a];return n=e.call.apply(e,[this].concat(i))||this,o(n,"botController",P,r(n)),o(n,"touchController",k,r(n)),o(n,"cameraController",D,r(n)),o(n,"trainingController",L,r(n)),o(n,"torusSpawner",A,r(n)),o(n,"backgroundController",H,r(n)),o(n,"MainCamera",W,r(n)),o(n,"MainCanvas",x,r(n)),o(n,"ScoreAnimPrefab",R,r(n)),n.GameMode=b.GAME_MODE.WITH_BOT,n.MainScore=0,n.BotScore=0,n.IsMaster=!1,n.IsTraining=!1,n.BotSkill=0,n}t(n,e);var i=n.prototype;return i.__preload=function(){b.gameController=this,this.touchController.enabled=!1,b.MainCamera=this.MainCamera,b.MainCanvas=this.MainCanvas,b.cameraController=this.cameraController},i.onLoad=function(){u.instance.fixedTimeStep=b.PHYSICS_FIXED_TIME_STEP,u.instance.maxSubSteps=b.PHYSICS_MAX_SUB_STEPS,window.GameController=this},i.start=function(){void 0!==window.blitzOnSceneLoaded&&window.blitzOnSceneLoaded(),this.startGame("Bot",d.randomRange(0,100),!0,!1,0)},i.startGame=function(e,n,t,o,r){b.RandomFixedSeed.seed=n,this.setGameMode(e),this.MainScore=0,this.BotScore=0,this.IsMaster=t,this.IsTraining=o,this.BotSkill=r,this.IsTraining?b.RandomFixedSeed.seed=100:this.trainingController.node.destroy(),this.torusSpawner.Spawn(),this.backgroundController.SpawnBG(),this.GameMode==b.GAME_MODE.WITH_BOT?this.botController.botStart():this.botController.botStop(),this.touchController.enabled=!0},i.stopTraining=function(){this.IsTraining&&null!==this.trainingController&&(this.trainingController.node.destroy(),this.trainingController=null)},i.gameStop=function(){this.touchController.enabled=!1,this.GameMode==b.GAME_MODE.WITH_BOT&&(this.botController.botStop(),this.botGameOver())},i.botGameOver=function(){void 0!==window.blitzOnBotGameOverOneWorld&&window.blitzOnBotGameOverOneWorld()},i.gameOver=function(){void 0!==window.blitzOnGameOver&&window.blitzOnGameOver()},i.setGameMode=function(e){switch(e){case"Human":this.GameMode=b.GAME_MODE.PVP;break;case"Bot":this.GameMode=b.GAME_MODE.WITH_BOT;break;case"SinglePlayer":this.GameMode=b.GAME_MODE.SOLO;break;default:console.log("GameMode not found")}},i.PlayUX=function(e){void 0!==window.blitzOnUX&&window.blitzOnUX(e)},i.addScore=function(e){this.MainScore+=e,this.checkLeader(e),void 0!==window.blitzOnScore&&window.blitzOnScore(this.MainScore)},i.addBotScore=function(e){this.BotScore+=e,this.checkLeader(e),void 0!==window.blitzOnNewBotScoreOneWorld&&window.blitzOnNewBotScoreOneWorld(this.BotScore)},i.checkLeader=function(e){if(!(e<=0)&&this.GameMode!==b.GAME_MODE.SOLO){var n=this.BotScore>this.MainScore;this.scheduleOnce((function(){b.opponentTorus.SetLeader(n),b.mainTorus.SetLeader(!n)}),.4)}},i.launchOpponentTorus=function(e){var n;null==(n=b.opponentTorus)||n.Launch(e)},n}(h)).prototype,"botController",[O],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),k=n(I.prototype,"touchController",[g],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),D=n(I.prototype,"cameraController",[y],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),L=n(I.prototype,"trainingController",[G],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),A=n(I.prototype,"torusSpawner",[T],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),H=n(I.prototype,"backgroundController",[v],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),W=n(I.prototype,"MainCamera",[B],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),x=n(I.prototype,"MainCanvas",[z],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),R=n(I.prototype,"ScoreAnimPrefab",[_],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),E=I))||E));i._RF.pop()}}}));

System.register("chunks:///_virtual/main",["./BackgroundController.ts","./BotController.ts","./CameraController.ts","./ComplimentPopup.ts","./Constants.ts","./CustomRandom.ts","./GameController.ts","./PoolManager.ts","./RotateComponent.ts","./ScoreController.ts","./Startup.ts","./Target.ts","./TargetController.ts","./Torus.ts","./TorusSpawner.ts","./TouchController.ts","./TrainingController.ts"],(function(){"use strict";return{setters:[null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null],execute:function(){}}}));

System.register("chunks:///_virtual/PoolManager.ts",["./rollupPluginModLoBabelHelpers.js","cc"],(function(t){"use strict";var o,e,n,i,a;return{setters:[function(t){o=t.createClass},function(t){e=t.cclegacy,n=t._decorator,i=t.instantiate,a=t.NodePool}],execute:function(){var r,c;e._RF.push({},"5e33aGSgzVJm7Wm1JoiXmXg","PoolManager",void 0);var s=n.ccclass;t("PoolManager",s("PoolManager")(((c=function(){function t(){this.dictPool={},this.dictPrefab={}}var e=t.prototype;return e.getNode=function(t,o){var e=t.data.name;this.dictPrefab[e]=t;var n=null;if(this.dictPool.hasOwnProperty(e)){var r=this.dictPool[e];n=r.size()>0?r.get():i(t)}else{var c=new a;this.dictPool[e]=c,n=i(t)}return n.parent=o,n},e.putNode=function(t){var o=t.name,e=null;this.dictPool.hasOwnProperty(o)?e=this.dictPool[o]:(e=new a,this.dictPool[o]=e),e.put(t)},e.clearPool=function(t){this.dictPool.hasOwnProperty(t)&&this.dictPool[t].clear()},o(t,null,[{key:"instance",get:function(){return this._instance||(this._instance=new t),this._instance}}]),t}())._instance=void 0,r=c))||r);e._RF.pop()}}}));

System.register("chunks:///_virtual/RotateComponent.ts",["./rollupPluginModLoBabelHelpers.js","cc"],(function(e){"use strict";var t,n,i,r,o,c,a,s;return{setters:[function(e){t=e.applyDecoratedDescriptor,n=e.inheritsLoose,i=e.initializerDefineProperty,r=e.assertThisInitialized},function(e){o=e.cclegacy,c=e._decorator,a=e.Vec3,s=e.Component}],execute:function(){var u,l,p,f,h,y,d;o._RF.push({},"457ebHanMRKlYHgtsCO7F6U","RotateComponent",void 0);var b=c.ccclass,g=c.property;e("RotateComponent",(u=b("RotateComponent"),l=g(Number),p=g(a),u((y=t((h=function(e){function t(){for(var t,n=arguments.length,o=new Array(n),c=0;c<n;c++)o[c]=arguments[c];return t=e.call.apply(e,[this].concat(o))||this,i(t,"Speed",y,r(t)),i(t,"Direction",d,r(t)),t}return n(t,e),t.prototype.update=function(e){var t=this.Speed*e,n=this.node.eulerAngles;this.node.eulerAngles=new a(n.x+this.Direction.x*t,n.y+this.Direction.y*t,n.z+this.Direction.z*t)},t}(s)).prototype,"Speed",[l],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return 0}}),d=t(h.prototype,"Direction",[p],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return new a}}),f=h))||f));o._RF.pop()}}}));

System.register("chunks:///_virtual/ScoreController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts","./PoolManager.ts"],(function(o){"use strict";var n,e,t,r,a,c,s,i;return{setters:[function(o){n=o.inheritsLoose},function(o){e=o.cclegacy,t=o._decorator,r=o.Vec3,a=o.Animation,c=o.Component},function(o){s=o.Constants},function(o){i=o.PoolManager}],execute:function(){var l;e._RF.push({},"44e57PoZmBDba/ht2k+EZdR","ScoreController",void 0);var u=t.ccclass;o("ScoreController",u("ScoreController")(l=function(o){function e(){return o.apply(this,arguments)||this}return n(e,o),e.prototype.ShowScore=function(o,n){var e=i.instance.getNode(s.gameController.ScoreAnimPrefab,s.MainCanvas.node),t=new r;s.MainCamera.convertToUINode(n,s.MainCanvas.node,t),e.setPosition(t);var c=e.getComponent(a);c.once(a.EventType.FINISHED,(function(){e.destroy()})),c.play()},e}(c))||l);e._RF.pop()}}}));

System.register("chunks:///_virtual/Startup.ts",["./rollupPluginModLoBabelHelpers.js","cc"],(function(t){"use strict";var e,n,r,o,c;return{setters:[function(t){e=t.inheritsLoose},function(t){n=t.cclegacy,r=t._decorator,o=t.director,c=t.Component}],execute:function(){var u;n._RF.push({},"97b5d1qDgBEnJkUNzLqOKTp","Startup",void 0);var i=r.ccclass;t("Startup",i("Startup")(u=function(t){function n(){return t.apply(this,arguments)||this}return e(n,t),n.prototype.start=function(){o.preloadScene("game",(function(){window.blitzOnSceneLoaded()}))},n}(c))||u);n._RF.pop()}}}));

System.register("chunks:///_virtual/Target.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts","./PoolManager.ts"],(function(t){"use strict";var e,r,o,n,i,a,l,s,c,u,d,p,f,C;return{setters:[function(t){e=t.applyDecoratedDescriptor,r=t.inheritsLoose,o=t.initializerDefineProperty,n=t.assertThisInitialized},function(t){i=t.cclegacy,a=t._decorator,l=t.Color,s=t.Material,c=t.Prefab,u=t.ParticleSystem,d=t.Quat,p=t.Component},function(t){f=t.Constants},function(t){C=t.PoolManager}],execute:function(){var h,m,y,g,b,P,v,M,_,x,w;i._RF.push({},"d6ab3TuehpLY5WQyEt0ADdB","Target",void 0);var A=a.ccclass,I=a.property;t("Target",(h=A("Target"),m=I({type:l}),y=I({type:l}),g=I(s),b=I({type:c}),h((M=e((v=function(t){function e(){for(var e,r=arguments.length,i=new Array(r),a=0;a<r;a++)i[a]=arguments[a];return e=t.call.apply(t,[this].concat(i))||this,o(e,"mainColors",M,n(e)),o(e,"additionalColors",_,n(e)),o(e,"material",x,n(e)),o(e,"ParticlePrefab",w,n(e)),e._currentColorIndex=null,e}r(e,t);var i=e.prototype;return i.onLoad=function(){this.setColor()},i.setColor=function(){this._currentColorIndex=f.RandomFixedSeed.Rand(this.mainColors.length),this.setColorMaterial(this.material,this.mainColors[this._currentColorIndex])},i.SetActive=function(t){this.node.active=t,this.setColor()},i.GetMainColor=function(){return this.mainColors[this._currentColorIndex]},i.GetAdditionalColor=function(){return this.additionalColors[this._currentColorIndex]},i.PlayParticles=function(t,e){var r=C.instance.getNode(this.ParticlePrefab,this.node.parent.parent);r.setWorldPosition(t);var o=r.getComponentsInChildren(u),n=this.GetMainColor(),i=this.GetAdditionalColor();o.forEach((function(t){t.startColor.colorMax=n,t.startColor.colorMin=i,t.play()}));var a=new d,l=new d;d.fromAngleZ(a,e),d.fromAngleZ(l,e+180),o[0].node.setWorldRotation(a),o[1].node.setWorldRotation(l)},i.setColorMaterial=function(t,e){null==t||t.setProperty("mainColor",e),null==t||t.setProperty("emissive",e)},e}(p)).prototype,"mainColors",[m],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),_=e(v.prototype,"additionalColors",[y],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),x=e(v.prototype,"material",[g],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),w=e(v.prototype,"ParticlePrefab",[b],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),P=v))||P));i._RF.pop()}}}));

System.register("chunks:///_virtual/TargetController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts","./ScoreController.ts","./Target.ts","./Torus.ts"],(function(e){"use strict";var t,r,n,o,i,a,l,s,c,g,u,d,h,T;return{setters:[function(e){t=e.applyDecoratedDescriptor,r=e.inheritsLoose,n=e.initializerDefineProperty,o=e.assertThisInitialized},function(e){i=e.cclegacy,a=e._decorator,l=e.Collider,s=e.Animation,c=e.Vec3,g=e.Component},function(e){u=e.Constants},function(e){d=e.ScoreController},function(e){h=e.Target},function(e){T=e.Torus}],execute:function(){var p,f,C,S,_,y,P,m,E;i._RF.push({},"cf0dcC5VfdAsYnHNbWjOBC6","TargetController",void 0);var R=a.ccclass,A=a.property;e("TargetController",(p=R("TargetController"),f=A({type:h}),C=A({type:l}),S=A({type:s}),p((P=t((y=function(e){function t(){for(var t,r=arguments.length,i=new Array(r),a=0;a<r;a++)i[a]=arguments[a];return t=e.call.apply(e,[this].concat(i))||this,n(t,"targets",P,o(t)),n(t,"TriggerCollider",m,o(t)),n(t,"Animation",E,o(t)),t._scoreController=new d,t._currentTargetIndex=0,t}r(t,e);var i=t.prototype;return i.onLoad=function(){u.target=this},i.start=function(){this.targets.forEach((function(e){e.enabled=!1})),this.enableRandomTarget(),this.TriggerCollider.on("onTriggerEnter",this.onTriggerEnter,this)},i.onDestroy=function(){this.TriggerCollider.off("onTriggerEnter",this.onTriggerEnter,this)},i.onTriggerEnter=function(e){var t=e.otherCollider.getComponent(T);this.onTrigger(t,!1)},i.onTrigger=function(e,t){var r=this;this.TriggerCollider.enabled=!1;var n=e.node.eulerAngles.z;this.targets[this._currentTargetIndex].PlayParticles(this.node.getWorldPosition(),n),this.scheduleOnce((function(){null!==e&&e.Type===u.TORUS_TYPE.MAIN?(u.gameController.addScore(u.ADD_SCORE),u.gameController.PlayUX(u.UX_TORUS_HIT),u.gameController.stopTraining(),r._scoreController.ShowScore(u.ADD_SCORE,new c(r.node.worldPosition.x,r.node.worldPosition.y,0)),u.ComplimentPopup.Show(),u.cameraController.PlayShakeAnimation()):e.Type===u.TORUS_TYPE.OPPONENT&&u.gameController.addBotScore(u.ADD_SCORE),r.setNextPos((function(){r.enableRandomTarget()}))}),u.SPHERE_TRIGGER_DELAY)},i.setNextPos=function(e){var t=u.RandomFixedSeed.RandInArray(u.SPHERE_SPAWN_POS_X,this.node.position.x),r=u.RandomFixedSeed.RandInArray(u.SPHERE_SPAWN_POS_Y,this.node.position.y),n=new c(t,r,0);this.node.setWorldPosition(n),e()},i.enableRandomTarget=function(){var e=this;this.targets[this._currentTargetIndex].SetActive(!1);var t=u.RandomFixedSeed.Rand(this.targets.length);this.targets[t].SetActive(!0),this._currentTargetIndex=t,this.Animation.play(u.SPHERE_ENABLE_ANIMATION),this.scheduleOnce((function(){e.TriggerCollider.enabled=!0}),.1)},t}(g)).prototype,"targets",[f],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),m=t(y.prototype,"TriggerCollider",[C],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),E=t(y.prototype,"Animation",[S],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),_=y))||_));i._RF.pop()}}}));

System.register("chunks:///_virtual/Torus.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts","./PoolManager.ts"],(function(e){"use strict";var t,i,o,n,r,s,a,l,d,h,u,c,_,p,f,T,y;return{setters:[function(e){t=e.applyDecoratedDescriptor,i=e.inheritsLoose,o=e.initializerDefineProperty,n=e.assertThisInitialized},function(e){r=e.cclegacy,s=e._decorator,a=e.Prefab,l=e.Node,d=e.AnimationComponent,h=e.ParticleSystem,u=e.RigidBody,c=e.Collider,_=e.Vec3,p=e.MeshRenderer,f=e.Component},function(e){T=e.Constants},function(e){y=e.PoolManager}],execute:function(){var E,O,C,g,P,N,R,L,m,S,A,b,v;r._RF.push({},"17747py+P5BmIqpsRq/Nhrt","Torus",void 0);var M=s.ccclass,w=s.property;e("Torus",(E=M("Torus"),O=w({type:a}),C=w({type:l}),g=w({type:l}),P=w({type:d}),N=w({type:h}),E((m=t((L=function(e){function t(){for(var t,i=arguments.length,r=new Array(i),s=0;s<i;s++)r[s]=arguments[s];return t=e.call.apply(e,[this].concat(r))||this,o(t,"leaderPrefab",m,n(t)),o(t,"startArrow",S,n(t)),o(t,"torusMeshNode",A,n(t)),o(t,"torusLaunchEffect",b,n(t)),o(t,"launchParticle",v,n(t)),t.Type=T.TORUS_TYPE.MAIN,t._rigidBody=void 0,t._collider=[],t._isFirstLaunch=!1,t._leaderNode=void 0,t._isLeader=void 0,t}i(t,e);var r=t.prototype;return r.onLoad=function(){var e,t;this._rigidBody=null!=(e=this.getComponentInChildren(u))?e:this.getComponent(u),this._collider=null!=(t=this.getComponentsInChildren(c))?t:this.getComponents(c),this._rigidBody.useGravity=!1},r.start=function(){T.gameController.GameMode!==T.GAME_MODE.SOLO&&(this.setPhysicsGroup(),this.initLeaderNode(),this.Type===T.TORUS_TYPE.OPPONENT&&(this.startArrow.active=!1)),T.gameController.IsTraining&&(this.Type===T.TORUS_TYPE.OPPONENT&&this.SetMeshEnabled(!1),this.startArrow.active=!1);for(var e=0;e<this._collider.length;e++)this._collider[e].on("onCollisionEnter",this.onCollisionEnter,this);T.gameController.node.on(T.GAME_EVENT.START_TOUCH,this.onStartTouch,this)},r.onDestroy=function(){for(var e=0;e<this._collider.length;e++)this._collider[e].off("onCollisionEnter",this.onCollisionEnter,this);T.gameController.node.off(T.GAME_EVENT.START_TOUCH,this.onStartTouch,this)},r.update=function(e){this.setLeaderNodePosition()},r.Launch=function(e,t){void 0===t&&(t=1),this._isFirstLaunch||(this._rigidBody.useGravity=!0,this._isFirstLaunch=!0),this.Type===T.TORUS_TYPE.MAIN?(this.torusLaunchEffect.play(),T.gameController.PlayUX(T.UX_TORUS_LAUNCH),this.launchParticle.play(),void 0!==window.blitzOnLaunchOpponentTorus&&1==t&&T.gameController.GameMode==T.GAME_MODE.PVP&&window.blitzOnLaunchOpponentTorus(e)):T.gameController.IsTraining&&T.opponentTorus.SetMeshEnabled(!0),this._rigidBody.setLinearVelocity(new _(0,0,0));var i=(e===T.MOVE_DIR.Left?new _(-.3,1,0):new _(.3,1,0)).multiplyScalar(T.TORUS_FORCE*t);this._rigidBody.applyForce(i)},r.SetLeader=function(e){this._isLeader=e},r.SetMeshEnabled=function(e){var t,i;(null!=(t=this.node.getComponentInChildren(p))?t:this.node.getComponent(p)).enabled=e,(null!=(i=this._leaderNode.getComponentInChildren(p))?i:this._leaderNode.getComponent(p)).enabled=e},r.initLeaderNode=function(){this._leaderNode=y.instance.getNode(this.leaderPrefab,this.node.parent),this._leaderNode.active=!1,this._isLeader=!1},r.setLeaderNodePosition=function(){if(this._leaderNode&&this._isLeader){var e=new _(this.node.worldPosition.x,this.node.worldPosition.y+T.LEADER_POS_Y,this.node.worldPosition.z);this._leaderNode.setPosition(e),this._leaderNode.active=!0}else this._leaderNode.active&&(this._leaderNode.active=!1)},r.onCollisionEnter=function(e){e.otherCollider.node.name==T.LEFT_BOARDER_NAME?this.Launch(T.MOVE_DIR.Right,T.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER):e.otherCollider.node.name==T.RIGHT_BOARDER_NAME&&this.Launch(T.MOVE_DIR.Left,T.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER)},r.onStartTouch=function(){this.Type===T.TORUS_TYPE.MAIN&&(this.startArrow.active=!1)},r.setPhysicsGroup=function(){this.Type===T.TORUS_TYPE.MAIN?this._rigidBody.setGroup(T.TORUS_PHYSICS_GROUP_MAIN):this._rigidBody.setGroup(T.TORUS_PHYSICS_GROUP_OPPONENT)},t}(f)).prototype,"leaderPrefab",[O],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),S=t(L.prototype,"startArrow",[C],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),A=t(L.prototype,"torusMeshNode",[g],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),b=t(L.prototype,"torusLaunchEffect",[P],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),v=t(L.prototype,"launchParticle",[N],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),R=L))||R));r._RF.pop()}}}));

System.register("chunks:///_virtual/TorusSpawner.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts","./Torus.ts"],(function(e){"use strict";var t,r,n,o,a,i,s,u,l,p,T,c,f,h;return{setters:[function(e){t=e.applyDecoratedDescriptor,r=e.inheritsLoose,n=e.initializerDefineProperty,o=e.assertThisInitialized},function(e){a=e.cclegacy,i=e._decorator,s=e.Prefab,u=e.Material,l=e.Texture2D,p=e.instantiate,T=e.MeshRenderer,c=e.Component},function(e){f=e.Constants},function(e){h=e.Torus}],execute:function(){var m,M,P,S,O,_,g,d,y,C,R;a._RF.push({},"b87ab7Z4AxES7AvVg6+WCJC","TorusSpawner",void 0);var b=i.ccclass,N=i.property;e("TorusSpawner",(m=b("TorusSpawner"),M=N({type:s}),P=N({type:u}),S=N({type:u}),O=N({type:l}),m((d=t((g=function(e){function t(){for(var t,r=arguments.length,a=new Array(r),i=0;i<r;i++)a[i]=arguments[i];return t=e.call.apply(e,[this].concat(a))||this,n(t,"torusPref",d,o(t)),n(t,"opponentMaterial",y,o(t)),n(t,"mainMaterial",C,o(t)),n(t,"torusTextures",R,o(t)),t}r(t,e);var a=t.prototype;return a.Spawn=function(){var e,t;f.gameController.IsMaster?(e=f.MAIN_TORUS_SPAWN_POS,t=f.OPPONENT_TORUS_SPAWN_POS):(e=f.OPPONENT_TORUS_SPAWN_POS,t=f.MAIN_TORUS_SPAWN_POS),(f.gameController.GameMode===f.GAME_MODE.SOLO||f.gameController.IsTraining)&&(e=f.TORUS_SPAWN_CENTER_POS),this.createMainTorus(e),f.gameController.GameMode!==f.GAME_MODE.SOLO&&this.createOpponentTorus(t)},a.createFakeTorus=function(e){var t=p(this.torusPref);t.parent=this.node.parent,t.position=e,this.setMaterial(t,this.mainMaterial),this.setRandomTexture(t,this.torusTextures),f.fakeTorus=t.getComponent(h),f.fakeTorus.Type=f.TORUS_TYPE.MAIN},a.createOpponentTorus=function(e){var t=p(this.torusPref);t.parent=this.node.parent,t.position=e,this.setMaterial(t,this.opponentMaterial),this.setRandomTexture(t,this.torusTextures),f.opponentTorus=t.getComponent(h),f.opponentTorus.Type=f.TORUS_TYPE.OPPONENT},a.createMainTorus=function(e){var t=p(this.torusPref);t.parent=this.node.parent,t.position=e,this.setMaterial(t,this.mainMaterial),this.setRandomTexture(t,this.torusTextures),f.mainTorus=t.getComponent(h),f.mainTorus.Type=f.TORUS_TYPE.MAIN},a.setRandomTexture=function(e,t){var r,n,o=null!=(r=e.getComponentInChildren(T))?r:e.getComponent(T);null==o||null==(n=o.getMaterial(0))||n.setProperty("mainTexture",t[f.RandomFixedSeed.Rand(t.length)])},a.setMaterial=function(e,t){var r,n=null!=(r=e.getComponentInChildren(T))?r:e.getComponent(T);null==n||n.setMaterial(t,0)},t}(c)).prototype,"torusPref",[M],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),y=t(g.prototype,"opponentMaterial",[P],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),C=t(g.prototype,"mainMaterial",[S],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),R=t(g.prototype,"torusTextures",[O],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return[]}}),_=g))||_));a._RF.pop()}}}));

System.register("chunks:///_virtual/TouchController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(t){"use strict";var n,o,e,i,c,r,s;return{setters:[function(t){n=t.inheritsLoose},function(t){o=t.cclegacy,e=t._decorator,i=t.view,c=t.Node,r=t.Component},function(t){s=t.Constants}],execute:function(){var h;o._RF.push({},"824b510kfVBeryCRRW9bgk8","TouchController",void 0);var u=e.ccclass;t("TouchController",u("TouchController")(h=function(t){function o(){for(var n,o=arguments.length,e=new Array(o),i=0;i<o;i++)e[i]=arguments[i];return(n=t.call.apply(t,[this].concat(e))||this)._screenWidth=void 0,n._canTouch=!0,n._touchTimer=0,n}n(o,t);var e=o.prototype;return e.start=function(){this._screenWidth=i.getVisibleSizeInPixel().width},e.onEnable=function(){this.node.on(c.EventType.TOUCH_START,this.onTouchStart,this)},e.onDisable=function(){this.node.off(c.EventType.TOUCH_START,this.onTouchStart,this)},e.onTouchStart=function(t,n){if(this._canTouch){var o,e;if(s.gameController.node.emit(s.GAME_EVENT.START_TOUCH),t.getLocation().x>this._screenWidth/2)null==(o=s.mainTorus)||o.Launch(s.MOVE_DIR.Right);else null==(e=s.mainTorus)||e.Launch(s.MOVE_DIR.Left);this._canTouch=!1,this._touchTimer=0}},e.update=function(t){this._touchTimer+=t,!this._canTouch&&this._touchTimer>=s.TOUCH_DELAY&&(this._canTouch=!0,this._touchTimer=0)},o}(r))||h);o._RF.pop()}}}));

System.register("chunks:///_virtual/TrainingController.ts",["./rollupPluginModLoBabelHelpers.js","cc","./Constants.ts"],(function(t){"use strict";var r,n,e,o,i,a,s,l,u,h,c,g,A,p,w;return{setters:[function(t){r=t.applyDecoratedDescriptor,n=t.inheritsLoose,e=t.initializerDefineProperty,o=t.assertThisInitialized},function(t){i=t.cclegacy,a=t._decorator,s=t.Animation,l=t.Prefab,u=t.instantiate,h=t.Vec3,c=t.MeshRenderer,g=t.Vec4,A=t.Quat,p=t.Component},function(t){w=t.Constants}],execute:function(){var d,f,m,T,y,v,b,_,C;i._RF.push({},"e31b72Fd4lI0YTsCm/CLfIh","TrainingController",void 0);var E=a.ccclass,B=a.property;t("TrainingController",(d=E("TrainingController"),f=B({type:s}),m=B({type:s}),T=B({type:l}),d((b=r((v=function(t){function r(){for(var r,n=arguments.length,i=new Array(n),a=0;a<n;a++)i[a]=arguments[a];return r=t.call.apply(t,[this].concat(i))||this,e(r,"leftButtonAnim",b,o(r)),e(r,"rightButtonAnim",_,o(r)),e(r,"arrowPref",C,o(r)),r.currentActiveAnimDir=void 0,r.targetArrow=void 0,r}n(r,t);var i=r.prototype;return i.start=function(){this.targetArrow=u(this.arrowPref),this.targetArrow.parent=w.target.node,this.targetArrow.scale=new h(1,1,1),this.targetArrow.active=!0,w.gameController.node.on(w.GAME_EVENT.START_TOUCH,this.onStartTouch,this)},i.swapAnim=function(t){switch(this.currentActiveAnimDir=t,t){case w.MOVE_DIR.Left:this.leftButtonAnim.play("training_button_left"),this.rightButtonAnim.play("training_button_idle");break;case w.MOVE_DIR.Right:this.leftButtonAnim.play("training_button_idle"),this.rightButtonAnim.play("training_button_right");break;default:return void console.log("Training dir not found")}},i.onDisable=function(){var t;this.leftButtonAnim.node.active=!1,this.rightButtonAnim.node.active=!1,null==(t=this.targetArrow)||t.destroy(),w.gameController.node.off(w.GAME_EVENT.START_TOUCH,this.onStartTouch,this)},i.onStartTouch=function(){},i.updateButtonsState=function(){var t=0;(t=w.mainTorus.node.position.x>w.target.node.position.x?w.MOVE_DIR.Left:w.MOVE_DIR.Right)!==this.currentActiveAnimDir&&this.swapAnim(t)},i.setTargetArrowAlpha=function(t){var r,n;null==(n=(null!=(r=this.targetArrow.getComponentInChildren(c))?r:this.targetArrow.getComponent(c)).getMaterial(0))||n.setProperty("albedo",new g(0,0,0,t))},i.updateTargetArrow=function(){if(null!==this.targetArrow){var t=new A;w.mainTorus.node.worldPosition.y>w.target.node.position.y?(this.targetArrow.lookAt(w.mainTorus.node.worldPosition,new h(0,0,1)),A.toEuler(t,this.targetArrow.rotation),this.targetArrow.setRotationFromEuler(new h(0,0,t.z))):(this.targetArrow.lookAt(w.mainTorus.node.worldPosition,new h(0,0,-1)),A.toEuler(t,this.targetArrow.rotation),this.targetArrow.setRotationFromEuler(new h(0,0,t.z+180)));var r=h.distance(w.mainTorus.node.worldPosition,w.target.node.worldPosition);r>=8?this.setTargetArrowAlpha(1):r<=2?this.setTargetArrowAlpha(0):this.setTargetArrowAlpha(r/8)}},i.update=function(t){this.updateButtonsState(),this.updateTargetArrow()},r}(p)).prototype,"leftButtonAnim",[f],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),_=r(v.prototype,"rightButtonAnim",[m],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),C=r(v.prototype,"arrowPref",[T],{configurable:!0,enumerable:!0,writable:!0,initializer:function(){return null}}),y=v))||y));i._RF.pop()}}}));

(function(r) {
  r('virtual:///prerequisite-imports/main', 'chunks:///_virtual/main'); 
})(function(mid, cid) {
    System.register(mid, [cid], function (_export, _context) {
    return {
        setters: [function(_m) {
            var _exportObj = {};

            for (var _key in _m) {
              if (_key !== "default" && _key !== "__esModule") _exportObj[_key] = _m[_key];
            }
      
            _export(_exportObj);
        }],
        execute: function () { }
    };
    });
});