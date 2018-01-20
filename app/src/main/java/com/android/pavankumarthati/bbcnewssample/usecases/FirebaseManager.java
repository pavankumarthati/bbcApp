package com.android.pavankumarthati.bbcnewssample.usecases;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by pavankumar.thati on 1/4/18.
 */

public final class FirebaseManager implements ChildEventListener {

  DatabaseReference mDatabaseRef;
  FirebaseCallbacks mFirebaseCallbacks;

  public FirebaseManager(@NonNull String name, @Nullable FirebaseCallbacks firebaseCallbacks) {
    mDatabaseRef = FirebaseDatabase.getInstance().getReference(name);
    mFirebaseCallbacks = firebaseCallbacks;
  }

  public void setCallbacks(FirebaseCallbacks firebaseCallbacks) {
    mFirebaseCallbacks = firebaseCallbacks;
  }

  @Override
  public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    mFirebaseCallbacks.onNewMessage(dataSnapshot);
  }

  @Override
  public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    mFirebaseCallbacks.onMessageChanged(dataSnapshot);
  }

  @Override
  public void onChildRemoved(DataSnapshot dataSnapshot) {
    mFirebaseCallbacks.onMessageRemoved(dataSnapshot);
  }

  @Override
  public void onChildMoved(DataSnapshot dataSnapshot, String s) {

  }

  @Override
  public void onCancelled(DatabaseError databaseError) {
    mFirebaseCallbacks.onCancelled(databaseError);
  }

  public void addListener() {
    mDatabaseRef.orderByKey().addChildEventListener(this);
  }

  public void removeListener() {
    mDatabaseRef.removeEventListener(this);
  }

  public interface FirebaseCallbacks {

    public void onNewMessage(DataSnapshot dataSnapshot);
    public void onMessageChanged(DataSnapshot dataSnapshot);
    public void onMessageRemoved(DataSnapshot dataSnapshot);
    public void onCancelled(DatabaseError databaseError);

  }

}
