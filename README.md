# What?

 Find amenities with your wearable

# How to build  

 you need to generate a google-maps API key ( replace HASH_OF_YOUR_KEY with the hash of your key):
  https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=HASH_OF_YOUR_KEY%3Bberlin.funemployed.wherewhat
  
  you need to add a maps api key e.g. here:
  
  wear/src/debug/res/values/google_maps_api.xml

 like this:
 ```xml
  <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">THE_API_KEY_YOU_GOT</string>
 ```

 now you can build the project 
 
 ```
  ./gradlew clean build
 ```
 
 NOTE: when releasing the app you also need to do this for the release-key
 
# Used libraries
 
  * https://github.com/johnjohndoe/Overpass
  * https://github.com/JakeWharton/butterknife
  * https://github.com/ligi/AXT
  * and the Android support stuff
  
# Licence 

GPLv3