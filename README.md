# er

/**
// To add to 'build.gradle' file in root folder where 'src/' folder is located
**/


android {
  ...
 
  repositories {
          mavenCentral()
      }
}

dependencies {
    compile 'com.squareup.picasso:picasso:2.5.2'
    compile 'com.android.support:cardview-v7:23.4.0'
    compile 'com.android.support:recyclerview-v7:23.4.0'
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'com.android.support:design:23.4.0'
    compile 'com.android.support:support-v4:23.4.0'
}
