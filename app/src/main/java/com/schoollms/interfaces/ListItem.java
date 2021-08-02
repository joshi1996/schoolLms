package com.schoollms.interfaces;

import com.schoollms.GsonModel.CourseContentList;

public interface ListItem {
    boolean isHeader();
    String getName();
    CourseContentList getData();

}
