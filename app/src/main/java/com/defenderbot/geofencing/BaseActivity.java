package com.defenderbot.geofencing;


import androidx.appcompat.app.AppCompatActivity;

//import com.tbruyelle.rxpermissions.RxPermissions;

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  protected void onStart() {
    super.onStart();
//    RxPermissions rxPermissions = new RxPermissions(this);
//    rxPermissions
//        .request(Manifest.permission.ACCESS_FINE_LOCATION)
//        .subscribe(new Action1<Boolean>() {
//          @Override
//          public void call(Boolean granted) {
//            if (granted) {
//              onLocationPermissionGranted();
//            } else {
//              Toast.makeText(BaseActivity.this, "Sorry, App did not work without Location permission", Toast.LENGTH_SHORT).show();
//            }
//          }
//        });
  }

  protected abstract void onLocationPermissionGranted();
}
