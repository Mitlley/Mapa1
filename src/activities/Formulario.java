package activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.talleres.android.mapa.R;

import datos.BaseDatosHelper;

public class Formulario extends Activity {
	
	private EditText etxtLat, etxtLon, etxtTitulo;
	private Button btnGuardar;
	
	private SQLiteDatabase escritura;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formualrio);
		
		Intent traspaso = getIntent();
		Bundle informacion = traspaso.getBundleExtra("datos");
		
		double latitud = informacion.getDouble("lat");
		double longitud = informacion.getDouble("lon");
		
		etxtLat = (EditText) findViewById(R.id.etxtLatitud);
		etxtLon = (EditText) findViewById(R.id.etxtLongitud);
		etxtTitulo = (EditText)	findViewById(R.id.etxtTitulo);
		
		btnGuardar = (Button) findViewById(R.id.btnGuardar);
		
		etxtLat.setText(latitud + "");
		etxtLon.setText("" + longitud);
		
		etxtLat.setEnabled(false);
		etxtLon.setEnabled(false);
		
		// BASE DE DATOS =================================================
		
		BaseDatosHelper bdHelper = new BaseDatosHelper(this, "LUGARES", null, 1);
		escritura = bdHelper.getWritableDatabase();
		
		btnGuardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String lat = etxtLat.getText().toString();
				String lon = etxtLon.getText().toString();
				String titulo = etxtTitulo.getText().toString();
				
				String insertar = "INSERT INTO lugares (latitud, longitud, titulo) VALUES ('" + lat + "','" + lon + "','" + titulo + "');";
				escritura.execSQL(insertar);
				
				Toast mensaje = Toast.makeText(getApplicationContext(), "Guardado con exito.", Toast.LENGTH_SHORT);
				mensaje.show();
				
			}
		});
		
	}
	
}
