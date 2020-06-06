package br.com.diegosilva.home.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}

object FirebaseSdk {
  def init: Unit = {
    val serviceAccount = FirebaseSdk.getClass.getClassLoader.getResourceAsStream("firebase.json")
    val options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://house-py.firebaseio.com").build
    FirebaseApp.initializeApp(options)
  }
}
