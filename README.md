OpenCellDroid
=============

An OpenCellID client for Android

This client shall help to find all phone cells around the world with their locations.
<br />The cell information are saved in a public database.

This is an independent project.
<br />Fore more information about the OpenCellID project, please visit http://www.opencellid.org

<b>PLEASE NOTE</b>
<br />You will need your own API key from http://www.opencellid.org and your own Google Maps API key to run this app.
When you got your own API keys, simply create a new xml file into res/values/apikey.xml and type in the following:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="opencellid_apikey">Your Open Cell ID API key</string>
    <string name="googlemaps_apikey">Your Google Maps API key</string>
</resources>
```
