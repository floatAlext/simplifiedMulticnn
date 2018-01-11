package com.tryrs.lq.smallmulticnndemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;


/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class WifiP2pBroadcastReceiver extends BroadcastReceiver {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    Context context;
    WifiP2pManager.PeerListListener peerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    WifiP2pBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener, WifiP2pManager.ConnectionInfoListener connectionInfoListener,Context context){
        this.manager=manager;
        this.channel=channel;
        this.peerListListener=peerListListener;
        this.connectionInfoListener=connectionInfoListener;
        this.context=context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals(manager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)){
            int state=intent.getIntExtra(manager.EXTRA_DISCOVERY_STATE,-1);
            if(state==manager.WIFI_P2P_DISCOVERY_STARTED){
                Toast.makeText(context,"discovery started!",Toast.LENGTH_SHORT).show();

            }else if(state==manager.WIFI_P2P_DISCOVERY_STOPPED){
                Toast.makeText(context,"discovery stopped!",Toast.LENGTH_SHORT).show();
            }
        }

        if(action.equals(manager.WIFI_P2P_PEERS_CHANGED_ACTION)){
            manager.requestPeers(channel,peerListListener);
        }

        if(action.equals(manager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
            NetworkInfo networkInfo=intent.getParcelableExtra(manager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                Toast.makeText(context,"connected successfully!",Toast.LENGTH_SHORT).show();
                manager.requestConnectionInfo(channel,connectionInfoListener);
            }else{
                Toast.makeText(context,"connection failed!",Toast.LENGTH_SHORT).show();
            }
        }

        if(action.equals(manager.WIFI_P2P_STATE_CHANGED_ACTION)){
            int state=intent.getIntExtra(manager.EXTRA_WIFI_STATE,-1);
            if(state==manager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"state enabled!",Toast.LENGTH_SHORT).show();
            }else if(state==manager.WIFI_P2P_STATE_DISABLED){
                Toast.makeText(context,"state disabled!",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
