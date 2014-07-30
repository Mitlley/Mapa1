package activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talleres.android.mapa.R;

import datos.BaseDatosHelper;

public class Mapa extends FragmentActivity {
	
	GoogleMap googleMap;
	
	private SQLiteDatabase lectura;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		
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
		
	}

}
