RestWS App
----------

This app is an example of how an android app could access the data of Drupal site 
using the module RESTfull Web Services (http://drupal.org/project/restws).It has 
full CRUD support on nodes which have Titles and Bodies, but you need to know the 
node id to edit them, and the user id to create them. Currently you can only 
create pages, but extending it to support other content types should be no
problem.
Please note that the UI of this app is just for prototyping purposes, thats why
it may look ugly.

Code is available here:

Vote for my RestWs proposal for GSoC 2012 here ;-)


Drupal Installation
-------------------
Install and activate the RESTfull Web Services module as well as the sub module
Basic authentication login, which comes with RestWs. Create a new user using
'restws_' as prefix and give him a role which can at least view nodes.
  
Android
-------

The app uses Android SDK level 10, which means that your phone needs at least
Android 2.3.3.

Compile the app, install it and login using [menu]->[login]. Enter a url which
points to your Drupal site (i.e. http://localhost/drupal or http://example.com)
as well as your user id(needed to create new nodes) and your username and 
password.