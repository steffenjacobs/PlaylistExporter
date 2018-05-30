# PlaylistExporter
This small tool can read a playlist and export all related media files into a new directory. This is useful e.g. if you use playlists on your computer and want to gather all media files from your playlist into a single folder to copy them to your mobile phone.

# Supported Playlist Formats
Currently, the playlist formats Windows Media Playlist (WPL), Zune Playlist (ZPL), Advanced Stream Redirector (ASX) and MP3 URL (M3U) are supported.

# Usage
java -jar ./PlaylistExporter.jar &lt;Path-To-PlaylistFile1&gt; [&lt;PlaylistFile2&gt;]*

It is possible to add multiple playlist files, separated by spaces.

# How it works
First, the tool will create a new directory in the same folder, the playlist file is in. The directory will be named after the playlist. 
The tool will then retrieve the file associated to each entry in the playlist and copies it into the new directory.
