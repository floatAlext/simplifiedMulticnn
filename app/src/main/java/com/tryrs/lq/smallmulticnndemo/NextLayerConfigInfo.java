package com.tryrs.lq.smallmulticnndemo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class NextLayerConfigInfo implements Serializable {
    int h_i, k, p, s;
    NextLayerConfigInfo(int h_i,int k,int p,int s){
        this.h_i=h_i;
        this.k=k;
        this.p=p;
        this.s=s;
    }
}
