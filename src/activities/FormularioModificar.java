package activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.talleres.android.mapa.R;

import datos.BaseDatosHelper;

public class FormularioModificar extends Activity {
	
	private EditText etxtTitulo;
	private Button btnModificar, btnEliminar;
	
	private SQLiteDatabase basedatos;
	private String latitud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario_modificar);
		
		etxtTitulo = (EditText) findViewById(R.id.etTitulo);
		
		btnModificar = (Button) findViewById(R.id.btnModificar);
		btnEliminar = (Button) findViewById(R.id.btnEliminar);
		
		
		Intent in =  getIntent();
		Bundle datos = in.getBundleExtra("datos");
		
		String titulo = datos.getString("titulo");
		latitud = datos.getString("lat");
		
		etxtTitulo.setText(titulo);
		
		BaseDatosHelper helper = new BaseDatosHelper(getApplicationContext(), "LUGARES", null, 1);
		basedatos = helper.getWritableDatabase();
		
		btnModificar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String titulo = etxtTitulo.getText().toString();
				String sql = "UPDATE lugares SET titulo = '" + titulo +  "' WHERE latitud = '" + latitud + "';";
				
				basedatos.execSQL(sql);
				
				finish();
			}
		});
		
		btnEliminar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String sql = "DELETE FROM lugares WHERE latitud = '" + latitud + "';";
				basedatos.execSQL(sql);
				
				finish();
				
			}
		});
	}

}
