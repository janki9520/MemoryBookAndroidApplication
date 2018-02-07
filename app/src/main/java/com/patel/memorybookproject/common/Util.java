package com.patel.memorybookproject.common;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.patel.memorybookproject.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ADMIN-PC on 09-12-2017.
 */

public class Util {

    public static void putDataToUsers(final User user) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUid())) {
                    Map<String, Object> userValues = user.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/users/" + user.getUid(), userValues);

                    databaseReference.updateChildren(childUpdates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static FirebaseUser getAuthUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    public static String getPathForEntries(String fUid) {
        return "/users/" + "/" + fUid + "/addMemories";
    }

}
