/*
Copyright 2011 Bricolsoft Consulting

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.bricolsoftconsulting.geocoderplus.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bricolsoftconsulting.geocoderplus.Address;
import com.bricolsoftconsulting.geocoderplus.Geocoder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MyMapActivity extends MapActivity {
	
	// Members
	private MapView mMapView;
	private Address mAddress;
	private List<Address> mAddresses;
	private Handler mHandler = new Handler();
	private Runnable mDisplayLocation = new Runnable()
	{
		public void run()
		{
			// Get the map center 
			GeoPoint mapCenter = new GeoPoint((int) (mAddress.getLatitudeE6()), (int) (mAddress.getLongitudeE6()));
			
			// Get the map controller
			MapController mapController = mMapView.getController();
			
			// Fix position
			mapController.animateTo(mapCenter);
			
			// Fix zoom
			mapController.zoomToSpan(mAddress.getViewPort().getLatitudeSpanE6(), mAddress.getViewPort().getLongitudeSpanE6());
		}
	};
	public Runnable mSelectLocation = new Runnable()
	{
		public void run()
		{
			showLocationPicker(mAddresses);
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Add button handler
        Button buttonGo = (Button) findViewById(R.id.buttonGo);
        buttonGo.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// Get the location text
				EditText editTextLocation = (EditText)findViewById(R.id.editTextLocation);
				String locationName = editTextLocation.getText().toString();
				
				// Hide the keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextLocation.getWindowToken(), 0);
				
				// Process the location string
				GeocodeTask geocodeTask = new GeocodeTask();
				geocodeTask.execute(locationName);
			}
		});
        
        // Get the map
        mMapView = (MapView) findViewById(R.id.theMap);
    }
    
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
    
    public List<Address> geocodeLocation(String locationName)
	{    	
		// Geocode the location
		Geocoder geocoder = new Geocoder();
		try
		{
	    	List<Address> addresses = geocoder.getFromLocationName(locationName);
	    	return addresses;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
    
    public class GeocodeTask extends AsyncTask<String, Void, List<Address>>
	{
		// Declare
		private ProgressDialog mPleaseWaitDialog = null;
		
		public void showLocationPendingDialog()
	    {
	    	if (mPleaseWaitDialog != null)
	    	{
	    		return;
	    	}
	    	
	    	mPleaseWaitDialog = new ProgressDialog(MyMapActivity.this);
	    	mPleaseWaitDialog.setMessage("Searching for location...");
	    	mPleaseWaitDialog.setTitle("Please wait");
	    	mPleaseWaitDialog.setIndeterminate(true);
	    	mPleaseWaitDialog.show();
	    }
	    
	    public void cancelLocationPendingDialog()
	    {	    	
	    	if (mPleaseWaitDialog != null)
	    	{
	    		mPleaseWaitDialog.dismiss();
	    		mPleaseWaitDialog = null;
	    	}
	    }
	    
		@Override
		protected void onPostExecute(List<Address> addresses)
		{
			super.onPostExecute(addresses);
			cancelLocationPendingDialog();
			
			// Check the number of results
			if (addresses != null)
			{
				if (addresses.size() > 1)
				{
					// Clear address
					mAddress = null;
					
					// Set addresses
					mAddresses = addresses;
					
					// Determine which address to display
					mHandler.post(mSelectLocation);
				}
				else
				{
					// Set position
					mAddress = addresses.get(0);
					
					// Display address
					mHandler.post(mDisplayLocation);
				}
			}
			else
			{
				showAlert("No results found!", "Error");
			}
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			showLocationPendingDialog();
		}
		
		@Override
		protected List<Address> doInBackground(String... args)
		{
			// Declare
			List<Address> addresses;
			
			// Extract parameters
			String locationName = args[0];
			
			// Geocode
			addresses = geocodeLocation(locationName);
			
			// Return
			return addresses;
		}
	};
	
	private void showAlert(final String message, String title)
	{		
		if (!isFinishing())
		{	
			AlertDialog.Builder builder = new AlertDialog.Builder(MyMapActivity.this);
			builder.setMessage(message);
			builder.setCancelable(true);
			builder.setTitle(title);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	private void showLocationPicker(final List<Address> results)
	{
		// Check input
		if(results.size() == 0) return;
		
		// Do not build dialog if the activity is finishing
		if (isFinishing()) return;
		
		// Convert the list of results to display strings
		final String[] items = getAddressStringArray(results);
		
		// Display alert
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Did you mean:");
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialogInterface, int itemIndex)
			{
				// Set address
				mAddress = results.get(itemIndex);
				
				// Display the position
				mHandler.post(mDisplayLocation);
			}
		});
		builder.create().show();
	}
	
	private String[] getAddressStringArray(List<Address> results)
	{		
		// Declare
		ArrayList<String> result = new ArrayList<String>();
		String[] resultType = new String[0];
		
		// Iterate over addresses
		for (int i = 0; i< results.size(); i++)
		{
			// Get the data
			String formattedAddress = results.get(i).getFormattedAddress();
			if (formattedAddress == null) formattedAddress = "";
			result.add(formattedAddress);
		}
		
		// Return
		if (result.size() == 0)
		{
			return null;
		}
		else
		{
			return (String[])result.toArray(resultType);
		}
	}
}