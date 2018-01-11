package com.tryrs.lq.smallmulticnndemo;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    String GoAddress;
    BroadcastReceiver mReceiver;
    IntentFilter intentFilter;
    LinkedList<String> names=new LinkedList<>();
    LinkedList<String> address=new LinkedList<>();
    MyAdapter myAdapter;
    ListView lv;
    MainHandler1 mainHandler1=new MainHandler1();
    MainHandler2 mainHandler2=new MainHandler2();
    NextLayerConfigInfo nextLayerConfigInfo1=null;
    NextLayerConfigInfo nextLayerConfigInfo2=null;
    CompareResultInfo compareResultInfo1=null;
    CompareResultInfo compareResultInfo2=null;
    Server1 server1;
    Server2 server2;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiate();
        manager=(WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel=manager.initialize(this,getMainLooper(),null);
        mReceiver=new WifiP2pBroadcastReceiver(manager,channel,new MyPeerListListener(),new MyConnectionInfoListener(),this);
        intentFilter=new IntentFilter();
        addActions();
        initListView();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    public void addActions(){
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
    }
    public void initiate(){
        Button btDiscover=(Button)findViewById(R.id.bt_discover);
        btDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this,"discovery succeed",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this,"discovery failed",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        Button btSend=(Button)findViewById(R.id.bt_sendMatrix);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ClientSendMatrixIntentService.class);
                intent.putExtra("GoAddress",GoAddress);
                startService(intent);

            }
        });
    }
    public void initListView(){
        lv=(ListView)findViewById(R.id.lv_showPeers);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("connect");
                builder.setMessage("please connect to this service before sending picture.");
                String name=names.get(position);
                final String addr=address.get(position);
                //不是一開application就自動connect了，而是一次connect之後，後面始終是connected的狀態
                builder.setPositiveButton("connect", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiP2pConfig wifiP2pConfig=new WifiP2pConfig();
                        wifiP2pConfig.deviceAddress=addr;
                        wifiP2pConfig.groupOwnerIntent=15;
                        //下面這句話很重要，connect不再阻塞
                        wifiP2pConfig.wps.setup = WpsInfo.PBC;
                        //每点击一次按钮，就会连接一次，可以重复连接
                        manager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("liuqi connect","connection succeed");

                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("liuqi connect","connection failed");
                            }
                        });

                    }
                });

                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //按了键之后会自动dismiss
                builder.show();


            }
        });


    }
    public class MainHandler1 extends Handler{
        //当第一个消息来得时候，被触发，但是如果在执行这个函数的时候碰到死循环，那么将不会对第二个消息做出响应
        public void handleMessage(Message message){

            if(message.what==1){
                nextLayerConfigInfo1=(NextLayerConfigInfo)message.obj;
                Log.i("receive","1");

            }

            if(nextLayerConfigInfo1!=null && nextLayerConfigInfo2!=null) {
                Toast.makeText(MainActivity.this, "have received data from 1 and 2", Toast.LENGTH_SHORT).show();
                compareResultInfo1 = new CompareResultInfo();
                compareResultInfo2 = new CompareResultInfo();
                int h_i_1 = nextLayerConfigInfo1.h_i, h_i_2 = nextLayerConfigInfo2.h_i;
                int k = nextLayerConfigInfo1.k, p = nextLayerConfigInfo1.p, s = nextLayerConfigInfo1.s;
                if (h_i_1 <= h_i_2) {
                    int tmp = h_i_1 % s;
                    int borrow;
                    if (tmp == 0) {
                        borrow = k - s;
                        compareResultInfo2.setBorrow(borrow);
                        compareResultInfo2.setReserve((h_i_2 + p - k) / s + 1);
                    } else {
                        borrow = k - (h_i_1 + p) % s;
                        compareResultInfo2.setBorrow(borrow);
                        compareResultInfo2.setReserve((h_i_2 - (s - tmp) + p - k) / s + 1);
                    }
                    compareResultInfo1.setReserve((p + h_i_1 + borrow - k) / s + 1);
                    compareResultInfo1.setBorrow(0);

                } else {//h_i_1>h_i_2
                    int tmp = h_i_2 % s;
                    int borrow;
                    if (tmp == 0) {
                        borrow = k - s;
                        compareResultInfo1.setBorrow(borrow);
                        compareResultInfo1.setReserve((h_i_1 + p - k) / s + 1);
                    } else {
                        borrow = k - (h_i_2 + p) % s;
                        compareResultInfo1.setBorrow(borrow);
                        compareResultInfo1.setReserve((h_i_1 - (s - tmp) + p - k) / s + 1);
                    }
                    compareResultInfo2.setReserve((p + h_i_2 + borrow - k) / s + 1);
                    compareResultInfo2.setBorrow(0);

                }
                Log.i("main", compareResultInfo1.toString() + "." + compareResultInfo2.toString());
                server1.setCompareResultInfo(compareResultInfo1);
                server2.setCompareResultInfo(compareResultInfo2);
                compareResultInfo1=null;compareResultInfo2=null;
                nextLayerConfigInfo1=null;nextLayerConfigInfo2=null;
            }
        }
    }
    //handler1中有死循环的话会阻塞主线程导致handler2不响应，也就是说handler并不是一个独立的线程，甚至可以把它看成一个普通的函数
    public class MainHandler2 extends Handler{
        public void handleMessage(Message message){
            if(message.what==2){
                nextLayerConfigInfo2=(NextLayerConfigInfo)message.obj;
                Log.i("receive","2");
            }
            if(nextLayerConfigInfo1!=null && nextLayerConfigInfo2!=null) {
                Toast.makeText(MainActivity.this, "have received data from 1 and 2", Toast.LENGTH_SHORT).show();
                compareResultInfo1 = new CompareResultInfo();
                compareResultInfo2 = new CompareResultInfo();
                int h_i_1 = nextLayerConfigInfo1.h_i, h_i_2 = nextLayerConfigInfo2.h_i;
                int k = nextLayerConfigInfo1.k, p = nextLayerConfigInfo1.p, s = nextLayerConfigInfo1.s;
                if (h_i_1 <= h_i_2) {
                    int tmp = h_i_1 % s;
                    int borrow;
                    if (tmp == 0) {
                        borrow = k - s;
                        compareResultInfo2.setBorrow(borrow);
                        compareResultInfo2.setReserve((h_i_2 + p - k) / s + 1);
                    } else {
                        borrow = k - (h_i_1 + p) % s;
                        compareResultInfo2.setBorrow(borrow);
                        compareResultInfo2.setReserve((h_i_2 - (s - tmp) + p - k) / s + 1);
                    }
                    compareResultInfo1.setReserve((p + h_i_1 + borrow - k) / s + 1);
                    compareResultInfo1.setBorrow(0);

                } else {//h_i_1>h_i_2
                    int tmp = h_i_2 % s;
                    int borrow;
                    if (tmp == 0) {
                        borrow = k - s;
                        compareResultInfo1.setBorrow(borrow);
                        compareResultInfo1.setReserve((h_i_1 + p - k) / s + 1);
                    } else {
                        borrow = k - (h_i_2 + p) % s;
                        compareResultInfo1.setBorrow(borrow);
                        compareResultInfo1.setReserve((h_i_1 - (s - tmp) + p - k) / s + 1);
                    }
                    compareResultInfo2.setReserve((p + h_i_2 + borrow - k) / s + 1);
                    compareResultInfo2.setBorrow(0);

                }
                Log.i("main", compareResultInfo1.toString() + "." + compareResultInfo2.toString());
                server1.setCompareResultInfo(compareResultInfo1);
                server2.setCompareResultInfo(compareResultInfo2);
                compareResultInfo1=null;compareResultInfo2=null;
                nextLayerConfigInfo1=null;nextLayerConfigInfo2=null;
            }

        }
    }

    public class MyPeerListListener implements WifiP2pManager.PeerListListener {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Collection<WifiP2pDevice> devices=peers.getDeviceList();
            Iterator<WifiP2pDevice> iterator=devices.iterator();
            names.clear();
            address.clear();
            while (iterator.hasNext()){
                WifiP2pDevice device=iterator.next();
                names.add(device.deviceName);
                address.add(device.deviceAddress);
            }
            myAdapter=new MyAdapter(names,address,getApplicationContext());
            lv.setAdapter(myAdapter);
        }
    }

    public class MyConnectionInfoListener implements WifiP2pManager.ConnectionInfoListener{

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            GoAddress=info.groupOwnerAddress.getHostAddress();
            Log.d("GO",info.isGroupOwner+"");
            if(info.groupFormed && info.isGroupOwner){

                //Intent intent1=new Intent(MainActivity.this,GOReceiveMatrixService.class);
                //Intent intent2=new Intent(MainActivity.this,GOReceiveMatrixService2.class);

//                startService(intent1);
//                startService(intent2);
                //bindService(intent1,myconn,BIND_AUTO_CREATE);
                //bindService(intent2,myconn,BIND_AUTO_CREATE);
                if(flag==0) {//如果没有这句话，会发生很奇怪的现象，即主线程无法终止其他线程的死循环，但是经过eclipse测试一个简单的demo，设置flag是可以终止死循环的
                    server1 = new Server1(mainHandler1);
                    server2 = new Server2(mainHandler2);
                    server1.start();
                    server2.start();
                    Log.i("flag",""+flag);
                    flag++;
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
