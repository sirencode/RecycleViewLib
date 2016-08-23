### 用途:
* 1 继承了recycle的下拉刷新下拉加载,此库可以做到和子view之间的最小耦合.
* 2 添加没用数据或者请求异常的时候的提示库.

### gradle 多模块依赖相同库处理

将公共依赖提取出来,防止依赖多个,或者依赖版本不同导致错误.
#### 1 在项目根目录创建config.gradle

    ext {
    // Android
    android = [
      compileSdkVersion: 24,
      buildToolsVersion: '24.0.1',
      applicationId    : 'com.baidao.ytxmobile',
      minSdkVersion    : 14,
      targetSdkVersion : 24
    ]

    // AllDependencies
    allDependencies = [
      support_recyclerview : 'com.android.support:recyclerview-v7:24.0.0',
      support : 'com.android.support:appcompat-v7:24.1.1',
      progress_switcher : 'com.github.drnkn:progress-switcher:1.1.3@aar'
    ]

    appDps = [
      support_recyclerview : "compile",
      support : 'compile',
      progress_switcher : 'compile'
    ]

    RefreshLoadLayoutDps = [
      support_recyclerview : "compile",
    ]

    dependenciesMethod = { dps, closure ->
    dps.each { entry ->
      def value = entry.value
      if (value instanceof List) {
        value.each { result ->
          closure result, allDependencies.get(entry.key), entry.key
        }
      } else {
        closure value, allDependencies.get(entry.key), entry.key
      }
    }
    }

    }


#### 2 添加config依赖
在项目的build.gralde文件头添加

    apply from: "config.gradle"


#### 3 在模块里面添加依赖

在对应模块dependencies{
}里添加依赖

    rootProject.dependenciesMethod rootProject.ext.RefreshLoadLayoutDps, { type, library, key ->
    if (type == 'compile') {
      compile library
    } else if (type == 'debugCompile') {
      debugCompile library
    } else if (type == 'testCompile') {
      testCompile library
    } else if (type == 'apt') {
      apt library
    } else if (type == 'debuggableReleaseCompile') {
      debuggableReleaseCompile library
    } else if (type == 'debugProCompile') {
      debugProCompile library
    }
    }