package com.schoollms.interfaces;

import com.schoollms.GsonModel.CourseContentList;
import com.schoollms.GsonModel.TopicModel.TopicDatum;

public class SearchHeaderModel implements ListItem1 {

    TopicDatum header;

    public void setheader(TopicDatum header) {
        this.header = header;
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public TopicDatum getHeaderData() {
        return header;
    }

    @Override
    public CourseContentList getChildData() {
        return null;
    }


}
