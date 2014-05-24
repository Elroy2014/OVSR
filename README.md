#README

This README will be a step by step tutorial on how to set up the OVSR application and server needed to cross-compile RenderScript.
To compile the OVSR source code you will need the [Android Developer Tools (ADT) ](https://developer.android.com/sdk/installing/bundle.html) and [Android NDK](https://developer.android.com/tools/sdk/ndk/index.html#Installing).
##OVSR
####Getting the source code
* Open a terminal window
* copy / paste " git clone https://github.com/degoossez/OVSR "

####Import into eclipse
* File 
* Import
* Android > Existing Android Code Into Workspace
* Browse to the OVSR directory you cloned in step 1
* Finish

####Setup the NDK
* Window > Preferences > Android > NDK
* Browser to the NDK directory, example: /home/user/android-ndk-r9d
* Apply and Ok

####Add NDK to OVSR project
* Right click on the OVSR project
* Android Tools > add native support 
* The name must be "libOVSR.so" so type OVSR in the input box!
* Click finish

####Add file browser library
* svn checkout http://android-file-dialog.googlecode.com/svn/trunk/ android-file-dialog-read-only
* Import the project into eclipse
* Build the project (don't run!) , use the build button or Ctrl + b
* There is a fileexplorer.jar in the bin directory of the fileexplorer project
* Right click on the OVSR project > Properties > Android > Add > FileExplorer
* When the jar is not created, follow this tutorial: http://aplacetogeek.wordpress.com/android-embedded-file-browser/

####Add video support
* Download the library's from (Ctrl + S to download them all at once!) https://drive.google.com/file/d/0B2MNqrU4BEonek5ldENzMlNSV2M/edit?usp=sharing  
* Unzip it into a folder you can remember
* In OVSR's libs folder you can find "JavaCV_Copy_Script.sh" 
* Open it and edit the second line to cp -r /the/path/where/you/extracted/the/library/* .
* Example: cp -r ~/AndroidJavaCVLibs/* .
* Save and close it
* Right click on the project > properties > Builders > New > Program > Ok
* Name: JavaCV_Copy_Script
* Location: ${workspace_loc:/OVSR/libs/JavaCV_Copy_Script.sh}
* Working directory: ${workspace_loc:/OVSR/libs}
* Ok
* Check it

####RenderScript

####OVSR server

The server is only necessary if you want to compile RenderScript code at runtime.
You also need to install this [FTP server](https://help.ubuntu.com/community/PureFTP) (very easy) which the application uses to download de bytecode from. It's highly recommended to use the same paths as in the tutorial.
To setup the OVSR server, the following steps need to be followed:
* Open a terminal window
* copy / paste " git clone https://github.com/degoossez/OVSRServer"


