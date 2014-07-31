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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.talleres.android.mapa.R;

public class Clima extends Activity {
	
	private TextView tvCiudad, tvPronostico, tvDescripcion, tvMin, tvMax;
	private double lat, lon;
	private Handler handler;
	
	private ProgressBar pbInternet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clima);
		
		handler = new Handler();
		
		tvCiudad = (TextView) findViewById(R.id.tvCiudad);
		tvPronostico = (TextView) findViewById(R.id.tvPronostico);
		tvMin = (TextView) findViewById(R.id.tvTmpMin);
		tvMax = (TextView) findViewById(R.id.tvTmpMax);
		tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
		pbInternet = (ProgressBar) findViewById(R.id.pbCarga);
		
		Intent traspaso = getIntent();
		Bundle info = traspaso.getBundleExtra("datos");
		
		lat = info.getDouble("lat");
		lon = info.getDouble("lon");
		
		Thread internet = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String contenido_pagina = obtenerContenido("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&lang=es");
				try {
					
					JSONObject pronostico = new JSONObject(contenido_pagina);
					JSONObject sys = pronostico.getJSONObject("sys");
					JSONArray clima = pronostico.getJSONArray("weather");
					JSONObject climaActual = clima.getJSONObject(0);
					
					final String ciudad = pronostico.getString("name");
					final String pais = sys.getString("country");
					final String climaPronostico = climaActual.getString("main");
					final String descripcion = climaActual.getString("description");
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pbInternet.setVisibility(View.INVISIBLE);
							tvCiudad.setText(ciudad + " (" + pais + ")");
							tvPronostico.setText(climaPronostico);
							tvDescripcion.setText(descripcion);
						}
					});
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		internet.start();
		
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
