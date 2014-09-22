package br.saraceni.bluetooth.terminal;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

public class BluetoothActivityExtension extends Activity {
	
	public static BluetoothHandler bluetoothHandler;
	public static ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
	public static final String TAG = "TerminalBluetooth";
	private static final int SELECIONAR_DEVICE = 69;
	private ProgressDialog progressDialog;
	public static final int MESSAGE_TAG = 36;
	
	/* -------------------------------- onCreate --------------------------------- */
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothHandler = new BluetoothHandler(this, bluetoothAdapter, new mmHandler());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Bluetooth");
        progressDialog.setMessage("Procurando dispositivos bluetooth...");
        progressDialog.setIcon(android.R.drawable.stat_sys_data_bluetooth);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
		registerReceivers();
	}
	
	/* ------------------------ Declaracao dos Broadcasts Receivers -------------------- */
	
	private final BroadcastReceiver discoveryResult = new BroadcastReceiver(){
    	@Override
    	public void onReceive(Context context, Intent intent){
    		String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
    		BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    		deviceList.add(remoteDevice);
    		Log.i(TAG, remoteDeviceName);
    	}
    };
    
    private final BroadcastReceiver finishDiscovery = new BroadcastReceiver(){
    	//BluetoothAdapter.ACTION_DISCOVERY_FINISHED
    	@Override
    	public void onReceive(Context context, Intent intent)
    	{
    		progressDialog.dismiss();
    		Log.i(TAG, "Entrou no finishDiscovery");
    		if(deviceList.isEmpty())
    		{
    			Toast.makeText(BluetoothActivityExtension.this, "Nenhum dispositivo foi encontrado!", Toast.LENGTH_SHORT).show();
    			Log.i(TAG, "deviceList.isEmpty()");
    		}
    		else{
    			Intent it = new Intent(BluetoothActivityExtension.this, DiscoveryResult.class);
    			Log.i(TAG, "Intent foi criada para chamar a activity DiscoveryResult");
    			it.putParcelableArrayListExtra("btDevices", deviceList);
    			Log.i(TAG, "Dispositivos Bluetooth anexados a Intent, vai chamar a Intent");
    			startActivityForResult(it, SELECIONAR_DEVICE);
    		}
    	}
    };
    
    /* -------------------------- Pede para o SO ligar o Bluetooth ---------------------- */
    
    private void enableBluetooth(){
    	startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
    }
    
    /* ------------------------- Ativa os Broadcast Receivers ---------------------- */
    
    private void registerReceivers(){
    	registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(finishDiscovery, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }
    
    /* ----------------------------- Desavtiva Broadcast Receivers ---------------------- */
    
    private void unregisterReceivers(){
    	unregisterReceiver(discoveryResult);
    	unregisterReceiver(finishDiscovery);
    }
    
    /* ---------------------- Metodos do Ciclo de Vida da Activity --------------------- */
    
    @Override
    public void onStop(){
    	super.onStop();
    	unregisterReceivers();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	registerReceivers();
    }
    
    /* --------------------------- Pesquisa Dispositivos Remotos ----------------------- */
    
    public final void pesquisarDispositivos(){
    	Log.i(TAG, "Iniciada a pesquisa de dispositivos!");
    	deviceList.clear();
    	progressDialog.show();
    	if(!bluetoothHandler.startDiscovery()){
    		Log.i(TAG, "Bluetooth Desativado!!!");
    		progressDialog.dismiss();
    		enableBluetooth();
    	}
    }
    
    /* ------------------------ Metodos Básicos da Comunicacao Bluetooth ------------- */
    
    public final void desconectar(){
    	bluetoothHandler.disconnect();
    }
    
    public final void conectarAUmDispositivo(BluetoothDevice btDevice){
    	bluetoothHandler.connectToServerSocket(btDevice);
    }
    
    public final void sendMessage(String str){
    	bluetoothHandler.sendMessage(str);
    }
    
    public final void sendMessage(byte[] msg){
    	bluetoothHandler.sendMessage(msg);
    }
    
    /* --------------------------------- onActivityResult ------------------------------- */
    
    protected void onActivityResult(int codigo, int resultado, Intent intent){
    	switch(codigo){
    	case SELECIONAR_DEVICE:
    		if(resultado > -1)
    		{
    			Log.i(TAG, "Resultado: " + resultado);
    			bluetoothHandler.connectToServerSocket(deviceList.get(resultado));
    		}
    		break;
    		default:
    			break;
    	}
    }
    
    /* ---------------------- Handler Para comunicar com a Thread ---------------------- */
    
    public class mmHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg){
    		Log.i(TAG, "Entrou no handleMessage da main!");
    		switch(msg.what){
    		case MESSAGE_TAG:
    			Log.i(TAG, "Entrou no case MESSAGE_TAG");
    			byte[] readBuffer = (byte[]) msg.obj;
    			String readMessage = new String(readBuffer, 0, msg.arg1);
    			onRecebeMsg(readMessage);
    		}
    	}
    }
    
    /* --------------- Método que dever ser sobescrito para receber mensagem ----------- */
    
    public void onRecebeMsg(String msg){
    	Log.i(TAG, "Entrou na onRecebeMsg da BluetoothActivityExtension");
    }
   
}
