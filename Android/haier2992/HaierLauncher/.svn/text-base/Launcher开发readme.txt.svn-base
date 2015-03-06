开发过程中的经验和总结，请往后添加：

1.将launcher的源码导入Eclipse时会报出很多错误，需要add library:android-common,android-core,android-framework,android-support-v13;

2.add library步骤：右键->Build Path->Add Librarys->User Library->User libraries->New...;
  a.ics_537312\out\target\common\obj\JAVA_LIBRARIES\android-common_intermediates
  b.ics_537312\out\target\common\obj\JAVA_LIBRARIES\core_intermediates
  c.ics_537312\out\target\common\obj\JAVA_LIBRARIES\framework_intermediates
  d.ics_537312\out\target\common\obj\JAVA_LIBRARIES\android-support-v13_intermediates

3.launcher的编译在ics的工程环境下使用mm命令编译，不要使用ecplipse编译，否则会出错；

4.开发launcher必须重命名，否则就会和系统自带的apk重名无法安装；安装时建议使用adb install Launcher.apk;

5.launcher包重命名步骤：
  a.com.android.launcher2右键->Refactor->Rename...->四个都选打钩，File name patterns:*.xml
  b.根据实际需要手动修改AndroidManifest.xml文件；
  c.根据实际需要手动修改/res/layout*/*.xml中的com.android.launcher2.xxx；
  d.根据实际需要手动修改/res/layout*/*.xml中的xmlns:launcher="http://schemas.android.com/apk/res/com.android.launcher"
  e.根据实际需要手动修改java代码里面的import com.android.launcher.R;

6.查看launcher运行过程中的打印输出：logcat -s Launcher:D Launcher.workspace:D;

7.在安装widget时报错，说权限不够：
java.lang.SecurityException: bindGagetId appWidgetId=8 provider=ComponentInfo{com.android.deskclock/com.android.alarmclock.AnalogAppWidgetProvider}: User 10044 does not have android.permission.BIND_APPWIDGET.
必须要将你的Launcher2.apk包 push到/System/app下面就行了;

   
   