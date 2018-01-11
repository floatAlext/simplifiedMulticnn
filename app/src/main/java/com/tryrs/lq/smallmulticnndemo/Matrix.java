package com.tryrs.lq.smallmulticnndemo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class Matrix implements Serializable {
    float[][][] a;

    public Matrix() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Matrix(float[][][] a) {
        super();
        this.a = a;
    }

    @Override
    public String toString() {
        String result="";
        for(int i=0;i<a.length;i++) {
            for(int j=0;j<a[0].length;j++) {
                result+= Arrays.toString(a[i][j]);
            }
        }
        return result;
    }
}