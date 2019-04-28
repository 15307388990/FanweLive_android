package com.fanwe.xianrou.common;

import com.tencent.lbssearch.object.result.SuggestionResultObject;

import java.util.List;

/**
 * Created by Administrator on 2017/4/4 0004.
 */

public class XRAppRumTimeData
{
    private static XRAppRumTimeData instance;

    public List<SuggestionResultObject.SuggestionData> mListModel;

    public static XRAppRumTimeData getInstance()
    {
        if (instance == null)
        {
            instance = new XRAppRumTimeData();
        }
        return instance;
    }


}
