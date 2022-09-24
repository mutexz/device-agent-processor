package com.wind.flow.agent.mtpagentrefactor.common.constant;

/**
 * @author wunanfang
 * 应用内部常量
 */
public class AppConstants {

    public static final String SWITCH_ENV_CMD = "am start -n air.tv.douyu.android/tv.douyu.view.activity.launcher.DYLauncherActivity --ei play_in_bg 0  --ei float_player 0 --ei auto_jump_room 0 --ei gee_test 0 --ei leak_switch 0 --ei shield_all 1 --el control_toggle_panel_time 60000";

    public static final String APP_COLD_START = "am instrument -e class  com.example.douyu.stabilitypage.testcase.ExampleInstrumentedTest#StartAPP  -w com.example.douyu.stabilitypage.test/android.support.test.runner.AndroidJUnitRunner";

    public static final String APP_COLD_START_LOG = "logcat |grep ActivityManager |grep Displayed";

    public static final String ROOM_START_LOG = "logcat |grep ac:rml_fs_c";

    public static final String INTO_PLAYER_TENCENT = "am instrument -e class  com.example.douyu.stabilitypage.testcase.PlayerPageTest#Test11jueDiQiuShengListIntoRoom  -w com.example.douyu.stabilitypage.test/android.support.test.runner.AndroidJUnitRunner";

    public static final String INTO_MOBILE_PLAYER = "am instrument -e class  com.example.douyu.stabilitypage.testcase.MobilePlayerPageTest#Test01GoToMobilePlayerRoom  -w com.example.douyu.stabilitypage.test/android.support.test.runner.AndroidJUnitRunner";

}
