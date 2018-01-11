package com.tryrs.lq.smallmulticnndemo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class CompareResultInfo implements Serializable {
    int borrow=0;
    int reserve=-1;
    public CompareResultInfo(){

    }
    public CompareResultInfo(int x,int reserve){
        this.borrow=x;
        this.reserve=reserve;

    }
    public void setBorrow(int x){
        this.borrow=x;
    }
    public void setReserve(int x){
        this.reserve=x;
    }
    @Override
    public String toString() {
        String result="";

        return result+borrow+","+reserve;
    }
}
