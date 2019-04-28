package com.fanwe.live.model;

import com.fanwe.library.common.SDSelectManager;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/15.
 */

public class HomeTabTitleModel implements SDSelectManager.Selectable, Serializable
{
    static final long serialVersionUID = 0;

    private int classified_id;
    private String title;
    /**
     * 分类类型，0 直播分类，1 webview分类podcast_id=164865&to_user_id=164823&room_id=20844
     */
    private Integer type;

    /**

     * 当type=1时有值，url链接
     */
    private String url;
    //
    private boolean selected;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getClassified_id()
    {
        return classified_id;
    }

    public void setClassified_id(int classified_id)
    {
        this.classified_id = classified_id;
    }

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj)
    {
        return ((HomeTabTitleModel) obj) .getClassified_id() == classified_id;
    }

    @Override
    public String toString()
    {
        return title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
