package com.jzy.plugin.amapplugin.navi;

import com.jzy.plugin.amapplugin.BuildConfig;

public class SimpleOptions {
    private static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;
    /**
     * 状态栏颜色
     * ToolBar颜色
     * ToolBar标题
     */
    public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
    public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
    public static final String EXTRA_TOOL_BAR_WIDGET_COLOR = EXTRA_PREFIX + ".ToolbarTitleColor";
    public static final String EXTRA_TOOL_BAR_TITLE = EXTRA_PREFIX + ".ToolbarTitle";
    public static final String EXTRA_TOOL_BAR_DRAWABLE = EXTRA_PREFIX + ".ToolbarDrawable";
    public static final String EXTRA_MARKERS = EXTRA_PREFIX + ".Markers";


}
