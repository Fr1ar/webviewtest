System.register("chunks:///_virtual/BackgroundController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Texture2D, Material, Color, MeshRenderer, Component, Constants;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Texture2D = module.Texture2D;
      Material = module.Material;
      Color = module.Color;
      MeshRenderer = module.MeshRenderer;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _class, _class2, _descriptor, _descriptor2, _descriptor3;

      cclegacy._RF.push({}, "ad494POf3hFr6R/5bz3tH2h", "BackgroundController", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var BackgroundController = exports('BackgroundController', (_dec = ccclass('BackgroundController'), _dec2 = property({
        type: Texture2D
      }), _dec3 = property(Material), _dec4 = property({
        type: Color
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(BackgroundController, _Component);

        function BackgroundController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "bgTextures", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "floorMaterial", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "floorColors", _descriptor3, _assertThisInitialized(_this));

          _this._mesh = void 0;
          return _this;
        }

        var _proto = BackgroundController.prototype;

        _proto.onLoad = function onLoad() {
          var _this$node$getCompone;

          this._mesh = (_this$node$getCompone = this.node.getComponentInChildren(MeshRenderer)) != null ? _this$node$getCompone : this.node.getComponent(MeshRenderer);
          this._mesh.enabled = false;
        };

        _proto.SpawnBG = function SpawnBG() {
          var index = Constants.RandomFixedSeed.Rand(this.bgTextures.length);
          this.setTexture(this._mesh, this.bgTextures[index]);
          this._mesh.enabled = true;
          this.setMainColorMaterial(this.floorMaterial, this.floorColors[index]);

          if (index == 2) {
            this.node.setPosition(0, -60, -410);
          }
        };

        _proto.setTexture = function setTexture(mesh, texture) {
          var _mesh$getMaterial;

          mesh == null ? void 0 : (_mesh$getMaterial = mesh.getMaterial(0)) == null ? void 0 : _mesh$getMaterial.setProperty("mainTexture", texture);
        };

        _proto.setMainColorMaterial = function setMainColorMaterial(material, color) {
          material.setProperty("mainColor", color);
        };

        return BackgroundController;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "bgTextures", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "floorMaterial", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "floorColors", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/BotController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _inheritsLoose, cclegacy, _decorator, Component, Constants;

  return {
    setters: [function (module) {
      _inheritsLoose = module.inheritsLoose;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _class;

      cclegacy._RF.push({}, "0e3402QL4JA6KOtUEb0CwYZ", "BotController", undefined);

      var ccclass = _decorator.ccclass;
      var BotController = exports('BotController', (_dec = ccclass('BotController'), _dec(_class = /*#__PURE__*/function (_Component) {
        _inheritsLoose(BotController, _Component);

        function BotController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;
          _this._canStep = true;
          _this._botSkill = 0;
          return _this;
        }

        var _proto = BotController.prototype;

        _proto.botStart = function botStart() {
          var _this2 = this;

          this._canStep = true;
          this._botSkill = Constants.gameController.BotSkill;
          this.scheduleOnce(function () {
            _this2.botStep();
          }, Constants.gameController.IsTraining ? Constants.BOT_TRAINING_START_DELAY : Constants.BOT_START_DELAY);
        };

        _proto.botStep = function botStep() {
          var _this3 = this;

          if (!this._canStep) return;
          var dir = Constants.target.node.getWorldPosition().subtract(Constants.opponentTorus.node.position);
          var nextBotStepDelay = 0;

          if (this._botSkill >= Constants.RandomNotFixedSeed.range(-0.1, 1.5) || this._botSkill >= 0.9) {
            if (dir.y >= -0.5 && dir.x >= 0) {
              var _Constants$opponentTo;

              (_Constants$opponentTo = Constants.opponentTorus) == null ? void 0 : _Constants$opponentTo.Launch(Constants.MOVE_DIR.Right);
            } else if (dir.y >= -0.5 && dir.x <= 0) {
              var _Constants$opponentTo2;

              (_Constants$opponentTo2 = Constants.opponentTorus) == null ? void 0 : _Constants$opponentTo2.Launch(Constants.MOVE_DIR.Left);
            }

            if (dir.y > 1) {
              nextBotStepDelay = Constants.RandomNotFixedSeed.range(Constants.BOT_STEP_DELAY_MIN_1, Constants.BOT_STEP_DELAY_MIN_2);
            } else {
              nextBotStepDelay = Constants.RandomNotFixedSeed.range(Constants.BOT_STEP_DELAY_MAX_1, Constants.BOT_STEP_DELAY_MAX_2);
            }
          } else {
            if (Constants.RandomNotFixedSeed.range(0, 1) > 0.5) {
              var _Constants$opponentTo3;

              (_Constants$opponentTo3 = Constants.opponentTorus) == null ? void 0 : _Constants$opponentTo3.Launch(Constants.MOVE_DIR.Right);
            } else {
              var _Constants$opponentTo4;

              (_Constants$opponentTo4 = Constants.opponentTorus) == null ? void 0 : _Constants$opponentTo4.Launch(Constants.MOVE_DIR.Left);
            }

            nextBotStepDelay = Constants.RandomNotFixedSeed.range(0.3, 1);
          }

          this.scheduleOnce(function () {
            _this3.botStep();
          }, nextBotStepDelay);
        };

        _proto.botStop = function botStop() {
          this._canStep = false;
        };

        _proto.onDestroy = function onDestroy() {
          this.botStop();
        };

        _proto.onDisable = function onDisable() {
          this.botStop();
        };

        return BotController;
      }(Component)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/CameraController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Animation, Vec3, Component, Constants;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Animation = module.Animation;
      Vec3 = module.Vec3;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _dec2, _class, _class2, _descriptor;

      cclegacy._RF.push({}, "16a02a11HVKD7C4ydSSnPjW", "CameraController", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var CameraController = exports('CameraController', (_dec = ccclass('CameraController'), _dec2 = property({
        type: Animation
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(CameraController, _Component);

        function CameraController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "Animation", _descriptor, _assertThisInitialized(_this));

          _this._prevMainTorusPos = void 0;
          return _this;
        }

        var _proto = CameraController.prototype;

        _proto.update = function update(deltaTime) {
          this.setCameraPosition_Y();
        };

        _proto.PlayShakeAnimation = function PlayShakeAnimation() {
          this.Animation.play('shake');
        };

        _proto.setCameraPosition_Y = function setCameraPosition_Y() {
          if (this._prevMainTorusPos != null) {
            var currentMainTorusPos = Constants.mainTorus.node.getPosition();
            var deltaMainTorusPos_Y = currentMainTorusPos.y - this._prevMainTorusPos.y;
            var newCameraPos = new Vec3(this.node.position.x, this.node.position.y + deltaMainTorusPos_Y / Constants.CAMERA_MOVE_COEFF, this.node.position.z);
            this.node.setPosition(newCameraPos);
          }

          this._prevMainTorusPos = Constants.mainTorus.node.getPosition();
        };

        return CameraController;
      }(Component), _descriptor = _applyDecoratedDescriptor(_class2.prototype, "Animation", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/ComplimentPopup.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Sprite, Animation, SpriteFrame, Quat, Vec3, Component, Constants;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Sprite = module.Sprite;
      Animation = module.Animation;
      SpriteFrame = module.SpriteFrame;
      Quat = module.Quat;
      Vec3 = module.Vec3;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _class, _class2, _descriptor, _descriptor2, _descriptor3;

      cclegacy._RF.push({}, "1335fYVzPhLfpJCa3T/B9gV", "ComplimentPopup", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var ComplimentPopup = exports('ComplimentPopup', (_dec = ccclass('ComplimentPopup'), _dec2 = property({
        type: Sprite
      }), _dec3 = property({
        type: Animation
      }), _dec4 = property({
        type: SpriteFrame
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(ComplimentPopup, _Component);

        function ComplimentPopup() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "mainSprite", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "animation", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "mainSpriteFrames", _descriptor3, _assertThisInitialized(_this));

          _this._countShow = 0;
          return _this;
        }

        var _proto = ComplimentPopup.prototype;

        _proto.start = function start() {
          Constants.ComplimentPopup = this;
          this.Disable(); // for test
          // this.scheduleOnce(() => {
          //     this.Show();
          //  }, 2);
        };

        _proto.Show = function Show() {
          this._countShow++;
          this.node.active = true;
          this.setPositionAndRotation();
          var frameIndex = this._countShow % this.mainSpriteFrames.length;
          this.mainSprite.spriteFrame = this.mainSpriteFrames[frameIndex];
          this.animation.play(Constants.COMPLIMENT_ENABLE_ANIMATION);
        };

        _proto.setPositionAndRotation = function setPositionAndRotation() {
          var angl = new Quat();

          if (Constants.RandomNotFixedSeed.Rand(2) == 0) {
            this.node.setPosition(new Vec3(-5, 100, 0));
            Quat.fromEuler(angl, 0, 0, 5);
          } else {
            this.node.setPosition(new Vec3(5, 100, 0));
            Quat.fromEuler(angl, 0, 0, -5);
          }

          this.node.setRotation(angl);
        };

        _proto.Disable = function Disable() {
          this.node.active = false;
        };

        return ComplimentPopup;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "mainSprite", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "animation", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "mainSpriteFrames", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/Constants.ts", ['cc', './CustomRandom.ts'], function (exports) {
  'use strict';

  var cclegacy, Vec3, CustomRandom;
  return {
    setters: [function (module) {
      cclegacy = module.cclegacy;
      Vec3 = module.Vec3;
    }, function (module) {
      CustomRandom = module.default;
    }],
    execute: function () {
      cclegacy._RF.push({}, "4d469WYnV1JS4w7x6VGpUJQ", "Constants", undefined);

      var GAME_MODE;

      (function (GAME_MODE) {
        GAME_MODE[GAME_MODE["SOLO"] = 0] = "SOLO";
        GAME_MODE[GAME_MODE["WITH_BOT"] = 1] = "WITH_BOT";
        GAME_MODE[GAME_MODE["PVP"] = 2] = "PVP";
      })(GAME_MODE || (GAME_MODE = {}));

      var TORUS_TYPE;

      (function (TORUS_TYPE) {
        TORUS_TYPE[TORUS_TYPE["MAIN"] = 0] = "MAIN";
        TORUS_TYPE[TORUS_TYPE["OPPONENT"] = 1] = "OPPONENT";
      })(TORUS_TYPE || (TORUS_TYPE = {}));

      var MOVE_DIR;

      (function (MOVE_DIR) {
        MOVE_DIR[MOVE_DIR["Left"] = 0] = "Left";
        MOVE_DIR[MOVE_DIR["Right"] = 1] = "Right";
      })(MOVE_DIR || (MOVE_DIR = {}));

      var HAPTIC_TYPE;

      (function (HAPTIC_TYPE) {
        HAPTIC_TYPE["LIGHT"] = "lightImpact";
        HAPTIC_TYPE["MEDIUM"] = "mediumImpact";
        HAPTIC_TYPE["HEAVY"] = "heavyImpact";
      })(HAPTIC_TYPE || (HAPTIC_TYPE = {}));

      var GAME_EVENT;

      (function (GAME_EVENT) {
        GAME_EVENT["START_TOUCH"] = "start_touch";
      })(GAME_EVENT || (GAME_EVENT = {}));

      var Constants = exports('Constants', function Constants() {});
      Constants.gameController = void 0;
      Constants.cameraController = void 0;
      Constants.mainTorus = void 0;
      Constants.opponentTorus = void 0;
      Constants.fakeTorus = void 0;
      Constants.target = void 0;
      Constants.MainCanvas = void 0;
      Constants.MainCamera = void 0;
      Constants.ComplimentPopup = void 0;
      Constants.RandomFixedSeed = new CustomRandom(111);
      Constants.RandomNotFixedSeed = new CustomRandom();
      Constants.GAME_MODE = GAME_MODE;
      Constants.TORUS_TYPE = TORUS_TYPE;
      Constants.MOVE_DIR = MOVE_DIR;
      Constants.HAPTIC_TYPE = HAPTIC_TYPE;
      Constants.GAME_EVENT = GAME_EVENT;
      Constants.LEFT_BOARDER_NAME = "left";
      Constants.RIGHT_BOARDER_NAME = "right";
      Constants.UX_TORUS_HIT = "\"play_hoops_random_hit\"";
      Constants.UX_TORUS_LAUNCH = "\"play_hoops_launch\"";
      Constants.ADD_SCORE = 10;
      Constants.TOUCH_DELAY = 0.15;
      Constants.MAIN_TORUS_SPAWN_POS = new Vec3(-2.5, 1.8, 0);
      Constants.OPPONENT_TORUS_SPAWN_POS = new Vec3(2.5, 1.8, 0);
      Constants.TORUS_SPAWN_CENTER_POS = new Vec3(0, 1.8, 0);
      Constants.TORUS_FORCE = 700;
      Constants.TORUS_PHYSICS_GROUP_MAIN = 2;
      Constants.TORUS_PHYSICS_GROUP_OPPONENT = 4;
      Constants.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER = 0.5;
      Constants.LEADER_POS_Y = 1.8;
      Constants.PHYSICS_FIXED_TIME_STEP = 1 / 60;
      Constants.PHYSICS_MAX_SUB_STEPS = 5;
      Constants.MAX_ACCUMULATOR = 1 / 5;
      Constants.SPHERE_SPAWN_POS_X = [-2.5, 0, 2.5];
      Constants.SPHERE_SPAWN_POS_Y = [5, 7, 9];
      Constants.SPHERE_ENABLE_ANIMATION = "sphere_enable";
      Constants.SPHERE_TRIGGER_DELAY = 0.2;
      Constants.COMPLIMENT_ENABLE_ANIMATION = "compliment_enable";
      Constants.COMPLIMENT_POPUP_DURATION = 1.5;
      Constants.BOT_START_DELAY = 4;
      Constants.BOT_TRAINING_START_DELAY = 12;
      Constants.BOT_STEP_DELAY_MIN_1 = 0.2;
      Constants.BOT_STEP_DELAY_MIN_2 = 0.3;
      Constants.BOT_STEP_DELAY_MAX_1 = 0.3;
      Constants.BOT_STEP_DELAY_MAX_2 = 0.5;
      Constants.CAMERA_MOVE_COEFF = 6;

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/CustomRandom.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc'], function (exports) {
  'use strict';

  var _createClass, cclegacy, _decorator;

  return {
    setters: [function (module) {
      _createClass = module.createClass;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
    }],
    execute: function () {
      var _dec, _class;

      cclegacy._RF.push({}, "9c6a8rwrYVGZpOCTLjZ/Rz6", "CustomRandom", undefined);

      var ccclass = _decorator.ccclass;
      var CustomRandom = exports('default', (_dec = ccclass('CustomRandom'), _dec(_class = /*#__PURE__*/function () {
        /**
             * Create a random number generator
         */
        function CustomRandom(seed) {
          this.seed = void 0;
          this.seed = seed;

          if (!this.seed && this.seed != 0) {
            this.seed = new Date().getTime();
          }
        }
        /**
             * Set the seed for the random number generator. If it is not set, it will actually take the current time in milliseconds.
         */


        var _proto = CustomRandom.prototype;
        /**
             * Returns a random floating point number between min and max
         */

        _proto.range = function range(min, max) {
          if (!this.seed && this.seed != 0) {
            this.seed = new Date().getTime();
          }

          max = max || 1;
          min = min || 0;
          this.seed = (this.seed * 9301 + 49297) % 233280;
          var rnd = this.seed / 233280.0;
          return min + rnd * (max - min);
        }
        /**
             * Returns an integer between [0, max)
        */
        ;

        _proto.Rand = function Rand(max) {
          return Math.floor(this.range(0, max));
        };

        _proto.RandInArray = function RandInArray(array, exclud1, exclud2) {
          array = array.filter(function (obj) {
            return obj !== exclud1;
          });
          array = array.filter(function (obj) {
            return obj !== exclud2;
          });
          return array[this.Rand(array.length)];
        };

        _createClass(CustomRandom, [{
          key: "value",
          get:
          /**
               * Returns a random number between 0.0 and 1.0
           */
          function get() {
            return this.range(0, 1);
          }
        }]);

        return CustomRandom;
      }()) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/GameController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './BotController.ts', './Constants.ts', './TouchController.ts', './TorusSpawner.ts', './BackgroundController.ts', './TrainingController.ts', './CameraController.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Camera, Canvas, Prefab, PhysicsSystem, math, Component, BotController, Constants, TouchController, TorusSpawner, BackgroundController, TrainingController, CameraController;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Camera = module.Camera;
      Canvas = module.Canvas;
      Prefab = module.Prefab;
      PhysicsSystem = module.PhysicsSystem;
      math = module.math;
      Component = module.Component;
    }, function (module) {
      BotController = module.BotController;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      TouchController = module.TouchController;
    }, function (module) {
      TorusSpawner = module.TorusSpawner;
    }, function (module) {
      BackgroundController = module.BackgroundController;
    }, function (module) {
      TrainingController = module.TrainingController;
    }, function (module) {
      CameraController = module.CameraController;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _dec5, _dec6, _dec7, _dec8, _dec9, _dec10, _class, _class2, _descriptor, _descriptor2, _descriptor3, _descriptor4, _descriptor5, _descriptor6, _descriptor7, _descriptor8, _descriptor9;

      cclegacy._RF.push({}, "72945lhD2RGPaT16DxHpjWT", "GameController", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var GameController = exports('GameController', (_dec = ccclass('GameController'), _dec2 = property({
        type: BotController
      }), _dec3 = property({
        type: TouchController
      }), _dec4 = property({
        type: CameraController
      }), _dec5 = property({
        type: TrainingController
      }), _dec6 = property({
        type: TorusSpawner
      }), _dec7 = property({
        type: BackgroundController
      }), _dec8 = property({
        type: Camera
      }), _dec9 = property({
        type: Canvas
      }), _dec10 = property({
        type: Prefab
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(GameController, _Component);

        function GameController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "botController", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "touchController", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "cameraController", _descriptor3, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "trainingController", _descriptor4, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "torusSpawner", _descriptor5, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "backgroundController", _descriptor6, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "MainCamera", _descriptor7, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "MainCanvas", _descriptor8, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "ScoreAnimPrefab", _descriptor9, _assertThisInitialized(_this));

          _this.GameMode = Constants.GAME_MODE.WITH_BOT;
          _this.MainScore = 0;
          _this.BotScore = 0;
          _this.IsMaster = false;
          _this.IsTraining = false;
          _this.BotSkill = 0;
          return _this;
        }

        var _proto = GameController.prototype;

        _proto.__preload = function __preload() {
          Constants.gameController = this;
          this.touchController.enabled = false;
          Constants.MainCamera = this.MainCamera;
          Constants.MainCanvas = this.MainCanvas;
          Constants.cameraController = this.cameraController;
        };

        _proto.onLoad = function onLoad() {
          PhysicsSystem.instance.fixedTimeStep = Constants.PHYSICS_FIXED_TIME_STEP;
          PhysicsSystem.instance.maxSubSteps = Constants.PHYSICS_MAX_SUB_STEPS;
          window.GameController = this;
        };

        _proto.start = function start() {
          if (window.blitzOnSceneLoaded !== undefined) window.blitzOnSceneLoaded();else // this.startGame("StrongBot", 0, true, false, 1) //green - 517
            this.startGame("Bot", math.randomRange(0, 100), true, false, 0); // this.startGame("SinglePlayer", 0, true)
        };

        _proto.startGame = function startGame(gameMode, seed, isMaster, isFirstLaunch, botSkill) {
          Constants.RandomFixedSeed.seed = seed;
          this.setGameMode(gameMode);
          this.MainScore = 0;
          this.BotScore = 0;
          this.IsMaster = isMaster;
          this.IsTraining = isFirstLaunch;
          this.BotSkill = botSkill;

          if (!this.IsTraining) {
            this.trainingController.node.destroy();
          } else {
            Constants.RandomFixedSeed.seed = 100;
          }

          this.torusSpawner.Spawn();
          this.backgroundController.SpawnBG();
          if (this.GameMode == Constants.GAME_MODE.WITH_BOT) this.botController.botStart();else this.botController.botStop();
          this.touchController.enabled = true;
        };

        _proto.stopTraining = function stopTraining() {
          if (this.IsTraining && this.trainingController !== null) {
            this.trainingController.node.destroy();
            this.trainingController = null;
          }
        };

        _proto.gameStop = function gameStop() {
          this.touchController.enabled = false;

          if (this.GameMode == Constants.GAME_MODE.WITH_BOT) {
            this.botController.botStop();
            this.botGameOver();
          }
        };

        _proto.botGameOver = function botGameOver() {
          if (window.blitzOnBotGameOverOneWorld !== undefined) window.blitzOnBotGameOverOneWorld();
        };

        _proto.gameOver = function gameOver() {
          if (window.blitzOnGameOver !== undefined) window.blitzOnGameOver();
        };

        _proto.setGameMode = function setGameMode(gameMode) {
          switch (gameMode) {
            case "Human":
              this.GameMode = Constants.GAME_MODE.PVP;
              break;

            case "Bot":
              this.GameMode = Constants.GAME_MODE.WITH_BOT;
              break;

            case "SinglePlayer":
              this.GameMode = Constants.GAME_MODE.SOLO;
              break;

            default:
              console.log("GameMode not found");
          }
        };

        _proto.PlayUX = function PlayUX(name) {
          if (window.blitzOnUX !== undefined) window.blitzOnUX(name);
        };

        _proto.addScore = function addScore(score) {
          this.MainScore += score;
          this.checkLeader(score);
          if (window.blitzOnScore !== undefined) window.blitzOnScore(this.MainScore);
        };

        _proto.addBotScore = function addBotScore(score) {
          this.BotScore += score;
          this.checkLeader(score);
          if (window.blitzOnNewBotScoreOneWorld !== undefined) window.blitzOnNewBotScoreOneWorld(this.BotScore);
        };

        _proto.checkLeader = function checkLeader(score) {
          if (score <= 0) {
            return;
          }

          if (this.GameMode !== Constants.GAME_MODE.SOLO) {
            var isBotLeader = this.BotScore > this.MainScore;
            this.scheduleOnce(function () {
              Constants.opponentTorus.SetLeader(isBotLeader);
              Constants.mainTorus.SetLeader(!isBotLeader);
            }, 0.4);
          }
        };

        _proto.launchOpponentTorus = function launchOpponentTorus(dir) {
          var _Constants$opponentTo;

          (_Constants$opponentTo = Constants.opponentTorus) == null ? void 0 : _Constants$opponentTo.Launch(dir);
        };

        return GameController;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "botController", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "touchController", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "cameraController", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor4 = _applyDecoratedDescriptor(_class2.prototype, "trainingController", [_dec5], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor5 = _applyDecoratedDescriptor(_class2.prototype, "torusSpawner", [_dec6], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor6 = _applyDecoratedDescriptor(_class2.prototype, "backgroundController", [_dec7], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor7 = _applyDecoratedDescriptor(_class2.prototype, "MainCamera", [_dec8], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor8 = _applyDecoratedDescriptor(_class2.prototype, "MainCanvas", [_dec9], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor9 = _applyDecoratedDescriptor(_class2.prototype, "ScoreAnimPrefab", [_dec10], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/main", ['./BackgroundController.ts', './BotController.ts', './CameraController.ts', './ComplimentPopup.ts', './Constants.ts', './CustomRandom.ts', './GameController.ts', './PoolManager.ts', './RotateComponent.ts', './ScoreController.ts', './Startup.ts', './Target.ts', './TargetController.ts', './Torus.ts', './TorusSpawner.ts', './TouchController.ts', './TrainingController.ts'], function () {
  'use strict';

  return {
    setters: [null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null],
    execute: function () {}
  };
});

System.register("chunks:///_virtual/PoolManager.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc'], function (exports) {
  'use strict';

  var _createClass, cclegacy, _decorator, instantiate, NodePool;

  return {
    setters: [function (module) {
      _createClass = module.createClass;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      instantiate = module.instantiate;
      NodePool = module.NodePool;
    }],
    execute: function () {
      var _dec, _class, _class2;

      cclegacy._RF.push({}, "5e33aGSgzVJm7Wm1JoiXmXg", "PoolManager", undefined);

      var ccclass = _decorator.ccclass;
      var PoolManager = exports('PoolManager', (_dec = ccclass("PoolManager"), _dec(_class = (_class2 = /*#__PURE__*/function () {
        function PoolManager() {
          this.dictPool = {};
          this.dictPrefab = {};
        }

        var _proto = PoolManager.prototype;

        _proto.getNode = function getNode(prefab, parent) {
          var name = prefab.data.name;
          this.dictPrefab[name] = prefab;
          var node = null;

          if (this.dictPool.hasOwnProperty(name)) {
            var pool = this.dictPool[name];

            if (pool.size() > 0) {
              node = pool.get();
            } else {
              node = instantiate(prefab);
            }
          } else {
            var _pool = new NodePool();

            this.dictPool[name] = _pool;
            node = instantiate(prefab);
          }

          node.parent = parent;
          return node;
        };

        _proto.putNode = function putNode(node) {
          var name = node.name;
          var pool = null;

          if (this.dictPool.hasOwnProperty(name)) {
            pool = this.dictPool[name];
          } else {
            pool = new NodePool();
            this.dictPool[name] = pool;
          }

          pool.put(node);
        };

        _proto.clearPool = function clearPool(name) {
          if (this.dictPool.hasOwnProperty(name)) {
            var pool = this.dictPool[name];
            pool.clear();
          }
        };

        _createClass(PoolManager, null, [{
          key: "instance",
          get: function get() {
            if (this._instance) {
              return this._instance;
            }

            this._instance = new PoolManager();
            return this._instance;
          }
        }]);

        return PoolManager;
      }(), _class2._instance = void 0, _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/RotateComponent.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Vec3, Component;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Vec3 = module.Vec3;
      Component = module.Component;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _class, _class2, _descriptor, _descriptor2;

      cclegacy._RF.push({}, "457ebHanMRKlYHgtsCO7F6U", "RotateComponent", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var RotateComponent = exports('RotateComponent', (_dec = ccclass('RotateComponent'), _dec2 = property(Number), _dec3 = property(Vec3), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(RotateComponent, _Component);

        function RotateComponent() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "Speed", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "Direction", _descriptor2, _assertThisInitialized(_this));

          return _this;
        }

        var _proto = RotateComponent.prototype;

        _proto.update = function update(deltaTime) {
          var addSpeed = this.Speed * deltaTime;
          var eulerAngles = this.node.eulerAngles;
          this.node.eulerAngles = new Vec3(eulerAngles.x + this.Direction.x * addSpeed, eulerAngles.y + this.Direction.y * addSpeed, eulerAngles.z + this.Direction.z * addSpeed);
        };

        return RotateComponent;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "Speed", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return 0;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "Direction", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return new Vec3();
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/ScoreController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts', './PoolManager.ts'], function (exports) {
  'use strict';

  var _inheritsLoose, cclegacy, _decorator, Vec3, Animation, Component, Constants, PoolManager;

  return {
    setters: [function (module) {
      _inheritsLoose = module.inheritsLoose;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Vec3 = module.Vec3;
      Animation = module.Animation;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      PoolManager = module.PoolManager;
    }],
    execute: function () {
      var _dec, _class;

      cclegacy._RF.push({}, "44e57PoZmBDba/ht2k+EZdR", "ScoreController", undefined);

      var ccclass = _decorator.ccclass;
      var ScoreController = exports('ScoreController', (_dec = ccclass('ScoreController'), _dec(_class = /*#__PURE__*/function (_Component) {
        _inheritsLoose(ScoreController, _Component);

        function ScoreController() {
          return _Component.apply(this, arguments) || this;
        }

        var _proto = ScoreController.prototype;

        _proto.ShowScore = function ShowScore(score, worldPos) {
          var node = PoolManager.instance.getNode(Constants.gameController.ScoreAnimPrefab, Constants.MainCanvas.node);
          var pos = new Vec3();
          var cameraComp = Constants.MainCamera;
          cameraComp.convertToUINode(worldPos, Constants.MainCanvas.node, pos);
          node.setPosition(pos);
          var animationComponent = node.getComponent(Animation);
          animationComponent.once(Animation.EventType.FINISHED, function () {
            node.destroy();
          });
          animationComponent.play();
        };

        return ScoreController;
      }(Component)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/Startup.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc'], function (exports) {
  'use strict';

  var _inheritsLoose, cclegacy, _decorator, director, Component;

  return {
    setters: [function (module) {
      _inheritsLoose = module.inheritsLoose;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      director = module.director;
      Component = module.Component;
    }],
    execute: function () {
      var _dec, _class;

      cclegacy._RF.push({}, "97b5d1qDgBEnJkUNzLqOKTp", "Startup", undefined);

      var ccclass = _decorator.ccclass;
      var Startup = exports('Startup', (_dec = ccclass('Startup'), _dec(_class = /*#__PURE__*/function (_Component) {
        _inheritsLoose(Startup, _Component);

        function Startup() {
          return _Component.apply(this, arguments) || this;
        }

        var _proto = Startup.prototype;

        _proto.start = function start() {
          director.preloadScene("game", function () {
            window.blitzOnSceneLoaded();
          });
        };

        return Startup;
      }(Component)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/Target.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts', './PoolManager.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Color, Material, Prefab, ParticleSystem, Quat, Component, Constants, PoolManager;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Color = module.Color;
      Material = module.Material;
      Prefab = module.Prefab;
      ParticleSystem = module.ParticleSystem;
      Quat = module.Quat;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      PoolManager = module.PoolManager;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _dec5, _class, _class2, _descriptor, _descriptor2, _descriptor3, _descriptor4;

      cclegacy._RF.push({}, "d6ab3TuehpLY5WQyEt0ADdB", "Target", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var Target = exports('Target', (_dec = ccclass('Target'), _dec2 = property({
        type: Color
      }), _dec3 = property({
        type: Color
      }), _dec4 = property(Material), _dec5 = property({
        type: Prefab
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(Target, _Component);

        function Target() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "mainColors", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "additionalColors", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "material", _descriptor3, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "ParticlePrefab", _descriptor4, _assertThisInitialized(_this));

          _this._currentColorIndex = null;
          return _this;
        }

        var _proto = Target.prototype;

        _proto.onLoad = function onLoad() {
          this.setColor();
        };

        _proto.setColor = function setColor() {
          this._currentColorIndex = Constants.RandomFixedSeed.Rand(this.mainColors.length);
          this.setColorMaterial(this.material, this.mainColors[this._currentColorIndex]);
        };

        _proto.SetActive = function SetActive(active) {
          this.node.active = active;
          this.setColor();
        };

        _proto.GetMainColor = function GetMainColor() {
          return this.mainColors[this._currentColorIndex];
        };

        _proto.GetAdditionalColor = function GetAdditionalColor() {
          return this.additionalColors[this._currentColorIndex];
        };

        _proto.PlayParticles = function PlayParticles(pos, rot_z) {
          var particleNode = PoolManager.instance.getNode(this.ParticlePrefab, this.node.parent.parent);
          particleNode.setWorldPosition(pos);
          var particleSystemComp = particleNode.getComponentsInChildren(ParticleSystem);
          var mainColor = this.GetMainColor();
          var additionalColor = this.GetAdditionalColor();
          particleSystemComp.forEach(function (element) {
            element.startColor.colorMax = mainColor;
            element.startColor.colorMin = additionalColor;
            element.play();
          });
          var leftRotation = new Quat();
          var rightRotation = new Quat();
          Quat.fromAngleZ(leftRotation, rot_z);
          Quat.fromAngleZ(rightRotation, rot_z + 180);
          particleSystemComp[0].node.setWorldRotation(leftRotation);
          particleSystemComp[1].node.setWorldRotation(rightRotation);
        };

        _proto.setColorMaterial = function setColorMaterial(material, color) {
          material == null ? void 0 : material.setProperty("mainColor", color);
          material == null ? void 0 : material.setProperty("emissive", color);
        };

        return Target;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "mainColors", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "additionalColors", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "material", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor4 = _applyDecoratedDescriptor(_class2.prototype, "ParticlePrefab", [_dec5], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/TargetController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts', './ScoreController.ts', './Target.ts', './Torus.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Collider, Animation, Vec3, Component, Constants, ScoreController, Target, Torus;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Collider = module.Collider;
      Animation = module.Animation;
      Vec3 = module.Vec3;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      ScoreController = module.ScoreController;
    }, function (module) {
      Target = module.Target;
    }, function (module) {
      Torus = module.Torus;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _class, _class2, _descriptor, _descriptor2, _descriptor3;

      cclegacy._RF.push({}, "cf0dcC5VfdAsYnHNbWjOBC6", "TargetController", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var TargetController = exports('TargetController', (_dec = ccclass('TargetController'), _dec2 = property({
        type: Target
      }), _dec3 = property({
        type: Collider
      }), _dec4 = property({
        type: Animation
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(TargetController, _Component);

        function TargetController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "targets", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "TriggerCollider", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "Animation", _descriptor3, _assertThisInitialized(_this));

          _this._scoreController = new ScoreController();
          _this._currentTargetIndex = 0;
          return _this;
        }

        var _proto = TargetController.prototype;

        _proto.onLoad = function onLoad() {
          Constants.target = this;
        };

        _proto.start = function start() {
          this.targets.forEach(function (element) {
            element.enabled = false;
          });
          this.enableRandomTarget();
          this.TriggerCollider.on('onTriggerEnter', this.onTriggerEnter, this);
        };

        _proto.onDestroy = function onDestroy() {
          this.TriggerCollider.off('onTriggerEnter', this.onTriggerEnter, this);
        };

        _proto.onTriggerEnter = function onTriggerEnter(event) {
          var torus = event.otherCollider.getComponent(Torus);
          this.onTrigger(torus, false);
        };

        _proto.onTrigger = function onTrigger(torus, test) {
          var _this2 = this;

          this.TriggerCollider.enabled = false;
          var torus_rot_z = torus.node.eulerAngles.z;

          this.targets[this._currentTargetIndex].PlayParticles(this.node.getWorldPosition(), torus_rot_z);

          this.scheduleOnce(function () {
            if (torus !== null && torus.Type === Constants.TORUS_TYPE.MAIN) {
              Constants.gameController.addScore(Constants.ADD_SCORE);
              Constants.gameController.PlayUX(Constants.UX_TORUS_HIT);
              Constants.gameController.stopTraining();

              _this2._scoreController.ShowScore(Constants.ADD_SCORE, new Vec3(_this2.node.worldPosition.x, _this2.node.worldPosition.y, 0));

              Constants.ComplimentPopup.Show();
              Constants.cameraController.PlayShakeAnimation();
            } else if (torus.Type === Constants.TORUS_TYPE.OPPONENT) {
              Constants.gameController.addBotScore(Constants.ADD_SCORE);
            }

            _this2.setNextPos(function () {
              _this2.enableRandomTarget();
            });
          }, Constants.SPHERE_TRIGGER_DELAY);
        };

        _proto.setNextPos = function setNextPos(callback) {
          var newPos_x = Constants.RandomFixedSeed.RandInArray(Constants.SPHERE_SPAWN_POS_X, this.node.position.x);
          var newPos_y = Constants.RandomFixedSeed.RandInArray(Constants.SPHERE_SPAWN_POS_Y, this.node.position.y);
          var newPos = new Vec3(newPos_x, newPos_y, 0);
          this.node.setWorldPosition(newPos);
          callback();
        };

        _proto.enableRandomTarget = function enableRandomTarget() {
          var _this3 = this;

          this.targets[this._currentTargetIndex].SetActive(false);

          var index = Constants.RandomFixedSeed.Rand(this.targets.length);
          this.targets[index].SetActive(true);
          this._currentTargetIndex = index;
          this.Animation.play(Constants.SPHERE_ENABLE_ANIMATION);
          this.scheduleOnce(function () {
            _this3.TriggerCollider.enabled = true;
          }, 0.1);
        };

        return TargetController;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "targets", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "TriggerCollider", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "Animation", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/Torus.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts', './PoolManager.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Prefab, Node, AnimationComponent, ParticleSystem, RigidBody, Collider, Vec3, MeshRenderer, Component, Constants, PoolManager;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Prefab = module.Prefab;
      Node = module.Node;
      AnimationComponent = module.AnimationComponent;
      ParticleSystem = module.ParticleSystem;
      RigidBody = module.RigidBody;
      Collider = module.Collider;
      Vec3 = module.Vec3;
      MeshRenderer = module.MeshRenderer;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      PoolManager = module.PoolManager;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _dec5, _dec6, _class, _class2, _descriptor, _descriptor2, _descriptor3, _descriptor4, _descriptor5;

      cclegacy._RF.push({}, "17747py+P5BmIqpsRq/Nhrt", "Torus", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var Torus = exports('Torus', (_dec = ccclass('Torus'), _dec2 = property({
        type: Prefab
      }), _dec3 = property({
        type: Node
      }), _dec4 = property({
        type: Node
      }), _dec5 = property({
        type: AnimationComponent
      }), _dec6 = property({
        type: ParticleSystem
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(Torus, _Component);

        function Torus() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "leaderPrefab", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "startArrow", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "torusMeshNode", _descriptor3, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "torusLaunchEffect", _descriptor4, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "launchParticle", _descriptor5, _assertThisInitialized(_this));

          _this.Type = Constants.TORUS_TYPE.MAIN;
          _this._rigidBody = void 0;
          _this._collider = [];
          _this._isFirstLaunch = false;
          _this._leaderNode = void 0;
          _this._isLeader = void 0;
          return _this;
        }

        var _proto = Torus.prototype;

        _proto.onLoad = function onLoad() {
          var _this$getComponentInC, _this$getComponentsIn;

          this._rigidBody = (_this$getComponentInC = this.getComponentInChildren(RigidBody)) != null ? _this$getComponentInC : this.getComponent(RigidBody);
          this._collider = (_this$getComponentsIn = this.getComponentsInChildren(Collider)) != null ? _this$getComponentsIn : this.getComponents(Collider);
          this._rigidBody.useGravity = false;
        };

        _proto.start = function start() {
          //this._rigidBody.useGravity = true;
          if (Constants.gameController.GameMode !== Constants.GAME_MODE.SOLO) {
            this.setPhysicsGroup();
            this.initLeaderNode();

            if (this.Type === Constants.TORUS_TYPE.OPPONENT) {
              this.startArrow.active = false;
            }
          }

          if (Constants.gameController.IsTraining) {
            if (this.Type === Constants.TORUS_TYPE.OPPONENT) this.SetMeshEnabled(false);
            this.startArrow.active = false;
          }

          for (var i = 0; i < this._collider.length; i++) {
            this._collider[i].on('onCollisionEnter', this.onCollisionEnter, this);
          }

          Constants.gameController.node.on(Constants.GAME_EVENT.START_TOUCH, this.onStartTouch, this);
        };

        _proto.onDestroy = function onDestroy() {
          for (var i = 0; i < this._collider.length; i++) {
            this._collider[i].off('onCollisionEnter', this.onCollisionEnter, this);
          }

          Constants.gameController.node.off(Constants.GAME_EVENT.START_TOUCH, this.onStartTouch, this);
        };

        _proto.update = function update(dt) {
          this.setLeaderNodePosition();
        };

        _proto.Launch = function Launch(direction, forceCoeff) {
          if (forceCoeff === void 0) {
            forceCoeff = 1;
          }

          if (!this._isFirstLaunch) {
            this._rigidBody.useGravity = true;
            this._isFirstLaunch = true;
          }

          if (this.Type === Constants.TORUS_TYPE.MAIN) {
            this.torusLaunchEffect.play();
            Constants.gameController.PlayUX(Constants.UX_TORUS_LAUNCH);
            this.launchParticle.play();

            if (window.blitzOnLaunchOpponentTorus !== undefined && forceCoeff == 1 && Constants.gameController.GameMode == Constants.GAME_MODE.PVP) {
              window.blitzOnLaunchOpponentTorus(direction);
            }
          } else {
            if (Constants.gameController.IsTraining) Constants.opponentTorus.SetMeshEnabled(true);
          }

          this._rigidBody.setLinearVelocity(new Vec3(0, 0, 0));

          var dirVec = direction === Constants.MOVE_DIR.Left ? new Vec3(-0.3, 1, 0) : new Vec3(0.3, 1, 0);
          var force = dirVec.multiplyScalar(Constants.TORUS_FORCE * forceCoeff);

          this._rigidBody.applyForce(force);
        };

        _proto.SetLeader = function SetLeader(leader) {
          this._isLeader = leader;
        };

        _proto.SetMeshEnabled = function SetMeshEnabled(enabled) {
          var _this$node$getCompone, _this$_leaderNode$get;

          var mesh = (_this$node$getCompone = this.node.getComponentInChildren(MeshRenderer)) != null ? _this$node$getCompone : this.node.getComponent(MeshRenderer);
          mesh.enabled = enabled;
          var leaderMesh = (_this$_leaderNode$get = this._leaderNode.getComponentInChildren(MeshRenderer)) != null ? _this$_leaderNode$get : this._leaderNode.getComponent(MeshRenderer);
          leaderMesh.enabled = enabled;
        };

        _proto.initLeaderNode = function initLeaderNode() {
          this._leaderNode = PoolManager.instance.getNode(this.leaderPrefab, this.node.parent);
          this._leaderNode.active = false;
          this._isLeader = false;
        };

        _proto.setLeaderNodePosition = function setLeaderNodePosition() {
          if (this._leaderNode && this._isLeader) {
            var pos = new Vec3(this.node.worldPosition.x, this.node.worldPosition.y + Constants.LEADER_POS_Y, this.node.worldPosition.z);

            this._leaderNode.setPosition(pos);

            this._leaderNode.active = true;
          } else if (this._leaderNode.active) {
            this._leaderNode.active = false;
          }
        };

        _proto.onCollisionEnter = function onCollisionEnter(event) {
          if (event.otherCollider.node.name == Constants.LEFT_BOARDER_NAME) {
            this.Launch(Constants.MOVE_DIR.Right, Constants.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER);
          } else if (event.otherCollider.node.name == Constants.RIGHT_BOARDER_NAME) {
            this.Launch(Constants.MOVE_DIR.Left, Constants.TORUS_LAUNCH_FORCE_COEFF_ON_BOARDER);
          }
        };

        _proto.onStartTouch = function onStartTouch() {
          if (this.Type === Constants.TORUS_TYPE.MAIN) {
            this.startArrow.active = false;
          }
        };

        _proto.setPhysicsGroup = function setPhysicsGroup() {
          if (this.Type === Constants.TORUS_TYPE.MAIN) this._rigidBody.setGroup(Constants.TORUS_PHYSICS_GROUP_MAIN);else this._rigidBody.setGroup(Constants.TORUS_PHYSICS_GROUP_OPPONENT);
        };

        return Torus;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "leaderPrefab", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "startArrow", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "torusMeshNode", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor4 = _applyDecoratedDescriptor(_class2.prototype, "torusLaunchEffect", [_dec5], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor5 = _applyDecoratedDescriptor(_class2.prototype, "launchParticle", [_dec6], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/TorusSpawner.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts', './Torus.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Prefab, Material, Texture2D, instantiate, MeshRenderer, Component, Constants, Torus;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Prefab = module.Prefab;
      Material = module.Material;
      Texture2D = module.Texture2D;
      instantiate = module.instantiate;
      MeshRenderer = module.MeshRenderer;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }, function (module) {
      Torus = module.Torus;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _dec5, _class, _class2, _descriptor, _descriptor2, _descriptor3, _descriptor4;

      cclegacy._RF.push({}, "b87ab7Z4AxES7AvVg6+WCJC", "TorusSpawner", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var TorusSpawner = exports('TorusSpawner', (_dec = ccclass('TorusSpawner'), _dec2 = property({
        type: Prefab
      }), _dec3 = property({
        type: Material
      }), _dec4 = property({
        type: Material
      }), _dec5 = property({
        type: Texture2D
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(TorusSpawner, _Component);

        function TorusSpawner() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "torusPref", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "opponentMaterial", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "mainMaterial", _descriptor3, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "torusTextures", _descriptor4, _assertThisInitialized(_this));

          return _this;
        }

        var _proto = TorusSpawner.prototype;

        _proto.Spawn = function Spawn() {
          var mainPos;
          var oppPos;

          if (Constants.gameController.IsMaster) {
            mainPos = Constants.MAIN_TORUS_SPAWN_POS;
            oppPos = Constants.OPPONENT_TORUS_SPAWN_POS;
          } else {
            mainPos = Constants.OPPONENT_TORUS_SPAWN_POS;
            oppPos = Constants.MAIN_TORUS_SPAWN_POS;
          }

          if (Constants.gameController.GameMode === Constants.GAME_MODE.SOLO || Constants.gameController.IsTraining) {
            mainPos = Constants.TORUS_SPAWN_CENTER_POS;
          } //this.createMainTorus(new Vec3(0, 8, 0));


          this.createMainTorus(mainPos);

          if (Constants.gameController.GameMode !== Constants.GAME_MODE.SOLO) {
            this.createOpponentTorus(oppPos);
          }
        };

        _proto.createFakeTorus = function createFakeTorus(position) {
          var mainTorus = instantiate(this.torusPref);
          mainTorus.parent = this.node.parent;
          mainTorus.position = position;
          this.setMaterial(mainTorus, this.mainMaterial);
          this.setRandomTexture(mainTorus, this.torusTextures);
          Constants.fakeTorus = mainTorus.getComponent(Torus);
          Constants.fakeTorus.Type = Constants.TORUS_TYPE.MAIN;
        };

        _proto.createOpponentTorus = function createOpponentTorus(position) {
          var opponentTorus = instantiate(this.torusPref);
          opponentTorus.parent = this.node.parent;
          opponentTorus.position = position;
          this.setMaterial(opponentTorus, this.opponentMaterial);
          this.setRandomTexture(opponentTorus, this.torusTextures);
          Constants.opponentTorus = opponentTorus.getComponent(Torus);
          Constants.opponentTorus.Type = Constants.TORUS_TYPE.OPPONENT;
        };

        _proto.createMainTorus = function createMainTorus(position) {
          var mainTorus = instantiate(this.torusPref);
          mainTorus.parent = this.node.parent;
          mainTorus.position = position;
          this.setMaterial(mainTorus, this.mainMaterial);
          this.setRandomTexture(mainTorus, this.torusTextures);
          Constants.mainTorus = mainTorus.getComponent(Torus);
          Constants.mainTorus.Type = Constants.TORUS_TYPE.MAIN;
        };

        _proto.setRandomTexture = function setRandomTexture(node, textures) {
          var _node$getComponentInC, _mesh$getMaterial;

          var mesh = (_node$getComponentInC = node.getComponentInChildren(MeshRenderer)) != null ? _node$getComponentInC : node.getComponent(MeshRenderer);
          mesh == null ? void 0 : (_mesh$getMaterial = mesh.getMaterial(0)) == null ? void 0 : _mesh$getMaterial.setProperty("mainTexture", textures[Constants.RandomFixedSeed.Rand(textures.length)]); // mesh?.getMaterial(0)?.setProperty("mainTexture", textures[7]);
        };

        _proto.setMaterial = function setMaterial(node, material) {
          var _node$getComponentInC2;

          var mesh = (_node$getComponentInC2 = node.getComponentInChildren(MeshRenderer)) != null ? _node$getComponentInC2 : node.getComponent(MeshRenderer);
          mesh == null ? void 0 : mesh.setMaterial(material, 0);
        };

        return TorusSpawner;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "torusPref", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "opponentMaterial", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "mainMaterial", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor4 = _applyDecoratedDescriptor(_class2.prototype, "torusTextures", [_dec5], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return [];
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/TouchController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _inheritsLoose, cclegacy, _decorator, view, Node, Component, Constants;

  return {
    setters: [function (module) {
      _inheritsLoose = module.inheritsLoose;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      view = module.view;
      Node = module.Node;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _class;

      cclegacy._RF.push({}, "824b510kfVBeryCRRW9bgk8", "TouchController", undefined);

      var ccclass = _decorator.ccclass;
      var TouchController = exports('TouchController', (_dec = ccclass('TouchController'), _dec(_class = /*#__PURE__*/function (_Component) {
        _inheritsLoose(TouchController, _Component);

        function TouchController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;
          _this._screenWidth = void 0;
          _this._canTouch = true;
          _this._touchTimer = 0;
          return _this;
        }

        var _proto = TouchController.prototype;

        _proto.start = function start() {
          this._screenWidth = view.getVisibleSizeInPixel().width;
        };

        _proto.onEnable = function onEnable() {
          this.node.on(Node.EventType.TOUCH_START, this.onTouchStart, this);
        };

        _proto.onDisable = function onDisable() {
          this.node.off(Node.EventType.TOUCH_START, this.onTouchStart, this);
        };

        _proto.onTouchStart = function onTouchStart(touch, event) {
          if (!this._canTouch) return;
          Constants.gameController.node.emit(Constants.GAME_EVENT.START_TOUCH);
          var touchPos = touch.getLocation();

          if (touchPos.x > this._screenWidth / 2) {
            var _Constants$mainTorus;

            (_Constants$mainTorus = Constants.mainTorus) == null ? void 0 : _Constants$mainTorus.Launch(Constants.MOVE_DIR.Right);
          } else {
            var _Constants$mainTorus2;

            (_Constants$mainTorus2 = Constants.mainTorus) == null ? void 0 : _Constants$mainTorus2.Launch(Constants.MOVE_DIR.Left);
          }

          this._canTouch = false;
          this._touchTimer = 0;
        };

        _proto.update = function update(dt) {
          this._touchTimer += dt;

          if (!this._canTouch && this._touchTimer >= Constants.TOUCH_DELAY) {
            this._canTouch = true;
            this._touchTimer = 0;
          }
        };

        return TouchController;
      }(Component)) || _class));

      cclegacy._RF.pop();
    }
  };
});

System.register("chunks:///_virtual/TrainingController.ts", ['./rollupPluginModLoBabelHelpers.js', 'cc', './Constants.ts'], function (exports) {
  'use strict';

  var _applyDecoratedDescriptor, _inheritsLoose, _initializerDefineProperty, _assertThisInitialized, cclegacy, _decorator, Animation, Prefab, instantiate, Vec3, MeshRenderer, Vec4, Quat, Component, Constants;

  return {
    setters: [function (module) {
      _applyDecoratedDescriptor = module.applyDecoratedDescriptor;
      _inheritsLoose = module.inheritsLoose;
      _initializerDefineProperty = module.initializerDefineProperty;
      _assertThisInitialized = module.assertThisInitialized;
    }, function (module) {
      cclegacy = module.cclegacy;
      _decorator = module._decorator;
      Animation = module.Animation;
      Prefab = module.Prefab;
      instantiate = module.instantiate;
      Vec3 = module.Vec3;
      MeshRenderer = module.MeshRenderer;
      Vec4 = module.Vec4;
      Quat = module.Quat;
      Component = module.Component;
    }, function (module) {
      Constants = module.Constants;
    }],
    execute: function () {
      var _dec, _dec2, _dec3, _dec4, _class, _class2, _descriptor, _descriptor2, _descriptor3;

      cclegacy._RF.push({}, "e31b72Fd4lI0YTsCm/CLfIh", "TrainingController", undefined);

      var ccclass = _decorator.ccclass,
          property = _decorator.property;
      var TrainingController = exports('TrainingController', (_dec = ccclass('TrainingController'), _dec2 = property({
        type: Animation
      }), _dec3 = property({
        type: Animation
      }), _dec4 = property({
        type: Prefab
      }), _dec(_class = (_class2 = /*#__PURE__*/function (_Component) {
        _inheritsLoose(TrainingController, _Component);

        function TrainingController() {
          var _this;

          for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
            args[_key] = arguments[_key];
          }

          _this = _Component.call.apply(_Component, [this].concat(args)) || this;

          _initializerDefineProperty(_this, "leftButtonAnim", _descriptor, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "rightButtonAnim", _descriptor2, _assertThisInitialized(_this));

          _initializerDefineProperty(_this, "arrowPref", _descriptor3, _assertThisInitialized(_this));

          _this.currentActiveAnimDir = void 0;
          _this.targetArrow = void 0;
          return _this;
        }

        var _proto = TrainingController.prototype;

        _proto.start = function start() {
          this.targetArrow = instantiate(this.arrowPref);
          this.targetArrow.parent = Constants.target.node;
          this.targetArrow.scale = new Vec3(1, 1, 1);
          this.targetArrow.active = true;
          Constants.gameController.node.on(Constants.GAME_EVENT.START_TOUCH, this.onStartTouch, this);
        };

        _proto.swapAnim = function swapAnim(dir) {
          this.currentActiveAnimDir = dir;

          switch (dir) {
            case Constants.MOVE_DIR.Left:
              this.leftButtonAnim.play("training_button_left");
              this.rightButtonAnim.play("training_button_idle");
              break;

            case Constants.MOVE_DIR.Right:
              this.leftButtonAnim.play("training_button_idle");
              this.rightButtonAnim.play("training_button_right");
              break;

            default:
              console.log("Training dir not found");
              return;
          }
        };

        _proto.onDisable = function onDisable() {
          var _this$targetArrow; // this.leftButtonAnim.node.destroy()
          // this.rightButtonAnim.node.destroy()


          this.leftButtonAnim.node.active = false;
          this.rightButtonAnim.node.active = false;
          (_this$targetArrow = this.targetArrow) == null ? void 0 : _this$targetArrow.destroy();
          Constants.gameController.node.off(Constants.GAME_EVENT.START_TOUCH, this.onStartTouch, this);
        };

        _proto.onStartTouch = function onStartTouch() {};

        _proto.updateButtonsState = function updateButtonsState() {
          var dir = 0;

          if (Constants.mainTorus.node.position.x > Constants.target.node.position.x) {
            dir = Constants.MOVE_DIR.Left;
          } else {
            dir = Constants.MOVE_DIR.Right;
          }

          if (dir !== this.currentActiveAnimDir) this.swapAnim(dir);
        };

        _proto.setTargetArrowAlpha = function setTargetArrowAlpha(alpha) {
          var _this$targetArrow$get, _mesh$getMaterial;

          var mesh = (_this$targetArrow$get = this.targetArrow.getComponentInChildren(MeshRenderer)) != null ? _this$targetArrow$get : this.targetArrow.getComponent(MeshRenderer);
          (_mesh$getMaterial = mesh.getMaterial(0)) == null ? void 0 : _mesh$getMaterial.setProperty('albedo', new Vec4(0, 0, 0, alpha));
        };

        _proto.updateTargetArrow = function updateTargetArrow() {
          if (this.targetArrow === null) return;
          var tempQuat = new Quat();

          if (Constants.mainTorus.node.worldPosition.y > Constants.target.node.position.y) {
            this.targetArrow.lookAt(Constants.mainTorus.node.worldPosition, new Vec3(0, 0, 1));
            Quat.toEuler(tempQuat, this.targetArrow.rotation);
            this.targetArrow.setRotationFromEuler(new Vec3(0, 0, tempQuat.z));
          } else {
            this.targetArrow.lookAt(Constants.mainTorus.node.worldPosition, new Vec3(0, 0, -1));
            Quat.toEuler(tempQuat, this.targetArrow.rotation);
            this.targetArrow.setRotationFromEuler(new Vec3(0, 0, tempQuat.z + 180));
          }

          var distance = Vec3.distance(Constants.mainTorus.node.worldPosition, Constants.target.node.worldPosition);

          if (distance >= 8) {
            this.setTargetArrowAlpha(1);
          } else if (distance <= 2) {
            this.setTargetArrowAlpha(0);
          } else {
            this.setTargetArrowAlpha(distance / 8);
          }
        };

        _proto.update = function update(deltaTime) {
          this.updateButtonsState();
          this.updateTargetArrow();
        };

        return TrainingController;
      }(Component), (_descriptor = _applyDecoratedDescriptor(_class2.prototype, "leftButtonAnim", [_dec2], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor2 = _applyDecoratedDescriptor(_class2.prototype, "rightButtonAnim", [_dec3], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      }), _descriptor3 = _applyDecoratedDescriptor(_class2.prototype, "arrowPref", [_dec4], {
        configurable: true,
        enumerable: true,
        writable: true,
        initializer: function initializer() {
          return null;
        }
      })), _class2)) || _class));

      cclegacy._RF.pop();
    }
  };
});

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
//# sourceMappingURL=index.js.map