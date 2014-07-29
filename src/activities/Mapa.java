package activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talleres.android.mapa.R;

public class Mapa extends FragmentActivity {
	
	GoogleMap googleMap;
	
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
		
		googleMap.addMarker(marcador);
		
		CameraPosition camara = new CameraPosition.Builder().target(punto).zoom(12).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara));
		
		googleMap.setMyLocationEnabled(true);
		
	}

}
