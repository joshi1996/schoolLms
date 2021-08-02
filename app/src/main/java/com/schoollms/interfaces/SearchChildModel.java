package com.schoollms.interfaces;

import com.schoollms.GsonModel.CourseContentList;
import com.schoollms.GsonModel.TopicModel.TopicDatum;

public class SearchChildModel implements ListItem1 {

    CourseContentList child;

    public void setChild(CourseContentList child) {
        this.child = child;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public TopicDatum getHeaderData() {
        return null;
    }


    @Override
    public CourseContentList getChildData() {
        return child;
    }
}
