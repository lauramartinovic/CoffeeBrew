# CoffeeBrew App

Mobile application made in Kotlin.  
The app lets you add, edit, delete and view records of coffee brews.  

## What the app does
- shows a list of all brews (coffee name, roaster, method and date)
- add a new brew with details
- each record has:
  - coffee name
  - roaster
  - brew method (choose from fixed list: V60, Aeropress, French Press, Espresso, Moka)
  - brew date
  - image (picked from the device)
- records can be edited or deleted

## Used technologies
- Kotlin
- MVVM architecture
- Room database
- ViewBinding
- RecyclerView
- DatePicker + Spinner for choosing date and method
- Coil for displaying images
- Custom app icon made in GIMP

## How to run
Open the project in Android Studio.  
You need JDK 17 and minimum Android SDK 24.  

