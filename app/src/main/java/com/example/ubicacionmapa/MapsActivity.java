package com.example.ubicacionmapa;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitud = 0.0;
    double longitud = 0.0;
    String direccion = "";
    TextView mensaje1;
    TextView mensaje2;
    int bandera = 0;
    int ban = 0;
    String iden = "";
    private DatabaseReference mDatabase;
    Button mButtonPanico;
    ArrayList<Double> datos=new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mButtonPanico = findViewById(R.id.button);





        // mensaje1 = (TextView) findViewById(R.id.mensaje_id);
        // mensaje2 = (TextView) findViewById(R.id.mensaje_id2);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            System.out.println("hola");
        }
        mButtonPanico.setOnClickListener(new View.OnClickListener() {
            int ban = 0;

            @Override
            public void onClick(View v) {
                locationStart();
                mostrarMensaje("Mensaje enviado vía WhatsApp");
               // LatLng sydney = new LatLng(latitud, longitud);
               // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
              //
                //enviarMensaje2(direccion,"59161591883");
            }

        });
    }

   public void obtenerDatos(){
       mDatabase = FirebaseDatabase.getInstance().getReference();
       mDatabase.child("Ubicacion").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               int j=100;
               for ( DataSnapshot snap : snapshot.getChildren()) {
                   String key = snap.getKey();
                   if (snapshot.exists()) {
                       String latitud = snapshot.child(key).child("latitud").getValue().toString();
                       String longitud = snapshot.child(key).child("longitud").getValue().toString();
                       double lat=Double.parseDouble(latitud);
                       double lon=Double.parseDouble(longitud);

                       cargarUbicaciones(mMap,lat,lon);
                   }

               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        obtenerDatos();


        // Add a marker in Sydney and move the camera

    }
    public void cargarUbicaciones(GoogleMap mapa,double latitud,double longitud){
           final LatLng sydney = new LatLng(latitud, longitud);
         mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


    }

    public void mostrarMensaje(String mensaje) {
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage(mensaje);
        AlertDialog dialog = myBuild.create();
        dialog.show();
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

//        mensaje1.setText("Localización agregada");
    //    mensaje2.setText("");
      //  ban = 0;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
//                    mensaje2.setText("Mi direccion es: \n"
 //                           + DirCalle.getAddressLine(0));
                    direccion = "Por Favor ayudame siento que estoy en peligro. Mi dirección es: " + DirCalle.getAddressLine(0);
                    latitud = loc.getLatitude();
                    longitud = loc.getLongitude();
                    Map<String, Object> hopperUpdates = new HashMap<>();
                    hopperUpdates.put("latitud", latitud);
                    hopperUpdates.put("longitud", longitud);
                    mDatabase.child("Ubicacion").push().setValue(hopperUpdates);
                    Log.e("key",""+iden);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void enviarMensaje2(String mensaje, String num) {
        String ID = "lFqydtLw6kGYAPvAYGVaGHl1cmd1ZW4wMDcxX2F0X2dtYWlsX2RvdF9jb20=";
        HttpURLConnection conexion = null;
        try {
            URL enlace = new URL("https://NiceApi.net/API");
            conexion = (HttpURLConnection) enlace.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("X-APIId", ID);
            conexion.setRequestProperty("X-APIMovile", num);
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conexion.setUseCaches(false);
            conexion.setDoOutput(true);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeBytes(mensaje);
            salida.close();
            InputStream entrada = conexion.getInputStream();
            BufferedReader lectura = new BufferedReader(new InputStreamReader(entrada));
            lectura.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MapsActivity mapsActivity;

        public MapsActivity getMainActivity() {
            return mapsActivity;
        }

        public void setMainActivity(MapsActivity mapsActivity) {
            this.mapsActivity = mapsActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion


            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            if (ban == 0) {
//                mensaje1.setText(Text);
                this.mapsActivity.setLocation(loc);
                ban++;
            }

        }


        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}