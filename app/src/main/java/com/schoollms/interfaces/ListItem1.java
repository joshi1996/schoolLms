package com.schoollms.interfaces;


import com.schoollms.GsonModel.CourseContentList;
import com.schoollms.GsonModel.TopicModel.TopicDatum;

public interface ListItem1 {
    boolean isHeader();
    TopicDatum getHeaderData();
    CourseContentList getChildData();

}
