package com.example.iirol.harjoitus9_10;

import android.Manifest;
import android.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	// CONSTANTS
	private final long LOCATION_REFRESH_MIN_TIME = 1; // 5s
	private final float LOCATION_REFRESH_DISTANCE = 1; // 2m
	private final String DEGREE_SYMBOL = " °";
	private final String SPEED_SYMBOL = " m/s";
	private final String PERCENTAGE_SYMBOL = " %";

	// FIELDS
	private TextView TextView_altitudeValue;
	private TextView TextView_latitudeValue;
	private TextView TextView_longitudeValue;
	private TextView TextView_bearingValue;
	private TextView TextView_speedValue;
	private SupportMapFragment SupportMapFragment_map;
	private CheckBox CheckBox_followLocationOnMap;
	private SeekBar SeekBar_zoom;
	private TextView TextView_zoomValue;

	private LocationManager locationManager;
	private GoogleMap googleMap;
	private Marker marker;
	private Location location;
	private LatLng locationLatLng;
	private int zoomLevel = 10;

	private boolean followLocationOnMap;

	// @FragmentActivity
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		// Get Views
		this.TextView_altitudeValue = this.findViewById(R.id.TextView_altitudeValue);
		this.TextView_latitudeValue = this.findViewById(R.id.TextView_latitudeValue);
		this.TextView_longitudeValue = this.findViewById(R.id.TextView_longitudeValue);
		this.TextView_bearingValue = this.findViewById(R.id.TextView_bearingValue);
		this.TextView_speedValue = this.findViewById(R.id.TextView_speedValue);
		this.SupportMapFragment_map = (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.SupportMapFragment_map);
		this.CheckBox_followLocationOnMap = this.findViewById(R.id.CheckBox_followLocationOnMap);
		this.SeekBar_zoom = this.findViewById(R.id.SeekBar_zoom);
		this.TextView_zoomValue = this.findViewById(R.id.TextView_zoomValue);

		// Set listeners
		this.SupportMapFragment_map.getMapAsync(this);
		this.CheckBox_followLocationOnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MainActivity.this.followLocationOnMap = isChecked;
				if (isChecked) {
					MainActivity.this.updateMarker();
				}
			}
		});
		this.SeekBar_zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				// Disable zero by adding 10 to each progress
				MainActivity.this.zoomLevel = progress;

				// Update zoom level text
				String zoomValue = String.valueOf(MainActivity.this.zoomLevel) + MainActivity.this.PERCENTAGE_SYMBOL;
				MainActivity.this.TextView_zoomValue.setText(zoomValue);

				// Update marker
				MainActivity.this.updateMarker();
			}
			@Override public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override public void onStopTrackingTouch(SeekBar seekBar) { }
		});

		// Permission requests
		ActivityCompat.requestPermissions(
	this,
			new String[] {
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			},
1
		);

		try {
			this.locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MainActivity.this.LOCATION_REFRESH_MIN_TIME, MainActivity.this.LOCATION_REFRESH_DISTANCE, this);
		} catch (SecurityException ex) {
			System.out.println("VIRHE!!!!! ---------------------------------------------------------------------");
			System.out.println(ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

	}
	@Override protected void onResume() {
		super.onResume();

	}

	// @OnMapReadyCallback
	@Override public void onMapReady(GoogleMap googleMap) {

		this.googleMap = googleMap;
	}

	// @LocationListener
	@Override public void onProviderEnabled(String provider) {
		Toast.makeText(MainActivity.this, "GPS is enabled!", Toast.LENGTH_LONG).show();
	}
	@Override public void onProviderDisabled(String provider) {
		Toast.makeText(MainActivity.this, "Please turn GPS on!", Toast.LENGTH_LONG).show();
	}
	@Override public void onLocationChanged(Location location) {

		// Update the location info
		this.location = location;
		this.locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());

		// MY CURRENT LOCATION-panel
		String currentAltitude = String.valueOf(location.getAltitude()) + this.DEGREE_SYMBOL;
		String currentLatitude = String.valueOf(location.getLatitude()) + this.DEGREE_SYMBOL;
		String currentLongtitude = String.valueOf(location.getLongitude()) + this.DEGREE_SYMBOL;

		String currentBearing = String.valueOf(location.getBearing()) + this.DEGREE_SYMBOL;
		String currentSpeed = String.valueOf(location.getSpeed()) + this.SPEED_SYMBOL;

		this.TextView_altitudeValue.setText(currentAltitude);
		this.TextView_latitudeValue.setText(currentLatitude);
		this.TextView_longitudeValue.setText(currentLongtitude);
		this.TextView_bearingValue.setText(currentBearing);
		this.TextView_speedValue.setText(currentSpeed);

		// Update map markers
		this.updateMarker();

	}
	private void updateMarker() {

		// If the map isn't ready yet, skip
		if (this.googleMap == null) { return; }

		// If the location is unknown, skip
		if (this.locationLatLng == null) { return; }

		if (this.marker == null) {
			// Create the marker if it doesn't exist yet
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.title(this.getResources().getString(R.string.mainactivity_me));
			markerOptions.position(this.locationLatLng);
			this.marker = this.googleMap.addMarker(markerOptions);

		} else {
			// Else move the existing marker to current location
			this.marker.setPosition(this.locationLatLng);
		}

		// If user chose to follow on map, zoom
		if (this.followLocationOnMap) {

			// Animate camera to current position
			this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.locationLatLng, this.zoomLevel));
		}
	}
	@Override public void onStatusChanged(String provider, int status, Bundle extras) { }

}
