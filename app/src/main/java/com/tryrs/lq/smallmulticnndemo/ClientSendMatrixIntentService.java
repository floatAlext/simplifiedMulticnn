package com.tryrs.lq.smallmulticnndemo;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class ClientSendMatrixIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public ClientSendMatrixIntentService(String name) {
        super(name);
    }
    public ClientSendMatrixIntentService() {
        super("");
    }
    CompareResultInfo compareResultInfo;
    Matrix borrowData;
    float[][][] data1={{{(float) 1.1,(float) 1.2,(float) 1.3,(float) 1.4,(float) 1.5},
                        {(float) 2.1,(float) 2.2,(float) 2.3,(float) 2.4,(float) 2.5},
                        {(float) 3.1,(float) 3.2,(float) 3.3,(float) 3.4,(float) 3.5},
                        {(float) 4.1,(float) 4.2,(float) 4.3,(float) 4.4,(float) 4.5},
                        {(float) 5.1,(float) 5.2,(float) 5.3,(float) 5.4,(float) 5.5}}};
    float[][][] data2={{{(float) 6.1,(float) 6.2,(float) 6.3,(float) 6.4,(float) 6.5},
            {(float) 7.1,(float) 7.2,(float) 7.3,(float) 7.4,(float) 7.5},
            {(float) 8.1,(float) 8.2,(float) 8.3,(float) 8.4,(float) 8.5},
            {(float) 9.1,(float) 9.2,(float) 9.3,(float) 9.4,(float) 9.5},
            {(float) 11.1,(float) 11.2,(float) 11.3,(float) 11.4,(float) 11.5}}};
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String GoAddress=intent.getStringExtra("GoAddress");
        Socket socket=new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(GoAddress,6666),5000);
            OutputStream outputStream=socket.getOutputStream();
            //outputStream.write("lalala".getBytes());
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            InputStream inputStream=socket.getInputStream();
            ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
            Log.i("connected","lalala");
            NextLayerConfigInfo nextLayerConfigInfo=new NextLayerConfigInfo(5,3,1,1);
            objectOutputStream.writeObject(nextLayerConfigInfo);
            objectOutputStream.flush();
            Log.i("aaa","ccc");
            //while(compareResultInfo==null){}
            //read会阻塞，等待可读数据到达
            compareResultInfo=(CompareResultInfo)objectInputStream.readObject();
            if(compareResultInfo.borrow==0){//别人不借我的数据，换句话说就是我要借别人的数据，等待
                Matrix borrowData=(Matrix) objectInputStream.readObject();
                //之后concat，这里简化，只打印数据
                Log.i("1 borrow data from 2:",borrowData.toString());
            }else{
                float[][][] tmp=new float[data1.length][compareResultInfo.borrow][data1[0][0].length];
                copyData(tmp,data1);
                Matrix beBorrowedData=new Matrix(tmp);
                objectOutputStream.writeObject(beBorrowedData);
                objectOutputStream.flush();//切割工作完成，下一层计算开始

            }
            int reserve=compareResultInfo.reserve;//下一层计算的结果需要保留的
            Log.i("c1 next layer reserve:",reserve+"");
            //socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void copyData(float[][][] tmp, float[][][] data1) {
        for(int i=0;i<data1.length;i++){
            for(int j=0;j<tmp[0].length;j++){
                for(int k=0;k<tmp[0][0].length;k++){
                    tmp[i][j][k]=data1[i][j][k];
                }
            }
        }
    }
}
