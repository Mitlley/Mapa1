package activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talleres.android.mapa.R;

import datos.BaseDatosHelper;

public class Mapa extends FragmentActivity {
	
	GoogleMap googleMap;
	
	private SQLiteDatabase lectura;
	private Handler handler;
	private Button btnCambiar;
	
	private int[] tiposMapas = new int[]{GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN};
	private int tipo = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		Log.e("CICLO DE VIDA", "CREATE");
		handler = new Handler();
		
		btnCambiar = (Button) findViewById(R.id.btnCambiar);
		
		
		
		// ============================================================================
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		
		LatLng punto = new LatLng(-36.6, -72.11666);
		
		MarkerOptions marcador = new MarkerOptions();
		marcador.position(punto);
		marcador.title("Chillan");
		marcador.draggable(true);
		marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.icono));
		marcador.snippet("Esto es un comentario.");
		
		googleMap.addMarker(marcador);
		
		CameraPosition camara = new CameraPosition.Builder().target(punto).zoom(12).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara));
		
		googleMap.setMyLocationEnabled(true);
		
		googleMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				
				double nueva_latitud = arg0.latitude;
				double nueva_longitud = arg0.longitude;
				
				Bundle info = new Bundle();
				info.putDouble("lat", nueva_latitud);
				info.putDouble("lon", nueva_longitud);
				
				Intent traspaso = new Intent(Mapa.this, Clima.class);
				traspaso.putExtra("datos", info);
				
				startActivity(traspaso);
				
				Log.d("MAPA", "Mi latitud: " + nueva_latitud + ", Mi longitud: " + nueva_longitud);
			}
		});
		
		
		googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				
				double nueva_latitud = arg0.latitude;
				double nueva_longitud = arg0.longitude;
				
				Bundle informacion = new Bundle();
				informacion.putDouble("lat", nueva_latitud);
				informacion.putDouble("lon", nueva_longitud);
				
				Intent traspaso = new Intent(Mapa.this, Formulario.class);
				
				traspaso.putExtra("datos", informacion);
				
				startActivity(traspaso);
			}
		});
		
		
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				
				String titulo = arg0.getTitle();				
				LatLng posicion = arg0.getPosition();
				
				String lat = posicion.latitude + "";
				
				Intent intent = new Intent(Mapa.this, FormularioModificar.class);
				
				Bundle info = new Bundle();
				info.putString("titulo", titulo);
				info.putString("lat", lat);
				
				intent.putExtra("datos", info);
				startActivity(intent);
				
				return false;
			}
		});
		
		btnCambiar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				googleMap.setMapType(tiposMapas[tipo]);
				
				tipo++;
				
				if(tipo >= tiposMapas.length){
					tipo = 0;
				}
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		googleMap.clear();
		
		BaseDatosHelper bdHelper = new BaseDatosHelper(this, "LUGARES", null, 1);
		lectura = bdHelper.getReadableDatabase();
		
		String consulta = "SELECT * FROM lugares;";
		Cursor lugares = lectura.rawQuery(consulta, null);
		
		while(lugares.moveToNext()){
			String sLatitud = lugares.getString(1);
			String sLongitud = lugares.getString(2);
			String titulo = lugares.getString(3);
			
			double dLatitud = Double.parseDouble(sLatitud);
			double dLongitud = Double.parseDouble(sLongitud);
			
			LatLng lugar = new LatLng(dLatitud, dLongitud);
			
			MarkerOptions marker = new MarkerOptions();
			marker.position(lugar);
			marker.title(titulo);
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			
			googleMap.addMarker(marker);
			
			Log.i("BASEDATOS", titulo);
		}
		
		// ============================================================================
				Thread proceso_internet = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String contenido_pagina = obtenerContenido("http://mitlley.cl/android/lugares.json");
						
						try {
							
							JSONArray lugares = new JSONArray(contenido_pagina);
							
							for(int i = 0; i < lugares.length(); i++){
								final JSONObject lugar = lugares.getJSONObject(i);
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											
											MarkerOptions marcador = new MarkerOptions();
											marcador.title(lugar.getString("nombre"));
											
											double latitud = Double.parseDouble(lugar.getString("lat"));
											double longitud = Double.parseDouble(lugar.getString("lon"));
											
											LatLng posicion = new LatLng(latitud, longitud);
											marcador.position(posicion);
											
											marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
											googleMap.addMarker(marcador);
											
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
							}
							
							
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
		
		if(!proceso_internet.isInterrupted()){
			proceso_internet.interrupt();
		}
		proceso_internet.start();
		
	}
	
	private String obtenerContenido(String url){
		StringBuilder builder = new StringBuilder();
		HttpClient cliente = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		
		try {
			
			HttpResponse respuesta = cliente.execute(httpGet);
			StatusLine lineaEstado = respuesta.getStatusLine();
			int codigo = lineaEstado.getStatusCode();
			if(codigo == 200){
				HttpEntity entidad = respuesta.getEntity();
				InputStream contenido = entidad.getContent();
				BufferedReader lector = new BufferedReader(new InputStreamReader(contenido));
				String linea;
				while((linea = lector.readLine()) != null){
					builder.append(linea);
				}
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return builder.toString();
	}

}










