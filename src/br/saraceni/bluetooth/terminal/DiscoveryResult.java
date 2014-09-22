package br.saraceni.bluetooth.terminal;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.Intent;
import android.bluetooth.*;
import java.util.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.util.*;

public class DiscoveryResult extends ListActivity {
	
	private final String CATEGORIA = "CarrinhoBluetooth";
	private ArrayList<BluetoothDevice> btDeviceList;
	private boolean itemSelected = false;
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		Log.i(CATEGORIA, "Entrou no onCreate() da DiscoveryResult");
		Intent intent = getIntent();
		if(intent != null){
			Log.i(CATEGORIA, "Obteve a Intent!");
			btDeviceList = (ArrayList<BluetoothDevice>) intent.getSerializableExtra("btDevices");
			String[] itens = new String[btDeviceList.size()];
			for(int i = 0; i < btDeviceList.size(); i++){
				itens[i] = btDeviceList.get(i).getName();
			}
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itens);
			setListAdapter(arrayAdapter);
			Log.i(CATEGORIA, "Terminou a leitura dos Bluetooth Devices e criou o ListAdapter");
		}
		else{
			Log.i(CATEGORIA, "Entrou na condicao intent = null - Discovery");
			finish();
		}
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		//Pega o item  daquela posicao
		itemSelected = true;
		Intent it = new Intent();
		setResult(position, it);
		Log.i(CATEGORIA, "Discovery: Item selecionado " + position);
		finish();
	}
	
	
	@Override
	public void onBackPressed()
	{
		Log.i(CATEGORIA, "onBackPressed() - Discovery");
		Intent it = new Intent();
		setResult(-1, it);
		finish();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(CATEGORIA, "onDestroy() - Discovey");
	}
}












