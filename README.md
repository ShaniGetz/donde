### README

#### Dónde
##### A wireless location App for finding your friends 
###### Team members: Shani Getz, Alon Emanuel, Batsheva Schiff
 
Donde is a wireless connection app. This may sound contradictory but this app should be used to 
connect local users at a small distance and pass between them location-data and other information
with no need for Internet connection or cellular coverage.

Each user is granted a personal user, with said user they can create Events. To make an Event the 
user must chose a name for said event, describe the event, chose the location, and invite friends 
who  are also users in the application. The next step of the application has to be done before the 
beginning of the event, while The user still has internet. The user downloades the chosen 
map area, and profile pictures of the other participants. This way when the users choses to go to 
said event, they can access the event, even with out Wi-Fi or a network coverage. The user will 
still be able to pass and receive information making this app perfect for use in crowded places. 

To achieve the passing of information we described we use Google's Nearby Connections API. This API 
is a peer-to-peer networking  API that allows apps to easily discover, connect to, and exchange data
with nearby devices in real-time, regardless of network connectivity. It uses a high level of protocol 
on top of Bluetooth and WiFi that acts as a socket. Devices are able to advertise, scan, and connect to
each another.

Also in the Application we use Cloud Firestore to store all the list of Events and users of the Application.
Sometime to access The information a local fireStore offline is created, which makes a local cache copy 
of the Cloud Firestore data. We write, read, listen to, and query the cached data and When the device comes 
back online, Cloud Firestore synchronizes any local changes made by your app to the Cloud Firestore backend.

One of the many obstacle we had to overcome during the building of this application is to download a map
and keep it available offline. This was challenging because this is not something that Google maps allows 
to do (legally and technologically). To download the map ahead of time we would first get the location 
wanted and download a map image from www.openstreetmap.org and then lay it over GoogleMaps as tiles, 
all this will keeping a local cache with map data  


