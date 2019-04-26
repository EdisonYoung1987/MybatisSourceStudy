package com.edison.entity.extend;

import java.util.List;

/**专门用于foreach查询时，可以传入不同类型的list，不过局限性较大，如果list数量增加，还得加个
 * 类或者修改此类*/
public class Qry {
    List<Integer> intList;
    List<String>  stringList;

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
