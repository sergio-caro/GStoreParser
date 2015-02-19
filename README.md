# GStoreParser [NOTE: Under Construction]
This is a **Java** application that generates **CSV files** (for importing into Excel, mainly) based on HTML files (as input) with a list of mobile ***applications from Google Play Store*** website. From this list of applications, this program makes requests to Google trying to retrieve key data (name, price, ratings and rating) and return it to CSV-format files (semicolon separators).

Because of the way of working of Google ("*lazy load*" of resources), the user must download the list of applications (web page as HTML) and tell the program the location of this file to start working.
