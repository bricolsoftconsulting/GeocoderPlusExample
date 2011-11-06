Geocoder Plus Example
=====================

What is GeocoderPlus?
---------------------
GeocoderPlus is a replacement for the built-in Android Geocoder library. Just like the original, GeocoderPlus geocodes locations -- you enter location names and it returns latitude and longitude coordinates. Unlike the original, GeocoderPlus actually gives you viewport information so you can zoom maps correctly to display entire locations on the screen -- whether those locations are countries, states, cities or buildings.

GeocoderPlus does not include reverse geocoding. You can use the built-in Android Geocoder for reverse geocoding -- no complaints about that functionality.

You can find the GeocoderPlus repository at https://github.com/bricolsoftconsulting/GeocoderPlus/

What is the GeocoderPlus Example?
---------------------------------
GeocoderPlus Example is a complete Android app that shows how to use GeocoderPlus to display locations on a map at the proper zoom factor.

Installation
------------
To setup this project:

1. Download a copy of this code.
1. Edit the `main.xml` file in `/res/layout` to add your Google Maps API Key on this line:

    android:apiKey="ENTER_GOOGLE_MAPS_API_KEY_HERE"

Usage
-----
Run the app, enter a location and then press `Go`. GeocoderPlus will get a list of addresses that match the location name. If multiple addresses match, the app will prompt you to select the desired address. Finally, the app will adjust the map appropriately to display the entire location on the screen.

Copyright
---------
Copyright 2011 Bricolsoft Consulting

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.