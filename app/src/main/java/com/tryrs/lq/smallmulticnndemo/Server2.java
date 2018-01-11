package com.tryrs.lq.smallmulticnndemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class Server2 extends Thread {
    CompareResultInfo compareResultInfo=null;
    Handler handler;
    static Matrix borrowFromClient1;
    //int flag=0;

    Server2(Handler handler){
        this.handler=handler;
    }
    public void run(){
        ServerSocket serverSocket= null;
        try {
            serverSocket = new ServerSocket(6667);
            while (true){
                Socket s = serverSocket.accept();
                Log.i("server2", "accept");
                InputStream inputStream = s.getInputStream();
                ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
                OutputStream outputStream=s.getOutputStream();
                ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);

                NextLayerConfigInfo nextLayerConfigInfo=(NextLayerConfigInfo)objectInputStream.readObject();
                Log.i("server2", nextLayerConfigInfo.h_i+"");
                Message message=Message.obtain();
                message.what=2;
                message.obj=nextLayerConfigInfo;
                handler.sendMessage(message);
                while(compareResultInfo==null){}

                Log.i("from main2",compareResultInfo.toString());
                if(compareResultInfo.borrow==0){
                    objectOutputStream.writeObject(compareResultInfo);
                    objectOutputStream.flush();
                    //wait for data
                    while(borrowFromClient1==null){}
                    objectOutputStream.writeObject(borrowFromClient1);
                    objectOutputStream.flush();
                    borrowFromClient1=null;
                }else{
                    objectOutputStream.writeObject(compareResultInfo);
                    objectOutputStream.flush();
                    Matrix tmp=(Matrix)objectInputStream.readObject();
                    Server1.setborrowFromClient2(tmp);
                }
                compareResultInfo=null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    void setCompareResultInfo(CompareResultInfo compareResultInfo){
        this.compareResultInfo=compareResultInfo;
        Log.i("2 setCompareResultInfo",(this.compareResultInfo==null)+this.compareResultInfo.toString());
        //flag=1;
    }
    public static void setborrowFromClient1(Matrix matrix){
        borrowFromClient1=matrix;
    }
}
