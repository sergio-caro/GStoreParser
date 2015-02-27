# GStoreParser
This is a **Java** application that generates **CSV files** (for importing into Excel, mainly) based on HTML files (as input) with a list of mobile ***applications from Google Play Store*** website. From this list of applications, this program makes requests to Google trying to retrieve key data (name, price, ratings and rating) and return it to CSV-format files (semicolon separators).

Because of the way of working of Google ("*lazy load*" of resources), the user must download the list of applications (web page as HTML) and tell the program the location of this file to start working.

## Download
Go to [Releases section](https://github.com/sergio-caro/GStoreParser/releases) and get the latest one.

After downloading the compressed file, uncompress it and execute the JAR file (GStoreParser.jar).

You may need to install Java JRE in its last version.

## User Guide
You could find information about execution on the [Wiki](https://github.com/sergio-caro/GStoreParser/wiki). 

At the moment, user guide is only in spanish.

## Do you want to contribute?
Fork the repository, change whatever you want and make a Pull Request.By the way, you are welcome!!

Take into account working with **Java SDK 1.7** and Netbeans IDE (optional, but highly recommended).
