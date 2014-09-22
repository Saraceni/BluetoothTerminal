package br.saraceni.bluetooth.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.*;
import android.app.ProgressDialog;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

public class BluetoothHandler implements Runnable {
	private static final String TAG = "BluetoothHandler";
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private static Context context;
	public static Handler handler;
	public final int MESSAGE_TAG = 36;
	
	/* ------------------------------- Construtor ------------------------------------  */
	
	public BluetoothHandler(Context context, BluetoothAdapter bluetoothAdapter, Handler nuHandler){
		this.context = context;
		this.bluetoothAdapter = bluetoothAdapter;
		handler = nuHandler;
	}
	
	/* ------------------------------ Faz A Conexao ----------------------------------- */
	
	public final void connectToServerSocket(BluetoothDevice device) {
    	try{
    		Log.i(TAG, "Vai tentar obter o socket!");
    		Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
    	    bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
    		Log.i(TAG, "Vai tentar conectar!");
    		bluetoothSocket.connect();
    		outputStream = bluetoothSocket.getOutputStream();
    		inputStream = bluetoothSocket.getInputStream();
    		Log.i(TAG, "Vai iniciar a Thread!!!");
    		new Thread(this).start();
    	}
    	catch(IOException ex){
    		Toast.makeText(context, "Nao foi possivel obter a conexao!", Toast.LENGTH_SHORT).show();
    		disconnect();
    	}
    	catch(NoSuchMethodException NSME){
    		Log.i(TAG, "Erro na funcao do metodo ao tentar obter o createRfcommSocket");
    	}
    	catch(InvocationTargetException ITE){
    		Log.i(TAG, "Erro no m.invoke(device, 1) InvocationTargetException");
    	}
    	catch(IllegalAccessException IAE){
    		Log.i(TAG, "Erro no m.invoke(device, 1) IllegalAccessException");
    	}
    }
	
	public final void connectInsecureToServerSocket(BluetoothDevice device) {
    	try{
    		Log.i(TAG, "Vai tentar obter o socket!");
    		Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
    	    bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
    		Log.i(TAG, "Vai tentar conectar!");
    		bluetoothSocket.connect();
    		outputStream = bluetoothSocket.getOutputStream();
    		inputStream = bluetoothSocket.getInputStream();
    		Log.i(TAG, "Vai iniciar a Thread!!!");
    		new Thread(this).start();
    	}
    	catch(IOException ex){
    		Toast.makeText(context, "Nao foi possivel obter a conexao!", Toast.LENGTH_SHORT).show();
    		disconnect();
    	}
    	catch(NoSuchMethodException NSME){
    		Log.i(TAG, "Erro na funcao do metodo ao tentar obter o createRfcommSocket");
    	}
    	catch(InvocationTargetException ITE){
    		Log.i(TAG, "Erro no m.invoke(device, 1) InvocationTargetException");
    	}
    	catch(IllegalAccessException IAE){
    		Log.i(TAG, "Erro no m.invoke(device, 1) IllegalAccessException");
    	}
    }
	
	/* ----------------------- Envia Dados Para Dispositivo Remoto -----------------  */
	
	public final void sendMessage(byte[] message){
    	if(bluetoothSocket != null && outputStream != null){
    		try{
    			Log.i(TAG, "Tentativa de envio de mensagem!");
    			outputStream.write(message);
    		}
    		catch(IOException IOE){
    			IOE.printStackTrace();
    			Log.i(TAG, IOE.getMessage());
    			Log.i(TAG, "Falha ao tentar enviar mensagem!");
    			disconnect();
    		}
    	}
    	else{
    		Toast.makeText(context, "Nao ha conexao com dispositivo remoto!", Toast.LENGTH_SHORT).show();
    	}
    }
	
	public final void sendMessage(String message){
		if(bluetoothSocket != null && outputStream != null){
			try{
				byte[] bytes = message.getBytes();
				Log.i(TAG, "Tentativa de envio de mensagem!");
				outputStream.write(bytes);
			}
			catch(IOException IOE){
				IOE.printStackTrace();
				Log.i(TAG, IOE.getMessage());
				Log.i(TAG, "Falha ao tentar enviar mensgaem!");
				disconnect();
			}
		}
	}
	
	/* ---------------------------------- Desconecta ----------------------------------- */
	
	public final void disconnect(){
    	Log.i(TAG, "Vai tentar desconectar!");
    	if(bluetoothSocket != null){
    		try{
    			bluetoothSocket.close();
    			outputStream.close();
    			inputStream.close();
    		}
    		catch(IOException IOE){
    			IOE.printStackTrace();
    			Log.i(TAG, IOE.getMessage());
    			Log.i(TAG, "Nao conseguiu desconectar!!!");
    		}
    		finally{
    			inputStream = null;
    			outputStream = null;
    			bluetoothSocket = null;
    		}
    	}
    }
	
	/* -------------------- Inicia Busca por Dispositivos Remotos ---------------------- */
	
	public final boolean startDiscovery(){
		if(bluetoothAdapter.isEnabled() && !bluetoothAdapter.isDiscovering())
		{
			Log.i(TAG, "Vai iniciar o bluetoothAdapter.startDiscovery");
			bluetoothAdapter.startDiscovery();
			return true;
		}
		return false;		
	}
	
	/* --------------- Thread que Recebe e Administra dados Recebidos ---------------- */
	
	public void run(){
		Log.i(TAG, "Thread esta rodando!!!");
		byte[] buffer = new byte[1024];
		int length;
		while( inputStream != null){
			try{
				//ByteArrayOutputStream baos = new ByteArrayOutputStream();
				length = inputStream.read(buffer);
				Log.i(TAG, "Terminou de ler o buffer.");
				Log.i(TAG, "byte lido: " + length);
				handler.obtainMessage(MESSAGE_TAG, length, -1, buffer).sendToTarget();
			}
			catch(IOException ioe){
				ioe.printStackTrace();
				Log.i(TAG, ioe.getMessage());
				break;
			}
		}
		Log.i(TAG, "Terminou a Thread do BluetoothHandler!");
	}

}
















